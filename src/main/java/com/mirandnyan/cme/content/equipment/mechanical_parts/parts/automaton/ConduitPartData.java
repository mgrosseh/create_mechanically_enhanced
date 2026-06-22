package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConduitPartData extends MechanicalPartData {
    private static int i = 0;

    private static final int INACTIVE = i++;
    private static final int EYE = i++;
    private static final int SHELL_CLOSED = i++;


    public ConduitPartData() {
        super(0.3f);
    }

    protected void addEffects(LivingEntity player) {
        player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 20, 0, true, true));
    }

    protected boolean isActive(ItemStack stack) {
        return stack.has(CMEDataComponents.CONDUIT_AUTOMATON_ACTIVE);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (isSelected) {
            var active = entity.isInWaterRainOrBubble();

            if (active) {
                stack.set(CMEDataComponents.CONDUIT_AUTOMATON_ACTIVE, Unit.INSTANCE);
                if (entity instanceof LivingEntity living)
                    addEffects(living);
                ambientSounds(stack, entity, level);
                return;
            }
        }
        stack.remove(CMEDataComponents.CONDUIT_AUTOMATON_ACTIVE);
    }

    private long nextAmbientSoundActivation; // TODO
    protected void ambientSounds(ItemStack stack, Entity entity, Level level) {
        if (level.getGameTime() % 80L == 0L) {
            level.playSound(null, entity.position().x, entity.position().y + 1, entity.position().z, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 0.8F, 1.0F);
        }

        if (level.getGameTime() > nextAmbientSoundActivation) {
            nextAmbientSoundActivation = level.getGameTime() + 60L + (long)level.getRandom().nextInt(40);
            level.playSound(null, entity.position().x, entity.position().y + 1, entity.position().z, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var active = isActive(stack);
        if (!active) {
            renderer.renderSolid(part.models[INACTIVE].get(), light);
        }
        else {
            ms.pushPose();
            ms.mulPose(Axis.YP.rotationDegrees(180f));
            renderer.renderSolid(part.models[EYE].get(), light);
            ms.popPose();
            ms.pushPose();
            float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f;
            angle %= 360;
            ms.mulPose(Axis.YP.rotationDegrees(angle));
            renderer.render(part.models[SHELL_CLOSED].get(), light);
            ms.popPose();
        }
    }
}
