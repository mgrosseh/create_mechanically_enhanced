package com.mirandnyan.mired.content.equipment.mechanical_drill;

import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.CMEItems;
import com.mirandnyan.mired.CMETranslations;
import com.mirandnyan.mired.content.equipment.MechanicalTool;
import com.mirandnyan.mired.content.equipment.mechanical_mods.FilledToolSlot;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalToolSlot;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber
public class MechanicalDrillItem extends MechanicalTool implements CustomArmPoseItem {

    public static final int DEFAULT_TRANSFER_RATIO = 2;
    public static final int INTERNAL_AIR_COLOR = 0x9090F0;

    // TODO: insta mining Deepslate Requires mining_efficiency of ~80 or more, make possible
    // TODO: more attributes
    // TODO: pose is weird (crossing) when in offhand: extendo grip, crossbow, other mechanical tool
    // TODO: enchants: unbreaking (for when no air)
    // TODO: no anvil
    // TODO: has slot: tag + if part has slot then this has that slot
    // TODO: parts: incompatible with tag
    // TODO: add right click (as opposed to right hold = refill) as an action parts can access
    // TODO: right / left click on drill in inventory

    // TODO: head: explosion generator, saw


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

    public static ItemStack defaultItemStack() {
        return newStackWithParts(
                MechanicalPart.DEFAULT_GRIP,
                MechanicalPart.WOODEN_COG,
                MechanicalPart.ANDESITE_GEARBOX,
                MechanicalPart.COPPER_TANK,
                MechanicalPart.IRON_DRILL_HEAD
        );
    }

    @SafeVarargs
    public static ItemStack newStackWithParts(RegistryEntry<MechanicalPart, MechanicalPart>... parts) {
        var stack = CMEItems.MECHANICAL_DRILL.asStack();

        for (var part : parts) {
            var slot = new FilledToolSlot(part.get().validSlot, part.getKey());
            insertFilledToolSlot(stack, slot);
        }
        recalculateTotalWeight(stack);
        var capacity = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        stack.set(CMEDataComponents.PRESSURIZED_AIR, capacity);
        return stack;
    }

    public MechanicalDrillItem(Properties properties) {
        super(properties
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1)
        );
    }

    // -- Breaking Blocks with Item --

    // TODO:
    protected static int airTransferRatio(ItemStack stack) {
        var slot = getToolSlot(stack, MechanicalToolSlot.GEARBOX_SLOT);
        var maybe_part = FilledToolSlot.getPartOf(slot);
        if (maybe_part.isEmpty())
            return DEFAULT_TRANSFER_RATIO;
        var part = maybe_part.get();
        return part.get().data.getTransferRatio();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void consumeDurabilityOnBlockBreak(BlockEvent.BreakEvent event) {
        findAndDamageItem(event.getPlayer());
        notifyPartsOfBlockBreak(event);
    }

    /* always server side */
    protected static void notifyPartsOfBlockBreak(BlockEvent.BreakEvent event) {
        var player = event.getPlayer();
        var item = player.getMainHandItem();
        if (!CMEItems.MECHANICAL_DRILL.isIn(item))
            return;

        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPart().ifPresent(p -> p.get().data.brokeBlock(player, item, event));
        }
    }

    protected static void findAndDamageItem(@NotNull Player player) {
        if (player.level().isClientSide)
            return;
        EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
        ItemStack item = player.getMainHandItem();
        if (!CMEItems.MECHANICAL_DRILL.isIn(item)) {
            item = player.getOffhandItem();
            equipmentSlot = EquipmentSlot.OFFHAND;
        }
        if (!CMEItems.MECHANICAL_DRILL.isIn(item))
            return;
        useAirOrHurtAndBreak(player, equipmentSlot, item);
    }

    protected static void useAirOrHurtAndBreak(Player player, EquipmentSlot slot, ItemStack stack) {
            if (hasAir(stack)) {
                drainInternalTank(stack, airTransferRatio(stack));
                return;
            }
        stack.hurtAndBreak(1, player, slot);
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        setLastToolHolder(stack, entity, isSelected);

        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPart().ifPresent(p -> p.get().data.inventoryTick(
                    stack, level, entity, slotId, isSelected
            ));
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // Change parts
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
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
            var part = tool_slot.getPart();
            if (part.isEmpty())
                continue;
            var handled = part.get().get().data.tryHandlingStackedOnMe(stack, other, slot, action, player, access);
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
        var partSlot = part.get().getSlot().getKey();

        var filledSlot = new FilledToolSlot(partSlot, part.getKey());

        var old = insertFilledToolSlot(stack, filledSlot);
        recalculateTotalWeight(stack);
        other.shrink(1);
        if (old == null || old.part() == null)
            return true;
        var item = MechanicalPart.get(old.part()).get().getItem().get().getDefaultInstance();
        access.set(item);

        return true;
    }



    // -- Visuals --
    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new MechanicalDrillRenderer()));
    }

    // prevent bobbing after mine
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.NONE;
    }

    // disable swing animation
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
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

        var maxAir = getMaxAir(stack);
        if (maxAir == 0)
            tooltip.add(CMETranslations.MECHANICAL_TOOL_NO_AIR.resolveComponent());
        else {
            tooltip.add(Component.empty().append(CMETranslations.MECHANICAL_TOOL_AIR_LEVEL_PRE.resolveComponent())
                    .append(CMETranslations.Components.number(getAir(stack)))
                    .append(CMETranslations.MECHANICAL_TOOL_AIR_LEVEL_IN.resolveComponent())
                    .append(CMETranslations.Components.number(maxAir))
                    .append(CMETranslations.MECHANICAL_TOOL_AIR_LEVEL_POST.resolveComponent())
            );
        }

        tooltip.add(Component.empty());
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            if (part.isEmpty())
                continue;
            part.get().get().data.appendHoverText(stack, context, tooltip, flagIn);
        }
        tooltip.add(Component.empty());
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    // -- Air Storage --
    public static int getMaxAir(ItemStack stack) {
        return stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
    }
    public static int getAir(ItemStack stack) {
        return stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
    }
    public static void setAir(ItemStack stack, int air) {
        stack.set(CMEDataComponents.PRESSURIZED_AIR, air);
    }
    public static void drainInternalTank(ItemStack stack, int amount) {
        setAir(stack, getAir(stack) - amount);
    }
    public static void fillInternalTank(ItemStack stack, int amount) {
        setAir(stack, getAir(stack) + amount);
    }
    public static boolean hasAir(ItemStack stack) {
        return getAir(stack) != 0;
    }

    // Refilling
    // TODO: rightClickAction on Backtank block should fill tank
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        if (getUseDuration(item, player) > 0) {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.success(item);
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return getMaxAir(stack) - getAir(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(livingEntity);

        var remaining = getUseDuration(stack, livingEntity);
        var transferRate = remaining / 100 + 5;
        var maxTransfer = Math.min(transferRate, remaining);

        var transferred = 0;
        if (livingEntity instanceof Player player && player.isCreative())
            transferred = maxTransfer;
        else {
            if (backtanks.isEmpty())
                return;

            for (var tank : backtanks) {
                var canTransfer = Math.min(maxTransfer - transferred, BacktankUtil.getAir(tank));
                transferred += canTransfer;
                BacktankUtil.consumeAir(livingEntity, tank, canTransfer);
            }
        }
        fillInternalTank(stack, transferred);
    }

    // Bar
    @Override
    public int getBarWidth(ItemStack stack) {
        var maxAir = getMaxAir(stack);
        var air = getAir(stack);
        if (maxAir > 0 && air > 0)
            return Math.round((float) air * 13.0F / (float) maxAir);
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        if (getAir(stack) > 0)
            return INTERNAL_AIR_COLOR;
        return super.getBarColor(stack);
    }
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    // -- Other Functions --
    //
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        var air = getAir(stack);
        if (air > 0 || stack.getDamageValue() < stack.getMaxDamage() - 1)
            return super.getDestroySpeed(stack, state);
        return 0.00001f;
    }

    // DIGGER ITEM
    // TODO: if it doesn't have durability, it takes damage, but if not still does damage but then takes no damage

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        var air = getAir(stack);
        return air > 0 || stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
    }
}
