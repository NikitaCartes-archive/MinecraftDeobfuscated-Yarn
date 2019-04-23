/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.recipebook.RecipeBookGui;

@Environment(value=EnvType.CLIENT)
public interface RecipeBookProvider {
    public void refreshRecipeBook();

    public RecipeBookGui getRecipeBookGui();
}

