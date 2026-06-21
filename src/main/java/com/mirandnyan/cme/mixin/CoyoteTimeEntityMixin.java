package com.mirandnyan.cme.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin.CoyoteTimeAttributeHandler;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class CoyoteTimeEntityMixin {

    @WrapOperation(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z"))
    private boolean jumpInCoyote(Entity instance, Operation<Boolean> original) {
        return original.call(instance) || CoyoteTimeAttributeHandler.inCoyoteTime(instance);
    }

}
