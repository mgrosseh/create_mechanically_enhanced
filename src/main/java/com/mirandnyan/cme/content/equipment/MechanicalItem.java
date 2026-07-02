package com.mirandnyan.cme.content.equipment;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMEItems;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.SlotEntry;
import com.mirandnyan.cme.content.equipment.mechanical_tool.RemovingPartResult;
import com.mirandnyan.cme.util.neoforge_helpers.ItemAttributeModifiersRebuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@EventBusSubscriber
public abstract class MechanicalItem extends Item {

    public static final int DEFAULT_TRANSFER_RATIO = 2;
    public static final int BLOCK_BREAK_DURABILITY_USE = 1;
    public static final int ENTITY_ATTACK_DURABILITY_USE = 2;


    public MechanicalItem(Properties properties) {
        super(properties);
    }

    protected static @Nullable FilledToolSlot getToolSlot(ItemStack stack, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        List<FilledToolSlot> slots = stack.get(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE);
        if (slots == null)
            return null;
        for (FilledToolSlot toolSlot : slots) {
            if (toolSlot.isSlot(slot))
                return toolSlot;
        }
        return null;
    }

    protected static boolean canSupportAll(Map<@NonNull ResourceKey<MechanicalToolSlot>, @NotNull Long> slotTypeCounts,
                                           RegistryEntry<MechanicalPart, MechanicalPart> insertingPart) {
        return slotTypeCounts.entrySet().stream()
                .allMatch(e -> insertingPart.get().supportingSlots(e.getKey()).count() >= e.getValue());
    }

    protected static Optional<RegistryEntry<MechanicalPart, MechanicalPart>>
    tryReplacingMechanicalPart(@NotNull ItemStack stack, RegistryEntry<MechanicalPart, MechanicalPart> insertingPart) {
        List<FilledToolSlot> filledToolSlots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        var slotType = insertingPart.get().getOriginSlot().getKey();

        // out of all populated slots, figure out if all children of potential swap, can be supported by insertingPart
        var candidateSlots = filledToolSlots
                .stream()
                .filter(s -> s.slot().type() == slotType)
                .filter(s -> {
                    var part = Optional.of(s.part());
                    var children = filledToolSlots.stream().filter(c -> c.parent().equals(part));
                    var childSlotTypeCounts = children
                            .collect(Collectors.groupingBy(c -> c.slot().type(), Collectors.counting()));
                    return canSupportAll(childSlotTypeCounts, insertingPart);
                });
        var maybe_replaceTarget = candidateSlots.findAny();
        if (maybe_replaceTarget.isEmpty())
            return Optional.empty();
        var replaceTarget = maybe_replaceTarget.get();

        var targetAsParent = Optional.of(replaceTarget.part());
        var newAsParent = Optional.of(insertingPart.getKey());

        //noinspection OptionalGetWithoutIsPresent // we know replaceTarget is in filled tool slots, so we always have a value
        var replaceSlot = filledToolSlots.stream().filter(s -> s == replaceTarget).map(FilledToolSlot::slot).findAny().get();

        var newList = filledToolSlots.stream()
                .map(s -> {
                    if (s == replaceTarget) {
                        return new FilledToolSlot(s.slot(), insertingPart.getKey(), s.parent());
                    }
                    if (s.parent().equals(targetAsParent)) {
                        return new FilledToolSlot(s.slot(), s.part(), newAsParent);
                    }
                    return s;
                }).toList();
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, newList);
        replaceTarget.getPart().data.onRemoved(replaceSlot, stack);
        insertingPart.get().data.onInserted(replaceSlot, stack);

        return Optional.of(replaceTarget.getPartEntry());
    }

    private static void insertMechanicalPart(ItemStack stack, SlotEntry entry, RegistryEntry<MechanicalPart, MechanicalPart> part) {
        ArrayList<FilledToolSlot> filledSlots = new ArrayList<>(stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of()));
        filledSlots.add(new FilledToolSlot(entry.id(), part.getKey(), entry.parent()));
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(filledSlots));
        part.get().data.onInserted(entry.id(), stack);
    }

    private static void removeMechanicalPart(ItemStack stack, FilledToolSlot filledToolSlot) {
        ArrayList<FilledToolSlot> filledSlots = new ArrayList<>(stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of()));
        filledSlots.remove(filledToolSlot);
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(filledSlots));
        filledToolSlot.getPart().data.onRemoved(filledToolSlot.slot(), stack);
    }

    protected static boolean tryInsertingMechanicalPart(ItemStack stack, RegistryEntry<MechanicalPart, MechanicalPart> toInsert) {
        return tryInsertingMechanicalPart(stack, toInsert, toInsert.get().getOriginSlot().getKey());
    }

    protected static Set<SlotEntry> populatedSlots(List<FilledToolSlot> filledToolSlots) {
        return filledToolSlots.stream().map(SlotEntry::fromFilled).collect(Collectors.toSet());
    }

    protected static boolean tryInsertingMechanicalPart(ItemStack stack, RegistryEntry<MechanicalPart, MechanicalPart> toInsert, ResourceKey<MechanicalToolSlot> slotType) {
        List<FilledToolSlot> oldSlots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        if (oldSlots.isEmpty() && slotType == MechanicalToolSlot.ROOT.getKey()) {
            insertMechanicalPart(stack, SlotEntry.root(), toInsert);
            return true;
        }


        Set<SlotEntry> populatedSlots = populatedSlots(oldSlots);
        var openSlots = oldSlots.stream().flatMap(s ->
                s.getPart().supportingSlots(slotType).map(SlotEntry.factoryOf(s.part())).filter(e -> !populatedSlots.contains(e))
        );

        var firstOpenSlot = openSlots.findAny();
        if (firstOpenSlot.isEmpty())
            return false;

        insertMechanicalPart(stack, firstOpenSlot.get(), toInsert);
        return true;
    }

    protected static RemovingPartResult tryRemovingMechanicalPart(@NotNull ItemStack stack, @NotNull SlotAccess access, int selectedToolSlot) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        if (slots.isEmpty() || selectedToolSlot >= slots.size() || selectedToolSlot == -1)
            return RemovingPartResult.impossible();

        // find part to remove
        var toolSlotToRemove = slots.get(selectedToolSlot);

        // make sure no part is child of
        var len = slots.size();
        for (int i = 0; i < len; i++) {
            var slot = slots.get(i);
            if (slot.parent().isEmpty())
                continue;
            if (slot.parent().get() == toolSlotToRemove.part())
                return RemovingPartResult.isParentOf(i);
        }

        // TODO: let parts block removal of other part maybe?

        var removedPart = toolSlotToRemove.getPartEntry().get();

        removeMechanicalPart(stack, toolSlotToRemove);

        ArrayList<FilledToolSlot> newSlots = new ArrayList<>(slots);
        newSlots.remove(selectedToolSlot);
        removedPart.data.onRemoved(toolSlotToRemove.slot(), stack);
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(newSlots));

        recalculateTotalWeight(stack);
        var item = removedPart.getItemRegistry().get().getDefaultInstance();
        access.set(item);

        return RemovingPartResult.success();
    }


    protected static void recalculateTotalWeight(ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        float weight = 0;
        for (FilledToolSlot slot : slots) {
            var part = slot.getPartEntry();
            weight += part.get().data.weight;
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(stack.getAttributeModifiers())
                .filter(e -> !e.attribute().equals(Attributes.ATTACK_SPEED)).add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -weight, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build()
        );
    }

    // --

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        ItemStack stack = player.getMainHandItem(); // TODO offhand too with boolean

        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPartEntry().get().data.playerTick(
                    player, stack
            );
        }
    }
    // -- Breaking Blocks with Item --
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void consumeDurabilityOnBlockBreak(BlockEvent.BreakEvent event) {
        findAndDamageItem(event.getPlayer());
        notifyPartsOfBlockBreak(event);
    }

    protected static void findAndDamageItem(@NotNull Player player) {
        if (player.level().isClientSide)
            return;
        EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
        ItemStack item = player.getMainHandItem();
        if (!CMEItems.MECHANICAL_TOOL.isIn(item)) {
            item = player.getOffhandItem();
            equipmentSlot = EquipmentSlot.OFFHAND;
        }
        if (!CMEItems.MECHANICAL_TOOL.isIn(item))
            return;

        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartEntry();
            var data = part.get().data;
            var absorbed = data.tryAbsorbDamage(player, item, equipmentSlot, BLOCK_BREAK_DURABILITY_USE);
            if (absorbed)
                return;
        }
        item.hurtAndBreak(BLOCK_BREAK_DURABILITY_USE, player, equipmentSlot);
    }


    @SubscribeEvent
    protected static void notifyPartsOfBlockBreak(BlockEvent.BreakEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        var item = player.getMainHandItem();
        if (!CMEItems.MECHANICAL_TOOL.isIn(item))
            return;

        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPartEntry().get().data.brokeBlock(player, item, event);
        }
    }

    @SubscribeEvent
    public static void modifyBlockDropsAfterBreak(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer player) || !player.getMainHandItem().has(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE))
            return;
        var item = player.getMainHandItem();

        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            part.data.blockDropEvent(slot.slot(), player, item, event);
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            ItemStack stack, @NotNull Player player,
            @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartEntry();
            var result = part.get().data.interactLivingEntity(stack, player, interactionTarget, usedHand);
            if (result != InteractionResult.PASS)
                return result;
        }
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, @NotNull Player player) {
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        var result = true;
        for (var slot : slots) {
            var part = slot.getPartEntry();
            result = result && part.get().data.onDroppedByPlayer(item, player);
        }
        return result && super.onDroppedByPlayer(item, player);
    }

    @Override
    public @NotNull Component getHighlightTip(@NotNull ItemStack item, @NotNull Component displayName) {
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        var result = super.getHighlightTip(item, displayName);
        for (var slot : slots) {
            var part = slot.getPartEntry();
            result = part.get().data.getHighlightTip(item, result);
        }
        return result;
    }

    // -- Parts --
    protected void setLastToolHolder(ItemStack stack, Entity entity, boolean isSelected) {
        if (!(entity instanceof Player player) || !isSelected)
            return;
        // TODO: use ID
        var name = player.getName().getString();
        stack.set(CMEDataComponents.LAST_TOOL_HOLDER_NAME, name);
    }
    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        setLastToolHolder(stack, entity, isSelected);

        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPartEntry().get().data.inventoryTick(
                    stack, level, entity, slotId, isSelected
            );
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var item = context.getItemInHand();
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartEntry();
            var result = part.get().data.useOn(context);
            if (result != InteractionResult.PASS)
                return result;
        }
        return super.useOn(context);
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        if (getUseDuration(item, player) > 0) {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.success(item);
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        var duration = 0;
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartEntry();
            duration = Math.max(duration, part.get().data.getUseDuration(stack, entity));
        }
        return duration;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartEntry();
            var data = part.get().data;
            var duration = data.getUseDuration(stack, livingEntity);
            if(duration == 0)
                continue;
            data.onUseTick(level, livingEntity, stack, duration);
        }
    }

    // Bar
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var data = slot.getPartEntry().get().data;
            if(!data.overridesBar(stack))
                continue;
            return data.getBarWidth(stack);
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var data = slot.getPartEntry().get().data;
            if(!data.overridesBar(stack))
                continue;
            return data.getBarColor(stack);
        }
        return super.getBarColor(stack);
    }
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    // -- Other Functions --
    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (canAbsorbDurability(stack, state))
            return super.getDestroySpeed(stack, state);
        return 0.00001f;
    }

    protected static boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull BlockState state) {
        return canAbsorbDurability(stack, BLOCK_BREAK_DURABILITY_USE, state, null, null);
    }
    protected static boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull LivingEntity attacker, @NotNull Entity target) {
        return canAbsorbDurability(stack, ENTITY_ATTACK_DURABILITY_USE, null, attacker, target);
    }

    protected static boolean canAbsorbDurability(@NotNull ItemStack stack, int amount, @org.jetbrains.annotations.Nullable BlockState state,
                                                 @org.jetbrains.annotations.Nullable LivingEntity attacker, @org.jetbrains.annotations.Nullable Entity target) {
        if (stack.getDamageValue() < stack.getMaxDamage() - amount)
            return true;

        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartEntry();
            boolean result = false;
            if (state != null)
                result = part.get().data.canAbsorbDurability(stack, state, amount);
            else if (attacker != null && target != null)
                result = part.get().data.canAbsorbDurability(stack, attacker, target, amount);
            if (result)
                return true;
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity entity) {
        if (canAbsorbDurability(stack, player, entity))
            return super.onLeftClickEntity(stack, player, entity);
        return true;
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack item = event.getItemStack();
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            part.data.leftClickBlock(slot.slot(), event.getEntity(), item, event);
        }
    }

    @SubscribeEvent
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack item = event.getItemStack();
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            part.data.leftClickEmpty(slot.slot(), event.getEntity(), item, event);
        }
    }

    // We set priority to highest just so we catch this before anyone does anything else
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClickEntity(AttackEntityEvent event) {
        Player attacker = event.getEntity();
        if (!(event.getTarget() instanceof LivingEntity target))
            return;
        ItemStack item = attacker.getItemInHand(InteractionHand.MAIN_HAND);

        boolean cancel = !canAbsorbDurability(item, target, attacker);

        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            part.data.leftClickEntity(slot.slot(), attacker, item, target, event, cancel);
        }
        if (cancel)
            event.setCanceled(true);
    }

    // DIGGER ITEM
    // TODO: if it doesn't have durability, it takes damage, but if not still does damage but then takes no damage

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return canAbsorbDurability(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPart();
            if (part.data.postHurtEnemy(slot.slot(), stack, attacker, target))
                return;
        }
        stack.hurtAndBreak(ENTITY_ATTACK_DURABILITY_USE, attacker, EquipmentSlot.MAINHAND);
    }
}
