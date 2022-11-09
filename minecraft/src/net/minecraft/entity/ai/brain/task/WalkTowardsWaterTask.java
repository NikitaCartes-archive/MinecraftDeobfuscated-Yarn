package net.minecraft.entity.ai.brain.task;

import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.mutable.MutableLong;

public class WalkTowardsWaterTask {
	public static Task<PathAwareEntity> create(int range, float speed) {
		MutableLong mutableLong = new MutableLong(0L);
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)
					)
					.apply(
						context,
						(attackTarget, walkTarget, lookTarget) -> (world, entity, time) -> {
								if (world.getFluidState(entity.getBlockPos()).isIn(FluidTags.WATER)) {
									return false;
								} else if (time < mutableLong.getValue()) {
									mutableLong.setValue(time + 40L);
									return true;
								} else {
									ShapeContext shapeContext = ShapeContext.of(entity);
									BlockPos blockPos = entity.getBlockPos();
									BlockPos.Mutable mutable = new BlockPos.Mutable();

									label45:
									for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, range, range, range)) {
										if ((blockPos2.getX() != blockPos.getX() || blockPos2.getZ() != blockPos.getZ())
											&& world.getBlockState(blockPos2).getCollisionShape(world, blockPos2, shapeContext).isEmpty()
											&& !world.getBlockState(mutable.set(blockPos2, Direction.DOWN)).getCollisionShape(world, blockPos2, shapeContext).isEmpty()) {
											for (Direction direction : Direction.Type.HORIZONTAL) {
												mutable.set(blockPos2, direction);
												if (world.getBlockState(mutable).isAir() && world.getBlockState(mutable.move(Direction.DOWN)).isOf(Blocks.WATER)) {
													lookTarget.remember(new BlockPosLookTarget(blockPos2));
													walkTarget.remember(new WalkTarget(new BlockPosLookTarget(blockPos2), speed, 0));
													break label45;
												}
											}
										}
									}

									mutableLong.setValue(time + 40L);
									return true;
								}
							}
					)
		);
	}
}
