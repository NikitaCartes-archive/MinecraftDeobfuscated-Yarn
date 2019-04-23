/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerLock;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Nameable;
import org.jetbrains.annotations.Nullable;

public abstract class LockableContainerBlockEntity
extends BlockEntity
implements Inventory,
NameableContainerProvider,
Nameable {
    private ContainerLock lock = ContainerLock.NONE;
    private Component customName;

    protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.lock = ContainerLock.deserialize(compoundTag);
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = Component.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        this.lock.serialize(compoundTag);
        if (this.customName != null) {
            compoundTag.putString("CustomName", Component.Serializer.toJsonString(this.customName));
        }
        return compoundTag;
    }

    public void setCustomName(Component component) {
        this.customName = component;
    }

    @Override
    public Component getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    protected abstract Component getContainerName();

    public boolean checkUnlocked(PlayerEntity playerEntity) {
        return LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName());
    }

    public static boolean checkUnlocked(PlayerEntity playerEntity, ContainerLock containerLock, Component component) {
        if (playerEntity.isSpectator() || containerLock.isEmpty(playerEntity.getMainHandStack())) {
            return true;
        }
        playerEntity.addChatMessage(new TranslatableComponent("container.isLocked", component), true);
        playerEntity.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return false;
    }

    @Override
    @Nullable
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            return this.createContainer(i, playerInventory);
        }
        return null;
    }

    protected abstract Container createContainer(int var1, PlayerInventory var2);
}

