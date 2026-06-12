package com.mirandnyan.cme;

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


public class CMETags {
    private static final CreateRegistrate REGISTRATE = CreateMechanicallyEnhanced.REGISTRATE;
    public static <T> TagKey<T> tag(Registry<T> registry, ResourceLocation id) {
        return TagKey.create(registry.key(), id);
    }



    public static class Items {
        public static final TagKey<Item> STONES = common("stones");

        public static final TagKey<Item> LOW_LIKED_MECHANICAL_CAT_FOOD = custom("low_liked_mechanical_cat_food");
        public static final TagKey<Item> MID_LIKED_MECHANICAL_CAT_FOOD = custom("mid_liked_mechanical_cat_food");
        public static final TagKey<Item> HIGH_LIKED_MECHANICAL_CAT_FOOD = custom("high_liked_mechanical_cat_food");

        public static TagKey<Item> common(String name) {
            return tag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
        }
        public static TagKey<Item> custom(String name) {
            return tag(BuiltInRegistries.ITEM, CreateMechanicallyEnhanced.asResource(name));
        }

        public static void genTags(RegistrateItemTagsProvider prov) {
            prov.addTag(LOW_LIKED_MECHANICAL_CAT_FOOD)
                    .add(CMEItems.MINTED_COPPER_COIN.asItem())
                    .add(CMEItems.MINTED_IRON_COIN.asItem())
                    .add(CMEItems.MINTED_BRASS_COIN.asItem());
            prov.addTag(MID_LIKED_MECHANICAL_CAT_FOOD)
                    .add(CMEItems.MINTED_IRON_COIN_AMETHYST.asItem())
                    .add(CMEItems.MINTED_IRON_COIN_DIAMOND.asItem())
                    .add(CMEItems.MINTED_IRON_COIN_EMERALD.asItem())
                    .add(CMEItems.MINTED_IRON_COIN_EXPERIENCE.asItem());
            prov.addTag(HIGH_LIKED_MECHANICAL_CAT_FOOD)
                    .add(CMEItems.MINTED_BRASS_COIN_AMETHYST.asItem())
                    .add(CMEItems.MINTED_BRASS_COIN_DIAMOND.asItem())
                    .add(CMEItems.MINTED_BRASS_COIN_EMERALD.asItem())
                    .add(CMEItems.MINTED_BRASS_COIN_EXPERIENCE.asItem());
        }
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
            return tag(BuiltInRegistries.BLOCK, CreateMechanicallyEnhanced.asResource(name));
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
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, CMETags.Blocks::genTags);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CMETags.Items::genTags);
    }
}
