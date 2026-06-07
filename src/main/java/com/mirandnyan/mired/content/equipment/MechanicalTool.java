package com.mirandnyan.mired.content.equipment;

import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.FilledToolSlot;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalToolSlot;
import com.mirandnyan.mired.util.ItemAttributeModifiersRebuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MechanicalTool extends Item {

    public MechanicalTool(Properties properties) {
        super(properties);
    }

    protected static @Nullable FilledToolSlot getToolSlot(ItemStack stack, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        List<FilledToolSlot> slots = stack.get(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE);
        if (slots == null)
            return null;
        for (FilledToolSlot toolSlot : slots) {
            if (toolSlot.isSlot(slot))
                return toolSlot;
        }
        return null;
    }
    protected static @Nullable FilledToolSlot insertFilledToolSlot(ItemStack stack, FilledToolSlot newSlot) {
        List<FilledToolSlot> slots = stack.get(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE);
        ArrayList<FilledToolSlot> newSlots = slots == null ? new ArrayList<>() : new ArrayList<>(slots);

        FilledToolSlot removed = null;
        for (int i = 0; i < newSlots.size(); i++) {
            var slot = newSlots.get(i);
            if (slot.isSlot(newSlot)) {
                removed = newSlots.remove(i);
                removed.getPart().ifPresent(part -> part.get().data.onRemoved(stack));
                break;
            }
        }
        newSlot.getPart().ifPresent(part -> part.get().data.onInserted(stack));
        newSlots.add(newSlot);
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(newSlots));
        return removed;
    }

    protected static void recalculateTotalWeight(ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        float weight = 0;
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            if (part.isEmpty())
                continue;
            weight += part.get().get().data.weight;
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(stack.getAttributeModifiers())
                .filter(e -> !e.attribute().equals(Attributes.ATTACK_SPEED)).add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -weight, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build()
        );
    }
}
