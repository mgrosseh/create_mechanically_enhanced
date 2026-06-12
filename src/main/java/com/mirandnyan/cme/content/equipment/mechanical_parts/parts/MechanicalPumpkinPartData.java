package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.AttributeHelpers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class MechanicalPumpkinPartData extends MechanicalPartData {
    // TODO: implementation
    private static int i = 0;

    private static final int PUMPKIN = i++;
    private static final int COG = i++;

    public MechanicalPumpkinPartData() {
        super(0.3f);
    }

    @Override
    public void onInserted(ItemStack tool) {
        MechanicalPartUtil.addEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.SILK_TOUCH), 1);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        MechanicalPartUtil.removeEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.SILK_TOUCH));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        //level.findSupportingBlock(); // TODO: mixin
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        ms.translate(0, 13 / 16f, -10 / 16f);

        // Cog
        ms.pushPose();
        float speedModifier = (float) (
                AttributeHelpers.calculateAttributeValue(stack, Attributes.MINING_EFFICIENCY, EquipmentSlot.MAINHAND)
                        * MechanicalPartUtil.MINING_EFFICIENCY_TO_COG_SPEED
        );

        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * speedModifier;
        angle %= 360;
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        renderer.renderSolid(part.models[COG].get(), light);
        ms.popPose();

        // Head
        renderer.renderSolid(part.models[PUMPKIN].get(), light);

        ms.popPose();
    }
}
