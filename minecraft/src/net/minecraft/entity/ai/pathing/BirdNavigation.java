package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BirdNavigation extends EntityNavigation {
	public BirdNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createPathNodeNavigator(int range) {
		this.nodeMaker = new BirdPathNodeMaker();
		this.nodeMaker.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.nodeMaker, range);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.canSwim() && this.isInLiquid() || !this.entity.hasVehicle();
	}

	@Override
	protected Vec3d getPos() {
		return this.entity.getPos();
	}

	@Override
	public Path findPathTo(Entity entity, int distance) {
		return this.findPathTo(entity.getBlockPos(), distance);
	}

	@Override
	public void tick() {
		this.tickCount++;
		if (this.shouldRecalculate) {
			this.recalculatePath();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.continueFollowingPath();
			} else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
				if (MathHelper.floor(this.entity.getX()) == MathHelper.floor(vec3d.x)
					&& MathHelper.floor(this.entity.getY()) == MathHelper.floor(vec3d.y)
					&& MathHelper.floor(this.entity.getZ()) == MathHelper.floor(vec3d.z)) {
					this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
				}
			}

			DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
			if (!this.isIdle()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				this.entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
			}
		}
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		int i = MathHelper.floor(origin.x);
		int j = MathHelper.floor(origin.y);
		int k = MathHelper.floor(origin.z);
		double d = target.x - origin.x;
		double e = target.y - origin.y;
		double f = target.z - origin.z;
		double g = d * d + e * e + f * f;
		if (g < 1.0E-8) {
			return false;
		} else {
			double h = 1.0 / Math.sqrt(g);
			d *= h;
			e *= h;
			f *= h;
			double l = 1.0 / Math.abs(d);
			double m = 1.0 / Math.abs(e);
			double n = 1.0 / Math.abs(f);
			double o = (double)i - origin.x;
			double p = (double)j - origin.y;
			double q = (double)k - origin.z;
			if (d >= 0.0) {
				o++;
			}

			if (e >= 0.0) {
				p++;
			}

			if (f >= 0.0) {
				q++;
			}

			o /= d;
			p /= e;
			q /= f;
			int r = d < 0.0 ? -1 : 1;
			int s = e < 0.0 ? -1 : 1;
			int t = f < 0.0 ? -1 : 1;
			int u = MathHelper.floor(target.x);
			int v = MathHelper.floor(target.y);
			int w = MathHelper.floor(target.z);
			int x = u - i;
			int y = v - j;
			int z = w - k;

			while (x * r > 0 || y * s > 0 || z * t > 0) {
				if (o < q && o <= p) {
					o += l;
					i += r;
					x = u - i;
				} else if (p < o && p <= q) {
					p += m;
					j += s;
					y = v - j;
				} else {
					q += n;
					k += t;
					z = w - k;
				}
			}

			return true;
		}
	}

	public void setCanPathThroughDoors(boolean canPathThroughDoors) {
		this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
	}

	public void setCanEnterOpenDoors(boolean canEnterOpenDoors) {
		this.nodeMaker.setCanEnterOpenDoors(canEnterOpenDoors);
	}

	@Override
	public boolean isValidPosition(BlockPos pos) {
		return this.world.getBlockState(pos).hasSolidTopSurface(this.world, pos, this.entity);
	}
}
