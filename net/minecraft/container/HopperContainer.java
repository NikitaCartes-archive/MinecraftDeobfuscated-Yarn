/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class HopperContainer
extends Container {
    private final Inventory inventory;

    public HopperContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, new BasicInventory(5));
    }

    public HopperContainer(int i, PlayerInventory playerInventory, Inventory inventory) {
        super(ContainerType.HOPPER, i);
        int k;
        this.inventory = inventory;
        HopperContainer.checkContainerSize(inventory, 5);
        inventory.onInvOpen(playerInventory.player);
        int j = 51;
        for (k = 0; k < 5; ++k) {
            this.addSlot(new Slot(inventory, k, 44 + k * 18, 20));
        }
        for (k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, k * 18 + 51));
            }
        }
        for (k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 109));
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

