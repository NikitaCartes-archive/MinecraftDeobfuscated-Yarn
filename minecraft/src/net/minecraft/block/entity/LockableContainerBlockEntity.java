package net.minecraft.block.entity;

import javax.annotation.Nullable;
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

public abstract class LockableContainerBlockEntity extends BlockEntity implements Inventory, NameableContainerProvider, Nameable {
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
		return this.customName != null ? this.customName : this.getContainerName();
	}

	@Override
	public Component getDisplayName() {
		return this.getName();
	}

	@Nullable
	@Override
	public Component getCustomName() {
		return this.customName;
	}

	protected abstract Component getContainerName();

	public boolean checkUnlocked(PlayerEntity playerEntity) {
		return checkUnlocked(playerEntity, this.lock, this.getDisplayName());
	}

	public static boolean checkUnlocked(PlayerEntity playerEntity, ContainerLock containerLock, Component component) {
		if (!playerEntity.isSpectator() && !containerLock.isEmpty(playerEntity.getMainHandStack())) {
			playerEntity.addChatMessage(new TranslatableComponent("container.isLocked", component), true);
			playerEntity.playSound(SoundEvents.field_14731, SoundCategory.field_15245, 1.0F, 1.0F);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.checkUnlocked(playerEntity) ? this.createContainer(i, playerInventory) : null;
	}

	protected abstract Container createContainer(int i, PlayerInventory playerInventory);
}
