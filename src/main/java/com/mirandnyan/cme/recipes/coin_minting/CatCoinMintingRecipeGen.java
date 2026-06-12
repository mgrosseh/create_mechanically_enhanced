package com.mirandnyan.cme.recipes.coin_minting;

import com.mirandnyan.cme.CMERecipeTypes;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CatCoinMintingRecipeGen extends StandardProcessingRecipeGen<CatCoinDieMintingRecipe> {

    public CatCoinMintingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected CMERecipeTypes getRecipeType() {
        return CMERecipeTypes.COIN_MINTING;
    }
}
