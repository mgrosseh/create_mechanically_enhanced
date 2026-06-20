package com.mirandnyan.cme.content.equipment.mechanical_tool;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMEItems;
import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.MechanicalItem;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalToolSlot;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber
public class MechanicalToolItem extends MechanicalItem implements CustomArmPoseItem {

    public static final int DEFAULT_TRANSFER_RATIO = 2;
    public static final int INTERNAL_AIR_COLOR = 0x9090F0;
    public static final int BLOCK_BREAK_DURABILITY_USE = 1;
    public static final int ENTITY_ATTACK_DURABILITY_USE = 2;

    // TODO: pose is weird (crossing) when in offhand: extendo grip, crossbow, other mechanical tool
    // TODO: enchants: unbreaking (for when no air)
    // TODO: no anvil
    // TODO: has slot: tag + if part has slot then this has that slot
    // TODO: parts: incompatible with tag
    // TODO: add right click (as opposed to right hold = refill) as an action parts can access
    // TODO: right / left click on drill in inventory
    // TODO: sound when air empty

    // TODO: head: explosion generator


    /*
    TODO: reworking Slots / Parts / FilledSlots etc
    Tool has a slot for Grip:
    Tag defines two slots on grip: cog + gearbox
    Each slot stores position offset on tool

    In render we find grip, render; then find attached parts, offset to their slots offset, render;
    then find their attached parts etc

    On trying to rightClick, for each part try inserting a valid part onto it recursively, the tool facilitates figuring
    out if it is a MechanicalPart and then calls tryInsertingTool on gripPart.data.
    It sees if one of its slots fits the Part, if not for each part it calls part.data.tryInsertingTool.

    it may still be best to store all parts linearly and use a map for structure of where it is
     */

    @SafeVarargs
    public static ItemStack newStackWithParts(RegistryEntry<MechanicalPart, MechanicalPart>... parts) {
        var stack = CMEItems.MECHANICAL_TOOL.asStack();

        for (var part : parts) {
            var slot = new FilledToolSlot(part.get().getOriginSlot().getKey(), part.getKey(), Optional.empty());
            insertFilledToolSlot(stack, slot);
        }
        recalculateTotalWeight(stack);
        var capacity = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        stack.set(CMEDataComponents.PRESSURIZED_AIR, capacity);
        return stack;
    }

    public MechanicalToolItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }


    @Override
    public boolean alwaysFit(ResourceKey<MechanicalToolSlot> slot) {
        return MechanicalToolSlot.GRIP_SLOT.getKey().equals(slot);
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        ItemStack stack = player.getMainHandItem(); // TODO offhand too with boolean

        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPartRegistry().get().data.playerTick(
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
            var part = slot.getPartRegistry();
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
            slot.getPartRegistry().get().data.brokeBlock(player, item, event);
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var item = context.getItemInHand();
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartRegistry();
            var result = part.get().data.useOn(context);
            if (result != InteractionResult.PASS)
                return result;
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            ItemStack stack, @NotNull Player player,
            @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartRegistry();
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
            var part = slot.getPartRegistry();
            result = result && part.get().data.onDroppedByPlayer(item, player);
        }
        return result && super.onDroppedByPlayer(item, player);
    }

    @Override
    public @NotNull Component getHighlightTip(@NotNull ItemStack item, @NotNull Component displayName) {
        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        var result = super.getHighlightTip(item, displayName);
        for (var slot : slots) {
            var part = slot.getPartRegistry();
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
            slot.getPartRegistry().get().data.inventoryTick(
                    stack, level, entity, slotId, isSelected
            );
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // Change parts
    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                            @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (tryMechanicalPartsHandlingStackOnMe(stack, other, slot, action, player, access))
            return true;

        if (other.isEmpty())
            return false;
        if (!(action == ClickAction.SECONDARY && slot.allowModification(player)))
            return false;

        return tryAddingMechanicalPart(stack, other, player, access);
    }

    protected boolean tryMechanicalPartsHandlingStackOnMe(@NotNull ItemStack stack, @NotNull ItemStack other,
                                                          @NotNull Slot slot, @NotNull ClickAction action,
                                                          @NotNull Player player, @NotNull SlotAccess access) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var tool_slot : slots) {
            var part = tool_slot.getPartRegistry();
            var handled = part.get().data.tryHandlingStackedOnMe(stack, other, slot, action, player, access);
            if (handled)
                return true;
        }

        return false;
    }

    protected boolean tryAddingMechanicalPart(@NotNull ItemStack stack, @NotNull ItemStack other,
                                              @NotNull Player player, @NotNull SlotAccess access) {
        if (!player.isCreative())
            return false;
        if (stack.getCount() != 1)
            return false;

        var maybe_part = MechanicalPart.getOfItem(other.getItem());
        if (maybe_part.isEmpty())
            return false;
        var part = maybe_part.get();
        var partSlot = part.get().getOriginSlot().getKey();

        var filledSlot = new FilledToolSlot(partSlot, part.getKey(), Optional.empty());

        var old = insertFilledToolSlot(stack, filledSlot);
        if (old == filledSlot)
            return false;
        recalculateTotalWeight(stack);
        other.shrink(1);
        if (old == null)
            return true;
        var item = old.getPartRegistry().get().getItem().get().getDefaultInstance();
        access.set(item);

        return true;
    }



    // -- Visuals --
    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new MechanicalToolRenderer()));
    }

    // prevent bobbing after mine
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.NONE;
    }

    // disable swing animation
    @Override
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        return true;
    }

    @Override
    @SuppressWarnings("removal")
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return true;
    }

    // make look nice in third person
    @Override
    @Nullable
    public HumanoidModel.ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        return HumanoidModel.ArmPose.CROSSBOW_HOLD;
    }

    // -- Tooltips --
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        tooltip.add(Component.empty());

        if (slots.isEmpty())
            tooltip.add(CMETranslations.TOOL_SLOTS_NONE.resolveComponent());
        else
            tooltip.add(CMETranslations.TOOL_SLOTS_TITLE.resolveComponent());
        for (FilledToolSlot slot : slots) {
            slot.appendHoverText(stack, context, tooltip, flagIn);
        }

        for (FilledToolSlot slot : slots) {
            var part = slot.getPartRegistry();
            part.get().data.appendHoverText(stack, context, tooltip, flagIn);
        }
        tooltip.add(Component.empty());
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    // Refilling
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
            var part = slot.getPartRegistry();
            duration = Math.max(duration, part.get().data.getUseDuration(stack, entity));
        }
        return duration;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartRegistry();
            var data = part.get().data;
            var duration = data.getUseDuration(stack, livingEntity);
            if(duration == 0)
                continue;
            data.onUseTick(level, livingEntity, stack, duration);
        }
    }

    // Bar
    @Override
    public int getBarWidth(ItemStack stack) {
        var maxAir = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        var air = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        if (maxAir > 0 && air > 0)
            return Math.round((float) air * MAX_BAR_WIDTH / (float) maxAir);
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        var air = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        if (air > 0)
            return INTERNAL_AIR_COLOR;
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

    protected boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull BlockState state) {
        return canAbsorbDurability(stack, BLOCK_BREAK_DURABILITY_USE, state, null, null);
    }
    protected boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull LivingEntity attacker, @NotNull Entity target) {
        return canAbsorbDurability(stack, ENTITY_ATTACK_DURABILITY_USE, null, attacker, target);
    }

    protected static boolean canAbsorbDurability(@NotNull ItemStack stack, int amount, @Nullable BlockState state,
                                                 @Nullable LivingEntity attacker, @Nullable Entity target) {
        if (stack.getDamageValue() < stack.getMaxDamage() - amount)
            return true;

        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPartRegistry();
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

    // DIGGER ITEM
    // TODO: if it doesn't have durability, it takes damage, but if not still does damage but then takes no damage

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return canAbsorbDurability(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(ENTITY_ATTACK_DURABILITY_USE, attacker, EquipmentSlot.MAINHAND);
    }
}
