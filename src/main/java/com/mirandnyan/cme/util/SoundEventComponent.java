package com.mirandnyan.cme.util;

import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalToolSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record SoundEventComponent(@NotNull SoundEvent sound, @NotNull Vec3 position, float volume, float pitch) {

    public static final Codec<SoundEventComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            SoundEvent.DIRECT_CODEC.fieldOf("sound").forGetter(SoundEventComponent::sound),
            Vec3.CODEC.fieldOf("position").forGetter(SoundEventComponent::position),
            Codec.FLOAT.fieldOf("volume").forGetter(SoundEventComponent::volume),
            Codec.FLOAT.fieldOf("pitch").forGetter(SoundEventComponent::pitch)
    ).apply(i, SoundEventComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundEventComponent> STREAM_CODEC = StreamCodec.composite(
            SoundEvent.DIRECT_STREAM_CODEC, SoundEventComponent::sound,
            CodecHelpers.VEC3_STREAM_CODEC, SoundEventComponent::position,
            ByteBufCodecs.FLOAT, SoundEventComponent::volume,
            ByteBufCodecs.FLOAT, SoundEventComponent::pitch,
            SoundEventComponent::new
    );
}
