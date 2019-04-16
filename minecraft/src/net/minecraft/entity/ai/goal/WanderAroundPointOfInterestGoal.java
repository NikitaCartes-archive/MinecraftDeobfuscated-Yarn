package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class WanderAroundPointOfInterestGoal extends WanderAroundGoal {
	public WanderAroundPointOfInterestGoal(MobEntityWithAi mobEntityWithAi, double d) {
		super(mobEntityWithAi, d, 10);
	}

	@Override
	public boolean canStart() {
		ServerWorld serverWorld = (ServerWorld)this.owner.world;
		BlockPos blockPos = new BlockPos(this.owner);
		return serverWorld.isNearOccupiedPointOfInterest(blockPos) ? false : super.canStart();
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		ServerWorld serverWorld = (ServerWorld)this.owner.world;
		BlockPos blockPos = new BlockPos(this.owner);
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
		ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
		if (chunkSectionPos2 != chunkSectionPos) {
			BlockPos blockPos2 = chunkSectionPos2.getCenterPos();
			return PathfindingUtil.method_6373(this.owner, 10, 7, new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ()));
		} else {
			return null;
		}
	}
}
