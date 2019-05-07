package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class GoToVillageGoal extends Goal {
	private final MobEntityWithAi mob;
	private final int searchRange;
	@Nullable
	private BlockPos targetPosition;

	public GoToVillageGoal(MobEntityWithAi mobEntityWithAi, int i) {
		this.mob = mobEntityWithAi;
		this.searchRange = i;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.mob.hasPassengers()) {
			return false;
		} else if (this.mob.world.isDaylight()) {
			return false;
		} else if (this.mob.getRand().nextInt(this.searchRange) != 0) {
			return false;
		} else {
			ServerWorld serverWorld = (ServerWorld)this.mob.world;
			BlockPos blockPos = new BlockPos(this.mob);
			if (!serverWorld.isNearOccupiedPointOfInterest(blockPos, 6)) {
				return false;
			} else {
				Vec3d vec3d = PathfindingUtil.findTargetStraight(
					this.mob, 15, 7, blockPosx -> (double)(-serverWorld.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(blockPosx)))
				);
				this.targetPosition = vec3d == null ? null : new BlockPos(vec3d);
				return this.targetPosition != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.targetPosition != null && !this.mob.getNavigation().isIdle() && this.mob.getNavigation().getTargetPos().equals(this.targetPosition);
	}

	@Override
	public void tick() {
		if (this.targetPosition != null) {
			EntityNavigation entityNavigation = this.mob.getNavigation();
			if (entityNavigation.isIdle() && !this.targetPosition.isWithinDistance(this.mob.getPos(), 10.0)) {
				Vec3d vec3d = new Vec3d(this.targetPosition);
				Vec3d vec3d2 = new Vec3d(this.mob.x, this.mob.y, this.mob.z);
				Vec3d vec3d3 = vec3d2.subtract(vec3d);
				vec3d = vec3d3.multiply(0.4).add(vec3d);
				Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
				BlockPos blockPos = new BlockPos(vec3d4);
				blockPos = this.mob.world.getTopPosition(Heightmap.Type.field_13203, blockPos);
				if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
					this.findOtherWaypoint();
				}
			}
		}
	}

	private void findOtherWaypoint() {
		Random random = this.mob.getRand();
		BlockPos blockPos = this.mob
			.world
			.getTopPosition(Heightmap.Type.field_13203, new BlockPos(this.mob).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.mob.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
