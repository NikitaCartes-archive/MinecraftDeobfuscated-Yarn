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

public class SmokerContainer
extends AbstractFurnaceContainer {
    public SmokerContainer(int i, PlayerInventory playerInventory) {
        super(ContainerType.SMOKER, RecipeType.SMOKING, i, playerInventory);
    }

    public SmokerContainer(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ContainerType.SMOKER, RecipeType.SMOKING, i, playerInventory, inventory, propertyDelegate);
    }
}

