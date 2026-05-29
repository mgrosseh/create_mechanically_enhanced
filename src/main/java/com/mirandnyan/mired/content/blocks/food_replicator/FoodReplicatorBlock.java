package com.mirandnyan.mired.content.blocks.food_replicator;

import com.mirandnyan.mired.CVABlocks;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoodReplicatorBlock extends KineticBlock implements IBE<FoodReplicatorBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final MapCodec<FoodReplicatorBlock> CODEC = simpleCodec(FoodReplicatorBlock::new);

    public FoodReplicatorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends KineticBlock> codec() {
        return CODEC;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VoxelShaper.forHorizontal(Shapes.or(
                    Block.box(0, 0, 0, 16, 12, 16),
                    Shapes.or(Block.box(0, 12, 0, 16, 24, 8),
                            Shapes.or(Block.box(5, 19, 8, 11, 24, 14),
                                    Shapes.or(Block.box(0, 12, 8, 2, 18, 16),
                                            Block.box(14, 12, 8, 16, 18, 16)
                                    )
                            )
                    )
                ), Direction.NORTH).get(state.getValue(FACING));
    }

    // BLOCK ENTITY
    @Override
    public Class<FoodReplicatorBlockEntity> getBlockEntityClass() {
        return FoodReplicatorBlockEntity.class;
    }
    @Override
    public BlockEntityType<? extends FoodReplicatorBlockEntity> getBlockEntityType() {
        return CVABlocks.FOOD_REPLICATOR_BLOCK_ENTITY.get();
    }

    // BLOCK STATE
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
