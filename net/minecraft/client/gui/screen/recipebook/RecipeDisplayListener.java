/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;

@Environment(value=EnvType.CLIENT)
public interface RecipeDisplayListener {
    public void onRecipesDisplayed(List<Recipe<?>> var1);
}

