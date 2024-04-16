package net.minecraft.entity.ai.brain.task;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableLong;

public class SeekWaterTask {
	public static Task<PathAwareEntity> create(int range, float speed) {
		MutableLong mutableLong = new MutableLong(0L);
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)
					)
					.apply(context, (attackTarget, walkTarget, lookTarget) -> (world, entity, time) -> {
							if (world.getFluidState(entity.getBlockPos()).isIn(FluidTags.WATER)) {
								return false;
							} else if (time < mutableLong.getValue()) {
								mutableLong.setValue(time + 20L + 2L);
								return true;
							} else {
								BlockPos blockPos = null;
								BlockPos blockPos2 = null;
								BlockPos blockPos3 = entity.getBlockPos();

								for (BlockPos blockPos4 : BlockPos.iterateOutwards(blockPos3, range, range, range)) {
									if (blockPos4.getX() != blockPos3.getX() || blockPos4.getZ() != blockPos3.getZ()) {
										BlockState blockState = entity.getWorld().getBlockState(blockPos4.up());
										BlockState blockState2 = entity.getWorld().getBlockState(blockPos4);
										if (blockState2.isOf(Blocks.WATER)) {
											if (blockState.isAir()) {
												blockPos = blockPos4.toImmutable();
												break;
											}

											if (blockPos2 == null && !blockPos4.isWithinDistance(entity.getPos(), 1.5)) {
												blockPos2 = blockPos4.toImmutable();
											}
										}
									}
								}

								if (blockPos == null) {
									blockPos = blockPos2;
								}

								if (blockPos != null) {
									lookTarget.remember(new BlockPosLookTarget(blockPos));
									walkTarget.remember(new WalkTarget(new BlockPosLookTarget(blockPos), speed, 0));
								}

								mutableLong.setValue(time + 40L);
								return true;
							}
						})
		);
	}
}
