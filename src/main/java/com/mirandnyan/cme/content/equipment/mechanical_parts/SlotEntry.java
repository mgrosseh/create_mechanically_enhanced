package com.mirandnyan.cme.content.equipment.mechanical_parts;

import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;

public record SlotEntry(FilledToolSlot.SlotId id, Optional<ResourceKey<MechanicalPart>> parent) {

    public static SlotEntry root() {
        return new SlotEntry(new FilledToolSlot.SlotId(MechanicalToolSlot.ROOT.getKey(), 0), Optional.empty());
    }

    public static SlotEntry fromFilled(FilledToolSlot filled) {
        return new SlotEntry(filled.slot(), filled.parent());
    }

    public static Function<FilledToolSlot.SlotId, SlotEntry> factoryOf(ResourceKey<MechanicalPart> parent) {
        return id -> new SlotEntry(id, Optional.ofNullable(parent));
    }
}
