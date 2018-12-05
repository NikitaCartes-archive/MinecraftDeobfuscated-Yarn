package net.minecraft.container;

import net.minecraft.inventory.Inventory;

public interface LockableContainer extends Inventory, ContainerProvider {
	boolean hasContainerLock();

	void setContainerLock(LockContainer lockContainer);

	LockContainer getContainerLock();
}
