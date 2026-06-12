package com.mirandnyan.cme.ponder.scenes;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RedStoneWireBlock;

public abstract class Scene {
    private final String title;
    private final String sceneId;
    private final int basePlateSize;
    private final int basePlateXOffset;
    private final int basePlateZOffset;

    public Scene(String sceneId, String title, int basePlateSize) {
        this(sceneId, title, basePlateSize, 0, 0);

    }
    public Scene(String sceneId, String title,
                 int basePlateSize, int basePlateXOffset, int basePlateZOffset) {
        this.sceneId = sceneId;
        this.title = title;
        this.basePlateSize = basePlateSize;
        this.basePlateXOffset = basePlateXOffset;
        this.basePlateZOffset = basePlateZOffset;
    }

    public void run(final SceneBuilder builder, final SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final OverlayInstructions overlays = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final EffectInstructions effects = scene.effects();
        final PositionUtil grid = util.grid();

        scene.title(sceneId, title);
        scene.configureBasePlate(basePlateXOffset, basePlateZOffset, basePlateSize);

        scene(scene, world, overlays, select, vector, effects, grid);
    }

    public abstract void scene(final CreateSceneBuilder scene, final CreateSceneBuilder.WorldInstructions world,
                               final OverlayInstructions overlay, final SelectionUtil select, final VectorUtil vector,
                               final EffectInstructions effects, final PositionUtil grid);


    protected static void setAnalogLeverPower(CreateSceneBuilder.WorldInstructions world, Selection sel, int power) {
        world.modifyBlockEntityNBT(sel, AnalogLeverBlockEntity.class, tag -> tag.putInt("State", power));
    }
    protected static void setNixiePower(CreateSceneBuilder.WorldInstructions world, Selection sel, int power) {
        world.modifyBlockEntityNBT(sel, NixieTubeBlockEntity.class, tag -> tag.putInt("RedstoneStrength", power));
    }
    protected static void setRedstoneWirePower(CreateSceneBuilder.WorldInstructions world, BlockPos pos, int power) {
        world.modifyBlock(pos, s -> s.setValue(RedStoneWireBlock.POWER, power), false);
    }
}
