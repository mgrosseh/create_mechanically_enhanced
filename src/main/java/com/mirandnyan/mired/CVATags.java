package com.mirandnyan.mired;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;


public class CVATags {
    public static <T> TagKey<T> tag(Registry<T> registry, ResourceLocation id) {
        return TagKey.create(registry.key(), id);
    }


    public static class Items {
        public static final TagKey<Item> STONES = common("stones");

        public static TagKey<Item> common(String name) {
            return tag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
