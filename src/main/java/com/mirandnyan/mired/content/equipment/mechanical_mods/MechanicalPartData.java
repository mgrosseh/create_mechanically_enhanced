package com.mirandnyan.mired.content.equipment.mechanical_mods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class MechanicalPartData {
    public final float weight;
    protected MechanicalPart parent;

    protected MechanicalPartData(float weight) {
        this.weight = weight;
    }

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

    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {

    }

    public void brokeBlock(Player player, ItemStack item, BlockEvent.BreakEvent event) { }
}
