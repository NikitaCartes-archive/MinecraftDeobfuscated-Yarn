package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class EscapeDangerGoal extends Goal {
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
		if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
			return false;
		} else {
			if (this.mob.isOnFire()) {
				BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 5, 4);
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

	protected boolean findTarget() {
		Vec3d vec3d = TargetFinder.findTarget(this.mob, 5, 4);
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
	protected BlockPos locateClosestWater(BlockView blockView, Entity entity, int rangeX, int rangeY) {
		BlockPos blockPos = entity.getBlockPos();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		float f = (float)(rangeX * rangeX * rangeY * 2);
		BlockPos blockPos2 = null;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = i - rangeX; l <= i + rangeX; l++) {
			for (int m = j - rangeY; m <= j + rangeY; m++) {
				for (int n = k - rangeX; n <= k + rangeX; n++) {
					mutable.set(l, m, n);
					if (blockView.getFluidState(mutable).isIn(FluidTags.WATER)) {
						float g = (float)((l - i) * (l - i) + (m - j) * (m - j) + (n - k) * (n - k));
						if (g < f) {
							f = g;
							blockPos2 = new BlockPos(mutable);
						}
					}
				}
			}
		}

		return blockPos2;
	}
}
