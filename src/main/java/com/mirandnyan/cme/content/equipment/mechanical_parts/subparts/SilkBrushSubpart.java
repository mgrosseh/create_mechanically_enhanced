package com.mirandnyan.cme.content.equipment.mechanical_parts.subparts;

import com.mirandnyan.cme.content.equipment.mechanical_parts.CMEMaterial;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalSubpart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.SimpleMiningCheckPartData;
import com.mirandnyan.cme.util.math.AffineTransform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public final class SilkBrushSubpart extends AutoMaterialSubpart {

    private final Function<Float, AffineTransform> origin;
    private SilkBrushSubpart(HashMap<ResourceKey<CMEMaterial>, PartialModel> casings, Function<Float, AffineTransform> origin) {
        super(casings);
        this.origin = origin;
    }

    @Override
    public void render(ItemStack stack, FilledToolSlot filledToolSlot, List<FilledToolSlot> filledToolSlots, MechanicalPart part,
                       MechanicalSubpart subpart, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();

        float angle = 0;
        if (part.data instanceof SimpleMiningCheckPartData data && data.getActive(stack, transformType)) {
            float animTime = Math.abs((AnimationTickHolder.getRenderTime() * 0.1f) % 2f - 1);
            angle = Mth.lerp(animTime, -40f, 40f);
        }

        // TODO: investigate bad rotate?
        //var offset = new Vector3f(8f, 6.1f, 0.4f);

//        var affine = new AffineTransform()
//                .translateBack(2, 2, 8)
//                //.mul(new AffineTransform().rotate(Axis.YP.rotationDegrees(angle)))
//                //.rotate(Axis.YP.rotationDegrees(angle))
//                .rotateAround(Axis.YP.rotationDegrees(angle), 2, 2, 8)
//                .rotateAround(Axis.XP.rotationDegrees(30), 2, 2, 8)
//                .rotateAround(Axis.ZP.rotationDegrees(45), 2, 2, 8)
//                .scale(0.7f)
//                .translate(offset)
//                .convertToBlockSpace();
//        affine.apply(ms);
        origin.apply(angle).apply(ms);
        renderer.render(currentCasing(filledToolSlot).get(), light);
        ms.popPose();
    }

    public static class Builder extends AutoMaterialSubpart.Builder<SilkBrushSubpart, Builder> {

        private Function<Float, AffineTransform> origin;

        public Builder(String... sharedSubpath) {
            super(sharedSubpath);
        }

        public Builder origin(Function<Float, AffineTransform> origin) {
            this.origin = origin;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public SilkBrushSubpart withOrigin(Function<Float, AffineTransform> origin) {
            return new SilkBrushSubpart(casings, origin);
        }

        @Override
        public SilkBrushSubpart build() {
            return new SilkBrushSubpart(casings, origin);
        }
    }
}
