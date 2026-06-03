package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;


public class MechanicalDrillPartData extends MechanicalPartData {

    Tool tool;

    public MechanicalDrillPartData(Tool toolProperties) {
        this.tool = toolProperties;
    }

    @Override
    public void onInserted(ItemStack tool) {
        tool.set(DataComponents.TOOL, this.tool);
        super.onInserted(tool);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        tool.remove(DataComponents.TOOL);
        super.onRemoved(tool);
    }

    float angle = 0;

    double speed;

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player))
            return;
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player)
            return;
        MultiPlayerGameMode game = mc.gameMode;
        var isDestroying = isSelected && game != null && game.isDestroying();
        if (isDestroying) {
            speed += 2.5;
            speed = Math.min(speed + 2.5, 132);
        }

        angle += (float) speed;
        angle %= 360;

        speed = Mth.lerp(0.1, speed, 0);
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        ms.pushPose();
        ms.translate(0, 1 / 16f, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(angle));
        ms.translate(0, -1 / 16f, 0);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
