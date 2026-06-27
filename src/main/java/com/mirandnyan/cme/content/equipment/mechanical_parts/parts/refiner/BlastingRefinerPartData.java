package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.refiner;

import com.mirandnyan.cme.CMETags;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.SimpleMiningCheckPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BlastingRefinerPartData extends SimpleMiningCheckPartData {

    public static int i = 0;
    public static final int FANS = i++;
    public static final int FIRE_EFFECT = i++;

    // TODO: render some sort of particles
    // TODO: spawn particles to indicate burning up items

    public BlastingRefinerPartData(float weight) {
        super(weight);
    }

    protected boolean isImmuneToBurnUp(ItemStack stack) {
        return stack.has(DataComponents.FIRE_RESISTANT) || stack.is(CMETags.Items.BURN_UP_IMMUNE);
    }
    protected boolean isLikelyToBurnUp(ItemStack stack) {
        return stack.getItemHolder().getData(NeoForgeDataMaps.FURNACE_FUELS) != null || stack.is(CMETags.Items.BURN_UP_HIGH_LIKELIHOOD);

    }

    @Override
    public void blockDropEvent(FilledToolSlot.SlotId slot, ServerPlayer player, ItemStack item, BlockDropsEvent event) {
        ArrayList<ItemEntity> toAdd = new ArrayList<>();
        var level = event.getLevel();
        for (var drop : event.getDrops()) {
            var stack = drop.getItem();
            //noinspection DataFlowIssue // Is never null for BLASTING
            var result = AllFanProcessingTypes.BLASTING.process(stack, level).stream()
                    .map(s ->
                            new ItemEntity(level, drop.position().x, drop.position().y, drop.position().z, s,
                                    Mth.randomBetween(level.getRandom(), -0.05f, 0.05f),
                                    0.1, Mth.randomBetween(level.getRandom(), -0.05f, 0.05f)))
                    .collect(Collectors.toSet());
            toAdd.addAll(result);
            if (!result.isEmpty()) {
                spawnParticles(level, drop.position());
                drop.setItem(ItemStack.EMPTY);
                continue;
            }
            if (isImmuneToBurnUp(stack))
                continue;

            var chance = 0; // TODO config // disabled for now since it kinda sucks lol
            if(isLikelyToBurnUp(stack))
                chance = 60;
            if (Mth.randomBetweenInclusive(event.getLevel().random, 0, 100) >= chance)
                continue;
            drop.setItem(ItemStack.EMPTY);
            level.playSound(null, event.getPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.3f, 1.4f);
            spawnParticles(level, drop.position());
        }
        event.getDrops().addAll(toAdd);

        super.blockDropEvent(slot, player, item, event);
    }

    protected void spawnParticles(ServerLevel level, Vec3 pos) {
        spawnParticles(level, (float) pos.x, (float) pos.y, (float) pos.z);
    }
    protected void spawnParticles(ServerLevel level, float x, float y, float z) {
        for (int i = 0; i < 3; i++) {
            var mx = x + Mth.randomBetween(level.getRandom(), -0.1f, 0.1f);
            var my = y + 0.25f;
            var mz = z + Mth.randomBetween(level.getRandom(), -0.1f, 0.1f);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, mx, my, mz, 1, 0, 1 / 16f, 0, 0);
        }

    }

    @Override
    public void brokeBlock(ServerPlayer player, ItemStack item, BlockEvent.BreakEvent event) {
        var level = event.getLevel();
        for (int i = 0; i < 10; i++) {
            var x = event.getPos().getX() + Mth.randomBetween(level.getRandom(), -0.1f, 0.1f);
            var y = event.getPos().getY() + 0.25f;
            var z = event.getPos().getZ() + Mth.randomBetween(level.getRandom(), -0.1f, 0.1f);
            level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0, 1 / 16f, 0);
        }
    }

    @Override
    public boolean postHurtEnemy(FilledToolSlot.@NotNull SlotId part, @NotNull ItemStack stack, @NotNull LivingEntity attacker, @NotNull LivingEntity target) {
        target.setRemainingFireTicks(120);
        return false;
    }

    private void renderParticles() {
        // new Particle()
        //Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.LARGE_SMOKE, );
        //var x = new LargeSmokeParticle.Provider().createParticle()

    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.render(stack, part, renderer, transformType, ms, buffer, light, overlay);
        if (getActive(stack, transformType)) {
            renderParticles();
            renderer.render(part.models[FIRE_EFFECT].get(), light);
        }
    }
}
