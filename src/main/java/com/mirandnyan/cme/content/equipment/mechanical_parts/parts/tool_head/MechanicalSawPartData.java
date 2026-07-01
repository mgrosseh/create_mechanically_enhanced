package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head;

import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.SimpleMiningCheckPartData;
import com.mirandnyan.cme.util.neoforge_helpers.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import org.jetbrains.annotations.NotNull;


public class MechanicalSawPartData extends SimpleMiningCheckPartData {

    private static final int SAW_OFF = 0;
    private static final int SAW_ON = 1;

    @SuppressWarnings("FieldCanBeLocal") // can be used in quering player for this, so it is useful
    private final AttributeModifier sawDamageModifier;
    private final ItemAttributeModifiers.Entry sawDamage;

    Tool tool;

    public MechanicalSawPartData(float attackDamage, Tool toolProperties) {
        super(1.6f);
        sawDamageModifier = new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE);
        sawDamage = new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, sawDamageModifier, EquipmentSlotGroup.MAINHAND);
        this.tool = toolProperties;
    }

    @Override
    public void onInserted(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        tool.set(DataComponents.TOOL, this.tool);
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                .takeAll()
                .add(sawDamage)
                .build());
        super.onInserted(replaceSlot, tool);
    }

    @Override
    public void onRemoved(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
        tool.remove(DataComponents.TOOL);
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                .removing(sawDamage)
                .build());
        super.onRemoved(replaceSlot, tool);
    }

    @Override
    public Component getHighlightTip(@NotNull ItemStack item, @NotNull Component displayName) {
        return Component.empty().append(displayName).append(CMETranslations.MECHANICAL_TOOL_SAW_TYPE.resolveComponent());
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(part.models[getActive(stack, transformType) ? SAW_ON : SAW_OFF].get(), light);
    }
}
