package com.mirandnyan.cme;

import com.mirandnyan.cme.content.blocks.part_crafter.PartCrafterBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class CMEBlocks {

    public static final BlockEntry<PartCrafterBlock> PART_CRAFTER = REGISTRATE.block("part_crafter", PartCrafterBlock::new)
            .initialProperties(() -> Blocks.SMITHING_TABLE)
            //.properties(p -> p)
            .blockstate((ctx, prov) ->
                    prov.simpleBlock(ctx.get(), prov.models()
                                    .cubeBottomTop("part_crafter",
                                            CreateMechanicallyEnhanced.asResource("block", "part_crafter", "side"),
                                            CreateMechanicallyEnhanced.asResource("block", "part_crafter", "bottom"),
                                            CreateMechanicallyEnhanced.asResource("block", "part_crafter", "top")
                                    )
                    )
            )
            .item()
            .model((c, p) -> p.withExistingParent("part_crafter",
                    CreateMechanicallyEnhanced.asResource("block", "part_crafter")))
            .recipe((ctx, prov) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                            .pattern("CC")
                            .pattern("WW")
                            .pattern("WW")
                            .define('C', CMETags.Items.CARDBOARD_LIKE)
                            .define('W', CMETags.Items.PLANKS)
                            .unlockedBy("has_cardboard_like", RegistrateRecipeProvider.has(CMETags.Items.CARDBOARD_LIKE))
                            .save(prov)
            )
            .build()
            .register();

    protected static <T extends Block> ModelFile getExisting(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        return prov.models().getExistingFile(CreateMechanicallyEnhanced.asResource("block/" + ctx.getName() + "/block"));
    }
    protected static <T extends BlockEntity> BlockEntityBuilder<T, CreateRegistrate> blockEntityOf(
            BlockEntry<?> entry,
            BlockEntityBuilder.BlockEntityFactory<T> factory) {
        String name = entry.getId().getPath();
        return REGISTRATE.blockEntity(name, factory).validBlock(entry);
    }


    public static <T extends BlockEntity> BlockEntityEntry<T> getBlockEntity(BlockEntry<?> entry) {
        return BlockEntityEntry.cast(entry.getSibling(Registries.BLOCK_ENTITY_TYPE));
    }
    public static ItemEntry<? extends Item> getItem(BlockEntry<?> entry) {
        return ItemEntry.cast(entry.getSibling(Registries.ITEM));
    }

    public static void register() {
        // load class
    }
}
