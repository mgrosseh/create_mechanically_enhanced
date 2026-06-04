package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.PhysicalFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;

import java.util.WeakHashMap;


public class MechanicalDrillPartData extends MechanicalPartData {


    private static class ClientData {
        PhysicalFloat pAngle = new PhysicalFloat(1).withDrag(0.1);
        static WeakHashMap<String, ClientData> clientData = new WeakHashMap<>();

        static ClientData of(String name) {
            return clientData.computeIfAbsent(name, s -> new ClientData());
        }
    }

    Tool tool;

    public MechanicalDrillPartData(Tool toolProperties) {
        this.tool = toolProperties;
    }

    @Override
    public void onInserted(ItemStack tool) {
        tool.set(DataComponents.TOOL, this.tool);
        super.onInserted(tool);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        tool.remove(DataComponents.TOOL);
        super.onRemoved(tool);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player) || !isSelected)
            return;
        // TODO: use ID
        var name = player.getName().getString();
        stack.set(CMEDataComponents.LAST_TOOL_HOLDER_NAME, name);
    }


    @Override
    public void playerTick(Player player, ItemStack stack) {
        if (!(player.level() instanceof ClientLevel clevel))
            return;

        var data = ClientData.of(player.getName().getString());
        if (clevel.levelRenderer.destroyingBlocks.containsKey(player.getId())) {
            data.pAngle.bump(5.5);
        }

        data.pAngle.tick();
    }

    private float getAngle(ItemStack stack) {
        var name = stack.get(CMEDataComponents.LAST_TOOL_HOLDER_NAME);
        if (name == null)
            return 0f;
        return ClientData.of(name).pAngle.getValue(AnimationTickHolder.getPartialTicks()) % 360;
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var angle = switch (transformType) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, // can only be mining in someone's hand
                 FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> getAngle(stack);
            case NONE, HEAD, GUI, GROUND, FIXED -> 0;
        };
        ms.pushPose();
        ms.translate(0, 1 / 16f, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(angle));
        ms.translate(0, -1 / 16f, 0);
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }
}
