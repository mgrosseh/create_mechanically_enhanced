package com.mirandnyan.cme.util;

import net.minecraft.util.Mth;
import net.neoforged.bus.api.IEventBus;

public class RenderHandler {
    protected float leftHandAnimation;
    protected float rightHandAnimation;
    protected float lastLeftHandAnimation;
    protected float lastRightHandAnimation;

    public void tick() {
        lastLeftHandAnimation = leftHandAnimation;
        lastRightHandAnimation = rightHandAnimation;
        leftHandAnimation *= animationDecay();
        rightHandAnimation *= animationDecay();
    }

    public float getAnimation(boolean rightHand, float partialTicks) {
        return Mth.lerp(partialTicks, rightHand ? lastRightHandAnimation : lastLeftHandAnimation,
                rightHand ? rightHandAnimation : leftHandAnimation);
    }

    protected float animationDecay() {
        return 0.8f;
    }

    public void registerListeners(IEventBus modEventBus) { }
}
