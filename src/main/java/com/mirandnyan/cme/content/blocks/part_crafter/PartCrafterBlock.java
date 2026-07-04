package com.mirandnyan.cme.content.blocks.part_crafter;

import com.mirandnyan.cme.CMEMenus;
import com.mirandnyan.cme.CMETranslations;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PartCrafterBlock extends HorizontalDirectionalBlock /* implements EntityBlock */ {
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<PartCrafterBlock> CODEC = simpleCodec(PartCrafterBlock::new);

    private static final Component CONTAINER_TITLE = CMETranslations.PART_CRAFTER_CONTAINER_TITLE.resolveComponent();

    public PartCrafterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
        super.createBlockStateDefinition(builder);
    }


    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext pContext) {
        final Direction dir = pContext.getHorizontalDirection().getOpposite();

        assert pContext.getPlayer() != null;
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, pContext.getPlayer().isShiftKeyDown() ? dir.getOpposite() : dir);
    }


    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        player.openMenu(state.getMenuProvider(level, pos));
        //player.awardStat(CMEStats.INTERACT_WITH_PART_CRAFTER);
        return InteractionResult.CONSUME;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, access) ->
                new PartCrafterMenu(CMEMenus.PART_CRAFTER.get(), containerId, playerInventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }
}
