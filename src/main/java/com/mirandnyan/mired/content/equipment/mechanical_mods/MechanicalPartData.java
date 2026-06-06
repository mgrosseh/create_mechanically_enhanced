package com.mirandnyan.mired.content.equipment.mechanical_mods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public abstract class MechanicalPartData {
    protected MechanicalPart parent;

    void setParent(MechanicalPart parent) {
        this.parent = parent;
    }

    // TODO: make data component
    public int getTransferRatio() {
        return 0;
    }

    public void onInserted(ItemStack tool) { }
    public void onRemoved(ItemStack tool) { }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) { }

    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.renderSolid(part.models[0].get(), light);
    }

    public void playerTick(Player player, ItemStack stack) { }

    public boolean tryHandlingStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                       @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        return false;
    }
}
