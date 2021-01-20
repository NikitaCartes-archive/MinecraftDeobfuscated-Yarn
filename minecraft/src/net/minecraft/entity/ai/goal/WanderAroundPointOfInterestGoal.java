package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.class_5532;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class WanderAroundPointOfInterestGoal extends WanderAroundGoal {
	public WanderAroundPointOfInterestGoal(PathAwareEntity entity, double speed, boolean bl) {
		super(entity, speed, 10, bl);
	}

	@Override
	public boolean canStart() {
		ServerWorld serverWorld = (ServerWorld)this.mob.world;
		BlockPos blockPos = this.mob.getBlockPos();
		return serverWorld.isNearOccupiedPointOfInterest(blockPos) ? false : super.canStart();
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		ServerWorld serverWorld = (ServerWorld)this.mob.world;
		BlockPos blockPos = this.mob.getBlockPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
		ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
		return chunkSectionPos2 != chunkSectionPos
			? class_5532.method_31512(this.mob, 10, 7, Vec3d.ofBottomCenter(chunkSectionPos2.getCenterPos()), (float) (Math.PI / 2))
			: null;
	}
}
