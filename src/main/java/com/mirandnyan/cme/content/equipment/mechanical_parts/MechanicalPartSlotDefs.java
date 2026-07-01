package com.mirandnyan.cme.content.equipment.mechanical_parts;


import com.mirandnyan.cme.util.math.AffineTransform;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class MechanicalPartSlotDefs {
    AffineTransform origin;
    ResourceKey<MechanicalToolSlot> fitsInto;
    SlotDefinition[] slots;

    public MechanicalPartSlotDefs(@NotNull AffineTransform origin, ResourceKey<MechanicalToolSlot> fitsInto, @NotNull SlotDefinition... slots) {
        this.origin = origin;
        this.fitsInto = fitsInto;
        this.slots = slots;
    }

    public Stream<FilledToolSlot.SlotId> supportingSlots(ResourceKey<MechanicalToolSlot> slotType) {
        return Arrays.stream(slots).filter(def -> def.slot().type().equals(slotType)).map(def -> def.slot);
    }

    public Optional<AffineTransform> getTransform(FilledToolSlot.SlotId slot) {
        return Arrays.stream(slots).filter(def -> def.slot.equals(slot)).map(def -> def.transform).findAny();
    }

    public AffineTransform getOriginTransform() {
        return origin;
    }

    public ResourceKey<MechanicalToolSlot> getOrigin() {
        return fitsInto;
    }

    public record SlotDefinition(AffineTransform transform, @NotNull FilledToolSlot.SlotId slot) {}
}
