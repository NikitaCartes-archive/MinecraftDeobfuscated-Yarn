package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
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

	@Override
	protected void updateLeash() {
		super.updateLeash();
		Entity entity = this.getHoldingEntity();
		if (entity != null && entity.getWorld() == this.getWorld()) {
			this.setPositionTarget(entity.getBlockPos(), 5);
			float f = this.distanceTo(entity);
			if (this instanceof TameableEntity && ((TameableEntity)this).isInSittingPose()) {
				if (f > 10.0F) {
					this.detachLeash(true, true);
				}

				return;
			}

			this.updateForLeashLength(f);
			if (f > 10.0F) {
				this.detachLeash(true, true);
				this.goalSelector.disableControl(Goal.Control.MOVE);
			} else if (f > 6.0F) {
				double d = (entity.getX() - this.getX()) / (double)f;
				double e = (entity.getY() - this.getY()) / (double)f;
				double g = (entity.getZ() - this.getZ()) / (double)f;
				this.setVelocity(this.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
				this.limitFallDistance();
			} else if (this.shouldFollowLeash()) {
				this.goalSelector.enableControl(Goal.Control.MOVE);
				float h = 2.0F;
				Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())
					.normalize()
					.multiply((double)Math.max(f - 2.0F, 0.0F));
				this.getNavigation().startMovingTo(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z, this.getFollowLeashSpeed());
			}
		}
	}

	protected boolean shouldFollowLeash() {
		return true;
	}

	protected double getFollowLeashSpeed() {
		return 1.0;
	}

	protected void updateForLeashLength(float leashLength) {
	}
}
