package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tank;

import com.mirandnyan.cme.CMEDataComponents;
import com.simibubi.create.AllSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CardboardTankPartData extends MechanicalTankPartData {

    public CardboardTankPartData(int capacity) {
        super(0.1f, capacity);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (Mth.randomBetweenInclusive(level.getRandom(), 0, 200) > 1)
            return;

        int air = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        if (air > 0) {
            stack.set(CMEDataComponents.PRESSURIZED_AIR, air - 1);
            level.playSound(null, entity.getX(), entity.getY() + 1f, entity.getZ(),
                    AllSoundEvents.STEAM.getMainEvent(), SoundSource.PLAYERS, 0.28f, 1.5f);
        }
    }
}
