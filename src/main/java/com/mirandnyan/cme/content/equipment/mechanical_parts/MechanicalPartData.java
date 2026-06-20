package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public abstract class MechanicalPartData {
    public final float weight;
    protected MechanicalPart parent;

    protected MechanicalPartData(float weight) {
        this.weight = weight;
    }

    void setParent(MechanicalPart parent) {
        this.parent = parent;
    }

    public void onInserted(ItemStack tool) { }
    public void onRemoved(ItemStack tool) { }

    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) { }

    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.renderSolid(part.models[0].get(), light);
    }

    public void playerTick(Player player, ItemStack stack) { }

    public boolean tryHandlingStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                       @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        return false;
    }

    public Optional<Boolean> overrideInsertingPart(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Player player, @NotNull SlotAccess access, @NotNull FilledToolSlot insertingPart) {
        return Optional.empty();
    }

    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
    }
    public boolean hasExtraTooltip(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                   @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        return false;
    }
    public void appendExtraTooltip(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                      @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {}

    public void brokeBlock(ServerPlayer player, ItemStack item, BlockEvent.BreakEvent event) { }

    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        return InteractionResult.PASS;
    }

    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return true;
    }

    public Component getHighlightTip(@NotNull ItemStack item, @NotNull Component displayName) {
        return displayName;
    }

    public boolean tryAbsorbDamage(@NotNull Player player, @NotNull ItemStack item, EquipmentSlot equipmentSlot, int amount) {
        return false;
    }

    public boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull BlockState state, int amount) {
        return false;
    }

    public boolean canAbsorbDurability(@NotNull ItemStack stack, @NotNull LivingEntity attacker, @NotNull Entity target, int amount) {
        return false;
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 0;
    }

    /** Only called if getUseDuration() > 0 */
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int duration) {
    }

    public boolean overridesBar(@NotNull ItemStack stack) {
        return false;
    }
    public int getBarWidth(@NotNull ItemStack stack) {
        return 0;
    }
    public int getBarColor(@NotNull ItemStack stack) {
        return 0;
    }
}
