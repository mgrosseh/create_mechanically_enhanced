package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.WeakHashMap;


public class MechanicalSawPartData extends MechanicalPartData {

    private static final int SAW_OFF = 0;
    private static final int SAW_ON = 1;

    @SuppressWarnings("FieldCanBeLocal") // can be used in quering player for this, so it is useful
    private final AttributeModifier sawDamageModifier;
    private final ItemAttributeModifiers.Entry sawDamage;

    private static class ClientData {
        int lastAir = 0;
        int lastDamage = 0;
        int active;
        static WeakHashMap<String, ClientData> clientData = new WeakHashMap<>();

        static ClientData of(String name) {
            return clientData.computeIfAbsent(name, s -> new ClientData());
        }

        static ClientData of(Player player) {
            return of(player.getName().getString());
        }
        static Optional<ClientData> of(ItemStack stack) {
            var name = stack.get(CMEDataComponents.LAST_TOOL_HOLDER_NAME);
            if (name == null)
                return Optional.empty();
            return Optional.of(of(name));
        }
    }

    Tool tool;

    public MechanicalSawPartData(float attackDamage, Tool toolProperties) {
        super(1.6f);
        sawDamageModifier = new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE);
        sawDamage = new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, sawDamageModifier, EquipmentSlotGroup.MAINHAND);
        this.tool = toolProperties;
    }

    @Override
    public void onInserted(ItemStack tool) {
        tool.set(DataComponents.TOOL, this.tool);
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                .takeAll()
                .add(sawDamage)
                .build());
        super.onInserted(tool);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        tool.remove(DataComponents.TOOL);
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                .removing(sawDamage)
                .build());
        super.onRemoved(tool);
    }

    @Override
    public void playerTick(Player player, ItemStack stack) {
        //noinspection DuplicatedCode
        if (!(player.level() instanceof ClientLevel clevel))
            return;

        var data = ClientData.of(player);


        var air = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        var damage = stack.getOrDefault(DataComponents.DAMAGE, 0);
        var mining = clevel.levelRenderer.destroyingBlocks.containsKey(player.getId())
                || air < data.lastAir
                || damage > data.lastDamage;
        data.lastAir = air;
        data.lastDamage = damage;
        if (mining)
            data.active = 20;

        if (data.active > 0) {
            data.active--;
        }
    }

    @Override
    public Component getHighlightTip(@NotNull ItemStack item, @NotNull Component displayName) {
        return Component.empty().append(displayName).append(CMETranslations.MECHANICAL_TOOL_SAW_TYPE.resolveComponent());
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var active = switch (transformType) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, // can only be mining in someone's hand
                 FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> ClientData.of(stack).map(d -> d.active).orElse(0);
            case NONE, HEAD, GUI, GROUND, FIXED -> 0;
        };
        ms.pushPose();
        renderer.render(part.models[active > 0 ? SAW_ON : SAW_OFF].get(), light);
        ms.popPose();
    }
}
