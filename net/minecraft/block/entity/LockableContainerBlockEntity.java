/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import org.jetbrains.annotations.Nullable;

public abstract class LockableContainerBlockEntity
extends BlockEntity
implements Inventory,
NameableContainerFactory,
Nameable {
    private ContainerLock lock = ContainerLock.EMPTY;
    private Text customName;

    protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.lock = ContainerLock.fromTag(compoundTag);
        if (compoundTag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(compoundTag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        this.lock.toTag(compoundTag);
        if (this.customName != null) {
            compoundTag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return compoundTag;
    }

    public void setCustomName(Text text) {
        this.customName = text;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    protected abstract Text getContainerName();

    public boolean checkUnlocked(PlayerEntity playerEntity) {
        return LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName());
    }

    public static boolean checkUnlocked(PlayerEntity playerEntity, ContainerLock containerLock, Text text) {
        if (playerEntity.isSpectator() || containerLock.canOpen(playerEntity.getMainHandStack())) {
            return true;
        }
        playerEntity.addChatMessage(new TranslatableText("container.isLocked", text), true);
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

