package com.mirandnyan.cme.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin.CoyoteTimeAttributeHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin.CoyoteTimeAttributeHandler.handleCoyoteTime;

@Mixin(LivingEntity.class)
public abstract class CoyoteTimeMixin extends Entity {

    public CoyoteTimeMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "handleOnClimbable", at = @At(value = "RETURN"), cancellable = true)
    private void fakeClimbingCoyote(CallbackInfoReturnable<Vec3> cir) {
        cir.setReturnValue(handleCoyoteTime((LivingEntity) (Object) this, cir.getReturnValue()));
    }

    @WrapOperation(method = {"aiStep", "travel", "tick", "getFrictionInfluencedSpeed"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onGround()Z"))
    private boolean regularWalkSpeedInCoyoteTravel(LivingEntity instance, Operation<Boolean> original) {
        return original.call(instance) || CoyoteTimeAttributeHandler.inCoyoteTime(instance);
    }

}
