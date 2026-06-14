package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.content.equipment.mechanical_tool.MechanicalToolItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MechanicalTankPartData extends MechanicalPartData {
    int capacity;
    public MechanicalTankPartData(int capacity) {
        super(0.4f);
        this.capacity = capacity;
    }

    @Override
    public void onInserted(ItemStack tool) {
        var air = tool.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        var maxAir = tool.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        var newMaxAir = maxAir + this.capacity;
        tool.set(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, newMaxAir);
        if (air > newMaxAir)
            tool.set(CMEDataComponents.PRESSURIZED_AIR, newMaxAir);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        var air = tool.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        var maxAir = tool.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        var newMaxAir = Math.max(maxAir - this.capacity, 0);
        tool.set(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, newMaxAir);
        if (air > newMaxAir)
            tool.set(CMEDataComponents.PRESSURIZED_AIR, newMaxAir);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;

        // TODO: rightClickAction on Backtank block should fill tank
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return Math.max(0,
                stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0)
                        - stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0));
    }


    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int duration) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(livingEntity);

        var transferRate = duration / 100 + 5;
        var maxTransfer = Math.min(transferRate, duration);

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
        stack.set(CMEDataComponents.PRESSURIZED_AIR, stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0) + transferred);
    }

    @Override
    public boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull BlockState state, int amount) {
        return stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0) >= amount;
    }

    @Override
    public boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull LivingEntity attacker, @NotNull Entity target, int amount) {
        return stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0) >= amount;
    }

    @Override
    public boolean tryAbsorbDamage(@NotNull Player player, @NotNull ItemStack item, EquipmentSlot equipmentSlot, int amount) {
        var air = item.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        if (air >= amount) {
            item.set(CMEDataComponents.PRESSURIZED_AIR,
                    air - item.getOrDefault(CMEDataComponents.AIR_TRANSFER_RATIO, MechanicalToolItem.DEFAULT_TRANSFER_RATIO));
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        int maxAir = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        if (maxAir == 0)
            tooltip.add(CMETranslations.MECHANICAL_TOOL_NO_AIR.resolveComponent());
        else {
            tooltip.add(Component.empty().append(CMETranslations.MECHANICAL_TOOL_AIR_LEVEL_PRE.resolveComponent())
                    .append(CMETranslations.Components.number(stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0)))
                    .append(CMETranslations.MECHANICAL_TOOL_AIR_LEVEL_IN.resolveComponent())
                    .append(CMETranslations.Components.number(maxAir))
                    .append(CMETranslations.MECHANICAL_TOOL_AIR_LEVEL_POST.resolveComponent())
            );
        }
    }
}
