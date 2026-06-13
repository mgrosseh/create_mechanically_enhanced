package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mechanical_cat;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum MechanicalCatGiftType implements StringRepresentable {
    DOUBLE_DROPS(0, 3),
    EXPERIENCE(DOUBLE_DROPS.endWeight, 74),
    CASHBACK(EXPERIENCE.endWeight, 18),
    JACKPOT(CASHBACK.endWeight, 5);

    private final int startWeight;
    private final int endWeight;

    public static final Codec<MechanicalCatGiftType> CODEC = StringRepresentable.fromValues(MechanicalCatGiftType::values);
    public static final IntFunction<MechanicalCatGiftType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, MechanicalCatGiftType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    MechanicalCatGiftType(int start, int size) {
        this.startWeight = start;
        this.endWeight = start + size;
    }

    public static @NotNull MechanicalCatGiftType random(RandomSource random) {
        var values = values();
        var maxWeight = values[values.length - 1].endWeight;
        var weight = Mth.randomBetweenInclusive(random, 0, maxWeight - 1);
        for (var value : values) {
            if (value.startWeight <= weight && value.endWeight > weight)
                return value;
        }
        throw new IllegalStateException("Logic error: MechanicalCatGift generating random should always work.");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.toString().toLowerCase();
    }
}
