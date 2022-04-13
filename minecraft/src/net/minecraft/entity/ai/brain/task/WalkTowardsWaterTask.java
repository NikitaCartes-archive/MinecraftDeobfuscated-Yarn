package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WalkTowardsWaterTask extends Task<PathAwareEntity> {
	private final int range;
	private final float speed;
	private long walkTowardsWaterTime;

	public WalkTowardsWaterTask(int range, float speed) {
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
		this.walkTowardsWaterTime = l + 40L;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return !pathAwareEntity.world.getFluidState(pathAwareEntity.getBlockPos()).isIn(FluidTags.WATER);
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		if (l >= this.walkTowardsWaterTime) {
			ShapeContext shapeContext = ShapeContext.of(pathAwareEntity);
			BlockPos blockPos = pathAwareEntity.getBlockPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, this.range, this.range, this.range)) {
				if ((blockPos2.getX() != blockPos.getX() || blockPos2.getZ() != blockPos.getZ())
					&& serverWorld.getBlockState(blockPos2).getCollisionShape(serverWorld, blockPos2, shapeContext).isEmpty()
					&& !serverWorld.getBlockState(mutable.set(blockPos2, Direction.DOWN)).getCollisionShape(serverWorld, blockPos2, shapeContext).isEmpty()) {
					for (Direction direction : Direction.Type.HORIZONTAL) {
						mutable.set(blockPos2, direction);
						if (serverWorld.getBlockState(mutable).isAir() && serverWorld.getBlockState(mutable.move(Direction.DOWN)).isOf(Blocks.WATER)) {
							this.walkTowardsWaterTime = l + 40L;
							LookTargetUtil.walkTowards(pathAwareEntity, blockPos2, this.speed, 0);
							return;
						}
					}
				}
			}
		}
	}
}
