package com.mirandnyan.mired.content.equipment.mechanical_parts.parts.mechanical_cat;

import com.mirandnyan.mired.CMEMobEffects;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public enum MechanicalCatBonusType implements StringRepresentable {
    NONE(-1, 0),
    FORTUNE(2, 60 * 20, 4),
    GIFTS(2, 60 * 20, 10),
    GLOWING(0, 8 * 20, MobEffects.GLOWING, 0),
    HASTE(1, 30 * 20, MobEffects.DIG_SPEED, 0),
    HUNGER_REGEN(1, 10 * 20, CMEMobEffects.HUNGER_REGENERATION, 3),
    BLOCK_INTERACTION_RANGE(1, 30 * 20, CMEMobEffects.RANGE_BLESSING, 0);

    public static final Codec<MechanicalCatBonusType> CODEC = StringRepresentable.fromValues(MechanicalCatBonusType::values);
    public static final IntFunction<MechanicalCatBonusType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, MechanicalCatBonusType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    public final int amplitude;
    public final @Nullable Holder<MobEffect> mobEffect;
    public final int value;
    public final int duration;

    MechanicalCatBonusType(int value, int duration) {
        this(value, duration, 0);
    }
    MechanicalCatBonusType(int value, int duration, int amplitude) {
        mobEffect = null;
        this.amplitude = amplitude;
        this.value = value;
        this.duration = duration;
    }
    MechanicalCatBonusType(int value, int duration, @NotNull Holder<MobEffect> effect, int amplitude) {
        this.mobEffect = effect;
        this.amplitude = amplitude;
        this.value = value;
        this.duration = duration;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.toString().toLowerCase();
    }
}
