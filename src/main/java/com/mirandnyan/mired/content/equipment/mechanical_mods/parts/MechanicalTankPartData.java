package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.CVADataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import net.minecraft.world.item.ItemStack;

public class MechanicalTankPartData extends MechanicalPartData {
    int capacity;
    public MechanicalTankPartData(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void onInserted(ItemStack tool) {
        var air = tool.getOrDefault(CVADataComponents.PRESSURIZED_AIR, 0);
        var maxAir = tool.getOrDefault(CVADataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        var newMaxAir = maxAir + this.capacity;
        tool.set(CVADataComponents.PRESSURIZED_AIR_CAPACITY, newMaxAir);
        if (air > newMaxAir)
            tool.set(CVADataComponents.PRESSURIZED_AIR, newMaxAir);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        var air = tool.getOrDefault(CVADataComponents.PRESSURIZED_AIR, 0);
        var maxAir = tool.getOrDefault(CVADataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        var newMaxAir = Math.max(maxAir - this.capacity, 0);
        tool.set(CVADataComponents.PRESSURIZED_AIR_CAPACITY, newMaxAir);
        if (air > newMaxAir)
            tool.set(CVADataComponents.PRESSURIZED_AIR, newMaxAir);
    }
}
