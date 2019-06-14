package net.minecraft.entity.ai.pathing;

import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BirdNavigation extends EntityNavigation {
	public BirdNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator method_6336(int i) {
		this.field_6678 = new BirdPathNodeMaker();
		this.field_6678.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.field_6678, i);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.canSwim() && this.isInLiquid() || !this.entity.hasVehicle();
	}

	@Override
	protected Vec3d method_6347() {
		return new Vec3d(this.entity.x, this.entity.y, this.entity.z);
	}

	@Override
	public Path method_6349(Entity entity) {
		return this.method_6348(new BlockPos(entity));
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
			} else if (this.field_6681 != null && this.field_6681.getCurrentNodeIndex() < this.field_6681.getLength()) {
				Vec3d vec3d = this.field_6681.method_47(this.entity, this.field_6681.getCurrentNodeIndex());
				if (MathHelper.floor(this.entity.x) == MathHelper.floor(vec3d.x)
					&& MathHelper.floor(this.entity.y) == MathHelper.floor(vec3d.y)
					&& MathHelper.floor(this.entity.z) == MathHelper.floor(vec3d.z)) {
					this.field_6681.setCurrentNodeIndex(this.field_6681.getCurrentNodeIndex() + 1);
				}
			}

			DebugRendererInfoManager.sendPathfindingData(this.field_6677, this.entity, this.field_6681, this.field_6683);
			if (!this.isIdle()) {
				Vec3d vec3d = this.field_6681.method_49(this.entity);
				this.entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
			}
		}
	}

	@Override
	protected boolean method_6341(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k) {
		int l = MathHelper.floor(vec3d.x);
		int m = MathHelper.floor(vec3d.y);
		int n = MathHelper.floor(vec3d.z);
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.y - vec3d.y;
		double f = vec3d2.z - vec3d.z;
		double g = d * d + e * e + f * f;
		if (g < 1.0E-8) {
			return false;
		} else {
			double h = 1.0 / Math.sqrt(g);
			d *= h;
			e *= h;
			f *= h;
			double o = 1.0 / Math.abs(d);
			double p = 1.0 / Math.abs(e);
			double q = 1.0 / Math.abs(f);
			double r = (double)l - vec3d.x;
			double s = (double)m - vec3d.y;
			double t = (double)n - vec3d.z;
			if (d >= 0.0) {
				r++;
			}

			if (e >= 0.0) {
				s++;
			}

			if (f >= 0.0) {
				t++;
			}

			r /= d;
			s /= e;
			t /= f;
			int u = d < 0.0 ? -1 : 1;
			int v = e < 0.0 ? -1 : 1;
			int w = f < 0.0 ? -1 : 1;
			int x = MathHelper.floor(vec3d2.x);
			int y = MathHelper.floor(vec3d2.y);
			int z = MathHelper.floor(vec3d2.z);
			int aa = x - l;
			int ab = y - m;
			int ac = z - n;

			while (aa * u > 0 || ab * v > 0 || ac * w > 0) {
				if (r < t && r <= s) {
					r += o;
					l += u;
					aa = x - l;
				} else if (s < r && s <= t) {
					s += p;
					m += v;
					ab = y - m;
				} else {
					t += q;
					n += w;
					ac = z - n;
				}
			}

			return true;
		}
	}

	public void setCanPathThroughDoors(boolean bl) {
		this.field_6678.setCanPathThroughDoors(bl);
	}

	public void setCanEnterOpenDoors(boolean bl) {
		this.field_6678.setCanEnterOpenDoors(bl);
	}

	@Override
	public boolean isValidPosition(BlockPos blockPos) {
		return this.field_6677.method_8320(blockPos).hasSolidTopSurface(this.field_6677, blockPos, this.entity);
	}
}
