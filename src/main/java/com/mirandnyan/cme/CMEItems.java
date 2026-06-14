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
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.registries.Registries;
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

    public static final ItemEntry<Item> DEFAULT_GRIP = part("part_default_grip");
    public static final ItemEntry<Item> NETHERITE_GRIP = part("part_netherite_grip");
    public static final ItemEntry<Item> SMALL_BRASS_VERTICAL_GEARBOX = part("small_vertical_brass_gearbox");
    public static final ItemEntry<Item> SMALL_ANDESITE_VERTICAL_GEARBOX = part("small_vertical_andesite_gearbox");
    public static final ItemEntry<Item> SMALL_COPPER_TANK = part("small_copper_tank");
    public static final ItemEntry<Item> SMALL_NETHERITE_TANK = part("small_netherite_tank");
    public static final ItemEntry<Item> SMALL_WOODEN_COG = part("small_wooden_cog");
    public static final ItemEntry<Item> SMALL_BRASS_COG = part("small_brass_cog");
    public static final ItemEntry<Item> SMALL_NETHERITE_COG = part("small_netherite_cog");
    public static final ItemEntry<Item> IRON_DRILL_HEAD = part("part_iron_drill_head");
    public static final ItemEntry<Item> DIAMOND_DRILL_HEAD = part("part_diamond_drill_head");
    public static final ItemEntry<Item> NETHERITE_DRILL_HEAD = part("part_netherite_drill_head");

    public static final ItemEntry<Item> SMALL_MECHANICAL_BLAZE = part("small_mechanical_blaze");
    public static final ItemEntry<Item> SMALL_MECHANICAL_CAT = part("small_mechanical_cat");
    public static final ItemEntry<Item> SMALL_MECHANICAL_PUMPKIN = part("small_mechanical_pumpkin");

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

    private static ItemEntry<Item> part(String name) {
        return inTab(REGISTRATE.item(name, Item::new)
                .properties(p -> p.stacksTo(1))
                .model(CMEItems::getExisting)
                .register());
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
                                        MechanicalPart.DEFAULT_GRIP,
                                        MechanicalPart.WOODEN_COG,
                                        MechanicalPart.ANDESITE_GEARBOX,
                                        MechanicalPart.IRON_DRILL_HEAD
                                ));
                                output.accept(MechanicalToolItem.newStackWithParts(
                                        MechanicalPart.DEFAULT_GRIP,
                                        MechanicalPart.WOODEN_COG,
                                        MechanicalPart.ANDESITE_GEARBOX,
                                        MechanicalPart.COPPER_TANK,
                                        MechanicalPart.IRON_DRILL_HEAD
                                ));
                                output.accept(MechanicalToolItem.newStackWithParts(
                                        MechanicalPart.DEFAULT_GRIP,
                                        MechanicalPart.NETHERITE_COG,
                                        MechanicalPart.BRASS_GEARBOX,
                                        MechanicalPart.NETHERITE_TANK,
                                        MechanicalPart.NETHERITE_DRILL_HEAD,
                                        MechanicalPart.SMALL_MECHANICAL_BLAZE
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
