package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.util.AffineTransform;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class MechanicalPartBuilder {
    // TODO: consider private fields -> protected
    private final String name;
    private ItemEntry<?> validItem = null;
    private MechanicalPartData data = null;
    private final ArrayList<ResourceLocation> models;
    private MechanicalPartSlotDefs.SlotDefinition origin = null;
    private final ArrayList<MechanicalPartSlotDefs.SlotDefinition> slots;

    private final String modId;

    public MechanicalPartBuilder(String name) {
        this(CreateMechanicallyEnhanced.MOD_ID, name);
    }
    public MechanicalPartBuilder(String modId, String name) {
        this.name = name;
        this.models = new ArrayList<>();
        this.slots = new ArrayList<>();
        this.modId = modId;
    }

    public MechanicalPartBuilder item(ItemEntry<?> validItem) {
        this.validItem = validItem;
        return this;
    }
    public MechanicalPartBuilder defaultData(float weight) {
        this.data = new MechanicalPartData(weight) {};
        return this;
    }
    public MechanicalPartBuilder data(MechanicalPartData data) {
        this.data = data;
        return this;
    }
    public MechanicalPartBuilder model(String... path) {
        model(resource(path));
        return this;
    }
    public MechanicalPartBuilder model(ResourceLocation location) {
        this.models.add(location);
        return this;
    }
    public MechanicalPartBuilder origin(RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return origin(new AffineTransform(), slot); // TODO: maybe different value for tool origin
    }
    public MechanicalPartBuilder origin(AffineTransform transform, ResourceKey<MechanicalToolSlot> slot) {
        this.origin = new MechanicalPartSlotDefs.SlotDefinition(transform.inverse(), slot);
        return this;
    }
    public MechanicalPartBuilder origin(AffineTransform transform, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return origin(transform, slot.getKey());
    }
    public MechanicalPartBuilder slot(AffineTransform transform, ResourceKey<MechanicalToolSlot> slot) {
        slots.add(new MechanicalPartSlotDefs.SlotDefinition(transform, slot));
        return this;
    }
    public MechanicalPartBuilder slot(AffineTransform transform, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return slot(transform, slot.getKey());
    }

    public RegistryEntry<MechanicalPart, MechanicalPart> build(ResourceKey<Registry<MechanicalPart>> registry) {
        if (origin == null)
            throw new RuntimeException("Part must have an origin.");
        if (data == null)
            throw new RuntimeException("Part must have data.");
        if (validItem == null)
            throw new RuntimeException("Part must have a valid item.");
        if (models.isEmpty())
            models.add(resource("tool_part", name));

        var def = new MechanicalPartSlotDefs(origin, slots.toArray(new MechanicalPartSlotDefs.SlotDefinition[]{}));
        return REGISTRATE.object(name).simple(registry, () ->
                new MechanicalPart(def, validItem.getKey(), data, name, models.toArray(new ResourceLocation[]{})));
    }

    public RegistryEntry<MechanicalPart, MechanicalPart> build() {
        return build(MechanicalPart.REGISTRY);
    }


    private ResourceLocation resource(String... pathParts) {
        return resource(String.join("/", pathParts));

    }
    private ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }
}

