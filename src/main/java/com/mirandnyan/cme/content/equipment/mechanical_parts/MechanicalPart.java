package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.*;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator.MechanicalAcceleratorPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator.MechanicalSimpleAcceleratorPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator.MechanicalStarAcceleratorPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.MechanicalBlazePartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.MechanicalCatPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.MechanicalPumpkinPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.MechanicalDrillPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.MechanicalSawPartData;
import com.mirandnyan.cme.content.equipment.mechanical_tool.MechanicalToolItem;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.*;
import com.mirandnyan.cme.util.AffineTransform;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.netty.buffer.ByteBuf;
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
import org.joml.Vector3f;

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
        return getItemRegistry().is(item);
    }

    public RegistryEntry<Item, Item> getItemRegistry() {
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

    // -------- Grips
    public static final RegistryEntry<MechanicalPart, MechanicalPart> WOODEN_GRIP = new MechanicalPartBuilder("wooden_grip")
            .origin(new AffineTransform().translate(0, -0.5, 2), MechanicalToolSlot.GRIP_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 3f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.WOODEN_GRIP)
            .data(new MechanicalGripPartData(600, 0.3f))
            .defaultModel("tool_grip")
            .build();


    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_GRIP = new MechanicalPartBuilder("netherite_grip")
            .origin(new AffineTransform().translate(0, -0.5, 2), MechanicalToolSlot.GRIP_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .item(CMEItems.NETHERITE_GRIP)
            .data(new MechanicalGripPartData(1300, 0.6f) {
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
            .defaultModel("tool_grip")
            .build();

    // -------- Cogs
    public static final RegistryEntry<MechanicalPart, MechanicalPart> WOODEN_COG = new MechanicalPartBuilder("wooden_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_WOODEN_COG)
            .data(new MechanicalCogPartData(2, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_COG = new MechanicalPartBuilder("andesite_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 10f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 6f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_ANDESITE_COG)
            .data(new MechanicalCogPartData(3, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_COG = new MechanicalPartBuilder("brass_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_BRASS_COG)
            .data(new MechanicalCogPartData(8, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_COG = new MechanicalPartBuilder("netherite_cog")
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_NETHERITE_COG)
            .data(new MechanicalCogPartData(14, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    // -------- Accelerators
    public static final RegistryEntry<MechanicalPart, MechanicalPart> SIMPLE_ANDESITE_ACCELERATOR = new MechanicalPartBuilder("simple_andesite_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SIMPLE_ANDESITE_ACCELERATOR)
            .data(new MechanicalSimpleAcceleratorPartData(0))
            .defaultModel("accelerator")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> STONE_ACCELERATOR = new MechanicalPartBuilder("stone_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 2f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.STONE_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(0))
            .defaultModel("accelerator")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_CASING_ACCELERATOR = new MechanicalPartBuilder("andesite_casing_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.ANDESITE_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(4))
            .customModel("accelerator", "andesite_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_CASING_ACCELERATOR = new MechanicalPartBuilder("copper_casing_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.COPPER_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(4))
            .customModel("accelerator", "copper_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_PIPE_NODE_ACCELERATOR = new MechanicalPartBuilder("copper_pipe_node_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 6f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.COPPER_PIPE_NODE_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(4))
            .customModel("accelerator", "copper_pipe_node")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_CASING_ACCELERATOR = new MechanicalPartBuilder("brass_casing_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.BRASS_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(6))
            .customModel("accelerator", "brass_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_CASING_ACCELERATOR = new MechanicalPartBuilder("netherite_casing_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.NETHERITE_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(6))
            .customModel("accelerator", "netherite_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHER_STAR_ACCELERATOR = new MechanicalPartBuilder("nether_star_accelerator")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, -0.5f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 2.5f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.NETHER_STAR_ACCELERATOR)
            .data(new MechanicalStarAcceleratorPartData(10))
            .defaultModel("accelerator")
            .build();

    // -------- Gearboxes
    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_GEARBOX = new MechanicalPartBuilder("andesite_gearbox")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_ANDESITE_VERTICAL_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_GEARBOX = new MechanicalPartBuilder("copper_gearbox")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_COPPER_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_GEARBOX = new MechanicalPartBuilder("netherite_gearbox")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_NETHERITE_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO / 2))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_GEARBOX = new MechanicalPartBuilder("brass_gearbox")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_BRASS_VERTICAL_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO / 2))
            .defaultModel("gearbox")
            .build();

    // -------- Tanks
    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_TANK = new MechanicalPartBuilder("copper_tank")
            .origin(new AffineTransform().translate(8f, 8f, 8f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_COPPER_TANK)
            .data(new MechanicalTankPartData(800))
            .defaultModel("tank")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_TANK = new MechanicalPartBuilder("netherite_tank")
            .origin(new AffineTransform().translate(8f, 8f, 8f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_NETHERITE_TANK)
            .data(new MechanicalTankPartData(1200))
            .defaultModel("tank")
            .build();

    // -------- Drill Heads
    public static final RegistryEntry<MechanicalPart, MechanicalPart> IRON_DRILL_HEAD = new MechanicalPartBuilder("iron_drill_head")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.IRON_DRILL_HEAD)
            .data(new MechanicalDrillPartData(1 + Tiers.IRON.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.IRON.getSpeed())),
                            1.0F,
                            0
                    )
            ))
            .defaultModel("drill")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> DIAMOND_DRILL_HEAD = new MechanicalPartBuilder("diamond_drill_head")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.DIAMOND_DRILL_HEAD)
            .data(new MechanicalDrillPartData(1 + Tiers.DIAMOND.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.DIAMOND.getSpeed())),
                            1.2F,
                            0
                    )
            ))
            .defaultModel("drill")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_DRILL_HEAD = new MechanicalPartBuilder("netherite_drill_head")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.NETHERITE_DRILL_HEAD)
            .data(new MechanicalDrillPartData(1 + Tiers.NETHERITE.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_NETHERITE_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_DRILL, Tiers.NETHERITE.getSpeed())),
                            1.5F,
                            0
                    )
            ))
            .defaultModel("drill")
            .build();


    // -------- Saw Heads
    public static final RegistryEntry<MechanicalPart, MechanicalPart> IRON_SAW_HEAD = new MechanicalPartBuilder("iron_saw_head")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.IRON_SAW_HEAD)
            .data(new MechanicalSawPartData(5 + Tiers.IRON.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_SAW, Tiers.IRON.getSpeed())),
                            1.0F,
                            0
                    )
            ))
            .customModel("saw", "iron_saw_off")
            .customModel("saw", "iron_saw_on")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_SAW_HEAD = new MechanicalPartBuilder("copper_saw_head")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.COPPER_SAW_HEAD)
            .data(new MechanicalSawPartData(5 + Tiers.IRON.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_SAW, Tiers.IRON.getSpeed())),
                            1.0F,
                            0
                    )
            ))
            .customModel("saw", "copper_saw_off")
            .customModel("saw", "copper_saw_on")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_SAW_HEAD = new MechanicalPartBuilder("netherite_saw_head")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.NETHERITE_SAW_HEAD)
            .data(new MechanicalSawPartData(6 + Tiers.NETHERITE.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_NETHERITE_TOOL),
                            Tool.Rule.minesAndDrops(CMETags.Blocks.MINEABLE_WITH_MECHANICAL_SAW, Tiers.NETHERITE.getSpeed())),
                            1.0F,
                            0
                    )
            ))
            .customModel("saw", "netherite_saw_off")
            .customModel("saw", "netherite_saw_on")
            .build();

    // -------- Mechanical Helpers
    public static final RegistryEntry<MechanicalPart, MechanicalPart> SMALL_MECHANICAL_BLAZE = new MechanicalPartBuilder("small_mechanical_blaze")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.SMALL_MECHANICAL_BLAZE)
            .data(new MechanicalBlazePartData())
            // TODO: auto assign names to not use hardcoded order
            .customModel("automaton", "blaze", "base_inert")
            .customModel("automaton", "blaze", "base_idle")
            .customModel("automaton", "blaze", "base_working")
            .customModel("automaton", "blaze", "base_idle_superheated")
            .customModel("automaton", "blaze", "base_working_superheated")
            .customModel("automaton", "blaze", "small_rods")
            .customModel("automaton", "blaze", "large_rods")
            .customModel("automaton", "blaze", "small_rods_superheated")
            .customModel("automaton", "blaze", "large_rods_superheated")
            .customModel("automaton", "cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> SMALL_MECHANICAL_CAT = new MechanicalPartBuilder("small_mechanical_cat")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.SMALL_MECHANICAL_CAT)
            .data(new MechanicalCatPartData())
            .customModel("automaton", "cat", "base")
            .customModel("automaton", "cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> SMALL_MECHANICAL_PUMPKIN = new MechanicalPartBuilder("small_mechanical_pumpkin")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.SMALL_MECHANICAL_PUMPKIN)
            .data(new MechanicalPumpkinPartData())
            .customModel("automaton", "pumpkin", "base")
            .customModel("automaton", "cog")
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
            if (slot.getPartRegistry().get() != this)
                continue;
            return true;
        }
        return false;

    }

    public static boolean isIn(ResourceKey<MechanicalPart> part, ItemStack stack) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.part().compareTo(part) != 0)
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
