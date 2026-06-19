package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.CMETranslations;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public record MechanicalToolSlot(CMETranslations.LangEntry lang) {

    public static final ResourceKey<Registry<MechanicalToolSlot>> REGISTRY =
            REGISTRATE.makeRegistry("mechanical_tool_slot", RegistryBuilder::new);

    public static final Codec<ResourceKey<MechanicalToolSlot>> CODEC = ResourceKey.codec(REGISTRY);
    public static final StreamCodec<ByteBuf, ResourceKey<MechanicalToolSlot>> STREAM_CODEC = ResourceKey.streamCodec(REGISTRY);

    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> GRIP_SLOT = register("grip",
            CMETranslations.GRIP_TOOL_SLOT);
    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> COG_SLOT = register("cog",
            CMETranslations.COG_TOOL_SLOT);
    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> ACCELERATOR_SLOT = register("accelerator",
            CMETranslations.ACCELERATOR_TOOL_SLOT);
    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> GEARBOX_SLOT = register("gearbox",
            CMETranslations.GEARBOX_TOOL_SLOT);
    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> TANK_SLOT = register("tank",
            CMETranslations.TANK_TOOL_SLOT);
    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> TIP_SLOT = register("tip",
            CMETranslations.TIP_TOOL_SLOT);
    public static final RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> AUTOMATON_SLOT = register("automaton_top",
            CMETranslations.AUTOMATON_TOOL_SLOT);

    public static RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> register(String name, CMETranslations.LangEntry lang) {
        return REGISTRATE.object(name).simple(REGISTRY, () -> new MechanicalToolSlot(lang));
    }
    public static RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> get(ResourceKey<MechanicalToolSlot> slot) {
        return REGISTRATE.get(slot.location().getPath(), slot.registryKey());
    }

    @ApiStatus.Internal
    public static void register() {
        // load this class
    }
}
