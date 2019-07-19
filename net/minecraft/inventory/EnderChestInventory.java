/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EnderChestInventory
extends BasicInventory {
    private EnderChestBlockEntity currentBlockEntity;

    public EnderChestInventory() {
        super(27);
    }

    public void setCurrentBlockEntity(EnderChestBlockEntity enderChestBlockEntity) {
        this.currentBlockEntity = enderChestBlockEntity;
    }

    public void readTags(ListTag listTag) {
        int i;
        for (i = 0; i < this.getInvSize(); ++i) {
            this.setInvStack(i, ItemStack.EMPTY);
        }
        for (i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 0xFF;
            if (j < 0 || j >= this.getInvSize()) continue;
            this.setInvStack(j, ItemStack.fromTag(compoundTag));
        }
    }

    public ListTag getTags() {
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.getInvSize(); ++i) {
            ItemStack itemStack = this.getInvStack(i);
            if (itemStack.isEmpty()) continue;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)i);
            itemStack.toTag(compoundTag);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.currentBlockEntity != null && !this.currentBlockEntity.canPlayerUse(playerEntity)) {
            return false;
        }
        return super.canPlayerUseInv(playerEntity);
    }

    @Override
    public void onInvOpen(PlayerEntity playerEntity) {
        if (this.currentBlockEntity != null) {
            this.currentBlockEntity.onOpen();
        }
        super.onInvOpen(playerEntity);
    }

    @Override
    public void onInvClose(PlayerEntity playerEntity) {
        if (this.currentBlockEntity != null) {
            this.currentBlockEntity.onClose();
        }
        super.onInvClose(playerEntity);
        this.currentBlockEntity = null;
    }
}

