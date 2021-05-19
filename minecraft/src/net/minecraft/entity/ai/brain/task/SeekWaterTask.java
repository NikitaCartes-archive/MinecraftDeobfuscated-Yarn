package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class SeekWaterTask extends Task<PathAwareEntity> {
	private final int range;
	private final float speed;
	private long field_33759;

	public SeekWaterTask(int range, float speed) {
		super(
			ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED
			)
		);
		this.range = range;
		this.speed = speed;
	}

	protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		this.field_33759 = l + 20L + 2L;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return !pathAwareEntity.world.getFluidState(pathAwareEntity.getBlockPos()).isIn(FluidTags.WATER);
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		if (l >= this.field_33759) {
			BlockPos blockPos = null;
			BlockPos blockPos2 = null;
			BlockPos blockPos3 = pathAwareEntity.getBlockPos();

			for (BlockPos blockPos4 : BlockPos.iterateOutwards(blockPos3, this.range, this.range, this.range)) {
				if (blockPos4.getX() != blockPos3.getX() || blockPos4.getZ() != blockPos3.getZ()) {
					BlockState blockState = pathAwareEntity.world.getBlockState(blockPos4.up());
					BlockState blockState2 = pathAwareEntity.world.getBlockState(blockPos4);
					if (blockState2.isOf(Blocks.WATER)) {
						if (blockState.isAir()) {
							blockPos = blockPos4.toImmutable();
							break;
						}

						if (blockPos2 == null && !blockPos4.isWithinDistance(pathAwareEntity.getPos(), 1.5)) {
							blockPos2 = blockPos4.toImmutable();
						}
					}
				}
			}

			if (blockPos == null) {
				blockPos = blockPos2;
			}

			if (blockPos != null) {
				this.field_33759 = l + 40L;
				LookTargetUtil.walkTowards(pathAwareEntity, blockPos, this.speed, 0);
			}
		}
	}
}
