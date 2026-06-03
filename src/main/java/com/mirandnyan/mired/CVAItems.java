package com.mirandnyan.mired;

import com.mirandnyan.mired.content.equipment.mechanical_drill.MechanicalDrill;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class CVAItems {
    private static final CreateRegistrate REGISTRATE = CreateVariousAdditions.getRegistrate();

    public static final ItemEntry<MechanicalDrill> MECHANICAL_DRILL = REGISTRATE.item("mechanical_drill", MechanicalDrill::new)
            .model(getExisting("default"))
            .register();


    public static final ItemEntry<Item> SMALL_BRASS_VERTICAL_GEARBOX = REGISTRATE.item("small_vertical_brass_gearbox", Item::new)
            .properties(p -> p.stacksTo(1))
            .model(CVAItems::getExisting)
            .register();
    public static final ItemEntry<Item> SMALL_ANDESITE_VERTICAL_GEARBOX = REGISTRATE.item("small_vertical_andesite_gearbox", Item::new)
            .properties(p -> p.stacksTo(1))
            .model(CVAItems::getExisting)
            .register();

    public static final ItemEntry<Item> SMALL_COPPER_TANK = REGISTRATE.item("small_copper_tank", Item::new)
            .properties(p -> p.stacksTo(1))
            .model(CVAItems::getExisting)
            .register();
    public static final ItemEntry<Item> SMALL_NETHERITE_TANK = REGISTRATE.item("small_netherite_tank", Item::new)
            .properties(p -> p.stacksTo(1))
            .model(CVAItems::getExisting)
            .register();


//    public static final ItemEntry<Item> TEST_ITEM = REGISTRATE.item("test", Item::new)
//            .model(CVAItems::getExisting)
//            .register();


    protected static <T extends Item> ModelFile getExisting(DataGenContext<Item, T> ctx, RegistrateItemModelProvider prov) {
        return prov.getExistingFile(CreateVariousAdditions.asResource("item/" + ctx.getName()));
    }
    protected static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> getExisting(String sublocation) {
        return (ctx, prov) ->
                prov.withExistingParent("item/" + ctx.getName(), prov.modLoc("item/" + ctx.getName() + "/" + sublocation));
    }


    public static void register() {
        // load class
    }
}
