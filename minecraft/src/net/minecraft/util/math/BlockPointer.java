package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.server.world.ServerWorld;

public record BlockPointer(ServerWorld world, BlockPos pos, BlockState state, DispenserBlockEntity blockEntity) {
	public Vec3d centerPos() {
		return this.pos.toCenterPos();
	}
}
