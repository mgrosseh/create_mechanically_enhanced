package com.mirandnyan.cme.content.equipment.mechanical_tool;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public class MechanicalToolRenderer extends CustomRenderedItemModelRenderer {

    private void renderSlot(FilledToolSlot filledToolSlot, List<FilledToolSlot> filledToolSlots,
                            ItemStack stack, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                            PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var maybe_part = filledToolSlot.getPart().map(DeferredHolder::get);
        if (maybe_part.isEmpty())
            return;
        var part = maybe_part.get();
        var origin = part.slots().getOriginTransform();
        //ms.pushPose();
        origin.apply(ms);
        part.data.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        //ms.popPose();
        for (var child : filledToolSlots) {
            if (child.parent().isEmpty() || child.parent().get() != filledToolSlot.part())
                continue;
            var attachmentTrans = part.slots().getTransform(child.slot());
            if (attachmentTrans.isEmpty())
                continue; // TODO log
            ms.pushPose();
            attachmentTrans.get().apply(ms);
            renderSlot(child, filledToolSlots, stack, renderer, transformType, ms, buffer, light, overlay);
            ms.popPose();
        }
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean renderedAnything = false;
        ms.pushPose();
        //ms.translate(0, 0.5 / 15f, -2 / 16f);
        // TODO: hand drawing

        List<FilledToolSlot> filledToolSlots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());


        for (var filledToolSlot : filledToolSlots) {
            if (filledToolSlot.getPart().isEmpty())
                return;
            renderedAnything = true;
            if (filledToolSlot.parent().isEmpty()) {
                ms.pushPose();
                renderSlot(filledToolSlot, filledToolSlots, stack, renderer, transformType, ms, buffer, light, overlay);
                ms.popPose();
            }

//            if (filledToolSlot.parent().isPresent()) {
//                var parent = filledToolSlot.parent().get();
//                var parentTrans = MechanicalPart.get(parent).get().slots().getTransform(part.slots().getOrigin());
//                if (parentTrans.isPresent()) { // TODO: logging?
//                    var partTrans = part.slots().getTransform();
//
//                    partTrans.apply(ms);
//                    parentTrans.get().apply(ms);
//                }
//            }
            //part.data.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        }

        if (!renderedAnything)
            renderer.renderSolid(model.getOriginalModel(), light);

        ms.popPose();
    }
}
