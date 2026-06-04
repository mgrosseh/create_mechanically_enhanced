package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import com.simibubi.create.api.registry.CreateDataMaps;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MechanicalBlazePartData extends MechanicalPartData {

    @Override
    public boolean tryHandlingStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                          @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (AllItems.CREATIVE_BLAZE_CAKE.isIn(other)) {
            // TODO
        }
        if (stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE))
            return false;

        Holder<Item> holder = other.getItemHolder();
        BlazeBurnerFuel superheatedFuel = holder.getData(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS);
        BlazeBurnerFuel normalFuel = holder.getData(CreateDataMaps.REGULAR_BLAZE_BURNER_FUELS);

        if (superheatedFuel != null) {
            stack.set(CMEDataComponents.BLAZE_BURNING_TIME, superheatedFuel.burnTime());
            stack.set(CMEDataComponents.BLAZE_BURNING_SUPER, Unit.INSTANCE);

            if (!player.isCreative())
                other.shrink(1);
            return true;
        }
        if (stack.has(CMEDataComponents.BLAZE_BURNING_SUPER))
            return false;


        if (normalFuel != null) {
            var time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, 0);
            stack.set(CMEDataComponents.BLAZE_BURNING_TIME, time + normalFuel.burnTime());

            if (!player.isCreative())
                other.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        var infinite = stack.has(CMEDataComponents.BLAZE_BURNING_TIME);
        if (infinite)
            return;

        var time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, 0);
        var superheated = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);

        // TODO



    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(180));
        ms.translate(0, 13 / 16f, 10 / 16f);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
