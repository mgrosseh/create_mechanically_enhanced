package com.mirandnyan.mired.content.equipment.mechanical_drill;

import com.mirandnyan.mired.CVADataComponents;
import com.mirandnyan.mired.CVAItems;
import com.mirandnyan.mired.CVATags;
import com.mirandnyan.mired.CVATranslations;
import com.mirandnyan.mired.content.equipment.MechanicalTool;
import com.mirandnyan.mired.content.equipment.mechanical_mods.FilledToolSlot;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalToolSlot;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class MechanicalDrill extends MechanicalTool {

    public static final int DEFAULT_TRANSFER_RATIO = 2;
    public static final int INTERNAL_AIR_COLOR = 0x9090F0;
    // TODO: no enchant, no anvil

    public MechanicalDrill(Properties properties) {
        super(properties
                .rarity(Rarity.UNCOMMON)
                .durability(DEFAULT_TRANSFER_RATIO) // TODO separate from backtank
                .attributes(createAttributes())
                .component(DataComponents.TOOL, createToolProperties())
        );
    }


    protected static Tool createToolProperties() {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(CVATags.Blocks.INCORRECT_FOR_MECHANICAL_DRILL),
                Tool.Rule.minesAndDrops(CVATags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.DIAMOND.getSpeed())),
                1.0F,
                0
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
        if (!CVAItems.MECHANICAL_DRILL.isIn(item)) {
            item = player.getOffhandItem();
            equipmentSlot = EquipmentSlot.OFFHAND;
        }
        if (!CVAItems.MECHANICAL_DRILL.isIn(item))
            return;
        useAirOrHurtAndBreak(player, equipmentSlot, item);
    }

    // TODO: simplify function count
    protected static void useAirOrHurtAndBreak(Player player, EquipmentSlot slot, ItemStack stack) {
        if (!absorbDamage(player, stack))
            stack.hurtAndBreak(1, player, slot);
    }
    protected static boolean absorbDamage(Player player, ItemStack stack) {
        if (hasAir(stack)) {
            drainInternalTank(stack, airTransferRatio(stack));
            return true;
        }
        return false;
    }

    // -- TODO: temp exchange parts --
    public boolean overrideOtherStackedOnMe(ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                            @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (stack.getCount() != 1)
            return false;
        if (other.isEmpty())
            return false;
        if (!(action == ClickAction.SECONDARY && slot.allowModification(player)))
            return false;

        // TODO
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

    // -- Tooltips --
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        List<FilledToolSlot> slots = stack.getOrDefault(CVADataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        tooltip.add(Component.empty());

        if (slots.isEmpty())
            tooltip.add(CVATranslations.TOOL_SLOTS_NONE.resolveComponent());
        else
            tooltip.add(CVATranslations.TOOL_SLOTS_TITLE.resolveComponent());
        for (FilledToolSlot slot : slots) {
            slot.appendHoverText(stack, context, tooltip, flagIn);
        }

        var maxAir = getMaxAir(stack);
        if (maxAir == 0)
            tooltip.add(CVATranslations.MECHANICAL_TOOL_NO_AIR.resolveComponent());
        else {
            tooltip.add(Component.empty().append(CVATranslations.MECHANICAL_TOOL_AIR_LEVEL_PRE.resolveComponent())
                    .append(CVATranslations.Components.number(getAir(stack)))
                    .append(CVATranslations.MECHANICAL_TOOL_AIR_LEVEL_IN.resolveComponent())
                    .append(CVATranslations.Components.number(maxAir))
                    .append(CVATranslations.MECHANICAL_TOOL_AIR_LEVEL_POST.resolveComponent())
            );
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    // -- Air Storage --
    public static int getMaxAir(ItemStack stack) {
        return stack.getOrDefault(CVADataComponents.PRESSURIZED_AIR_CAPACITY, 0);
    }
    public static int getAir(ItemStack stack) {
        return stack.getOrDefault(CVADataComponents.PRESSURIZED_AIR, 0);
    }
    public static void setAir(ItemStack stack, int air) {
        stack.set(CVADataComponents.PRESSURIZED_AIR, air);
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
    public int getBarColor(ItemStack stack) {
        return INTERNAL_AIR_COLOR;
    }
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    // -- Other Functions --
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Tool tool = stack.get(DataComponents.TOOL);

        return tool != null ? tool.getMiningSpeed(state) : 1.0F;
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
