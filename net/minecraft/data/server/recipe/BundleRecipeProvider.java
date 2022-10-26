/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

public class BundleRecipeProvider
extends RecipeProvider {
    public BundleRecipeProvider(DataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    protected void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.BUNDLE).input(Character.valueOf('#'), Items.RABBIT_HIDE).input(Character.valueOf('-'), Items.STRING).pattern("-#-").pattern("# #").pattern("###").criterion("has_string", BundleRecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter);
    }
}

