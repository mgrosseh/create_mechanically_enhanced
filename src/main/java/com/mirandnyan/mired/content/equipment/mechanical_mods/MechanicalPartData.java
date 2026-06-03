package com.mirandnyan.mired.content.equipment.mechanical_mods;

import net.minecraft.world.item.ItemStack;

public abstract class MechanicalPartData {

    // TODO: make data component
    public int getTransferRatio() {
        return 0;
    }

    public void onInserted(ItemStack tool) { }
    public void onRemoved(ItemStack tool) { }
}
