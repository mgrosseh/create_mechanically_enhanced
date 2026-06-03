package com.mirandnyan.mired.content.equipment.mechanical_mods;

import com.mirandnyan.mired.CVADataComponents;
import com.mirandnyan.mired.CVAItems;
import com.mirandnyan.mired.CVATranslations;
import com.mirandnyan.mired.content.equipment.MechanicalTool;
import com.mirandnyan.mired.content.equipment.mechanical_drill.MechanicalDrill;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.mirandnyan.mired.CreateVariousAdditions.REGISTRATE;

public class MechanicalPart {
    public static final ResourceKey<Registry<MechanicalPart>> REGISTRY =
            REGISTRATE.makeRegistry("mechanical_part", RegistryBuilder::new);

    public static final Codec<ResourceKey<MechanicalPart>> CODEC = ResourceKey.codec(REGISTRY);
    public static final StreamCodec<ByteBuf, ResourceKey<MechanicalPart>> STREAM_CODEC = ResourceKey.streamCodec(REGISTRY);

    public final MechanicalPartData data;
    public final @NotNull ResourceKey<Item> validItem;
    public final @NotNull ResourceKey<MechanicalToolSlot> validSlot;

    protected CVATranslations.LangEntry lang;

    protected MechanicalPart(@NotNull ResourceKey<MechanicalToolSlot> validSlot,
                             @NotNull ResourceKey<Item> validItem,
                             MechanicalPartData data) {
        this.validSlot = validSlot;
        this.validItem = validItem;
        this.data = data;
    }

    public boolean isItem(Item item) {
        return getItem().is(item);
    }

    public RegistryEntry<Item, Item> getItem() {
        return REGISTRATE.get(validItem.location().getPath(), validItem.registryKey());
    }
    public RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> getSlot() {
        return REGISTRATE.get(validSlot.location().getPath(), validSlot.registryKey());
    }

    // STATIC PARTS

    // TODO: maybe make them with transform(Item, MechanicalToolSlot, apply) out of Items
    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_TANK = register("copper_tank",
            MechanicalToolSlot.TANK_SLOT,
            CVAItems.SMALL_COPPER_TANK,
            new MechanicalTankPartData(800)
    );
    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_TANK = register("netherite_tank",
            MechanicalToolSlot.TANK_SLOT,
            CVAItems.SMALL_NETHERITE_TANK,
            new MechanicalTankPartData(1200)
    );

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_GEARBOX = register("andesite_gearbox",
            MechanicalToolSlot.GEARBOX_SLOT,
            CVAItems.SMALL_ANDESITE_VERTICAL_GEARBOX,
            new MechanicalPartData() {
                @Override
                public int getTransferRatio() {
                    return MechanicalDrill.DEFAULT_TRANSFER_RATIO;
                }
            }
    );
    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_GEARBOX = register("brass_gearbox",
            MechanicalToolSlot.GEARBOX_SLOT,
            CVAItems.SMALL_BRASS_VERTICAL_GEARBOX,
            new MechanicalPartData() {
                @Override
                public int getTransferRatio() {
                    return MechanicalDrill.DEFAULT_TRANSFER_RATIO / 2;
                }
            }
    );


    public static Stream<RegistryEntry<MechanicalPart, MechanicalPart>> getAll(Predicate<? super RegistryEntry<MechanicalPart, MechanicalPart>> filter) {
        return REGISTRATE.getAll(REGISTRY).stream().filter(filter);
    }
    public static Optional<RegistryEntry<MechanicalPart, MechanicalPart>> getOfItem(Item item) {
        return MechanicalPart.getAll(part -> part.get().isItem(item)).findAny();
    }
    public static RegistryEntry<MechanicalPart, MechanicalPart> get(ResourceKey<MechanicalPart> part) {
        return REGISTRATE.get(part.location().getPath(), part.registryKey());
    }

    protected static RegistryEntry<MechanicalPart, MechanicalPart> register(
            String name,
            RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> validSlot,
            ItemEntry<?> validItem, MechanicalPartData data) {
        return REGISTRATE.object(name).simple(REGISTRY, () -> new MechanicalPart(validSlot.getKey(), validItem.getKey(), data));
    }

    @ApiStatus.Internal
    public static void register() {
        // load this class
    }
}
