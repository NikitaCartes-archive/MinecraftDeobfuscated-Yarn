/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.ContainerType;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class FurnaceContainer
extends AbstractFurnaceContainer {
    public FurnaceContainer(int i, PlayerInventory playerInventory) {
        super(ContainerType.FURNACE, RecipeType.SMELTING, i, playerInventory);
    }

    public FurnaceContainer(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ContainerType.FURNACE, RecipeType.SMELTING, i, playerInventory, inventory, propertyDelegate);
    }
}

