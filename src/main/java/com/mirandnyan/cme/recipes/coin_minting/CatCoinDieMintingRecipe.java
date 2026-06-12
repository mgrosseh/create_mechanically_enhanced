package com.mirandnyan.cme.recipes.coin_minting;

import com.mirandnyan.cme.CMERecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CatCoinDieMintingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

    public CatCoinDieMintingRecipe(ProcessingRecipeParams params) {
        super(CMERecipeTypes.COIN_MINTING, params);
    }

    @Override
    public boolean matches(SingleRecipeInput inv, @NotNull Level worldIn) {
        return ingredients.getFirst()
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    public static boolean canMint(Level world, ItemStack stack) {
        return !getMatchingRecipes(world, stack).isEmpty();
    }

    public static ItemStack applyMint(Level world, ItemStack toMint) {
        List<RecipeHolder<Recipe<SingleRecipeInput>>> matchingRecipes = getMatchingRecipes(world, toMint);
        if (!matchingRecipes.isEmpty())
            return matchingRecipes.getFirst().value()
                    .assemble(new SingleRecipeInput(toMint), world.registryAccess())
                    .copy();
        return toMint;
    }

    public static List<RecipeHolder<Recipe<SingleRecipeInput>>> getMatchingRecipes(Level world, ItemStack stack) {
        return world.getRecipeManager()
                .getRecipesFor(CMERecipeTypes.COIN_MINTING.getType(), new SingleRecipeInput(stack), world);
    }
}
