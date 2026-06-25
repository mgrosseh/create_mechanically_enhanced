package com.mirandnyan.cme.content.equipment;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.SlotEntry;
import com.mirandnyan.cme.content.equipment.mechanical_tool.RemovingPartResult;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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

    protected static boolean canSupportAll(Map<@NonNull ResourceKey<MechanicalToolSlot>, @NotNull Long> slotTypeCounts,
                                           RegistryEntry<MechanicalPart, MechanicalPart> insertingPart) {
        return slotTypeCounts.entrySet().stream()
                .allMatch(e -> insertingPart.get().supportingSlots(e.getKey()).count() >= e.getValue());
    }

    protected static Optional<RegistryEntry<MechanicalPart, MechanicalPart>>
    tryReplacingMechanicalPart(@NotNull ItemStack stack, RegistryEntry<MechanicalPart, MechanicalPart> insertingPart) {
        List<FilledToolSlot> filledToolSlots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        var slotType = insertingPart.get().getOriginSlot().getKey();

        // out of all populated slots, figure out if all children of potential swap, can be supported by insertingPart
        var candidateSlots = filledToolSlots
                .stream()
                .filter(s -> s.slot().type() == slotType)
                .filter(s -> {
                    var part = Optional.of(s.part());
                    var children = filledToolSlots.stream().filter(c -> c.parent().equals(part));
                    var childSlotTypeCounts = children
                            .collect(Collectors.groupingBy(c -> c.slot().type(), Collectors.counting()));
                    return canSupportAll(childSlotTypeCounts, insertingPart);
                });
        var maybe_replaceTarget = candidateSlots.findAny();
        if (maybe_replaceTarget.isEmpty())
            return Optional.empty();
        var replaceTarget = maybe_replaceTarget.get();

        var targetAsParent = Optional.of(replaceTarget.part());
        var newAsParent = Optional.of(insertingPart.getKey());

        //noinspection OptionalGetWithoutIsPresent // we know replaceTarget is in filled tool slots, so we always have a value
        var replaceSlot = filledToolSlots.stream().filter(s -> s == replaceTarget).map(FilledToolSlot::slot).findAny().get();

        var newList = filledToolSlots.stream()
                .map(s -> {
                    if (s == replaceTarget) {
                        return new FilledToolSlot(s.slot(), insertingPart.getKey(), s.parent());
                    }
                    if (s.parent().equals(targetAsParent)) {
                        return new FilledToolSlot(s.slot(), s.part(), newAsParent);
                    }
                    return s;
                }).toList();
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, newList);
        replaceTarget.getPart().data.onRemoved(replaceSlot, stack);
        insertingPart.get().data.onInserted(replaceSlot, stack);

        return Optional.of(replaceTarget.getPartEntry());
    }

    private static void insertMechanicalPart(ItemStack stack, SlotEntry entry, RegistryEntry<MechanicalPart, MechanicalPart> part) {
        ArrayList<FilledToolSlot> filledSlots = new ArrayList<>(stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of()));
        filledSlots.add(new FilledToolSlot(entry.id(), part.getKey(), entry.parent()));
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(filledSlots));
        part.get().data.onInserted(entry.id(), stack);
    }

    private static void removeMechanicalPart(ItemStack stack, FilledToolSlot filledToolSlot) {
        ArrayList<FilledToolSlot> filledSlots = new ArrayList<>(stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of()));
        filledSlots.remove(filledToolSlot);
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(filledSlots));
        filledToolSlot.getPart().data.onRemoved(filledToolSlot.slot(), stack);
    }

    protected static boolean tryInsertingMechanicalPart(ItemStack stack, RegistryEntry<MechanicalPart, MechanicalPart> toInsert) {
        return tryInsertingMechanicalPart(stack, toInsert, toInsert.get().getOriginSlot().getKey());
    }

    protected static Set<SlotEntry> populatedSlots(List<FilledToolSlot> filledToolSlots) {
        return filledToolSlots.stream().map(SlotEntry::fromFilled).collect(Collectors.toSet());
    }

    protected static boolean tryInsertingMechanicalPart(ItemStack stack, RegistryEntry<MechanicalPart, MechanicalPart> toInsert, ResourceKey<MechanicalToolSlot> slotType) {
        List<FilledToolSlot> oldSlots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        if (oldSlots.isEmpty() && slotType == MechanicalToolSlot.ROOT.getKey()) {
            insertMechanicalPart(stack, SlotEntry.root(), toInsert);
            return true;
        }


        Set<SlotEntry> populatedSlots = populatedSlots(oldSlots);
        var openSlots = oldSlots.stream().flatMap(s ->
                s.getPart().supportingSlots(slotType).map(SlotEntry.factoryOf(s.part())).filter(e -> !populatedSlots.contains(e))
        );

        var firstOpenSlot = openSlots.findAny();
        if (firstOpenSlot.isEmpty())
            return false;

        insertMechanicalPart(stack, firstOpenSlot.get(), toInsert);
        return true;
    }

    protected static RemovingPartResult tryRemovingMechanicalPart(@NotNull ItemStack stack, @NotNull SlotAccess access, int selectedToolSlot) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        if (slots.isEmpty() || selectedToolSlot >= slots.size() || selectedToolSlot == -1)
            return RemovingPartResult.impossible();

        // find part to remove
        var toolSlotToRemove = slots.get(selectedToolSlot);

        // make sure no part is child of
        var len = slots.size();
        for (int i = 0; i < len; i++) {
            var slot = slots.get(i);
            if (slot.parent().isEmpty())
                continue;
            if (slot.parent().get() == toolSlotToRemove.part())
                return RemovingPartResult.isParentOf(i);
        }

        // TODO: let parts block removal of other part maybe?

        var removedPart = toolSlotToRemove.getPartEntry().get();

        removeMechanicalPart(stack, toolSlotToRemove);

        ArrayList<FilledToolSlot> newSlots = new ArrayList<>(slots);
        newSlots.remove(selectedToolSlot);
        removedPart.data.onRemoved(toolSlotToRemove.slot(), stack);
        stack.set(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(newSlots));

        recalculateTotalWeight(stack);
        var item = removedPart.getItemRegistry().get().getDefaultInstance();
        access.set(item);

        return RemovingPartResult.success();
    }


    protected static void recalculateTotalWeight(ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        float weight = 0;
        for (FilledToolSlot slot : slots) {
            var part = slot.getPartEntry();
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
