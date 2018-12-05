package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;

public class DoubleLockableContainer implements LockableContainer {
	private final TextComponent customName;
	private final LockableContainer first;
	private final LockableContainer second;

	public DoubleLockableContainer(TextComponent textComponent, LockableContainer lockableContainer, LockableContainer lockableContainer2) {
		this.customName = textComponent;
		if (lockableContainer == null) {
			lockableContainer = lockableContainer2;
		}

		if (lockableContainer2 == null) {
			lockableContainer2 = lockableContainer;
		}

		this.first = lockableContainer;
		this.second = lockableContainer2;
		if (lockableContainer.hasContainerLock()) {
			lockableContainer2.setContainerLock(lockableContainer.getContainerLock());
		} else if (lockableContainer2.hasContainerLock()) {
			lockableContainer.setContainerLock(lockableContainer2.getContainerLock());
		}
	}

	@Override
	public int getInvSize() {
		return this.first.getInvSize() + this.second.getInvSize();
	}

	@Override
	public boolean isInvEmpty() {
		return this.first.isInvEmpty() && this.second.isInvEmpty();
	}

	public boolean isPart(Inventory inventory) {
		return this.first == inventory || this.second == inventory;
	}

	@Override
	public TextComponent getName() {
		if (this.first.hasCustomName()) {
			return this.first.getName();
		} else {
			return this.second.hasCustomName() ? this.second.getName() : this.customName;
		}
	}

	@Override
	public boolean hasCustomName() {
		return this.first.hasCustomName() || this.second.hasCustomName();
	}

	@Override
	public ItemStack getInvStack(int i) {
		return i >= this.first.getInvSize() ? this.second.getInvStack(i - this.first.getInvSize()) : this.first.getInvStack(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return i >= this.first.getInvSize() ? this.second.takeInvStack(i - this.first.getInvSize(), j) : this.first.takeInvStack(i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return i >= this.first.getInvSize() ? this.second.removeInvStack(i - this.first.getInvSize()) : this.first.removeInvStack(i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		if (i >= this.first.getInvSize()) {
			this.second.setInvStack(i - this.first.getInvSize(), itemStack);
		} else {
			this.first.setInvStack(i, itemStack);
		}
	}

	@Override
	public int getInvMaxStackAmount() {
		return this.first.getInvMaxStackAmount();
	}

	@Override
	public void markDirty() {
		this.first.markDirty();
		this.second.markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.first.canPlayerUseInv(playerEntity) && this.second.canPlayerUseInv(playerEntity);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		this.first.onInvOpen(playerEntity);
		this.second.onInvOpen(playerEntity);
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		this.first.onInvClose(playerEntity);
		this.second.onInvClose(playerEntity);
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		return true;
	}

	@Override
	public int getInvProperty(int i) {
		return 0;
	}

	@Override
	public void setInvProperty(int i, int j) {
	}

	@Override
	public int getInvPropertyCount() {
		return 0;
	}

	@Override
	public boolean hasContainerLock() {
		return this.first.hasContainerLock() || this.second.hasContainerLock();
	}

	@Override
	public void setContainerLock(LockContainer lockContainer) {
		this.first.setContainerLock(lockContainer);
		this.second.setContainerLock(lockContainer);
	}

	@Override
	public LockContainer getContainerLock() {
		return this.first.getContainerLock();
	}

	@Override
	public String getContainerId() {
		return this.first.getContainerId();
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new GenericContainer(playerInventory, this, playerEntity);
	}

	@Override
	public void clearInv() {
		this.first.clearInv();
		this.second.clearInv();
	}
}
