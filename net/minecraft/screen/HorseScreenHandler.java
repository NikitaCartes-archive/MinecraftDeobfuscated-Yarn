/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HorseScreenHandler
extends ScreenHandler {
    private final Inventory inventory;
    private final HorseBaseEntity entity;

    public HorseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, final HorseBaseEntity entity) {
        super(null, syncId);
        int l;
        int k;
        this.inventory = inventory;
        this.entity = entity;
        int i = 3;
        inventory.onOpen(playerInventory.player);
        int j = -18;
        this.addSlot(new Slot(inventory, 0, 8, 18){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasStack() && entity.canBeSaddled();
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return entity.canBeSaddled();
            }
        });
        this.addSlot(new Slot(inventory, 1, 8, 36){

            @Override
            public boolean canInsert(ItemStack stack) {
                return entity.isHorseArmor(stack);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return entity.hasArmorSlot();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        if (entity instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)entity).hasChest()) {
            for (k = 0; k < 3; ++k) {
                for (l = 0; l < ((AbstractDonkeyEntity)entity).getInventoryColumns(); ++l) {
                    this.addSlot(new Slot(inventory, 2 + l + k * ((AbstractDonkeyEntity)entity).getInventoryColumns(), 80 + l * 18, 18 + k * 18));
                }
            }
        }
        for (k = 0; k < 3; ++k) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 102 + k * 18 + -18));
            }
        }
        for (k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player) && this.entity.isAlive() && this.entity.distanceTo(player) < 8.0f;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            int i = this.inventory.size();
            if (index < i) {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true)) {
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
            } else if (i <= 2 || !this.insertItem(itemStack2, 2, i, false)) {
                int k;
                int j = i;
                int l = k = j + 27;
                int m = l + 9;
                if (index >= l && index < m ? !this.insertItem(itemStack2, j, k, false) : (index >= j && index < k ? !this.insertItem(itemStack2, l, m, false) : !this.insertItem(itemStack2, l, k, false))) {
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
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}

