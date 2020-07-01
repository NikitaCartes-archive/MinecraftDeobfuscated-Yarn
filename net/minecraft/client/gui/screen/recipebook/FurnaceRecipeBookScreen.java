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
public class FurnaceRecipeBookScreen
extends AbstractFurnaceRecipeBookScreen {
    @Override
    protected Text getToggleCraftableButtonText() {
        return new TranslatableText("gui.recipebook.toggleRecipes.smeltable");
    }

    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}

