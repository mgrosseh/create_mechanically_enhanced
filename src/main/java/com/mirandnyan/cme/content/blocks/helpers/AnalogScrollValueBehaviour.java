package com.mirandnyan.cme.content.blocks.helpers;

import com.google.common.collect.ImmutableList;
import com.mirandnyan.cme.CMETranslations;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class AnalogScrollValueBehaviour extends ScrollValueBehaviour {
    public AnalogScrollValueBehaviour(final Component label, final SmartBlockEntity be, final ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Override
    public ValueSettingsBoard createBoard(final Player player, final BlockHitResult hitResult) {
        return new ValueSettingsBoard(this.label, this.max, 15, ImmutableList.of(CMETranslations.ANALOG_SCROLL_VALUE.resolveComponent()),
                new ValueSettingsFormatter(ValueSettings::format));
    }
}
