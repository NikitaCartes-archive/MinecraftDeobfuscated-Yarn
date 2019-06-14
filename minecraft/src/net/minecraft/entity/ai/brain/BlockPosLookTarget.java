package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockPosLookTarget implements LookTarget {
	private final BlockPos blockPos;
	private final Vec3d field_18341;

	public BlockPosLookTarget(BlockPos blockPos) {
		this.blockPos = blockPos;
		this.field_18341 = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Override
	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	@Override
	public Vec3d method_18991() {
		return this.field_18341;
	}

	@Override
	public boolean isSeenBy(LivingEntity livingEntity) {
		return true;
	}
}
