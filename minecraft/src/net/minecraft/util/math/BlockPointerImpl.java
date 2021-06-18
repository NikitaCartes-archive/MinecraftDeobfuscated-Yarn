package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

public class BlockPointerImpl implements BlockPointer {
	private final ServerWorld world;
	private final BlockPos pos;

	public BlockPointerImpl(ServerWorld world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	@Override
	public ServerWorld getWorld() {
		return this.world;
	}

	@Override
	public double getX() {
		return (double)this.pos.getX() + 0.5;
	}

	@Override
	public double getY() {
		return (double)this.pos.getY() + 0.5;
	}

	@Override
	public double getZ() {
		return (double)this.pos.getZ() + 0.5;
	}

	@Override
	public BlockPos getPos() {
		return this.pos;
	}

	@Override
	public BlockState getBlockState() {
		return this.world.getBlockState(this.pos);
	}

	@Override
	public <T extends BlockEntity> T getBlockEntity() {
		return (T)this.world.getBlockEntity(this.pos);
	}
}
