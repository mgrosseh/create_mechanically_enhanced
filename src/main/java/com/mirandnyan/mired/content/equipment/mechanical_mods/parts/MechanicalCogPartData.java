package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.CVAClient;
import com.mirandnyan.mired.CVADataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MechanicalCogPartData extends MechanicalPartData {


    int speedModifier;
    public MechanicalCogPartData(int speedModifier) {
        this.speedModifier = speedModifier;
    }

    @Override
    public void onInserted(ItemStack tool) {
        var speed = tool.getOrDefault(CVADataComponents.SPEED_MODIFIER, 0);
        tool.set(CVADataComponents.SPEED_MODIFIER, speed + speedModifier);
    }
    @Override
    public void onRemoved(ItemStack tool) {
        var speed = tool.getOrDefault(CVADataComponents.SPEED_MODIFIER, 0);
        tool.set(CVADataComponents.SPEED_MODIFIER, Math.max(speed - speedModifier, 0));
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * ((100 + speedModifier) / 100f);

        angle %= 360;


        ms.pushPose();
        ms.translate(0, 1 / 16f, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(angle));
        ms.translate(0, -1 / 16f, 0);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
