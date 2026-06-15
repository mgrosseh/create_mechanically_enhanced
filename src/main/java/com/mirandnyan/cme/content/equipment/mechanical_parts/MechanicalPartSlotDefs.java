package com.mirandnyan.cme.content.equipment.mechanical_parts;


import com.mirandnyan.cme.util.AffineTransform;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

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

    public record SlotDefinition(AffineTransform transform, @NotNull ResourceKey<MechanicalToolSlot> slot) {}
}
