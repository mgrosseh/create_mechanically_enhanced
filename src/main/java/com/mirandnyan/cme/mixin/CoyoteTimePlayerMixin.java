package com.mirandnyan.cme.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin.CoyoteTimeAttributeHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class CoyoteTimePlayerMixin extends LivingEntity {

    protected CoyoteTimePlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
    private boolean jumpInCoyote(Player instance, Operation<Boolean> original) {
        return original.call(instance) || CoyoteTimeAttributeHandler.inCoyoteTime(instance);
    }

}
