/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public interface CraftingRecipeJsonBuilder {
    public static final Identifier field_39377 = new Identifier("recipes/root");

    public CraftingRecipeJsonBuilder criterion(String var1, CriterionConditions var2);

    public CraftingRecipeJsonBuilder group(@Nullable String var1);

    public Item getOutputItem();

    public void offerTo(Consumer<RecipeJsonProvider> var1, Identifier var2);

    default public void offerTo(Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, CraftingRecipeJsonBuilder.getItemId(this.getOutputItem()));
    }

    default public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipePath) {
        Identifier identifier2 = new Identifier(recipePath);
        Identifier identifier = CraftingRecipeJsonBuilder.getItemId(this.getOutputItem());
        if (identifier2.equals(identifier)) {
            throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
        }
        this.offerTo(exporter, identifier2);
    }

    public static Identifier getItemId(ItemConvertible item) {
        return Registry.ITEM.getId(item.asItem());
    }
}

