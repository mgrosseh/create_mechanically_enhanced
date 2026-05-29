package com.mirandnyan.mired;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CVAItems {
    private static final CreateRegistrate REGISTRATE = CreateVariousAdditions.getRegistrate();

//    public static final ItemEntry<Item> DIODE_BASE = REGISTRATE.item("diode_base", Item::new)
//            .model((ctx, prov) ->
//                    prov.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateVariousAdditions.MOD_ID, "item/diode_base")))
//            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
//                    .pattern("RBR")
//                    .pattern("SSS")
//                    .define('R', Items.REDSTONE)
//                    .define('B', AllItems.BRASS_SHEET)
//                    .define('S', CVATags.Items.STONES)
//                    .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(AllItems.BRASS_SHEET))
//                    .save(p, CreateVariousAdditions.asResource("diode_base"))
//            )
//            .register();


    public static void register() {
        // load class
    }
}
