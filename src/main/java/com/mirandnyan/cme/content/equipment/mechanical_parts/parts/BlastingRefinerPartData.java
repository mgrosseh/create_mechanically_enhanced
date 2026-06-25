package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMETags;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BlastingRefinerPartData extends MechanicalPartData {

    public BlastingRefinerPartData() {
        this(0.1f);
    }
    public BlastingRefinerPartData(float weight) {
        super(weight);
    }

    protected boolean isImmuneToBurnUp(ItemStack stack) {
        return stack.has(DataComponents.FIRE_RESISTANT) || stack.is(CMETags.Items.BURN_UP_IMMUNE);
    }
    protected boolean isLikelyToBurnUp(ItemStack stack) {
        return stack.getItemHolder().getData(NeoForgeDataMaps.FURNACE_FUELS) != null || stack.is(CMETags.Items.BURN_UP_HIGH_LIKELIHOOD);

    }

    @Override
    public void blockDropEvent(FilledToolSlot.SlotId slot, ServerPlayer player, ItemStack item, BlockDropsEvent event) {
        ArrayList<ItemEntity> toAdd = new ArrayList<>();
        var level = event.getLevel();
        for (var drop : event.getDrops()) {
            var stack = drop.getItem();
            //noinspection DataFlowIssue // Is never null for BLASTING
            var result = AllFanProcessingTypes.BLASTING.process(stack, level).stream()
                    .map(s ->
                            new ItemEntity(level, drop.position().x, drop.position().y, drop.position().z, s,
                                    Mth.randomBetween(level.getRandom(), -0.05f, 0.05f),
                                    0.1, Mth.randomBetween(level.getRandom(), -0.05f, 0.05f)))
                    .collect(Collectors.toSet());
            toAdd.addAll(result);
            if (!result.isEmpty()) {
                drop.setItem(ItemStack.EMPTY);
                continue;
            }
            if (isImmuneToBurnUp(stack))
                continue;

            var chance = 5;
            if(isLikelyToBurnUp(stack))
                chance = 60;
            if (Mth.randomBetweenInclusive(event.getLevel().random, 0, 100) >= chance)
                continue;
            drop.setItem(ItemStack.EMPTY);
        }
        event.getDrops().addAll(toAdd);

        super.blockDropEvent(slot, player, item, event);
    }

    @Override
    public boolean postHurtEnemy(FilledToolSlot.@NotNull SlotId part, @NotNull ItemStack stack, @NotNull LivingEntity attacker, @NotNull LivingEntity target) {
        target.setRemainingFireTicks(120);
        return false;
    }
}
