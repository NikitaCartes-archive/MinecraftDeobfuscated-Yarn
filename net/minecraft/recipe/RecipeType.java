/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * The recipe type allows matching recipes more efficiently by only checking
 * recipes under a given type.
 * 
 * @param <T> the common supertype of recipes within a recipe type
 */
public interface RecipeType<T extends Recipe<?>> {
    public static final RecipeType<CraftingRecipe> CRAFTING = RecipeType.register("crafting");
    public static final RecipeType<SmeltingRecipe> SMELTING = RecipeType.register("smelting");
    public static final RecipeType<BlastingRecipe> BLASTING = RecipeType.register("blasting");
    public static final RecipeType<SmokingRecipe> SMOKING = RecipeType.register("smoking");
    public static final RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeType.register("campfire_cooking");
    public static final RecipeType<StonecuttingRecipe> STONECUTTING = RecipeType.register("stonecutting");
    public static final RecipeType<SmithingRecipe> SMITHING = RecipeType.register("smithing");

    public static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(id), new RecipeType<T>(){

            public String toString() {
                return id;
            }
        });
    }

    /**
     * {@return the given {@code recipe} if it matches, otherwise empty}
     * 
     * <p>This utility method casts the {@code recipe} from {@code Recipe<C>} to
     * {@code T} conveniently.
     * 
     * @param inventory the input inventory
     * @param world the input world
     * @param recipe the recipe to match and cast
     */
    default public <C extends Inventory> Optional<T> match(Recipe<C> recipe, World world, C inventory) {
        return recipe.matches(inventory, world) ? Optional.of(recipe) : Optional.empty();
    }
}

