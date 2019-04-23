/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.container;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.container.AbstractFurnaceRecipeBookScreen;
import net.minecraft.item.Item;

@Environment(value=EnvType.CLIENT)
public class BlastFurnaceRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isBlastFurnaceFilteringCraftable();
    }

    @Override
    protected void setFilteringCraftable(boolean bl) {
        this.recipeBook.setBlastFurnaceFilteringCraftable(bl);
    }

    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isBlastFurnaceGuiOpen();
    }

    @Override
    protected void setGuiOpen(boolean bl) {
        this.recipeBook.setBlastFurnaceGuiOpen(bl);
    }

    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.blastable";
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

