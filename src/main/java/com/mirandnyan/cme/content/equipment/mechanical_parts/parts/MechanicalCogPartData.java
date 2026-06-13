package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class MechanicalCogPartData extends MechanicalPartData {

    private final AttributeModifier cogBoostModifier;
    private final ItemAttributeModifiers.Entry cogBoost;

    int speedModifier;
    public MechanicalCogPartData(int speedModifier) {
        super(0.1f);
        this.speedModifier = speedModifier;
        cogBoostModifier =
                new AttributeModifier(CreateMechanicallyEnhanced.asResource("cog_mining_boost"),
                        speedModifier, AttributeModifier.Operation.ADD_VALUE);
        cogBoost = new ItemAttributeModifiers.Entry(
                Attributes.MINING_EFFICIENCY,
                cogBoostModifier,
                EquipmentSlotGroup.MAINHAND
        );
    }

    @Override
    public void onInserted(ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .takeAll()
                        .add(cogBoost)
                        .build()
        );
    }
    @Override
    public void onRemoved(ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .filter(e -> !e.equals(cogBoost))
                        .build()
        );
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * (float) (speedModifier * MechanicalPartUtil.MINING_EFFICIENCY_TO_COG_SPEED);

        angle %= 360;


        ms.pushPose();
        ms.translate(0, 1 / 16f, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(angle));
        ms.translate(0, -1 / 16f, 0);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
