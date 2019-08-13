package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class EscapeDangerGoal extends Goal {
	protected final MobEntityWithAi mob;
	protected final double speed;
	protected double targetX;
	protected double targetY;
	protected double targetZ;

	public EscapeDangerGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
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
		Vec3d vec3d = PathfindingUtil.findTarget(this.mob, 5, 4);
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			return true;
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Nullable
	protected BlockPos locateClosestWater(BlockView blockView, Entity entity, int i, int j) {
		BlockPos blockPos = new BlockPos(entity);
		int k = blockPos.getX();
		int l = blockPos.getY();
		int m = blockPos.getZ();
		float f = (float)(i * i * j * 2);
		BlockPos blockPos2 = null;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int n = k - i; n <= k + i; n++) {
			for (int o = l - j; o <= l + j; o++) {
				for (int p = m - i; p <= m + i; p++) {
					mutable.set(n, o, p);
					if (blockView.getFluidState(mutable).matches(FluidTags.field_15517)) {
						float g = (float)((n - k) * (n - k) + (o - l) * (o - l) + (p - m) * (p - m));
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
