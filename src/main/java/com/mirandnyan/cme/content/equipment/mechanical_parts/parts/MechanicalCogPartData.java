package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.neoforge_helpers.ItemAttributeModifiersRebuilder;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class MechanicalCogPartData extends MechanicalPartData {

    private final ItemAttributeModifiers.Entry cogBoost;

    Vector3f rotationOffset;
    int speedModifier;
    public MechanicalCogPartData(int speedModifier) {
        this(speedModifier, new Vector3f(0f, 0f, 0f));
    }
    public MechanicalCogPartData(int speedModifier, Vector3f rotationOffset) {
        this(0.1f, speedModifier, rotationOffset);
    }
    public MechanicalCogPartData(float weight, int speedModifier, Vector3f rotationOffset) {
        super(weight);
        this.rotationOffset = rotationOffset;
        this.speedModifier = speedModifier;
        AttributeModifier cogBoostModifier = new AttributeModifier(CreateMechanicallyEnhanced.asResource("cog_mining_boost"),
                speedModifier, AttributeModifier.Operation.ADD_VALUE);
        cogBoost = new ItemAttributeModifiers.Entry(
                Attributes.MINING_EFFICIENCY,
                cogBoostModifier,
                EquipmentSlotGroup.MAINHAND
        );
    }

    @Override
    public void onInserted(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .takeAll()
                        .add(cogBoost)
                        .build()
        );
    }
    @Override
    public void onRemoved(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .filter(e -> !e.equals(cogBoost))
                        .build()
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * (float) (speedModifier * MechanicalPartUtil.MINING_EFFICIENCY_TO_COG_SPEED);

        angle %= 360;

        ms.pushPose();
        ms.rotateAround(Axis.ZP.rotationDegrees(angle), rotationOffset.x / 16f, rotationOffset.y / 16f, rotationOffset.z / 16f);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
