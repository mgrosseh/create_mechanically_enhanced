package com.mirandnyan.cme.content.equipment.mechanical_tool;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.util.math.AffineTransform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MechanicalToolRenderer extends CustomRenderedItemModelRenderer {
    // -[ DEBUG:
    public static final PartialModel arrow_up = PartialModel.of(CreateMechanicallyEnhanced.asResource("debug/arrow_up"));
    public static final PartialModel arrow_south = PartialModel.of(CreateMechanicallyEnhanced.asResource("debug/arrow_south"));
    public static final PartialModel arrow_east = PartialModel.of(CreateMechanicallyEnhanced.asResource("debug/arrow_east"));
    public static final PartialModel cube = PartialModel.of(CreateMechanicallyEnhanced.asResource("debug/full_cube"));
    public static final PartialModel pixel = PartialModel.of(CreateMechanicallyEnhanced.asResource("debug/1pixel"));

    private void renderSlotDebug(FilledToolSlot filledToolSlot, List<FilledToolSlot> filledToolSlots,
                            ItemStack stack, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                            PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        MechanicalPart part = filledToolSlot.getPartEntry().get();
        var origin = part.slotDefinitions().getOriginTransform();

        var childName = "blaze_automaton";
        var parentName = "cardboard_gearbox";

        var childOriginTransform = AffineTransform.identity
                //.rotateXDegrees(180f)
                .translate(8f, 0f, 8f);

        var slotTransform = AffineTransform.identity
                .rotateXDegrees(90f)
                .translate(8f, 4f + 7, 11f)
                ;

        if (part.name.equals(childName)) {
            origin = childOriginTransform
                    .convertToBlockSpace()
                    .inverse();
        }


        origin.apply(ms);

        if (part.name.equals("wooden_grip")) {
            renderer.render(arrow_up.get(), light);
            renderer.render(arrow_east.get(), light);
            renderer.render(arrow_south.get(), light);
        }

        part.data.render(stack, filledToolSlot, filledToolSlots, part, renderer, transformType, ms, buffer, light, overlay);
        for (var child : filledToolSlots) {
            if (child.parent().isEmpty() || child.parent().get() != filledToolSlot.part())
                continue;
            var attachmentTrans = part.slotDefinitions().getTransform(child.slot());
            ms.pushPose();

            if (part.name.equals(parentName) && child.getPart().name.equals(childName)) {
                var fakeTrans = slotTransform.convertToBlockSpace();
                fakeTrans.apply(ms);
            }
            else
                attachmentTrans.ifPresent(p -> p.apply(ms));


            renderSlotDebug(child, filledToolSlots, stack, renderer, transformType, ms, buffer, light, overlay);
            ms.popPose();
        }
    }
    // ]-

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
