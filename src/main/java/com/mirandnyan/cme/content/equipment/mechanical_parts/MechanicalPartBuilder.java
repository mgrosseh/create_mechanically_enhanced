package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.CMEMaterials;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.util.AffineTransform;
import com.mirandnyan.cme.util.java_helpers.VarArgs;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class MechanicalPartBuilder {
    public static final String MECHANICAL_PART_LOCATION_PREFIX = "mechanical_part";

    // TODO: consider private fields -> protected
    private final String name;
    private ItemEntry<?> validItem = null;
    private MechanicalPartData data = null;
    private final ArrayList<ResourceLocation> models;
    private final ArrayList<MechanicalSubpart> subparts;
    private AffineTransform origin = null;
    private ResourceKey<MechanicalToolSlot> fitsInto  = null;
    private final ArrayList<MechanicalPartSlotDefs.SlotDefinition> slots;
    private ResourceKey<CMEMaterial> material;

    private final String modId;

    public MechanicalPartBuilder(String name) {
        this(CreateMechanicallyEnhanced.MOD_ID, name);
    }
    public MechanicalPartBuilder(String modId, String name) {
        this.name = name;
        this.models = new ArrayList<>();
        this.subparts = new ArrayList<>();
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

    public MechanicalPartBuilder defaultModel(String... path) {
        model(resource(VarArgs.of(MECHANICAL_PART_LOCATION_PREFIX).and(path).and(name).toArray()));
        return this;
    }
    public MechanicalPartBuilder customModel(String... path) {
        model(resource(VarArgs.of(MECHANICAL_PART_LOCATION_PREFIX).and(path).toArray()));
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
        this.origin = transform;
        this.fitsInto = slot;
        return this;
    }
    public MechanicalPartBuilder origin(AffineTransform transform, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return origin(transform, slot.getKey());
    }

    private int nextOrdinal(ResourceKey<MechanicalToolSlot> slotType) {
        return slots.stream()
                .filter(f -> f.slot().type() == slotType)
                .map(f -> f.slot().ordinal())
                .max(Integer::compareTo)
                .map(i -> i + 1)
                .orElse(0);
    }

    public MechanicalPartBuilder slot(AffineTransform transform, ResourceKey<MechanicalToolSlot> slotType) {
        slots.add(new MechanicalPartSlotDefs.SlotDefinition(transform, new FilledToolSlot.SlotId(slotType, nextOrdinal(slotType))));
        return this;
    }
    public MechanicalPartBuilder slot(AffineTransform transform, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return slot(transform, slot.getKey());
    }
    public MechanicalPartBuilder subpart(MechanicalSubpart subpart) {
        this.subparts.add(subpart);
        return this;
    }
    public MechanicalPartBuilder material(RegistryEntry<CMEMaterial, CMEMaterial> material) {
        return material(material.getKey());
    }

    private MechanicalPartBuilder material(ResourceKey<CMEMaterial> material) {
        this.material = material;
        return this;
    }


    public RegistryEntry<MechanicalPart, MechanicalPart> build(ResourceKey<Registry<MechanicalPart>> registry) {
        if (origin == null)
            throw new RuntimeException("Part must have an origin.");
        if (data == null)
            throw new RuntimeException("Part must have data.");
        if (validItem == null)
            throw new RuntimeException("Part must have a valid item.");
        if (models.isEmpty())
            defaultModel();

        // Annoying model space to hand space offsets
        var pixelToBlock = new AffineTransform().scale(1 / 16f).translate(-0.5f);
        var blockToPixel = pixelToBlock.inverse();

        var originTrans = pixelToBlock.copy().mul(origin).mul(blockToPixel);
        origin = originTrans.inverse();

        var defs = new MechanicalPartSlotDefs(origin, fitsInto, slots.stream()
                .map(def -> new MechanicalPartSlotDefs.SlotDefinition(
                        pixelToBlock.copy().mul(def.transform()).mul(blockToPixel),
                        def.slot()))
                .toArray(MechanicalPartSlotDefs.SlotDefinition[]::new));

        return REGISTRATE.object(name).simple(registry, () ->
                new MechanicalPart(defs, validItem.getKey(), data, name, material,
                        subparts.toArray(MechanicalSubpart[]::new), models.toArray(ResourceLocation[]::new)));
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

