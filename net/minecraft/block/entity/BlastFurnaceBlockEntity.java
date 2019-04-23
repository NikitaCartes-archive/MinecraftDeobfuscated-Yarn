/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.recipe.RecipeType;

public class BlastFurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    public BlastFurnaceBlockEntity() {
        super(BlockEntityType.BLAST_FURNACE, RecipeType.BLASTING);
    }

    @Override
    protected Component getContainerName() {
        return new TranslatableComponent("container.blast_furnace", new Object[0]);
    }

    @Override
    protected int getFuelTime(ItemStack itemStack) {
        return super.getFuelTime(itemStack) / 2;
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return new BlastFurnaceContainer(i, playerInventory, this, this.propertyDelegate);
    }
}

