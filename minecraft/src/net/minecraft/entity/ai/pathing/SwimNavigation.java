package net.minecraft.entity.ai.pathing;

import net.minecraft.class_4209;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class SwimNavigation extends EntityNavigation {
	private boolean field_6689;

	public SwimNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createPathNodeNavigator(int i) {
		this.field_6689 = this.entity instanceof DolphinEntity;
		this.nodeMaker = new WaterPathNodeMaker(this.field_6689);
		return new PathNodeNavigator(this.nodeMaker, i);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.field_6689 || this.isInLiquid();
	}

	@Override
	protected Vec3d method_6347() {
		return new Vec3d(this.entity.x, this.entity.y + (double)this.entity.getHeight() * 0.5, this.entity.z);
	}

	@Override
	public void tick() {
		this.tickCount++;
		if (this.shouldRecalculate) {
			this.recalculatePath();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.method_6339();
			} else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
				if (MathHelper.floor(this.entity.x) == MathHelper.floor(vec3d.x)
					&& MathHelper.floor(this.entity.y) == MathHelper.floor(vec3d.y)
					&& MathHelper.floor(this.entity.z) == MathHelper.floor(vec3d.z)) {
					this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
				}
			}

			class_4209.method_19470(this.world, this.entity, this.currentPath, this.field_6683);
			if (!this.isIdle()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				this.entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
			}
		}
	}

	@Override
	protected void method_6339() {
		if (this.currentPath != null) {
			Vec3d vec3d = this.method_6347();
			float f = this.entity.getWidth();
			float g = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
			Vec3d vec3d2 = this.entity.getVelocity();
			if (Math.abs(vec3d2.x) > 0.2 || Math.abs(vec3d2.z) > 0.2) {
				g = (float)((double)g * vec3d2.length() * 6.0);
			}

			int i = 6;
			Vec3d vec3d3 = this.currentPath.getCurrentPosition();
			if (Math.abs(this.entity.x - (vec3d3.x + 0.5)) < (double)g
				&& Math.abs(this.entity.z - (vec3d3.z + 0.5)) < (double)g
				&& Math.abs(this.entity.y - vec3d3.y) < (double)(g * 2.0F)) {
				this.currentPath.next();
			}

			for (int j = Math.min(this.currentPath.getCurrentNodeIndex() + 6, this.currentPath.getLength() - 1); j > this.currentPath.getCurrentNodeIndex(); j--) {
				vec3d3 = this.currentPath.getNodePosition(this.entity, j);
				if (!(vec3d3.squaredDistanceTo(vec3d) > 36.0) && this.canPathDirectlyThrough(vec3d, vec3d3, 0, 0, 0)) {
					this.currentPath.setCurrentNodeIndex(j);
					break;
				}
			}

			this.method_6346(vec3d);
		}
	}

	@Override
	protected void method_6346(Vec3d vec3d) {
		if (this.tickCount - this.field_6674 > 100) {
			if (vec3d.squaredDistanceTo(this.field_6672) < 2.25) {
				this.stop();
			}

			this.field_6674 = this.tickCount;
			this.field_6672 = vec3d;
		}

		if (this.currentPath != null && !this.currentPath.isFinished()) {
			Vec3d vec3d2 = this.currentPath.getCurrentPosition();
			if (vec3d2.equals(this.field_6680)) {
				this.field_6670 = this.field_6670 + (SystemUtil.getMeasuringTimeMs() - this.field_6669);
			} else {
				this.field_6680 = vec3d2;
				double d = vec3d.distanceTo(this.field_6680);
				this.field_6682 = this.entity.getMovementSpeed() > 0.0F ? d / (double)this.entity.getMovementSpeed() * 100.0 : 0.0;
			}

			if (this.field_6682 > 0.0 && (double)this.field_6670 > this.field_6682 * 2.0) {
				this.field_6680 = Vec3d.ZERO;
				this.field_6670 = 0L;
				this.field_6682 = 0.0;
				this.stop();
			}

			this.field_6669 = SystemUtil.getMeasuringTimeMs();
		}
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k) {
		Vec3d vec3d3 = new Vec3d(vec3d2.x, vec3d2.y + (double)this.entity.getHeight() * 0.5, vec3d2.z);
		return this.world
				.rayTrace(new RayTraceContext(vec3d, vec3d3, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this.entity))
				.getType()
			== HitResult.Type.NONE;
	}

	@Override
	public boolean isValidPosition(BlockPos blockPos) {
		return !this.world.getBlockState(blockPos).isFullOpaque(this.world, blockPos);
	}

	@Override
	public void setCanSwim(boolean bl) {
	}
}
