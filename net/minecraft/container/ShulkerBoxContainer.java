/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.ShulkerBoxSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxContainer
extends Container {
    private final Inventory inventory;

    public ShulkerBoxContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, new BasicInventory(27));
    }

    public ShulkerBoxContainer(int i, PlayerInventory playerInventory, Inventory inventory) {
        super(ContainerType.SHULKER_BOX, i);
        int m;
        int l;
        ShulkerBoxContainer.checkContainerSize(inventory, 27);
        this.inventory = inventory;
        inventory.onInvOpen(playerInventory.player);
        int j = 3;
        int k = 9;
        for (l = 0; l < 3; ++l) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new ShulkerBoxSlot(inventory, m + l * 9, 8 + m * 18, 18 + l * 18));
            }
        }
        for (l = 0; l < 3; ++l) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 84 + l * 18));
            }
        }
        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return this.inventory.canPlayerUseInv(playerEntity);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (i < this.inventory.getInvSize() ? !this.insertItem(itemStack2, this.inventory.getInvSize(), this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.inventory.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        this.inventory.onInvClose(playerEntity);
    }
}

