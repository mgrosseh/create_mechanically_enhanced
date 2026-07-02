package com.mirandnyan.cme;

import com.simibubi.create.AllTags;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import static net.minecraft.world.item.Items.*;


public class CMETags {
    private static final CreateRegistrate REGISTRATE = CreateMechanicallyEnhanced.REGISTRATE;
    public static <T> TagKey<T> tag(Registry<T> registry, ResourceLocation id) {
        return TagKey.create(registry.key(), id);
    }



    public static class Items {
        public static final TagKey<Item> PLANKS = minecraft("planks");

        public static final TagKey<Item> WOOL = common("wool");

        public static final TagKey<Item> WOODEN_RODS = common("rods/wooden");
        public static final TagKey<Item> WATER_BUCKETS = common("buckets/water");
        public static final TagKey<Item> STONES = common("stones");
        public static final TagKey<Item> CROPS = common("crops");

        public static final TagKey<Item> ALL_BLACKSTONE = common("all_blackstone");

        public static final TagKey<Item> LOW_LIKED_MECHANICAL_CAT_FOOD = custom("low_liked_mechanical_cat_food");
        public static final TagKey<Item> MID_LIKED_MECHANICAL_CAT_FOOD = custom("mid_liked_mechanical_cat_food");
        public static final TagKey<Item> HIGH_LIKED_MECHANICAL_CAT_FOOD = custom("high_liked_mechanical_cat_food");


        public static final TagKey<Item> NOT_CARDBOARD_BASE = custom("not_cardboard_base");
        public static final TagKey<Item> NOT_CARDBOARD_BINDING = custom("not_cardboard_binding");

        public static final TagKey<Item> CARDBOARD_PLATES = common("plates/cardboard");
        public static final TagKey<Item> CARDBOARD_LIKE = custom("cardboard_like");

        public static final TagKey<Item> BURN_UP_IMMUNE = custom("burn_up_immune");
        public static final TagKey<Item> BURN_UP_HIGH_LIKELIHOOD = custom("burn_up_high_liekelihood");

        public static TagKey<Item> common(String name) {
            return tag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
        }
        public static TagKey<Item> custom(String name) {
            return tag(BuiltInRegistries.ITEM, CreateMechanicallyEnhanced.asResource(name));
        }
        public static TagKey<Item> minecraft(String name) {
            return tag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", name));
        }

        public static void genTags(RegistrateItemTagsProvider prov) {
            prov.addTag(ALL_BLACKSTONE)
                    .add(BLACKSTONE)
                    .add(BLACKSTONE_SLAB)
                    .add(BLACKSTONE_STAIRS)
                    .add(BLACKSTONE_WALL)
                    .add(POLISHED_BLACKSTONE)
                    .add(POLISHED_BLACKSTONE_BRICK_SLAB)
                    .add(POLISHED_BLACKSTONE_BRICKS)
                    .add(POLISHED_BLACKSTONE_BRICK_STAIRS)
                    .add(POLISHED_BLACKSTONE_BUTTON)
                    .add(POLISHED_BLACKSTONE_SLAB)
                    .add(POLISHED_BLACKSTONE_BRICK_WALL)
                    .add(POLISHED_BLACKSTONE_PRESSURE_PLATE)
                    .add(POLISHED_BLACKSTONE_STAIRS)
                    .add(POLISHED_BLACKSTONE_WALL)
                    .add(CRACKED_POLISHED_BLACKSTONE_BRICKS)
                    .add(CHISELED_POLISHED_BLACKSTONE)
                    .add(GILDED_BLACKSTONE);

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

            prov.addTag(NOT_CARDBOARD_BASE)
                    .add(PAPER)
                    .add(KELP)
                    .add(LILY_PAD)
                    .add(BAMBOO);

            prov.addTag(NOT_CARDBOARD_BINDING)
                    .addTag(Tags.Items.NUGGETS_IRON)
                    .add(CLAY);

            prov.addTag(CARDBOARD_PLATES); // TODO: I guess somehow I have to make create load before me here? idk why it doesn't
            prov.addTag(CARDBOARD_LIKE)
                    .addTag(CARDBOARD_PLATES)
                    .add(CMEItems.NOT_CARDBOARD.getKey());


            prov.addTag(BURN_UP_IMMUNE)
                    .addTag(Tags.Items.OBSIDIANS)
                    .addTag(Tags.Items.NETHER_STARS)
                    .addTag(Tags.Items.NETHERRACKS)
                    .addTag(Tags.Items.BRICKS_NETHER)
                    .addTag(Tags.Items.END_STONES)
                    .addTag(Tags.Items.GEMS)
                    .addTag(Tags.Items.INGOTS)
                    .addTag(Tags.Items.NUGGETS)
                    .addTag(Tags.Items.RODS_BLAZE)
                    .addTag(Tags.Items.SANDSTONE_BLOCKS)
                    .addTag(Tags.Items.STONES)
                    .addTag(Tags.Items.SHULKER_BOXES)
                    .addTag(ALL_BLACKSTONE)
            // TODO: add all metals
            // TODO: add all copper
            ;

            prov.addTag(BURN_UP_HIGH_LIKELIHOOD) // not that many blocks are furnace fuel and skipped
                    .addTag(CROPS)
            ;
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
