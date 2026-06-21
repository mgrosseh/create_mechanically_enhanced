package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton;

import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ConduitPartData extends MechanicalPartData {
    public ConduitPartData(float weight) {
        super(weight);
    }

    protected void addEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
    }
}
