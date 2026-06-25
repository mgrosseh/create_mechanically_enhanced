package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.cat.MechanicalCatBonusType;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.cat.MechanicalCatGiftType;
import com.mirandnyan.cme.content.items.cat_coin_die.CoinMintingItemComponent;
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
            "blaze_automaton_remaining_burning_time",
            builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
    );
    public static final DataComponentType<Unit> BLAZE_BURNING_SUPER = register(
            "blaze_automaton_burning_supercharged",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final DataComponentType<Unit> BLAZE_BURNING_INFINITE = register(
            "blaze_automaton_burning_infinite",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );


    public static final DataComponentType<Long> MECHANICAL_CAT_BONUS_BLOCKED = register(
            "cat_automaton_bonus_blocked",
            builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
    );
    public static final DataComponentType<MechanicalCatBonusType> MECHANICAL_CAT_BONUS = register(
            "cat_automaton_bonus",
            builder -> builder
                    .persistent(MechanicalCatBonusType.CODEC)
                    .networkSynchronized(MechanicalCatBonusType.STREAM_CODEC)
    );
    public static final DataComponentType<Unit> MECHANICAL_CAT_APPLY_BONUS = register(
            "cat_automaton_apply_bonus",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final DataComponentType<MechanicalCatGiftType> MECHANICAL_CAT_GIVE_GIFT = register(
            "cat_automaton_give_gift",
            builder -> builder.persistent(MechanicalCatGiftType.CODEC).networkSynchronized(MechanicalCatGiftType.STREAM_CODEC)
    );


    public static final DataComponentType<Unit> EXPLOSION_IMMUNE = register(
            "explosion_resistant",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );

    public static final DataComponentType<CoinMintingItemComponent> COIN_MINTING_ITEM = register(
            "coin_minting_item",
            builder -> builder.persistent(CoinMintingItemComponent.CODEC).networkSynchronized(CoinMintingItemComponent.STREAM_CODEC)
    );

    public static final DataComponentType<Unit> CONDUIT_AUTOMATON_ACTIVE = register(
            "conduit_automaton_active",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
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
