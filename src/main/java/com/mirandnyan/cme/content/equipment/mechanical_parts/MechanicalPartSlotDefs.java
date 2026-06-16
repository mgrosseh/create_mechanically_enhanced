package com.mirandnyan.cme.content.equipment.mechanical_parts;


import com.mirandnyan.cme.util.AffineTransform;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MechanicalPartSlotDefs {
    SlotDefinition origin;
    SlotDefinition[] slots;

    public MechanicalPartSlotDefs(@NotNull SlotDefinition origin, @NotNull SlotDefinition... slots) {
        this.origin = origin;
        this.slots = slots;
    }

    public boolean has(ResourceKey<MechanicalToolSlot> slot) {
        for (var slotDefs : slots) {
            if (slotDefs.slot().equals(slot))
                return true;
        }
        return false;
    }

    public Optional<AffineTransform> getTransform(ResourceKey<MechanicalToolSlot> slot) {
        for (var slotDefs : slots) {
            if (slotDefs.slot().equals(slot))
                return Optional.of(slotDefs.transform);
        }
        return Optional.empty();
    }

    public AffineTransform getOriginTransform() {
        return origin.transform;
    }

    public ResourceKey<MechanicalToolSlot> getOrigin() {
        return origin.slot;
    }

    public record SlotDefinition(AffineTransform transform, @NotNull ResourceKey<MechanicalToolSlot> slot) {}
}
