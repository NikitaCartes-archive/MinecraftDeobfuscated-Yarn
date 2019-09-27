package net.minecraft.block.entity;

public class TrappedChestBlockEntity extends ChestBlockEntity {
	public TrappedChestBlockEntity() {
		super(BlockEntityType.TRAPPED_CHEST);
	}

	@Override
	protected void onInvOpenOrClose() {
		super.onInvOpenOrClose();
		this.world.updateNeighborsAlways(this.pos.method_10074(), this.getCachedState().getBlock());
	}
}
