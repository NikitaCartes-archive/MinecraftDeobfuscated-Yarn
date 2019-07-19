/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.ArrayPropertyDelegate;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class LecternContainer
extends Container {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public LecternContainer(int i) {
        this(i, new BasicInventory(1), new ArrayPropertyDelegate(1));
    }

    public LecternContainer(int i, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ContainerType.LECTERN, i);
        LecternContainer.checkContainerSize(inventory, 1);
        LecternContainer.checkContainerDataCount(propertyDelegate, 1);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new Slot(inventory, 0, 0, 0){

            @Override
            public void markDirty() {
                super.markDirty();
                LecternContainer.this.onContentChanged(this.inventory);
            }
        });
        this.addProperties(propertyDelegate);
    }

    @Override
    public boolean onButtonClick(PlayerEntity playerEntity, int i) {
        if (i >= 100) {
            int j = i - 100;
            this.setProperty(0, j);
            return true;
        }
        switch (i) {
            case 2: {
                int j = this.propertyDelegate.get(0);
                this.setProperty(0, j + 1);
                return true;
            }
            case 1: {
                int j = this.propertyDelegate.get(0);
                this.setProperty(0, j - 1);
                return true;
            }
            case 3: {
                if (!playerEntity.canModifyWorld()) {
                    return false;
                }
                ItemStack itemStack = this.inventory.removeInvStack(0);
                this.inventory.markDirty();
                if (!playerEntity.inventory.insertStack(itemStack)) {
                    playerEntity.dropItem(itemStack, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setProperty(int i, int j) {
        super.setProperty(i, j);
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return this.inventory.canPlayerUseInv(playerEntity);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getBookItem() {
        return this.inventory.getInvStack(0);
    }

    @Environment(value=EnvType.CLIENT)
    public int getPage() {
        return this.propertyDelegate.get(0);
    }
}

