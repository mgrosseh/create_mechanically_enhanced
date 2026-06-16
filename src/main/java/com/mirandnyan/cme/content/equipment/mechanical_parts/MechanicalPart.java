package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.*;
import com.mirandnyan.cme.content.equipment.mechanical_tool.MechanicalToolItem;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.*;
import com.mirandnyan.cme.util.AffineTransform;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class MechanicalPart {
    public static final ResourceKey<Registry<MechanicalPart>> REGISTRY =
            REGISTRATE.makeRegistry("mechanical_part", RegistryBuilder::new);

    public static final Codec<ResourceKey<MechanicalPart>> CODEC = ResourceKey.codec(REGISTRY);
    public static final StreamCodec<ByteBuf, ResourceKey<MechanicalPart>> STREAM_CODEC = ResourceKey.streamCodec(REGISTRY);

    public final MechanicalPartData data;
    public final @NotNull ResourceKey<Item> validItem;
    public final @NotNull MechanicalPartSlotDefs slotDefinitions;

    public final @NotNull String name;
    public final @NotNull PartialModel[] models;

    protected MechanicalPart(@NotNull MechanicalPartSlotDefs slotDefinitions,
                             @NotNull ResourceKey<Item> validItem,
                             MechanicalPartData data,
                             @NotNull String name,
                             @NotNull ResourceLocation... models) {
        this.name = name;
        this.slotDefinitions = slotDefinitions;
        this.validItem = validItem;
        this.data = data;
        data.setParent(this);
        this.models = Arrays.stream(models)
                .map(PartialModel::of)
                .toArray(PartialModel[]::new);
    }

    public boolean isItem(Item item) {
        return getItem().is(item);
    }

    public RegistryEntry<Item, Item> getItem() {
        return REGISTRATE.get(validItem.location().getPath(), validItem.registryKey());
    }

    public RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> getOriginSlot() {
        return REGISTRATE.get(slotDefinitions.origin.slot().location().getPath(), slotDefinitions.origin.slot().registryKey());
    }

    public MechanicalPartSlotDefs slots() {
        return slotDefinitions;
    }

    @Override
    public String toString() {
        return "Part{" + name + "}";
    }

    // STATIC

    // TODO: maybe make them with transform(Item, MechanicalToolSlot, apply) out of Items
    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_TANK = new MechanicalPartBuilder("copper_tank")
            .origin(new AffineTransform().translate(8f, 5f, -2f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_COPPER_TANK)
            .data(new MechanicalTankPartData(800))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_TANK = new MechanicalPartBuilder("netherite_tank")
            .origin(new AffineTransform().translate(8f, 5f, -2f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_NETHERITE_TANK)
            .data(new MechanicalTankPartData(1200))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_GEARBOX = new MechanicalPartBuilder("andesite_gearbox")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 1f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 13f, -2f), MechanicalToolSlot.GEARED_TOP_SLOT)
            .slot(new AffineTransform().translate(8f, 5f, -2f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, -6f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_ANDESITE_VERTICAL_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_GEARBOX = new MechanicalPartBuilder("brass_gearbox")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 1f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 13f, -2f), MechanicalToolSlot.GEARED_TOP_SLOT)
            .slot(new AffineTransform().translate(8f, 5f, -2f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, -6f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_BRASS_VERTICAL_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO / 2))
            .build();


    public static final RegistryEntry<MechanicalPart, MechanicalPart> WOODEN_COG = new MechanicalPartBuilder("wooden_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .item(CMEItems.SMALL_WOODEN_COG)
            .data(new MechanicalCogPartData(4))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_COG = new MechanicalPartBuilder("brass_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .item(CMEItems.SMALL_BRASS_COG)
            .data(new MechanicalCogPartData(8))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_COG = new MechanicalPartBuilder("netherite_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .item(CMEItems.SMALL_NETHERITE_COG)
            .data(new MechanicalCogPartData(14))
            .build();


    public static final RegistryEntry<MechanicalPart, MechanicalPart> DEFAULT_GRIP = new MechanicalPartBuilder("default_grip")
            .origin(MechanicalToolSlot.GRIP_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 1f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.DEFAULT_GRIP)
            .data(new MechanicalGripPartData(600))
            .build();


    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_GRIP = new MechanicalPartBuilder("netherite_grip")
            .origin(MechanicalToolSlot.GRIP_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 1f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.NETHERITE_GRIP)
            .data(new MechanicalGripPartData(1300) {
                @Override
                public void onInserted(ItemStack tool) {
                    tool.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
                    super.onInserted(tool);
                }
                @Override
                public void onRemoved(ItemStack tool) {
                    tool.remove(DataComponents.FIRE_RESISTANT);
                    super.onInserted(tool);
                }
            })
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> IRON_DRILL_HEAD = new MechanicalPartBuilder("iron_drill_head")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, -6f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.IRON_DRILL_HEAD)
            .data(new MechanicalDrillPartData(1 + Tiers.IRON.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.IRON.getSpeed())),
                            1.0F,
                            0
                    )
            ))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> DIAMOND_DRILL_HEAD = new MechanicalPartBuilder("diamond_drill_head")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, -6f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.DIAMOND_DRILL_HEAD)
            .data(new MechanicalDrillPartData(1 + Tiers.DIAMOND.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.DIAMOND.getSpeed())),
                            1.2F,
                            0
                    )
            ))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_DRILL_HEAD = new MechanicalPartBuilder("netherite_drill_head")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, -6f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.NETHERITE_DRILL_HEAD)
            .data(new MechanicalDrillPartData(1 + Tiers.NETHERITE.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_NETHERITE_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.NETHERITE.getSpeed())),
                            1.5F,
                            0
                    )
            ))
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> SMALL_MECHANICAL_BLAZE = new MechanicalPartBuilder("small_mechanical_blaze")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.GEARED_TOP_SLOT)
            .item(CMEItems.SMALL_MECHANICAL_BLAZE)
            .data(new MechanicalBlazePartData())
            // TODO: auto assign names to not use hardcoded order
            .model("tool_part", "small_mechanical_blaze", "base_inert")
            .model("tool_part", "small_mechanical_blaze", "base_idle")
            .model("tool_part", "small_mechanical_blaze", "base_working")
            .model("tool_part", "small_mechanical_blaze", "base_idle_superheated")
            .model("tool_part", "small_mechanical_blaze", "base_working_superheated")
            .model("tool_part", "small_mechanical_blaze", "small_rods")
            .model("tool_part", "small_mechanical_blaze", "large_rods")
            .model("tool_part", "small_mechanical_blaze", "small_rods_superheated")
            .model("tool_part", "small_mechanical_blaze", "large_rods_superheated")
            .model("tool_part", "small_mechanical_blaze", "cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> SMALL_MECHANICAL_CAT = new MechanicalPartBuilder("small_mechanical_cat")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.GEARED_TOP_SLOT)
            .item(CMEItems.SMALL_MECHANICAL_CAT)
            .data(new MechanicalCatPartData())
            .model("tool_part", "small_mechanical_cat")
            .model("tool_part", "small_mechanical_blaze", "cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> SMALL_MECHANICAL_PUMPKIN = new MechanicalPartBuilder("small_mechanical_pumpkin")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.GEARED_TOP_SLOT)
            .item(CMEItems.SMALL_MECHANICAL_PUMPKIN)
            .data(new MechanicalPumpkinPartData())
            .model("tool_part", "small_mechanical_pumpkin")
            .model("tool_part", "small_mechanical_blaze", "cog")
            .build();

    public static Stream<RegistryEntry<MechanicalPart, MechanicalPart>> getAll(Predicate<? super RegistryEntry<MechanicalPart, MechanicalPart>> filter) {
        return REGISTRATE.getAll(REGISTRY).stream().filter(filter);
    }

    public static Optional<RegistryEntry<MechanicalPart, MechanicalPart>> getOfItem(Item item) {
        return MechanicalPart.getAll(part -> part.get().isItem(item)).findAny();
    }

    public static RegistryEntry<MechanicalPart, MechanicalPart> get(ResourceKey<MechanicalPart> part) {
        return REGISTRATE.get(part.location().getPath(), part.registryKey());
    }

    public boolean isIn(ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var part = slot.getPart();
            if ( part.isEmpty() || part.get().get() != this)
                continue;
            return true;
        }
        return false;

    }

    public static boolean isIn(ResourceKey<MechanicalPart> part, ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.part() == null || slot.part().compareTo(part) != 0)
                continue;
            return true;
        }
        return false;
    }

    @ApiStatus.Internal
    public static void register() {
        // load this class
    }
}
