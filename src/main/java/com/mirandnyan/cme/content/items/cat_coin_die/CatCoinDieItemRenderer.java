package com.mirandnyan.cme.content.items.cat_coin_die;

import com.mirandnyan.cme.CMEDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CatCoinDieItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        LocalPlayer player = mc.player;
        float partialTicks = AnimationTickHolder.getPartialTicks();

        boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean firstPerson = leftHand || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

        ms.pushPose();

        if (stack.has(CMEDataComponents.COIN_MINTING_ITEM)) {
            ms.pushPose();

            if (transformType == ItemDisplayContext.GUI) {
                ms.translate(0.0F, .2f, 1.0F);
                ms.scale(.75f, .75f, .75f);
            } else {
                int modifier = leftHand ? -1 : 1;
                ms.mulPose(Axis.YP.rotationDegrees(modifier * 40));
            }

            // Reverse bobbing
            float time = (float) (player == null ? 0 : player.getUseItemRemainingTicks());
            if (time / (float) (player == null ? 0 : stack.getUseDuration(player)) < 0.8F) {
                float bobbing = -Mth.abs(Mth.cos(time / 4.0F * (float) Math.PI) * 0.1F);

                if (transformType == ItemDisplayContext.GUI)
                    ms.translate(bobbing, bobbing, 0.0F);
                else
                    ms.translate(0.0f, bobbing, 0.0F);
            }

            //noinspection DataFlowIssue // since inside of guard
            ItemStack toPolish = stack.get(CMEDataComponents.COIN_MINTING_ITEM).item();
            itemRenderer.renderStatic(toPolish, ItemDisplayContext.GUI, light, overlay, ms, buffer, mc.level, 0);

            ms.popPose();
        }

        if (firstPerson) {
            int itemInUseCount = player == null ? 0 : player.getUseItemRemainingTicks();
            if (itemInUseCount > 0) {
                int modifier = leftHand ? -1 : 1;
                ms.translate(modifier * .5f, 0, -.25f);
                ms.mulPose(Axis.ZP.rotationDegrees(modifier * 40));
                ms.mulPose(Axis.XP.rotationDegrees(modifier * 10));
                ms.mulPose(Axis.YP.rotationDegrees(modifier * 90));
            }
        }

        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());

        ms.popPose();
    }

}
