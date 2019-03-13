package net.minecraft.block.entity;

public class TrappedChestBlockEntity extends ChestBlockEntity {
	public TrappedChestBlockEntity() {
		super(BlockEntityType.TRAPPED_CHEST);
	}

	@Override
	protected void onInvOpenOrClose() {
		super.onInvOpenOrClose();
		this.world.method_8452(this.field_11867.down(), this.method_11010().getBlock());
	}
}
