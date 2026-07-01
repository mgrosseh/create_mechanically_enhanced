package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_parts.*;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.*;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator.MechanicalAcceleratorPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator.MechanicalSimpleAcceleratorPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator.MechanicalStarAcceleratorPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.pumpkin.PumpkinAutomatonPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mining_refiner.BlastingRefinerPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mining_refiner.SilkBrushRefinerPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tank.CardboardTankPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tank.MechanicalTankPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.CardboardDrillPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.CardboardSawPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.subparts.AutoMaterialSubpart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.subparts.AutomatonBaseSubpart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.ConduitPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.MechanicalBlazePartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.cat.CatAutomatonPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.MechanicalDrillPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.MechanicalSawPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.subparts.SilkBrushSubpart;
import com.mirandnyan.cme.content.equipment.mechanical_tool.MechanicalToolItem;
import com.mirandnyan.cme.util.math.AffineTransform;
import com.mojang.math.Axis;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class CMEMechanicalParts {

    // TODO: maybe make them with transform(Item, MechanicalToolSlot, apply) out of Items

    // -------- Grips
    public static final RegistryEntry<MechanicalPart, MechanicalPart> WOODEN_GRIP = new MechanicalPartBuilder("wooden_grip")
            .material(CMEMaterials.WOOD)
            .origin(new AffineTransform().translate(0, -0.5, 2), MechanicalToolSlot.ROOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 3f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.WOODEN_GRIP)
            .data(new MechanicalGripPartData(600, 0.3f))
            .defaultModel("tool_grip")
            .build();


    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_GRIP = new MechanicalPartBuilder("netherite_grip")
            .material(CMEMaterials.NETHERITE)
            .origin(new AffineTransform().translate(0, -0.5, 2), MechanicalToolSlot.ROOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 9f, 5f), MechanicalToolSlot.COG_SLOT)
            .item(CMEItems.NETHERITE_GRIP)
            .data(new MechanicalGripPartData(1300, 0.6f) {
                @Override
                public void onInserted(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
                    tool.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
                    super.onInserted(replaceSlot, tool);
                }
                @Override
                public void onRemoved(FilledToolSlot.SlotId replaceSlot, ItemStack tool) {
                    tool.remove(DataComponents.FIRE_RESISTANT);
                    super.onInserted(replaceSlot, tool);
                }
            })
            .defaultModel("tool_grip")
            .build();

    // -------- Cogs
    public static final RegistryEntry<MechanicalPart, MechanicalPart> CARDBOARD_COG = new MechanicalPartBuilder("cardboard_cog")
            .material(CMEMaterials.CARDBOARD)
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_CARDBOARD_COG)
            .data(new MechanicalCogPartData(0.05f, 0, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> WOODEN_COG = new MechanicalPartBuilder("wooden_cog")
            .material(CMEMaterials.WOOD)
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_WOODEN_COG)
            .data(new MechanicalCogPartData(2, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_COG = new MechanicalPartBuilder("andesite_cog")
            .material(CMEMaterials.ANDESITE)
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_ANDESITE_COG)
            .data(new MechanicalCogPartData(3, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_COG = new MechanicalPartBuilder("brass_cog")
            .material(CMEMaterials.BRASS)
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_BRASS_COG)
            .data(new MechanicalCogPartData(8, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_COG = new MechanicalPartBuilder("netherite_cog")
            .material(CMEMaterials.NETHERITE)
            .origin(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 9f), MechanicalToolSlot.COG_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 7f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .item(CMEItems.SMALL_NETHERITE_COG)
            .data(new MechanicalCogPartData(14, new Vector3f(0f, -4f, 0f)))
            .defaultModel("cog")
            .build();

    // -------- Accelerators
    public static final RegistryEntry<MechanicalPart, MechanicalPart> SIMPLE_ANDESITE_ACCELERATOR = new MechanicalPartBuilder("simple_andesite_accelerator")
            .material(CMEMaterials.ANDESITE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SIMPLE_ANDESITE_ACCELERATOR)
            .data(new MechanicalSimpleAcceleratorPartData(0))
            .defaultModel("accelerator")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> STONE_ACCELERATOR = new MechanicalPartBuilder("stone_accelerator")
            .material(CMEMaterials.STONE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 2f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.STONE_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(0))
            .defaultModel("accelerator")
            .build();


    public static final RegistryEntry<MechanicalPart, MechanicalPart> CARDBOARD_CASING_ACCELERATOR = new MechanicalPartBuilder("cardboard_casing_accelerator")
            .material(CMEMaterials.CARDBOARD)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.CARDBOARD_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(0.0f, 0))
            .customModel("accelerator", "cardboard_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_CASING_ACCELERATOR = new MechanicalPartBuilder("andesite_casing_accelerator")
            .material(CMEMaterials.ANDESITE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.ANDESITE_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(4))
            .customModel("accelerator", "andesite_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_CASING_ACCELERATOR = new MechanicalPartBuilder("copper_casing_accelerator")
            .material(CMEMaterials.COPPER)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.COPPER_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(4))
            .customModel("accelerator", "copper_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_PIPE_NODE_ACCELERATOR = new MechanicalPartBuilder("copper_pipe_node_accelerator")
            .material(CMEMaterials.COPPER)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 6f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.COPPER_PIPE_NODE_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(4))
            .customModel("accelerator", "copper_pipe_node")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_CASING_ACCELERATOR = new MechanicalPartBuilder("brass_casing_accelerator")
            .material(CMEMaterials.BRASS)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.ACCELERATOR_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 3f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .item(CMEItems.BRASS_CASING_ACCELERATOR)
            .data(new MechanicalAcceleratorPartData(6))
            .customModel("accelerator", "brass_casing")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_CASING_ACCELERATOR = new MechanicalPartBuilder("netherite_casing_accelerator")
            .material(CMEMaterials.NETHERITE)
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
    public static final RegistryEntry<MechanicalPart, MechanicalPart> CARDBOARD_GEARBOX = new MechanicalPartBuilder("cardboard_gearbox")
            .material(CMEMaterials.CARDBOARD)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_CARDBOARD_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO * 20))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> ANDESITE_GEARBOX = new MechanicalPartBuilder("andesite_gearbox")
            .material(CMEMaterials.ANDESITE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_ANDESITE_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_GEARBOX = new MechanicalPartBuilder("copper_gearbox")
            .material(CMEMaterials.COPPER)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_COPPER_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_GEARBOX = new MechanicalPartBuilder("netherite_gearbox")
            .material(CMEMaterials.NETHERITE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_NETHERITE_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO / 2))
            .defaultModel("gearbox")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BRASS_GEARBOX = new MechanicalPartBuilder("brass_gearbox")
            .material(CMEMaterials.BRASS)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.GEARBOX_SLOT)
            .slot(new AffineTransform().rotateXDegrees(270f).translate(8f, 4f, 12f), MechanicalToolSlot.AUTOMATON_SLOT)
            .slot(new AffineTransform().rotateXDegrees(90f).translate(8f, 4f, 4f), MechanicalToolSlot.TANK_SLOT)
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 8f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SMALL_BRASS_GEARBOX)
            .data(new MechanicalGearboxPartData(MechanicalToolItem.DEFAULT_TRANSFER_RATIO / 2))
            .defaultModel("gearbox")
            .build();

    // -------- Tanks
    public static final RegistryEntry<MechanicalPart, MechanicalPart> CARDBOARD_TANK = new MechanicalPartBuilder("cardboard_tank")
            .material(CMEMaterials.CARDBOARD)
            .origin(new AffineTransform().translate(8f, 8f, 8f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_CARDBOARD_TANK)
            .data(new CardboardTankPartData(100))
            .defaultModel("tank")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> COPPER_TANK = new MechanicalPartBuilder("copper_tank")
            .material(CMEMaterials.COPPER)
            .origin(new AffineTransform().translate(8f, 8f, 8f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_COPPER_TANK)
            .data(new MechanicalTankPartData(800))
            .defaultModel("tank")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> NETHERITE_TANK = new MechanicalPartBuilder("netherite_tank")
            .material(CMEMaterials.NETHERITE)
            .origin(new AffineTransform().translate(8f, 8f, 8f), MechanicalToolSlot.TANK_SLOT)
            .item(CMEItems.SMALL_NETHERITE_TANK)
            .data(new MechanicalTankPartData(1200))
            .defaultModel("tank")
            .build();

    // -------- Drill Heads
    public static final RegistryEntry<MechanicalPart, MechanicalPart> CARDBOARD_DRILL_HEAD = new MechanicalPartBuilder("cardboard_drill_head")
            .material(CMEMaterials.CARDBOARD)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.CARDBOARD_DRILL_HEAD)
            .data(new CardboardDrillPartData(0 + CMETiers.CARDBOARD.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_WOODEN_TOOL),
                            Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, CMETiers.CARDBOARD.getSpeed())),
                            0.9F,
                            0
                    )
            ))
            .defaultModel("drill")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> IRON_DRILL_HEAD = new MechanicalPartBuilder("iron_drill_head")
            .material(CMEMaterials.IRON)
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
            .material(CMEMaterials.DIAMOND)
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
            .material(CMEMaterials.NETHERITE)
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
    // TODO: cardboard attack should bonk (drill too)
    public static final RegistryEntry<MechanicalPart, MechanicalPart> CARDBOARD_SAW_HEAD = new MechanicalPartBuilder("cardboard_saw_head")
            .material(CMEMaterials.CARDBOARD)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.CARDBOARD_SAW_HEAD)
            .data(new CardboardSawPartData(0 + CMETiers.CARDBOARD.getAttackDamageBonus(),
                    new Tool(List.of(
                            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_WOODEN_TOOL),
                            Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE, CMETiers.CARDBOARD.getSpeed())),
                            0.8F,
                            0
                    )
            ))
            .customModel("saw", "cardboard_saw_off")
            .customModel("saw", "cardboard_saw_on")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> IRON_SAW_HEAD = new MechanicalPartBuilder("iron_saw_head")
            .material(CMEMaterials.IRON)
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
            .material(CMEMaterials.COPPER)
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
            .material(CMEMaterials.NETHERITE)
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

    // -------- Automatons
    public static final AutomatonBaseSubpart AUTOMATON_BASE = new AutomatonBaseSubpart.Builder("automaton")
            .casing(CMEMaterials.CARDBOARD.getKey(), "cardboard_base")
            .casing(CMEMaterials.ANDESITE.getKey(), "andesite_base")
            .casing(CMEMaterials.COPPER.getKey(), "copper_base")
            .casing(CMEMaterials.BRASS.getKey(), "brass_base")
            .casing(CMEMaterials.NETHERITE.getKey(), "netherite_base")
            .cog(CMEMaterials.CARDBOARD.getKey(), "cardboard_cog")
            .cog(CMEMaterials.NETHERITE.getKey(), "netherite_cog")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BLAZE_AUTOMATON = new MechanicalPartBuilder("blaze_automaton")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.BLAZE_AUTOMATON)
            .data(new MechanicalBlazePartData())
            .subpart(AUTOMATON_BASE)
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
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> CAT_AUTOMATON = new MechanicalPartBuilder("cat_automaton")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.CAT_AUTOMATON)
            .data(new CatAutomatonPartData())
            .subpart(AUTOMATON_BASE)
            .customModel("automaton", "cat", "base")
            .customModel("automaton", "cat", "left_ear")
            .customModel("automaton", "cat", "right_ear")
            .customModel("automaton", "cat", "eye_stars")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> PUMPKIN_AUTOMATON = new MechanicalPartBuilder("pumpkin_automaton")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.PUMPKIN_AUTOMATON)
            .data(new PumpkinAutomatonPartData())
            .subpart(AUTOMATON_BASE)
            .customModel("automaton", "pumpkin", "base")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> CONDUIT_AUTOMATON = new MechanicalPartBuilder("conduit_automaton")
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.AUTOMATON_SLOT)
            .item(CMEItems.CONDUIT_AUTOMATON)
            .data(new ConduitPartData())
            .subpart(AUTOMATON_BASE)
            .customModel("automaton", "conduit", "inactive")
            .customModel("automaton", "conduit", "eye")
            .customModel("automaton", "conduit", "shell_open")
            .build();

    // -------- Mining Refiners
    public static final AutoMaterialSubpart MINING_REFINER_BASE = new AutoMaterialSubpart.GenericBuilder("mining_refiner")
            .casing(CMEMaterials.CARDBOARD.getKey(), "cardboard_base")
            .casing(CMEMaterials.ANDESITE.getKey(), "andesite_base")
            .casing(CMEMaterials.COPPER.getKey(), "copper_base")
            .casing(CMEMaterials.BRASS.getKey(), "brass_base")
            .casing(CMEMaterials.NETHERITE.getKey(), "netherite_base")
            .build();

    public static final RegistryEntry<MechanicalPart, MechanicalPart> BLASTING_REFINER = new MechanicalPartBuilder("blasting_refiner")
            .subpart(MINING_REFINER_BASE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT) // TODO: type tag system then use REFINER SLOT
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.BLASTING_MINING_REFINER)
            .data(new BlastingRefinerPartData(0))
            .customModel("mining_refiner", "blasting", "fans")
            .customModel("mining_refiner", "blasting", "fire_effect")
            .build();


    public static final SilkBrushSubpart.Builder SILK_BRUSH_REFINER_SUBPART = new SilkBrushSubpart.Builder("mining_refiner", "silk_brush")
            .casing(CMEMaterials.ANDESITE.getKey(), "andesite_brush")
            .casing(CMEMaterials.COPPER.getKey(), "copper_brush")
            .casing(CMEMaterials.BRASS.getKey(), "brass_brush")
            .casing(CMEMaterials.NETHERITE.getKey(), "netherite_brush");

    public static final RegistryEntry<MechanicalPart, MechanicalPart> SILK_BRUSH_REFINER = new MechanicalPartBuilder("silk_brush_refiner")
            .subpart(MINING_REFINER_BASE)
            .origin(new AffineTransform().rotateXDegrees(180f).translate(8f, 0f, 8f), MechanicalToolSlot.TIP_SLOT) // TODO: type tag system then use REFINER SLOT
            .slot(new AffineTransform().rotateXDegrees(180f).translate(8f, 1f, 8f), MechanicalToolSlot.TIP_SLOT)
            .item(CMEItems.SILK_BRUSH_MINING_REFINER)
            .data(new SilkBrushRefinerPartData(0))
            .subpart(SILK_BRUSH_REFINER_SUBPART.withOrigin(
                    angle -> new AffineTransform()
                            .translateBack(2, 2, 8)
                            .rotateAround(Axis.YP.rotationDegrees(angle), 2, 2, 8)
                            .rotateAround(Axis.XP.rotationDegrees(30), 2, 2, 8)
                            .rotateAround(Axis.ZP.rotationDegrees(45), 2, 2, 8)
                            .scale(0.7f)
                            .translate(8f, 6.1f, 0.4f)
                            .convertToBlockSpace()))
            .subpart(SILK_BRUSH_REFINER_SUBPART.withOrigin(
                    angle -> new AffineTransform()
                            .translateBack(2, 2, 8)
                            .rotateAround(Axis.YP.rotationDegrees(angle), 2, 2, 8)
                            .rotateAround(Axis.XN.rotationDegrees(30), 2, 2, 8)
                            .rotateAround(Axis.ZP.rotationDegrees(45), 2, 2, 8)
                            .scale(0.7f)
                            .translate(8f, 6.1f, 15.6f)
                            .convertToBlockSpace()))
            .subpart(SILK_BRUSH_REFINER_SUBPART.withOrigin(
                    angle -> new AffineTransform()
                            .translateBack(2, 2, 8)
                            .rotateAround(Axis.YP.rotationDegrees(angle - 90), 2, 2, 8)
                            .rotateAround(Axis.XN.rotationDegrees(30), 2, 2, 8)
                            .rotateAround(Axis.ZP.rotationDegrees(45), 2, 2, 8)
                            .scale(0.7f)
                            .translate(0.4f, 6.1f, 8f)
                            .convertToBlockSpace()))
            .subpart(SILK_BRUSH_REFINER_SUBPART.withOrigin(
                    angle -> new AffineTransform()
                            .translateBack(2, 2, 8)
                            .rotateAround(Axis.YP.rotationDegrees(angle + 90), 2, 2, 8)
                            .rotateAround(Axis.XN.rotationDegrees(30), 2, 2, 8)
                            .rotateAround(Axis.ZP.rotationDegrees(45), 2, 2, 8)
                            .scale(0.7f)
                            .translate(15.6f, 6.1f, 8f)
                            .convertToBlockSpace()))
            .build();


    public static Stream<RegistryEntry<MechanicalPart, MechanicalPart>> getAll(Predicate<? super RegistryEntry<MechanicalPart, MechanicalPart>> filter) {
        return REGISTRATE.getAll(MechanicalPart.REGISTRY).stream().filter(filter);
    }

    public static Optional<RegistryEntry<MechanicalPart, MechanicalPart>> getOfItem(Item item) {
        return getAll(part -> part.get().isItem(item)).findAny();
    }

    public static RegistryEntry<MechanicalPart, MechanicalPart> get(ResourceKey<MechanicalPart> part) {
        return REGISTRATE.get(part.location().getPath(), part.registryKey());
    }

    @ApiStatus.Internal
    public static void register() {
        // load this class
    }
}
