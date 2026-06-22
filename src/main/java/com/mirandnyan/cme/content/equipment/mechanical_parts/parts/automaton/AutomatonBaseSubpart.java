package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton;

import com.mirandnyan.cme.CMEMaterials;
import com.mirandnyan.cme.CMEMechanicalParts;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.*;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.MechanicalPartUtil;
import com.mirandnyan.cme.util.AttributeHelpers;
import com.mirandnyan.cme.util.java_helpers.VarArgs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public final class AutomatonBaseSubpart extends MechanicalSubpart {
    public final HashMap<ResourceKey<CMEMaterial>, PartialModel> casings;
    public final HashMap<ResourceKey<CMEMaterial>, PartialModel> cogs;

    private AutomatonBaseSubpart(HashMap<ResourceKey<CMEMaterial>, PartialModel> casings, HashMap<ResourceKey<CMEMaterial>, PartialModel> cogs) {
        this.casings = casings;
        this.cogs = cogs;
    }

    private ResourceKey<CMEMaterial> getMaterial(FilledToolSlot partSlot) {
        return partSlot.parent().map(p -> CMEMechanicalParts.get(p).get().material).orElse(null);
    }

    private PartialModel currentCog(FilledToolSlot partSlot) {
        var material = getMaterial(partSlot);
        if (!cogs.containsKey(material))
            material = CMEMaterials.NETHERITE.getKey();
        return cogs.get(material);
    }
    private PartialModel currentCasing(FilledToolSlot partSlot) {
        var material = getMaterial(partSlot);
        if (!casings.containsKey(material))
            material = CMEMaterials.ANDESITE.getKey();
        return casings.get(material);
    }

    @Override
    public void render(ItemStack stack, FilledToolSlot filledToolSlot, List<FilledToolSlot> filledToolSlots, MechanicalPart part,
                       MechanicalSubpart subpart, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.renderSolid(currentCasing(filledToolSlot).get(), light);

        ms.pushPose();
        float speedModifier = (float) (
                AttributeHelpers.calculateAttributeValue(stack, Attributes.MINING_EFFICIENCY, EquipmentSlot.MAINHAND)
                        * MechanicalPartUtil.MINING_EFFICIENCY_TO_COG_SPEED
        );

        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * speedModifier;
        angle %= 360;
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        renderer.renderSolid(currentCog(filledToolSlot).get(), light);
        ms.popPose();
    }

    public static class Builder {
        private final String sharedSubpath;
        private final HashMap<ResourceKey<CMEMaterial>, PartialModel> casings = new HashMap<>();
        private final HashMap<ResourceKey<CMEMaterial>, PartialModel> cogs = new HashMap<>();

        public Builder(String... sharedSubpath) {
            this.sharedSubpath = String.join("/", sharedSubpath);
        }

        public Builder casing(ResourceKey<CMEMaterial> material, String... location) {
            casings.put(material, PartialModel.of(resource(VarArgs.of(sharedSubpath).and(location).toArray())));
            return this;
        }
        public Builder cog(ResourceKey<CMEMaterial> material, String... location) {
            cogs.put(material, PartialModel.of(resource(VarArgs.of(sharedSubpath).and(location).toArray())));
            return this;
        }

        public AutomatonBaseSubpart build() {
            return new AutomatonBaseSubpart(casings, cogs);
        }

        private ResourceLocation resource(String... pathParts) {
            return CreateMechanicallyEnhanced.asResource(
                    MechanicalPartBuilder.MECHANICAL_PART_LOCATION_PREFIX + "/" + String.join("/", pathParts));
        }
    }
}
