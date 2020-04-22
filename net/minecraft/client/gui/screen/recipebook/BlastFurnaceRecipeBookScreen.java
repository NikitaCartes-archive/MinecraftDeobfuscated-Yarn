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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class BlastFurnaceRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isBlastFurnaceFilteringCraftable();
    }

    @Override
    protected void setFilteringCraftable(boolean filteringCraftable) {
        this.recipeBook.setBlastFurnaceFilteringCraftable(filteringCraftable);
    }

    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isBlastFurnaceGuiOpen();
    }

    @Override
    protected void setGuiOpen(boolean opened) {
        this.recipeBook.setBlastFurnaceGuiOpen(opened);
    }

    @Override
    protected Text getToggleCraftableButtonText() {
        return new TranslatableText("gui.recipebook.toggleRecipes.blastable");
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

