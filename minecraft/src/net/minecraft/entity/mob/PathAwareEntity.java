package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class PathAwareEntity extends MobEntity {
	protected static final float DEFAULT_PATHFINDING_FAVOR = 0.0F;

	protected PathAwareEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	public float getPathfindingFavor(BlockPos pos) {
		return this.getPathfindingFavor(pos, this.getWorld());
	}

	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return 0.0F;
	}

	@Override
	public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
		return this.getPathfindingFavor(this.getBlockPos(), world) >= 0.0F;
	}

	public boolean isNavigating() {
		return !this.getNavigation().isIdle();
	}

	public boolean isPanicking() {
		if (this.brain.hasMemoryModule(MemoryModuleType.IS_PANICKING)) {
			return this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_PANICKING).isPresent();
		} else {
			for (PrioritizedGoal prioritizedGoal : this.goalSelector.getGoals()) {
				if (prioritizedGoal.isRunning() && prioritizedGoal.getGoal() instanceof EscapeDangerGoal) {
					return true;
				}
			}

			return false;
		}
	}

	protected boolean shouldFollowLeash() {
		return true;
	}

	@Override
	public void onShortLeashTick(Entity entity) {
		super.onShortLeashTick(entity);
		if (this.shouldFollowLeash() && !this.isPanicking()) {
			this.goalSelector.enableControl(Goal.Control.MOVE);
			float f = 2.0F;
			float g = this.distanceTo(entity);
			Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())
				.normalize()
				.multiply((double)Math.max(g - 2.0F, 0.0F));
			this.getNavigation().startMovingTo(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z, this.getFollowLeashSpeed());
		}
	}

	@Override
	public boolean beforeLeashTick(Entity leashHolder, float distance) {
		this.setPositionTarget(leashHolder.getBlockPos(), 5);
		return true;
	}

	protected double getFollowLeashSpeed() {
		return 1.0;
	}
}
