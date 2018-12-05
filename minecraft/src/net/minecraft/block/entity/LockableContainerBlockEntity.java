package net.minecraft.block.entity;

import net.minecraft.container.LockContainer;
import net.minecraft.container.LockableContainer;
import net.minecraft.nbt.CompoundTag;

public abstract class LockableContainerBlockEntity extends BlockEntity implements LockableContainer {
	private LockContainer lock = LockContainer.EMPTY;

	protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.lock = LockContainer.deserialize(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (this.lock != null) {
			this.lock.serialize(compoundTag);
		}

		return compoundTag;
	}

	@Override
	public boolean hasContainerLock() {
		return this.lock != null && !this.lock.isEmpty();
	}

	@Override
	public LockContainer getContainerLock() {
		return this.lock;
	}

	@Override
	public void setContainerLock(LockContainer lockContainer) {
		this.lock = lockContainer;
	}
}
