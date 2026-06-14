package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mechanical_cat.MechanicalCatBonusType;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mechanical_cat.MechanicalCatGiftType;
import com.mirandnyan.cme.util.SoundEventComponent;
import com.mojang.serialization.Codec;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.UnaryOperator;

public class CMEDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateMechanicallyEnhanced.MOD_ID);

    public static final DataComponentType<List<FilledToolSlot>> TOOL_SLOTS_COMPONENT_TYPE = register(
            "tool_slots",
builder -> builder.persistent(FilledToolSlot.CODEC.listOf()).networkSynchronized(CatnipStreamCodecBuilders.list(FilledToolSlot.STREAM_CODEC))
    );


    public static final DataComponentType<Integer> AIR_TRANSFER_RATIO = register(
            "air_transfer_ratio",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DataComponentType<Integer> PRESSURIZED_AIR = register(
      "pressurized_air",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DataComponentType<Integer> PRESSURIZED_AIR_CAPACITY = register(
            "pressurized_air_capacity",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DataComponentType<Long> BLAZE_BURNING_TIME = register(
            "mechanical_blaze_remaining_burning_time",
            builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
    );
    public static final DataComponentType<Unit> BLAZE_BURNING_SUPER = register(
            "mechanical_blaze_burning_supercharged",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final DataComponentType<Unit> BLAZE_BURNING_INFINITE = register(
            "mechanical_blaze_burning_infinite",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );


    public static final DataComponentType<Long> MECHANICAL_CAT_BONUS_BLOCKED = register(
            "mechanical_cat_bonus_blocked",
            builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
    );
    public static final DataComponentType<MechanicalCatBonusType> MECHANICAL_CAT_BONUS = register(
            "mechanical_cat_bonus",
            builder -> builder
                    .persistent(MechanicalCatBonusType.CODEC)
                    .networkSynchronized(MechanicalCatBonusType.STREAM_CODEC)
    );
    public static final DataComponentType<Unit> MECHANICAL_CAT_APPLY_BONUS = register(
            "mechanical_cat_apply_bonus",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final DataComponentType<MechanicalCatGiftType> MECHANICAL_CAT_GIVE_GIFT = register(
            "mechanical_cat_give_gift",
            builder -> builder.persistent(MechanicalCatGiftType.CODEC).networkSynchronized(MechanicalCatGiftType.STREAM_CODEC)
    );

    public static final DataComponentType<String> LAST_TOOL_HOLDER_NAME = register(
            "last_tool_holder_name",
            builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );

    public static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
