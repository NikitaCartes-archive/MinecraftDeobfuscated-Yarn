/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HorseContainer
extends Container {
    private final Inventory playerInv;
    private final HorseBaseEntity entity;

    public HorseContainer(int i, PlayerInventory playerInventory, Inventory inventory, final HorseBaseEntity horseBaseEntity) {
        super(null, i);
        int m;
        int l;
        this.playerInv = inventory;
        this.entity = horseBaseEntity;
        int j = 3;
        inventory.onInvOpen(playerInventory.player);
        int k = -18;
        this.addSlot(new Slot(inventory, 0, 8, 18){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() == Items.SADDLE && !this.hasStack() && horseBaseEntity.canBeSaddled();
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return horseBaseEntity.canBeSaddled();
            }
        });
        this.addSlot(new Slot(inventory, 1, 8, 36){

            @Override
            public boolean canInsert(ItemStack itemStack) {
                return horseBaseEntity.canEquip(itemStack);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return horseBaseEntity.canEquip();
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        if (horseBaseEntity instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)horseBaseEntity).hasChest()) {
            for (l = 0; l < 3; ++l) {
                for (m = 0; m < ((AbstractDonkeyEntity)horseBaseEntity).method_6702(); ++m) {
                    this.addSlot(new Slot(inventory, 2 + m + l * ((AbstractDonkeyEntity)horseBaseEntity).method_6702(), 80 + m * 18, 18 + l * 18));
                }
            }
        }
        for (l = 0; l < 3; ++l) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 102 + l * 18 + -18));
            }
        }
        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return this.playerInv.canPlayerUseInv(playerEntity) && this.entity.isAlive() && this.entity.distanceTo(playerEntity) < 8.0f;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slotList.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            int j = this.playerInv.getInvSize();
            if (i < j) {
                if (!this.insertItem(itemStack2, j, this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (j <= 2 || !this.insertItem(itemStack2, 2, j, false)) {
                int l;
                int k = j;
                int m = l = k + 27;
                int n = m + 9;
                if (i >= m && i < n ? !this.insertItem(itemStack2, k, l, false) : (i >= k && i < l ? !this.insertItem(itemStack2, m, n, false) : !this.insertItem(itemStack2, m, l, false))) {
                    return ItemStack.EMPTY;
                }
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
        this.playerInv.onInvClose(playerEntity);
    }
}

