package com.smashingmods.alchemylib.api.recipe;

import net.minecraft.world.item.crafting.RecipeInput;

/**
 * The input and output of ProcessingRecipe can be of any type without defined class inheritance.
 * As such, input and output must return an Object as it's not possible to know the correct type in advance.
 *
 * <p>Consumers of this recipe need to handle type casting when getting the input and output.</p>
 */
@SuppressWarnings("unused")
public interface ProcessingRecipeInput extends RecipeInput {

    Object getInput();

    Object getOutput();
}
