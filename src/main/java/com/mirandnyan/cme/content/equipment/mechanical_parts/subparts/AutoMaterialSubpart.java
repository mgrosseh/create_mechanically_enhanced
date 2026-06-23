package com.mirandnyan.cme.content.equipment.mechanical_parts.subparts;

import com.mirandnyan.cme.CMEMaterials;
import com.mirandnyan.cme.CMEMechanicalParts;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.*;
import com.mirandnyan.cme.util.java_helpers.VarArgs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class AutoMaterialSubpart extends MechanicalSubpart {

    public final HashMap<ResourceKey<CMEMaterial>, PartialModel> casings;

    protected AutoMaterialSubpart(HashMap<ResourceKey<CMEMaterial>, PartialModel> casings) {
        this.casings = casings;
    }

    protected ResourceKey<CMEMaterial> getMaterial(FilledToolSlot partSlot) {
        return partSlot.parent().map(p -> CMEMechanicalParts.get(p).get().material).orElse(null);
    }

    protected PartialModel currentCasing(FilledToolSlot partSlot) {
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
    }

    public static class GenericBuilder extends Builder<AutoMaterialSubpart, GenericBuilder> {
        public GenericBuilder(String... sharedSubpath) {
            this(CreateMechanicallyEnhanced.MOD_ID, sharedSubpath);
        }
        protected GenericBuilder(String mod_id, String[] sharedSubpath) {
            super(mod_id, AutoMaterialSubpart::new, sharedSubpath);
        }
        public static GenericBuilder withMod(String mod_id, String... sharedSubpath) {
            return new GenericBuilder(mod_id, sharedSubpath);
        }

        @Override
        protected GenericBuilder self() {
            return this;
        }
    }

    public static abstract class Builder<T extends AutoMaterialSubpart, B extends Builder<T, B>> {
        protected final String sharedSubpath;
        protected final HashMap<ResourceKey<CMEMaterial>, PartialModel> casings = new HashMap<>();
        protected final Function<HashMap<ResourceKey<CMEMaterial>, PartialModel>, T> factory;
        protected final String mod_id;

        protected Builder(Function<HashMap<ResourceKey<CMEMaterial>, PartialModel>, T> factory, String... sharedSubpath) {
            this(CreateMechanicallyEnhanced.MOD_ID, factory, sharedSubpath);
        }
        protected Builder(String mod_id, Function<HashMap<ResourceKey<CMEMaterial>, PartialModel>, T> factory, String... sharedSubpath) {
            this.mod_id = mod_id;
            this.factory = factory;
            this.sharedSubpath = String.join("/", sharedSubpath);
        }
        protected Builder(String... sharedSubpath) {
            this(CreateMechanicallyEnhanced.MOD_ID, sharedSubpath);
        }
        protected Builder(String mod_id, String... sharedSubpath) {
            this(mod_id, null, sharedSubpath);
        }

        public B casing(ResourceKey<CMEMaterial> material, String... location) {
            casings.put(material, PartialModel.of(resource(VarArgs.of(sharedSubpath).and(location).toArray())));
            return this.self();
        }

        protected abstract B self();

        public T build() {
            return factory.apply(casings);
        }

        protected ResourceLocation resource(String... pathParts) {
            return ResourceLocation.fromNamespaceAndPath(mod_id,
                    MechanicalPartBuilder.MECHANICAL_PART_LOCATION_PREFIX + "/" + String.join("/", pathParts));
        }
    }
}
