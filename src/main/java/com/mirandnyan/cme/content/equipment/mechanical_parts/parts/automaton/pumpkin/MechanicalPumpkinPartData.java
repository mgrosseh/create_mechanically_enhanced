package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin;

import com.mirandnyan.cme.CMEAttributes;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.MechanicalPartUtil;
import com.mirandnyan.cme.util.AttributeHelpers;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

public class MechanicalPumpkinPartData extends MechanicalPartData {
    // TODO: implementation
    private static int i = 0;

    private static final int PUMPKIN = i++;
    private static final int COG = i++;

    // TODO: on hit enemy: make them levitate shortly
    // TODO: maybe catch once every few mins when taking fall damage

    private final ItemAttributeModifiers.Entry pumpkinAttribute;

    public MechanicalPumpkinPartData() {
        super(0.3f);

        AttributeModifier acceleratorBoostModifier =
                new AttributeModifier(CreateMechanicallyEnhanced.asResource("pumpkin_coyote_time_boost"),
                        3, AttributeModifier.Operation.ADD_VALUE);
        pumpkinAttribute = new ItemAttributeModifiers.Entry(
                CMEAttributes.COYOTE_TIME_ATTRIBUTE,
                acceleratorBoostModifier,
                EquipmentSlotGroup.MAINHAND
        );
    }

    @Override
    public void onInserted(ItemStack tool) {
        MechanicalPartUtil.addEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.SILK_TOUCH), 1);
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .adding(pumpkinAttribute)
                        .build()
        );
    }

    @Override
    public void onRemoved(ItemStack tool) {
        MechanicalPartUtil.removeEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.SILK_TOUCH));
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS,
                new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                        .removing(pumpkinAttribute)
                        .build()
        );
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        //level.findSupportingBlock()
        //level.findSupportingBlock(); // TODO: mixin
        //entity.getPersistentData().
//        if (isSelected) {
//            entity.getPersistentData().putBoolean(PUMPKIN_ACTIVE, true);
//        }
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();

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
