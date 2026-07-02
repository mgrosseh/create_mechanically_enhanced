package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class MechanicalSubpart {


    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, FilledToolSlot slot, List<FilledToolSlot> filledToolSlots, MechanicalPart part,
                       MechanicalSubpart subpart, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        render(stack, part, subpart, renderer, transformType, ms, buffer, light, overlay);
    }
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, MechanicalPart part, MechanicalSubpart subpart, PartialItemModelRenderer renderer,
                       ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    }
}
