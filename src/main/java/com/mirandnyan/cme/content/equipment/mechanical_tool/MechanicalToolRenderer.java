package com.mirandnyan.cme.content.equipment.mechanical_tool;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MechanicalToolRenderer extends CustomRenderedItemModelRenderer {

    private void renderSlot(FilledToolSlot filledToolSlot, List<FilledToolSlot> filledToolSlots,
                            ItemStack stack, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                            PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        MechanicalPart part = filledToolSlot.getPartEntry().get();
        var origin = part.slotDefinitions().getOriginTransform();

        origin.apply(ms);
        part.data.render(stack, filledToolSlot, filledToolSlots, part, renderer, transformType, ms, buffer, light, overlay);
        for (var child : filledToolSlots) {
            if (child.parent().isEmpty() || child.parent().get() != filledToolSlot.part())
                continue;
            var attachmentTrans = part.slotDefinitions().getTransform(child.slot());
            ms.pushPose();
            attachmentTrans.ifPresent(p -> p.apply(ms));
            renderSlot(child, filledToolSlots, stack, renderer, transformType, ms, buffer, light, overlay);
            ms.popPose();
        }
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        // TODO: hand drawing

        boolean renderedAnything = false;
        ms.pushPose();

        List<FilledToolSlot> filledToolSlots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());


        for (var filledToolSlot : filledToolSlots) {
            renderedAnything = true;
            if (filledToolSlot.parent().isEmpty()) {
                ms.pushPose();
                renderSlot(filledToolSlot, filledToolSlots, stack, renderer, transformType, ms, buffer, light, overlay);
                ms.popPose();
            }
        }

        if (!renderedAnything)
            renderer.renderSolid(model.getOriginalModel(), light);

        ms.popPose();
    }
}
