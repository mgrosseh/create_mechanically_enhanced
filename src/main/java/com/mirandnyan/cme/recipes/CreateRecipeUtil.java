package com.mirandnyan.cme.recipes;

import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.ResourceLocation;

public class CreateRecipeUtil {
    public static ItemApplicationRecipe.Builder<DeployerApplicationRecipe> deployer(ResourceLocation location) {
        return new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new, location);
    }

    public static StandardProcessingRecipe.Builder<PressingRecipe> pressing(ResourceLocation location) {
        return new StandardProcessingRecipe.Builder<>(PressingRecipe::new, location);
    }
}
