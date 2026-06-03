package com.mirandnyan.mired.content.equipment.mechanical_drill;

import com.mirandnyan.mired.CVADataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.FilledToolSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MechanicalDrillRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean renderedAnything = false;

        List<FilledToolSlot> slots = stack.getOrDefault(CVADataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.getPart().isEmpty())
                return;
            renderedAnything = true;
            var part = slot.getPart().get().get();
            part.data.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        }

        if (!renderedAnything)
            renderer.renderSolid(model.getOriginalModel(), light);

    }
}
