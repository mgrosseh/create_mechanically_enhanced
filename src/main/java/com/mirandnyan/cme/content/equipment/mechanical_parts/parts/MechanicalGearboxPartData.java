package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import net.minecraft.world.item.ItemStack;

public class MechanicalGearboxPartData extends MechanicalPartData {
    public final int airTransferRatio;
    public MechanicalGearboxPartData(int transferRatio) {
        super(0.15f);
        this.airTransferRatio = transferRatio;
    }

    @Override
    public void onInserted(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        tool.set(CMEDataComponents.AIR_TRANSFER_RATIO, airTransferRatio);
    }

    @Override
    public void onRemoved(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        tool.remove(CMEDataComponents.AIR_TRANSFER_RATIO);
    }
}
