package com.mirandnyan.mired.content.equipment.mechanical_mods;

import com.mirandnyan.mired.*;
import com.mirandnyan.mired.content.equipment.mechanical_drill.MechanicalDrillItem;
import com.mirandnyan.mired.content.equipment.mechanical_mods.parts.MechanicalCogPartData;
import com.mirandnyan.mired.content.equipment.mechanical_mods.parts.MechanicalDrillPartData;
import com.mirandnyan.mired.content.equipment.mechanical_mods.parts.MechanicalTankPartData;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.mirandnyan.mired.CreateMechanicallyEnhanced.REGISTRATE;

public class MechanicalPart {
    public static final ResourceKey<Registry<MechanicalPart>> REGISTRY =
            REGISTRATE.makeRegistry("mechanical_part", RegistryBuilder::new);

    public static final Codec<ResourceKey<MechanicalPart>> CODEC = ResourceKey.codec(REGISTRY);
    public static final StreamCodec<ByteBuf, ResourceKey<MechanicalPart>> STREAM_CODEC = ResourceKey.streamCodec(REGISTRY);

    public final MechanicalPartData data;
    public final @NotNull ResourceKey<Item> validItem;
    public final @NotNull ResourceKey<MechanicalToolSlot> validSlot;

    public final @NotNull String name;

    public final @NotNull PartialModel model;

    protected CMETranslations.LangEntry lang;

    protected MechanicalPart(@NotNull ResourceKey<MechanicalToolSlot> validSlot,
                             @NotNull ResourceKey<Item> validItem,
                             MechanicalPartData data,
                             @NotNull ResourceLocation model,
                             @NotNull String name) {
        this.name = name;
        this.validSlot = validSlot;
        this.validItem = validItem;
        this.data = data;
        this.model = PartialModel.of(model);
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

    @Override
    public String toString() {
        return "Part{" + name + "}";
    }

    // STATIC PARTS

    // TODO: maybe make them with transform(Item, MechanicalToolSlot, apply) out of Items
    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_TANK = register("copper_tank",
            MechanicalToolSlot.TANK_SLOT,
            CMEItems.SMALL_COPPER_TANK,
            new MechanicalTankPartData(800)
    );
    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_TANK = register("netherite_tank",
            MechanicalToolSlot.TANK_SLOT,
            CMEItems.SMALL_NETHERITE_TANK,
            new MechanicalTankPartData(1200)
    );

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_GEARBOX = register("andesite_gearbox",
            MechanicalToolSlot.GEARBOX_SLOT,
            CMEItems.SMALL_ANDESITE_VERTICAL_GEARBOX,
            new MechanicalPartData() {
                @Override
                public int getTransferRatio() {
                    return MechanicalDrillItem.DEFAULT_TRANSFER_RATIO;
                }
            }
    );
    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_GEARBOX = register("brass_gearbox",
            MechanicalToolSlot.GEARBOX_SLOT,
            CMEItems.SMALL_BRASS_VERTICAL_GEARBOX,
            new MechanicalPartData() {
                @Override
                public int getTransferRatio() {
                    return MechanicalDrillItem.DEFAULT_TRANSFER_RATIO / 2;
                }
            }
    );


    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_COG = register("brass_cog",
            MechanicalToolSlot.COG_SLOT,
            CMEItems.SMALL_BRASS_COG,
            new MechanicalCogPartData(100)
    );
    public static final RegistryEntry<MechanicalPart, MechanicalPart> WOODEN_COG = register("wooden_cog",
            MechanicalToolSlot.COG_SLOT,
            CMEItems.SMALL_WOODEN_COG,
            new MechanicalCogPartData(0)
    );

    public static final RegistryEntry<MechanicalPart, MechanicalPart> DEFAULT_GRIP = register("default_grip",
            MechanicalToolSlot.GRIP_SLOT,
            CMEItems.DEFAULT_GRIP,
            new MechanicalPartData() {} // TODO
    );

//    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_GRIP = register("netherite_grip",
//            MechanicalToolSlot.GRIP_SLOT,
//            CVAItems.NETHERITE_GRIP,
//            new MechanicalPartData() {
//                @Override
//                public void onInserted(ItemStack tool) {
//                    tool.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
//                    super.onInserted(tool);
//                }
//                @Override
//                public void onRemoved(ItemStack tool) {
//                    tool.remove(DataComponents.FIRE_RESISTANT);
//                    super.onInserted(tool);
//                }
//            }
//    );

    public static final RegistryEntry<MechanicalPart, MechanicalPart> IRON_DRILL_HEAD = register("iron_drill_head",
            MechanicalToolSlot.TIP_SLOT,
            CMEItems.IRON_DRILL_HEAD,
            new MechanicalDrillPartData(
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.IRON.getSpeed())),
                            1.0F,
                            0
                    )
            )
    );
    public static final RegistryEntry<MechanicalPart, MechanicalPart> DIAMOND_DRILL_HEAD = register("diamond_drill_head",
            MechanicalToolSlot.TIP_SLOT,
            CMEItems.DIAMOND_DRILL_HEAD,
            new MechanicalDrillPartData(
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(CMETags.Blocks.INCORRECT_FOR_MECHANICAL_DRILL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.DIAMOND.getSpeed())),
                            1.0F,
                            0
                    )
            )
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

        return REGISTRATE.object(name).simple(REGISTRY, () ->
                new MechanicalPart(validSlot.getKey(), validItem.getKey(), data, CreateMechanicallyEnhanced.asResource("tool_part", name), name));
    }

    @ApiStatus.Internal
    public static void register() {
        // load this class
    }
}
