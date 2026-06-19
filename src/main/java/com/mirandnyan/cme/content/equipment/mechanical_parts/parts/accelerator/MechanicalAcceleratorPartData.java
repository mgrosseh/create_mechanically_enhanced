package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator;

import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class MechanicalAcceleratorPartData extends MechanicalPartData {

    private final AttributeModifier acceleratorBoostModifier;
    private final ItemAttributeModifiers.Entry acceleratorBoost;

    int speedModifier;
    protected MechanicalAcceleratorPartData(float weight, int speedModifier) {
        super(weight);
        this.speedModifier = speedModifier;
        acceleratorBoostModifier =
                new AttributeModifier(CreateMechanicallyEnhanced.asResource("accelerator_mining_boost"),
                        speedModifier, AttributeModifier.Operation.ADD_VALUE);
        acceleratorBoost = new ItemAttributeModifiers.Entry(
                Attributes.MINING_EFFICIENCY,
                acceleratorBoostModifier,
                EquipmentSlotGroup.MAINHAND
        );
    }
    public MechanicalAcceleratorPartData(int speedModifier) {
        this(0.0f, speedModifier);
    }

    @Override
    public void onInserted(ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .takeAll()
                        .add(acceleratorBoost)
                        .build()
        );
    }
    @Override
    public void onRemoved(ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .filter(e -> !e.equals(acceleratorBoost))
                        .build()
        );
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
    }
}
