
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

