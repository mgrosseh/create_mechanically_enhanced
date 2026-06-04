package com.mirandnyan.mired.content.equipment.mechanical_mods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public abstract class MechanicalPartData {

    // TODO: make data component
    public int getTransferRatio() {
        return 0;
    }

    public void onInserted(ItemStack tool) { }
    public void onRemoved(ItemStack tool) { }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) { }

    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.renderSolid(part.model.get(), light);
    }

    public void playerTick(Player player, ItemStack stack) { }
}
