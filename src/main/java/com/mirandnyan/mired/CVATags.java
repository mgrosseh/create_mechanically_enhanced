package com.mirandnyan.mired;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;


public class CVATags {
    private static final CreateRegistrate REGISTRATE = CreateVariousAdditions.REGISTRATE;
    public static <T> TagKey<T> tag(Registry<T> registry, ResourceLocation id) {
        return TagKey.create(registry.key(), id);
    }



    public static class Items {
        public static final TagKey<Item> STONES = common("stones");

        public static TagKey<Item> common(String name) {
            return tag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
        }
        public static TagKey<Item> custom(String name) {
            return tag(BuiltInRegistries.ITEM, CreateVariousAdditions.asResource(name));
        }

        public static void genTags(RegistrateItemTagsProvider registrateItemTagsProvider) {}
    }

    public static class Blocks {
        public static final TagKey<Block> MINEABLE_WITH_MECHANICAL_DRILL = custom("minable_with_mechanical_drill");
        public static final TagKey<Block> INCORRECT_FOR_MECHANICAL_DRILL = custom("incorrect_for_mechanical_drill");

        public static final TagKey<Block> MINEABLE_WITH_MECHANICAL_SAW = custom("minable_with_mechanical_saw");
        public static final TagKey<Block> INCORRECT_FOR_MECHANICAL_SAW = custom("incorrect_for_mechanical_saw");

        public static TagKey<Block> common(String name) {
            return tag(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", name));
        }
        public static TagKey<Block> custom(String name) {
            return tag(BuiltInRegistries.BLOCK, CreateVariousAdditions.asResource(name));
        }

        protected static void genTags(RegistrateTagsProvider<Block> prov) {
            prov.addTag(MINEABLE_WITH_MECHANICAL_DRILL)
                    .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .addTag(BlockTags.MINEABLE_WITH_SHOVEL);
            prov.addTag(INCORRECT_FOR_MECHANICAL_DRILL)
                    .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);

            prov.addTag(MINEABLE_WITH_MECHANICAL_SAW)
                    .addTag(BlockTags.MINEABLE_WITH_AXE)
                    .addTag(BlockTags.MINEABLE_WITH_HOE);
            prov.addTag(INCORRECT_FOR_MECHANICAL_SAW)
                    .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
        }
    }


    public static void addGenerators() {
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, CVATags.Blocks::genTags);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CVATags.Items::genTags);
    }
}
