/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class LockableContainerBlockEntity
extends BlockEntity
implements Inventory,
NamedScreenHandlerFactory,
Nameable {
    private ContainerLock lock = ContainerLock.EMPTY;
    private Text customName;

    protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.lock = ContainerLock.fromNbt(tag);
        if (tag.contains("CustomName", NbtTypeIds.STRING)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        this.lock.writeNbt(tag);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return tag;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
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

    public boolean checkUnlocked(PlayerEntity player) {
        return LockableContainerBlockEntity.checkUnlocked(player, this.lock, this.getDisplayName());
    }

    public static boolean checkUnlocked(PlayerEntity player, ContainerLock lock, Text containerName) {
        if (player.isSpectator() || lock.canOpen(player.getMainHandStack())) {
            return true;
        }
        player.sendMessage(new TranslatableText("container.isLocked", containerName), true);
        player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return false;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            return this.createScreenHandler(i, playerInventory);
        }
        return null;
    }

    protected abstract ScreenHandler createScreenHandler(int var1, PlayerInventory var2);
}

