package net.minecraft.block.entity;

public class TrappedChestBlockEntity extends ChestBlockEntity {
	public TrappedChestBlockEntity() {
		super(BlockEntityType.field_11891);
	}

	@Override
	protected void onInvOpenOrClose() {
		super.onInvOpenOrClose();
		this.world.updateNeighborsAlways(this.pos.down(), this.getCachedState().getBlock());
	}
}
