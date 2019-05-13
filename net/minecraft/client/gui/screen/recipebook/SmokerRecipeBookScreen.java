/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.item.Item;

@Environment(value=EnvType.CLIENT)
public class SmokerRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isSmokerFilteringCraftable();
    }

    @Override
    protected void setFilteringCraftable(boolean bl) {
        this.recipeBook.setSmokerFilteringCraftable(bl);
    }

    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isSmokerGuiOpen();
    }

    @Override
    protected void setGuiOpen(boolean bl) {
        this.recipeBook.setSmokerGuiOpen(bl);
    }

    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.smokable";
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

