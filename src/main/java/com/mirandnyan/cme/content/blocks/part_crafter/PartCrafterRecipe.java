package com.mirandnyan.cme.content.blocks.part_crafter;

import com.mirandnyan.cme.CMEBlocks;
import com.mirandnyan.cme.CMERecipeTypes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PartCrafterRecipe extends SingleItemRecipe {

    public PartCrafterRecipe(String group, Ingredient ingredient, ItemStack result) {
        //noinspection DataFlowIssue // passing null overriding only usage getSerializer
        super(CMERecipeTypes.PART_CRAFTING.getType(), null, group, ingredient, result);
    }


    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CMERecipeTypes.PART_CRAFTING.getSerializer();
    }


    public boolean matches(SingleRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.item());
    }

    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(CMEBlocks.PART_CRAFTER);
    }

    public static SingleItemRecipeBuilder builder(Ingredient ingredient, RecipeCategory category, ItemLike result) {
        return new SingleItemRecipeBuilder(category, PartCrafterRecipe::new, ingredient, result, 1);
    }

    public static SingleItemRecipeBuilder builder(Ingredient ingredient, RecipeCategory category, ItemLike result, int count) {
        return new SingleItemRecipeBuilder(category, PartCrafterRecipe::new, ingredient, result, count);
    }

    public static class Serializer extends SingleItemRecipe.Serializer<PartCrafterRecipe> {
        public Serializer() {
            super(PartCrafterRecipe::new);
        }
    }
}
