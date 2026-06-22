package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.*;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.*;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class MechanicalPart {
    public static final ResourceKey<Registry<MechanicalPart>> REGISTRY =
            REGISTRATE.makeRegistry("mechanical_part", RegistryBuilder::new);

    public static final Codec<ResourceKey<MechanicalPart>> CODEC = ResourceKey.codec(REGISTRY);
    public static final StreamCodec<ByteBuf, ResourceKey<MechanicalPart>> STREAM_CODEC = ResourceKey.streamCodec(REGISTRY);

    public final MechanicalPartData data;
    public final @NotNull ResourceKey<Item> validItem;
    public final @NotNull MechanicalPartSlotDefs slotDefinitions;

    public final @NotNull String name;
    public final @NotNull PartialModel[] models;
    public final @NotNull MechanicalSubpart[] subparts;

    public final @Nullable ResourceKey<CMEMaterial> material;

    protected MechanicalPart(@NotNull MechanicalPartSlotDefs slotDefinitions,
                             @NotNull ResourceKey<Item> validItem,
                             @NotNull MechanicalPartData data,
                             @NotNull String name,
                             @NotNull ResourceLocation... models) {
        this(slotDefinitions, validItem, data, name, null, new MechanicalSubpart[]{}, models);
    }

    protected MechanicalPart(@NotNull MechanicalPartSlotDefs slotDefinitions,
                             @NotNull ResourceKey<Item> validItem,
                             @NotNull MechanicalPartData data,
                             @NotNull String name,
                             @Nullable ResourceKey<CMEMaterial> material,
                             @NotNull MechanicalSubpart[] subparts,
                             @NotNull ResourceLocation[] models) {
        this.name = name;
        this.slotDefinitions = slotDefinitions;
        this.validItem = validItem;
        this.data = data;
        data.setParent(this);
        this.models = Arrays.stream(models)
                .map(PartialModel::of)
                .toArray(PartialModel[]::new);
        this.subparts = subparts;
        this.material = material;
    }

    public boolean isItem(Item item) {
        return getItemRegistry().is(item);
    }

    public RegistryEntry<Item, Item> getItemRegistry() {
        return REGISTRATE.get(validItem.location().getPath(), validItem.registryKey());
    }

    public RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> getOriginSlot() {
        return REGISTRATE.get(slotDefinitions.origin.slot().location().getPath(), slotDefinitions.origin.slot().registryKey());
    }

    public MechanicalPartSlotDefs slots() {
        return slotDefinitions;
    }

    @Override
    public String toString() {
        return "Part{" + name + "}";
    }

    // STATIC

    public boolean isIn(ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.getPartRegistry().get() != this)
                continue;
            return true;
        }
        return false;

    }

    public static boolean isIn(ResourceKey<MechanicalPart> part, ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.part().compareTo(part) != 0)
                continue;
            return true;
        }
        return false;
    }

}
