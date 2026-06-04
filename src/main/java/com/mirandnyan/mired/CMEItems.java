package com.mirandnyan.mired;

import com.mirandnyan.mired.content.equipment.mechanical_drill.MechanicalDrillItem;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

import static com.mirandnyan.mired.CreateMechanicallyEnhanced.REGISTRATE;

public class CMEItems {
    public static final List<ItemEntry<?>> creativeModeItem = new ArrayList<>();

    protected static ItemEntry<Item> part(String name) {
        var item = REGISTRATE.item(name, Item::new)
                .properties(p -> p.stacksTo(1))
                .model(CMEItems::getExisting)
                .register();
        creativeModeItem.add(item);
        return item;
    }

    public static final ItemEntry<MechanicalDrillItem> MECHANICAL_DRILL = REGISTRATE.item("mechanical_drill", MechanicalDrillItem::new)
            .model(getExisting("default"))
            .register();

    public static final ItemEntry<Item> SMALL_BRASS_VERTICAL_GEARBOX = part("small_vertical_brass_gearbox");
    public static final ItemEntry<Item> SMALL_ANDESITE_VERTICAL_GEARBOX = part("small_vertical_andesite_gearbox");
    public static final ItemEntry<Item> SMALL_COPPER_TANK = part("small_copper_tank");
    public static final ItemEntry<Item> SMALL_NETHERITE_TANK = part("small_netherite_tank");
    public static final ItemEntry<Item> SMALL_WOODEN_COG = part("small_wooden_cog");
    public static final ItemEntry<Item> SMALL_BRASS_COG = part("small_brass_cog");
    public static final ItemEntry<Item> DEFAULT_GRIP = part("part_default_grip");
    public static final ItemEntry<Item> IRON_DRILL_HEAD = part("part_iron_drill_head");
    public static final ItemEntry<Item> DIAMOND_DRILL_HEAD = part("part_diamond_drill_head");
    public static final ItemEntry<Item> NETHERITE_DRILL_HEAD = part("part_netherite_drill_head");

    public static final ItemEntry<Item> SMALL_MECHANICAL_BLAZE = part("small_mechanical_blaze");


    protected static <T extends Item> ModelFile getExisting(DataGenContext<Item, T> ctx, RegistrateItemModelProvider prov) {
        return prov.getExistingFile(CreateMechanicallyEnhanced.asResource("item/" + ctx.getName()));
    }
    protected static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> getExisting(String sublocation) {
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
                                output.accept(MechanicalDrillItem.defaultItemStack());
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
