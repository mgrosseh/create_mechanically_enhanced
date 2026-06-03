package com.mirandnyan.mired.content.blocks.helpers;

import com.mirandnyan.mired.CreateMechanicallyEnhanced;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public abstract class BlockStateGenerator<T extends Block> {
    protected String model_location;

    protected BlockStateGenerator(String model_location) {
        this.model_location = model_location;
    }

    public abstract <P extends T> void generator(DataGenContext<Block, P> ctx, RegistrateBlockstateProvider prov);
    public <P extends T> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> generate() {
        return this::generator;
    }

    protected ModelFile block_model(final RegistrateBlockstateProvider p, final String suffix) {
        return block_model(p, model_location, suffix);
    }
    protected ModelFile block_model(final RegistrateBlockstateProvider p, final String infix, final String suffix) {
        return p.models().getExistingFile(CreateMechanicallyEnhanced.path("block/" + infix + "/" + suffix));
    }

    protected static ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> rotate(ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> builder, Direction dir) {
        return builder.rotationX(dirToRotX(dir)).rotationY(dirToRotY(dir));
    }

    protected static int dirToRotX(Direction dir) {
        return switch (dir) {
            case DOWN -> 180;
            case UP -> 0;
            case NORTH -> 90;
            case SOUTH -> 90;
            case WEST -> 90;
            case EAST -> 90;
        };
    }

    protected static int dirToRotY(Direction dir) {
        return switch (dir) {
            case DOWN -> 0;
            case UP -> 0;
            case NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
        };
    }
}
