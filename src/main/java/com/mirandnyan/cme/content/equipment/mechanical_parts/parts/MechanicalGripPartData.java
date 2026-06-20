package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class MechanicalGripPartData extends MechanicalPartData {

    private final int maxDamage;

    public MechanicalGripPartData(int maxDamage) {
        this(maxDamage, 0.5f);
    }
    public MechanicalGripPartData(int maxDamage, float weight) {
        super(weight);
        this.maxDamage = maxDamage;
    }

    @Override
    public void onInserted(ItemStack tool) {
        tool.set(DataComponents.MAX_DAMAGE, maxDamage);
        tool.set(DataComponents.DAMAGE, 0);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        tool.remove(DataComponents.MAX_DAMAGE);
        tool.remove(DataComponents.DAMAGE);
    }
}
