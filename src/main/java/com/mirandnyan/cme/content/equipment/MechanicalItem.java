package com.mirandnyan.cme.content.equipment;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalToolSlot;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class MechanicalItem extends Item {

    public MechanicalItem(Properties properties) {
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

    public abstract boolean alwaysFit(ResourceKey<MechanicalToolSlot> slot);

    /**
     *
     * @param stack the item to insert the slot into, must have CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE
     * @param newSlot the slot to insert
     * @return newSlot if cannot fit in stack, the previous FilledToolSlot if it existed, null if it fit and didn't replace
     */
    protected static @Nullable FilledToolSlot insertFilledToolSlot(ItemStack stack, FilledToolSlot newSlot) {
        List<FilledToolSlot> slots = stack.get(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE);
        ArrayList<FilledToolSlot> newSlots = slots == null ? new ArrayList<>() : new ArrayList<>(slots);

        FilledToolSlot removed = null;
        Optional<ResourceKey<MechanicalPart>> parent = Optional.empty();
        for (int i = 0; i < newSlots.size(); i++) {
            var slot = newSlots.get(i);
            if (slot.isSlot(newSlot)) {
                removed = newSlots.remove(i);
                removed.getPartRegistry().get().data.onRemoved(stack);
                break;
            }
            if (slot.has(newSlot)) {
                parent = Optional.of(slot.part());
            }
        }
        if (removed != null) {
            parent = removed.parent();
            removed = new FilledToolSlot(removed.slot(), removed.part(), Optional.empty());
        }
        if (parent.isEmpty() && (!(stack.getItem() instanceof MechanicalItem item) || !item.alwaysFit(newSlot.slot()))) {
            return newSlot;
        }

        newSlot.getPartRegistry().get().data.onInserted(stack);
        newSlots.add(new FilledToolSlot(newSlot.slot(), newSlot.part(), parent));
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(newSlots));
        return removed;
    }

    protected static void recalculateTotalWeight(ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        float weight = 0;
        for (FilledToolSlot slot : slots) {
            var part = slot.getPartRegistry();
            weight += part.get().data.weight;
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
