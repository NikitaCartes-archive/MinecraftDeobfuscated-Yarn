/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.recipe.RecipeType;

public class FurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    public FurnaceBlockEntity() {
        super(BlockEntityType.FURNACE, RecipeType.SMELTING);
    }

    @Override
    protected Component getContainerName() {
        return new TranslatableComponent("container.furnace", new Object[0]);
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return new FurnaceContainer(i, playerInventory, this, this.propertyDelegate);
    }
}

