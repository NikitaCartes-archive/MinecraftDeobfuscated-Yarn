package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class EscapeDangerGoal extends Goal {
	public static final int field_36271 = 1;
	protected final PathAwareEntity mob;
	protected final double speed;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected boolean active;

	public EscapeDangerGoal(PathAwareEntity mob, double speed) {
		this.mob = mob;
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (!this.isInDanger()) {
			return false;
		} else {
			if (this.mob.isOnFire()) {
				BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 5);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				}
			}

			return this.findTarget();
		}
	}

	protected boolean isInDanger() {
		return this.mob.getAttacker() != null || this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
	}

	protected boolean findTarget() {
		Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 5, 4);
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			return true;
		}
	}

	public boolean isActive() {
		return this.active;
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
		this.active = true;
	}

	@Override
	public void stop() {
		this.active = false;
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Nullable
	protected BlockPos locateClosestWater(BlockView world, Entity entity, int rangeX) {
		BlockPos blockPos = entity.getBlockPos();
		return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()
			? null
			: (BlockPos)BlockPos.findClosest(entity.getBlockPos(), rangeX, 1, pos -> world.getFluidState(pos).isIn(FluidTags.WATER)).orElse(null);
	}
}
