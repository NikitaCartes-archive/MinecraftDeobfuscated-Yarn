package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask {
	private static final int DEFAULT_HORIZONTAL_RANGE = 10;
	private static final int DEFAULT_VERTICAL_RANGE = 7;

	public static SingleTickTask<PathAwareEntity> create(float walkSpeed) {
		return create(walkSpeed, 10, 7);
	}

	public static SingleTickTask<PathAwareEntity> create(float walkSpeed, int horizontalRange, int verticalRange) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, walkTarget -> (world, entity, time) -> {
						BlockPos blockPos = entity.getBlockPos();
						Vec3d vec3d;
						if (world.isNearOccupiedPointOfInterest(blockPos)) {
							vec3d = FuzzyTargeting.find(entity, horizontalRange, verticalRange);
						} else {
							ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
							ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(world, chunkSectionPos, 2);
							if (chunkSectionPos2 != chunkSectionPos) {
								vec3d = NoPenaltyTargeting.findTo(entity, horizontalRange, verticalRange, Vec3d.ofBottomCenter(chunkSectionPos2.getCenterPos()), (float) (Math.PI / 2));
							} else {
								vec3d = FuzzyTargeting.find(entity, horizontalRange, verticalRange);
							}
						}

						walkTarget.remember(Optional.ofNullable(vec3d).map(pos -> new WalkTarget(pos, walkSpeed, 0)));
						return true;
					})
		);
	}
}
