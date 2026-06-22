package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_tool.MechanicalToolItem;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.items.TooltipItem;
import com.mirandnyan.cme.content.items.cat_coin_die.CatCoinDieItem;
import com.mirandnyan.cme.recipes.CreateRecipeUtil;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class CMEItems {
    public static final List<ItemEntry<?>> creativeModeItem = new ArrayList<>();

    public static final ItemEntry<MechanicalToolItem> MECHANICAL_TOOL = REGISTRATE.item("mechanical_tool", MechanicalToolItem::new)
            .model(getExisting("default"))
            .register();

    public static final ItemEntry<Item> WOODEN_GRIP = inTab(part("grip_wooden").lang("Wooden Grip").register());
    public static final ItemEntry<Item> NETHERITE_GRIP = inTab(part("grip_netherite").lang("Netherite Grip").register());

    public static final ItemEntry<Item> SIMPLE_ANDESITE_ACCELERATOR = inTab(part("accelerator_simple_andesite").lang("Simple Connector").register());
    public static final ItemEntry<Item> STONE_ACCELERATOR = inTab(part("accelerator_stone").lang("Stone Accelerator").register());
    public static final ItemEntry<Item> ANDESITE_CASING_ACCELERATOR = inTab(part("accelerator_andesite_casing").lang("Andesite Encased Accelerator").register());
    public static final ItemEntry<Item> COPPER_CASING_ACCELERATOR = inTab(part("accelerator_copper_casing").lang("Copper Encased Accelerator").register());
    public static final ItemEntry<Item> BRASS_CASING_ACCELERATOR = inTab(part("accelerator_brass_casing").lang("Brass Encased Accelerator").register());
    public static final ItemEntry<Item> NETHERITE_CASING_ACCELERATOR = inTab(part("accelerator_netherite_casing").lang("Netherite Encased Accelerator").register());
    public static final ItemEntry<Item> COPPER_PIPE_NODE_ACCELERATOR = inTab(part("accelerator_copper_pipe_node").lang("Copper Pipe Accelerator").register());
    public static final ItemEntry<TooltipItem> NETHER_STAR_ACCELERATOR = inTab(
            REGISTRATE.item("accelerator_nether_star", p -> new TooltipItem(p, CMETranslations.NETHER_STAR_ACCELERATOR_TOOLTIP::resolveComponent))
                    .properties(p -> p
                            .stacksTo(1)
                            .rarity(Rarity.EPIC)
                            .component(CMEDataComponents.EXPLOSION_IMMUNE, Unit.INSTANCE))
                    .model(CMEItems::getExisting)
                    .lang("Nether Star Accelerator")
                    .register());

    public static final ItemEntry<Item> SMALL_WOODEN_COG = inTab(part("small_cog_wooden").register());
    public static final ItemEntry<Item> SMALL_ANDESITE_COG = inTab(part("small_cog_andesite").register());
    public static final ItemEntry<Item> SMALL_BRASS_COG = inTab(part("small_cog_brass").register());
    public static final ItemEntry<Item> SMALL_NETHERITE_COG = inTab(part("small_cog_netherite").register());

    public static final ItemEntry<Item> SMALL_ANDESITE_VERTICAL_GEARBOX = inTab(part("small_gearbox_andesite").register());
    public static final ItemEntry<Item> SMALL_COPPER_GEARBOX = inTab(part("small_gearbox_copper").register());
    public static final ItemEntry<Item> SMALL_BRASS_VERTICAL_GEARBOX = inTab(part("small_gearbox_brass").register());
    public static final ItemEntry<Item> SMALL_NETHERITE_GEARBOX = inTab(part("small_gearbox_netherite").register());

    public static final ItemEntry<Item> SMALL_COPPER_TANK = inTab(part("tank_copper").register());
    public static final ItemEntry<Item> SMALL_NETHERITE_TANK = inTab(part("tank_netherite").register());

    public static final ItemEntry<Item> IRON_DRILL_HEAD = inTab(part("drill_head_iron").lang("Iron Drill Head").register());
    public static final ItemEntry<Item> DIAMOND_DRILL_HEAD = inTab(part("drill_head_diamond").lang("Diamond Drill Head").register());
    public static final ItemEntry<Item> NETHERITE_DRILL_HEAD = inTab(part("drill_head_netherite").lang("Netherite Drill Head").register());

    public static final ItemEntry<Item> IRON_SAW_HEAD = inTab(part("saw_head_iron").lang("Iron Saw head").register());
    public static final ItemEntry<Item> COPPER_SAW_HEAD = inTab(part("saw_head_copper").lang("Copper Saw head").register());
    public static final ItemEntry<Item> NETHERITE_SAW_HEAD = inTab(part("saw_head_netherite").lang("Netherite Saw Head").register());

    public static final ItemEntry<Item> SMALL_MECHANICAL_BLAZE = inTab(part("automaton_blaze").register());
    public static final ItemEntry<Item> SMALL_MECHANICAL_CAT = inTab(part("automaton_cat").register());
    public static final ItemEntry<Item> SMALL_MECHANICAL_PUMPKIN = inTab(part("automaton_pumpkin").register());

    public static final ItemEntry<CatCoinDieItem> CAT_COIN_DIE = inTab(REGISTRATE.item("cat_coin_die", CatCoinDieItem::new)
            .defaultModel()
            .register());
    public static final ItemEntry<TooltipItem> MINTED_COPPER_COIN = inTab(
            REGISTRATE.item("copper_coin", p -> new TooltipItem(p, CMETranslations.CAT_COIN_TIER_1::resolveComponent))
                    .properties(p -> p.rarity(Rarity.UNCOMMON))
                    .model(generated("cat_coins"))
                    .lang("Copper Coin")
                    // TODO: move recipe in here with a builder instead of create approach
                    .register());

    public static final ItemEntry<TooltipItem> UNMINTED_IRON_COIN = inTab(
            REGISTRATE.item("iron_coin", p -> new TooltipItem(p, CMETranslations.CAT_COIN_TIER_0::resolveComponent))
                    .properties(p -> p.rarity(Rarity.UNCOMMON))
                    .recipe((ctx, prov) ->
                            CreateRecipeUtil.deployer(CreateMechanicallyEnhanced.asResource(ctx.getName()))
                                    .require(AllItems.IRON_SHEET)
                                    .require(CAT_COIN_DIE)
                                    .toolNotConsumed()
                                    .output(ctx.getEntry())
                                    .build(prov))
                    .model(generated("cat_coins"))
                    .lang("Unminted Iron Coin")
                    .register());

    public static final ItemEntry<TooltipItem> MINTED_IRON_COIN =
            coin("iron_coin_minted", "Minted Iron Coin", CMETranslations.CAT_COIN_TIER_1,
                    coin_recipe(UNMINTED_IRON_COIN));
    public static final ItemEntry<TooltipItem> MINTED_IRON_COIN_AMETHYST =
            coin("iron_coin_minted_amethyst", "Minted Iron Coin (Amethyst)", CMETranslations.CAT_COIN_TIER_2,
                    coin_recipe(UNMINTED_IRON_COIN, Items.AMETHYST_SHARD));
    public static final ItemEntry<TooltipItem> MINTED_IRON_COIN_DIAMOND =
            coin("iron_coin_minted_diamond", "Minted Iron Coin (Diamond)", CMETranslations.CAT_COIN_TIER_2,
                    coin_recipe(UNMINTED_IRON_COIN, Items.DIAMOND));
    public static final ItemEntry<TooltipItem> MINTED_IRON_COIN_EMERALD =
            coin("iron_coin_minted_emerald", "Minted Iron Coin (Emerald)", CMETranslations.CAT_COIN_TIER_2,
                    coin_recipe(UNMINTED_IRON_COIN, Items.EMERALD));
    public static final ItemEntry<TooltipItem> MINTED_IRON_COIN_EXPERIENCE =
            coin("iron_coin_minted_experience", "Minted Iron Coin (Experience)", CMETranslations.CAT_COIN_TIER_2,
                    coin_recipe(UNMINTED_IRON_COIN, AllItems.EXP_NUGGET));

    public static final ItemEntry<TooltipItem> UNMINTED_BRASS_COIN = inTab(
            REGISTRATE.item("brass_coin", p -> new TooltipItem(p, CMETranslations.CAT_COIN_TIER_0::resolveComponent))
                    .properties(p -> p.rarity(Rarity.UNCOMMON))
                    .model(generated("cat_coins"))
                    .recipe((ctx, prov) ->
                            CreateRecipeUtil.deployer(CreateMechanicallyEnhanced.asResource(ctx.getName()))
                                    .require(AllItems.BRASS_SHEET)
                                    .require(CAT_COIN_DIE)
                                    .toolNotConsumed()
                                    .output(ctx.getEntry())
                                    .build(prov))
                    .lang("Unminted Brass Coin")
                    .register());

    public static final ItemEntry<TooltipItem> MINTED_BRASS_COIN =
            coin("brass_coin_minted", "Minted Brass Coin", CMETranslations.CAT_COIN_TIER_1,
                    coin_recipe(UNMINTED_BRASS_COIN));
    public static final ItemEntry<TooltipItem> MINTED_BRASS_COIN_AMETHYST =
            coin("brass_coin_minted_amethyst", "Minted Brass Coin (Amethyst)", CMETranslations.CAT_COIN_TIER_3,
                    coin_recipe(UNMINTED_BRASS_COIN, Items.AMETHYST_SHARD));
    public static final ItemEntry<TooltipItem> MINTED_BRASS_COIN_DIAMOND =
            coin("brass_coin_minted_diamond", "Minted Brass Coin (Diamond)", CMETranslations.CAT_COIN_TIER_3,
                    coin_recipe(UNMINTED_BRASS_COIN, Items.DIAMOND));
    public static final ItemEntry<TooltipItem> MINTED_BRASS_COIN_EMERALD =
            coin("brass_coin_minted_emerald", "Minted Brass Coin (Emerald)", CMETranslations.CAT_COIN_TIER_3,
                    coin_recipe(UNMINTED_BRASS_COIN, Items.EMERALD));
    public static final ItemEntry<TooltipItem> MINTED_BRASS_COIN_EXPERIENCE =
            coin("brass_coin_minted_experience", "Minted Brass Coin (Experience)", CMETranslations.CAT_COIN_TIER_3,
                    coin_recipe(UNMINTED_BRASS_COIN, AllItems.EXP_NUGGET));

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> coin_recipe(ItemLike unminted) {
        return (ctx, prov) ->
                CreateRecipeUtil.pressing(CreateMechanicallyEnhanced.asResource("coin_minting", ctx.getName()))
                        .require(unminted)
                        .output(ctx.getEntry())
                        .build(prov);
        // TODO advancements
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> coin_recipe(ItemLike coin, ItemLike mint) {
        return (ctx, prov) ->
                new SequencedAssemblyRecipeBuilder(CreateMechanicallyEnhanced.asResource("coin_minting", ctx.getName()))
                        .require(coin)
                        .transitionTo(coin)
                        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(mint))
                        .addStep(PressingRecipe::new, rb -> rb)
                        .loops(1)
                        .addOutput(ctx.getEntry(), 1)
                        .build(prov);
        // TODO advancements
    }

    private static ItemEntry<TooltipItem> coin(String name, String lang, CMETranslations.LangEntry entry,
                                               NonNullBiConsumer<DataGenContext<Item, TooltipItem>, RegistrateRecipeProvider> cons) {
        return inTab(REGISTRATE.item(name, p -> new TooltipItem(p, entry::resolveComponent))
                .properties(p -> p.rarity(Rarity.UNCOMMON))
                .model(generated("cat_coins"))
                .recipe(cons)
                .lang(lang)
                .register());
    }

    private static ItemBuilder<Item, CreateRegistrate> part(String name) {
        return REGISTRATE.item(name, Item::new)
                .properties(p -> p.stacksTo(1))
                .model(CMEItems::getExisting);
    }

    private static <T extends Item> ItemEntry<T> inTab(ItemEntry<T> entry) {
        creativeModeItem.add(entry);
        return entry;
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generated(String sublocation) {
        return (ctx, prov) ->
                prov.generated(ctx::getEntry, prov.modLoc("item/" + sublocation + "/" + prov.name(ctx::getEntry)));
    }

    private static <T extends Item> ModelFile getExisting(DataGenContext<Item, T> ctx, RegistrateItemModelProvider prov) {
        return prov.getExistingFile(CreateMechanicallyEnhanced.asResource("item/" + ctx.getName()));
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> getExisting(String sublocation) {
        return (ctx, prov) ->
                prov.withExistingParent("item/" + ctx.getName(), prov.modLoc("item/" + ctx.getName() + "/" + sublocation));
    }
    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> getIn(String sublocation) {
        return (ctx, prov) ->
                prov.withExistingParent("item/" + ctx.getName(), prov.modLoc("item/" + sublocation + "/" + ctx.getName()));
    }

    // -- Creative Mode Tab --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateMechanicallyEnhanced.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
            CREATIVE_MODE_TABS.register("tab",
                    () -> CreativeModeTab.builder()
                            .title(CMETranslations.CREATIVE_MODE_TAB.resolveComponent())
                            .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getId())
                            .icon(SMALL_BRASS_VERTICAL_GEARBOX::asStack)
                            .displayItems((parameters, output) -> {
                                output.accept(MechanicalToolItem.newStackWithParts(
                                        CMEMechanicalParts.WOODEN_GRIP,
                                        CMEMechanicalParts.WOODEN_COG,
                                        CMEMechanicalParts.SIMPLE_ANDESITE_ACCELERATOR
                                ));

                                output.accept(MechanicalToolItem.newStackWithParts(
                                        CMEMechanicalParts.WOODEN_GRIP,
                                        CMEMechanicalParts.WOODEN_COG,
                                        CMEMechanicalParts.STONE_ACCELERATOR,
                                        CMEMechanicalParts.ANDESITE_GEARBOX,
                                        CMEMechanicalParts.COPPER_TANK,
                                        CMEMechanicalParts.IRON_DRILL_HEAD
                                ));
                                output.accept(MechanicalToolItem.newStackWithParts(
                                        CMEMechanicalParts.NETHERITE_GRIP,
                                        CMEMechanicalParts.NETHERITE_COG,
                                        CMEMechanicalParts.NETHER_STAR_ACCELERATOR,
                                        CMEMechanicalParts.NETHERITE_GEARBOX,
                                        CMEMechanicalParts.NETHERITE_TANK,
                                        CMEMechanicalParts.NETHERITE_DRILL_HEAD,
                                        CMEMechanicalParts.BLAZE_AUTOMATON
                                ));


                                output.accept(MechanicalToolItem.newStackWithParts(
                                        CMEMechanicalParts.WOODEN_GRIP,
                                        CMEMechanicalParts.ANDESITE_COG,
                                        CMEMechanicalParts.COPPER_CASING_ACCELERATOR,
                                        CMEMechanicalParts.COPPER_GEARBOX,
                                        CMEMechanicalParts.COPPER_TANK,
                                        CMEMechanicalParts.COPPER_SAW_HEAD
                                ));
                                output.accept(MechanicalToolItem.newStackWithParts(
                                        CMEMechanicalParts.NETHERITE_GRIP,
                                        CMEMechanicalParts.NETHERITE_COG,
                                        CMEMechanicalParts.NETHER_STAR_ACCELERATOR,
                                        CMEMechanicalParts.NETHERITE_GEARBOX,
                                        CMEMechanicalParts.NETHERITE_TANK,
                                        CMEMechanicalParts.NETHERITE_DRILL_HEAD,
                                        CMEMechanicalParts.BLAZE_AUTOMATON
                                ));
                                output.accept(CMEBlocks.FOOD_REPLICATOR);
                                for (var x : creativeModeItem) {
                                    output.accept(x);
                                }
                            }).build()
            );


    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
