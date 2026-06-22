package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin;

import com.mirandnyan.cme.CMEAttributes;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CoyoteTimeAttributeHandler {
    private static final String COYOTE_TIME_OVER = "coyoteTimeOver";
    private static final double COYOTE_TIME_DOWN_MOVEMENT = -0.001;
    private static final double COYOTE_TIME_MIN_SPEED_SQUARED = 0.01;
    public static Vec3 handleCoyoteTime(LivingEntity entity, Vec3 value) {
        var attribute = entity.getAttribute(CMEAttributes.COYOTE_TIME_ATTRIBUTE);
        if (attribute == null)
            return value;
        double coyoteDuration = attribute.getValue();
        if (coyoteDuration <= 0)
            return value;

        double influence = 0;
        if (entity.onGround()) {
            entity.getPersistentData().remove(COYOTE_TIME_OVER);
            influence = 1;
        }
        else {
            var currentTime = entity.level().getGameTime();
            var activeUpTo = entity.getPersistentData().getLong(COYOTE_TIME_OVER);

            if (activeUpTo == 0L) {
                var endTime = currentTime + (long) (20 * coyoteDuration);
                entity.getPersistentData().putLong(COYOTE_TIME_OVER, endTime);
                activeUpTo = endTime;
            }
            if (currentTime >= activeUpTo)
                return value;

            double remainingTimeSecs = (activeUpTo - currentTime) / 20.0f;
            influence =  remainingTimeSecs / coyoteDuration;
            influence = 1 - Math.pow(1 - influence, 3);

            if (value.y > 0.0001 || entity.isShiftKeyDown() || (entity.level().isClientSide && value.horizontalDistanceSqr() < COYOTE_TIME_MIN_SPEED_SQUARED)) {
                entity.getPersistentData().putLong(COYOTE_TIME_OVER, currentTime);
                influence = 0;
            }
        }
        return value.with(Direction.Axis.Y, Mth.lerp(Mth.clamp(influence, 0, 1), value.y, Math.max(value.y, COYOTE_TIME_DOWN_MOVEMENT)));
    }

    public static boolean inCoyoteTime(Entity entity) {
        var currentTime = entity.level().getGameTime();
        var activeUpTo = entity.getPersistentData().getLong(COYOTE_TIME_OVER);
        return currentTime < activeUpTo;
    }

    public static void endCoyoteTime(Entity entity) {
        if (!entity.getPersistentData().contains(COYOTE_TIME_OVER))
            return;
        var currentTime = entity.level().getGameTime();
        entity.getPersistentData().putLong(COYOTE_TIME_OVER, currentTime);
    }
}
