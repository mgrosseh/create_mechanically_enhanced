package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MechanicalBlazePartData extends MechanicalPartData {

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        ms.pushPose();
        ms.translate(10 / 16f, 13 / 16f, 0);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
