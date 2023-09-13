package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.server.world.ServerWorld;

public record BlockPointer(ServerWorld getWorld, BlockPos getPos, BlockState getBlockState, DispenserBlockEntity getBlockEntity) {
	public Vec3d method_53906() {
		return this.getPos.toCenterPos();
	}
}
