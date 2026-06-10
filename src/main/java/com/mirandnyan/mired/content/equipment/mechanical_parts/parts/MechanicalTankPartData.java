package com.mirandnyan.mired.content.equipment.mechanical_parts.parts;

import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_parts.MechanicalPartData;
import net.minecraft.world.item.ItemStack;

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
}
