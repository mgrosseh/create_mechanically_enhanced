package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_parts.CMEMaterial;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class CMEMaterials {
    public static final ResourceKey<Registry<CMEMaterial>> REGISTRY =
            REGISTRATE.makeRegistry("cme_material", RegistryBuilder::new);

    public static final RegistryEntry<CMEMaterial, CMEMaterial> WOOD = register("wood", "Wood");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> STONE = register("stone", "Stone");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> IRON = register("iron", "Iron");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> ANDESITE = register("andesite", "Andesite");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> COPPER = register("copper", "Copper");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> DIAMOND = register("diamond", "Diamond");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> BRASS = register("brass", "Brass");
    public static final RegistryEntry<CMEMaterial, CMEMaterial> NETHERITE = register("netherite", "Netherite");

    public static RegistryEntry<CMEMaterial, CMEMaterial> register(String name, String lang) {
        var entry = new CMETranslations.LangEntry("material." + name, lang);
        return REGISTRATE.object(name).simple(REGISTRY, () -> new CMEMaterial(name, entry));
    }
    public static RegistryEntry<CMEMaterial, CMEMaterial> register(String name, CMETranslations.LangEntry lang) {
        return REGISTRATE.object(name).simple(REGISTRY, () -> new CMEMaterial(name, lang));
    }

    public static RegistryEntry<CMEMaterial, CMEMaterial> get(ResourceKey<CMEMaterial> part) {
        return REGISTRATE.get(part.location().getPath(), part.registryKey());
    }

    public static void register() {

    }
}
