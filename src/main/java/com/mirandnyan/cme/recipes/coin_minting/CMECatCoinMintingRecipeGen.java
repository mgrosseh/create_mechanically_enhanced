package com.mirandnyan.cme.recipes.coin_minting;

import com.mirandnyan.cme.CMEItems;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMECatCoinMintingRecipeGen extends CatCoinMintingRecipeGen {

    BaseRecipeProvider.GeneratedRecipe
            COPPER_COIN = create(CreateMechanicallyEnhanced.MOD_ID,
            () -> Items.COPPER_INGOT, b -> b.output(CMEItems.MINTED_COPPER_COIN.get()))
            ;

    public CMECatCoinMintingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMechanicallyEnhanced.MOD_ID);
    }
}
