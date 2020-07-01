/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class FurnaceScreenHandler
extends AbstractFurnaceScreenHandler {
    public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory);
    }

    public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
    }
}

