package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin;

import com.mirandnyan.cme.CMEAttributes;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.MechanicalPartUtil;
import com.mirandnyan.cme.util.neoforge_helpers.ItemAttributeModifiersRebuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;

public class PumpkinAutomatonPartData extends MechanicalPartData {

    // TODO: on hit enemy: make them levitate shortly
    // TODO: maybe catch once every few mins when taking fall damage

    private final ItemAttributeModifiers.Entry pumpkinAttribute;

    public PumpkinAutomatonPartData() {
        super(0.3f);

        AttributeModifier acceleratorBoostModifier =
                new AttributeModifier(CreateMechanicallyEnhanced.asResource("pumpkin_coyote_time_boost"),
                        3, AttributeModifier.Operation.ADD_VALUE);
        pumpkinAttribute = new ItemAttributeModifiers.Entry(
                CMEAttributes.COYOTE_TIME_ATTRIBUTE,
                acceleratorBoostModifier,
                EquipmentSlotGroup.MAINHAND
        );
    }

    @Override
    public void onInserted(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        MechanicalPartUtil.addEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.SILK_TOUCH), 1);
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .adding(pumpkinAttribute)
                        .build()
        );
    }

    @Override
    public void onRemoved(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        MechanicalPartUtil.removeEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.SILK_TOUCH));
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .removing(pumpkinAttribute)
                        .build()
        );
    }
}
