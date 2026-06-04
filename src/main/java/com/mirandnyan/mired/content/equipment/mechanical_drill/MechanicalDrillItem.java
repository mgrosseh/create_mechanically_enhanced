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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber
public class MechanicalDrillItem extends MechanicalTool implements CustomArmPoseItem {

    public static final int DEFAULT_DURABILITY = 600;
    public static final int DEFAULT_TRANSFER_RATIO = 2;
    public static final int INTERNAL_AIR_COLOR = 0x9090F0;

    // TODO: no enchant, no anvil
    // TODO: has slot: tag + if part has slot then this has that slot
    // TODO: parts: incompatible with tag
    // TODO: add right click (as opposed to right hold = refill) as an action parts can access
    // TODO: right / left click on drill in inventory

    // TODO: head: explosion generator, saw

    public static ItemStack defaultItemStack() {
        var stack = CMEItems.MECHANICAL_DRILL.asStack();
        RegistryEntry<MechanicalPart, MechanicalPart>[] defaultParts = new RegistryEntry[]{
                MechanicalPart.DEFAULT_GRIP,
                MechanicalPart.WOODEN_COG,
                MechanicalPart.ANDESITE_GEARBOX,
                MechanicalPart.COPPER_TANK,
                MechanicalPart.IRON_DRILL_HEAD,
        };
        for (var part : defaultParts) {
            var slot = new FilledToolSlot(part.get().validSlot, part.getKey());
            insertFilledToolSlot(stack, slot);
        }
        var capacity = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        stack.set(CMEDataComponents.PRESSURIZED_AIR, capacity);
        return stack;
    }

    public MechanicalDrillItem(Properties properties) {
        super(properties
                .rarity(Rarity.UNCOMMON)
                .durability(DEFAULT_DURABILITY)
                .attributes(createAttributes())
        );
    }
    public static ItemAttributeModifiers createAttributes() {
        float attackDamage = 1.0f;
        float attackSpeed = -2.8f;
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (attackDamage + Tiers.DIAMOND.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
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
    }

    protected static void findAndDamageItem(Player player) {
        if (player == null)
            return;
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


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            slot.getPart().ifPresent(p -> p.get().data.inventoryTick(
                    stack, level, entity, slotId, isSelected
            ));
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // Change parts
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
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    // -- Cog --
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        int multiplier = Math.max(100 + stack.getOrDefault(CMEDataComponents.SPEED_MODIFIER, 0), 0);
        return super.getDestroySpeed(stack, state) * (multiplier / 100f);
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
    // TODO: false if durability 1
    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player) {
        return super.canAttackBlock(state, worldIn, pos, player);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return super.isCorrectToolForDrops(stack, state);
    }

    // DIGGER ITEM

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
    }
}
