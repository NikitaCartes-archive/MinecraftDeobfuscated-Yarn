/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;

public class BrewingRecipeRegistry {
    public static final int field_30942 = 20;
    private static final List<Recipe<Potion>> POTION_RECIPES = Lists.newArrayList();
    private static final List<Recipe<Item>> ITEM_RECIPES = Lists.newArrayList();
    private static final List<Ingredient> POTION_TYPES = Lists.newArrayList();
    private static final Predicate<ItemStack> POTION_TYPE_PREDICATE = stack -> {
        for (Ingredient ingredient : POTION_TYPES) {
            if (!ingredient.test((ItemStack)stack)) continue;
            return true;
        }
        return false;
    };

    public static boolean isValidIngredient(ItemStack stack) {
        return BrewingRecipeRegistry.isItemRecipeIngredient(stack) || BrewingRecipeRegistry.isPotionRecipeIngredient(stack);
    }

    protected static boolean isItemRecipeIngredient(ItemStack stack) {
        int j = ITEM_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            if (!BrewingRecipeRegistry.ITEM_RECIPES.get((int)i).ingredient.test(stack)) continue;
            return true;
        }
        return false;
    }

    protected static boolean isPotionRecipeIngredient(ItemStack stack) {
        int j = POTION_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            if (!BrewingRecipeRegistry.POTION_RECIPES.get((int)i).ingredient.test(stack)) continue;
            return true;
        }
        return false;
    }

    public static boolean isBrewable(Potion potion) {
        int j = POTION_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            if (BrewingRecipeRegistry.POTION_RECIPES.get((int)i).output != potion) continue;
            return true;
        }
        return false;
    }

    public static boolean hasRecipe(ItemStack input, ItemStack ingredient) {
        if (!POTION_TYPE_PREDICATE.test(input)) {
            return false;
        }
        return BrewingRecipeRegistry.hasItemRecipe(input, ingredient) || BrewingRecipeRegistry.hasPotionRecipe(input, ingredient);
    }

    protected static boolean hasItemRecipe(ItemStack input, ItemStack ingredient) {
        Item item = input.getItem();
        int j = ITEM_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            Recipe<Item> recipe = ITEM_RECIPES.get(i);
            if (recipe.input != item || !recipe.ingredient.test(ingredient)) continue;
            return true;
        }
        return false;
    }

    protected static boolean hasPotionRecipe(ItemStack input, ItemStack ingredient) {
        Potion potion = PotionUtil.getPotion(input);
        int j = POTION_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            Recipe<Potion> recipe = POTION_RECIPES.get(i);
            if (recipe.input != potion || !recipe.ingredient.test(ingredient)) continue;
            return true;
        }
        return false;
    }

    public static ItemStack craft(ItemStack ingredient, ItemStack input) {
        if (!input.isEmpty()) {
            Recipe<Object> recipe;
            int i;
            Potion potion = PotionUtil.getPotion(input);
            Item item = input.getItem();
            int j = ITEM_RECIPES.size();
            for (i = 0; i < j; ++i) {
                recipe = ITEM_RECIPES.get(i);
                if (recipe.input != item || !recipe.ingredient.test(ingredient)) continue;
                return PotionUtil.setPotion(new ItemStack((ItemConvertible)recipe.output), potion);
            }
            j = POTION_RECIPES.size();
            for (i = 0; i < j; ++i) {
                recipe = POTION_RECIPES.get(i);
                if (recipe.input != potion || !recipe.ingredient.test(ingredient)) continue;
                return PotionUtil.setPotion(new ItemStack(item), (Potion)recipe.output);
            }
        }
        return input;
    }

    public static void registerDefaults() {
        BrewingRecipeRegistry.registerPotionType(Items.POTION);
        BrewingRecipeRegistry.registerPotionType(Items.SPLASH_POTION);
        BrewingRecipeRegistry.registerPotionType(Items.LINGERING_POTION);
        BrewingRecipeRegistry.registerItemRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        BrewingRecipeRegistry.registerItemRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    private static void registerItemRecipe(Item input, Item ingredient, Item output) {
        if (!(input instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(input));
        }
        if (!(output instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(output));
        }
        ITEM_RECIPES.add(new Recipe<Item>(input, Ingredient.ofItems(ingredient), output));
    }

    private static void registerPotionType(Item item) {
        if (!(item instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(item));
        }
        POTION_TYPES.add(Ingredient.ofItems(item));
    }

    private static void registerPotionRecipe(Potion input, Item item, Potion output) {
        POTION_RECIPES.add(new Recipe<Potion>(input, Ingredient.ofItems(item), output));
    }

    static class Recipe<T> {
        final T input;
        final Ingredient ingredient;
        final T output;

        public Recipe(T input, Ingredient ingredient, T output) {
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }
    }
}

