package net.minecraft.block.entity;

public class TrappedChestBlockEntity extends ChestBlockEntity {
	public TrappedChestBlockEntity() {
		super(BlockEntityType.TRAPPED_CHEST);
	}

	@Override
	protected void method_11049() {
		super.method_11049();
		this.world.updateNeighborsAlways(this.pos.down(), this.getCachedState().getBlock());
	}
}
