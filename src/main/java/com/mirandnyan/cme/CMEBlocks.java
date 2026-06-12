package com.mirandnyan.cme;

import com.mirandnyan.cme.content.blocks.food_replicator.FoodReplicatorBlock;
import com.mirandnyan.cme.content.blocks.food_replicator.FoodReplicatorBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class CMEBlocks {

    public static final BlockEntry<FoodReplicatorBlock> FOOD_REPLICATOR = REGISTRATE.block("food_replicator", FoodReplicatorBlock::new)
            .initialProperties(AllBlocks.BRASS_CASING)
            .blockstate((ctx, prov) ->
                    prov.horizontalBlock(ctx.get(), getExisting(ctx, prov)))
            .properties(prop -> prop
                    .strength(8.0f, 9.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .requiresCorrectToolForDrops())
            .item()
            // TODO: recipe
            .model(AssetLookup::customItemModel)
            .build()
            .register();
    public static final BlockEntityEntry<FoodReplicatorBlockEntity> FOOD_REPLICATOR_BLOCK_ENTITY = blockEntityOf(FOOD_REPLICATOR, FoodReplicatorBlockEntity::new)
            //.visual(() -> FoodReplicatorVisual::new, false)
            //.renderer(() -> FoodReplicatorRenderer::new)
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
