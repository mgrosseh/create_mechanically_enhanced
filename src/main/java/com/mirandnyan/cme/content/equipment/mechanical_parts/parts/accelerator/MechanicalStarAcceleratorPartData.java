package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;

@EventBusSubscriber
public class MechanicalStarAcceleratorPartData extends MechanicalAcceleratorPartData {
    public MechanicalStarAcceleratorPartData(int speedModifier) {
        super(-0.8f, speedModifier);
    }

    @Override
    public void onInserted(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        super.onInserted(replaceSlot, tool);
        tool.set(CMEDataComponents.EXPLOSION_IMMUNE, Unit.INSTANCE);
    }

    @Override
    public void onRemoved(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        super.onRemoved(replaceSlot, tool);
        tool.remove(CMEDataComponents.EXPLOSION_IMMUNE);
    }

    @SubscribeEvent
    public static void preventExplosionDamage(EntityInvulnerabilityCheckEvent event) {
        if(!(event.getEntity() instanceof ItemEntity entity))
            return;
        if (entity.getItem().has(CMEDataComponents.EXPLOSION_IMMUNE) && event.getSource().is(DamageTypeTags.IS_EXPLOSION))
            event.setInvulnerable(true);
    }
}
