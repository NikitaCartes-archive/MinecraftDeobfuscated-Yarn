package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobNavigation extends EntityNavigation {
	private boolean avoidSunlight;

	public MobNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator method_6336(int i) {
		this.field_6678 = new LandPathNodeMaker();
		this.field_6678.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.field_6678, i);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.entity.onGround || this.isInLiquid() || this.entity.hasVehicle();
	}

	@Override
	protected Vec3d method_6347() {
		return new Vec3d(this.entity.x, (double)this.method_6362(), this.entity.z);
	}

	@Override
	public Path method_6348(BlockPos blockPos) {
		if (this.field_6677.method_8320(blockPos).isAir()) {
			BlockPos blockPos2 = blockPos.down();

			while (blockPos2.getY() > 0 && this.field_6677.method_8320(blockPos2).isAir()) {
				blockPos2 = blockPos2.down();
			}

			if (blockPos2.getY() > 0) {
				return super.method_6348(blockPos2.up());
			}

			while (blockPos2.getY() < this.field_6677.getHeight() && this.field_6677.method_8320(blockPos2).isAir()) {
				blockPos2 = blockPos2.up();
			}

			blockPos = blockPos2;
		}

		if (!this.field_6677.method_8320(blockPos).method_11620().isSolid()) {
			return super.method_6348(blockPos);
		} else {
			BlockPos blockPos2 = blockPos.up();

			while (blockPos2.getY() < this.field_6677.getHeight() && this.field_6677.method_8320(blockPos2).method_11620().isSolid()) {
				blockPos2 = blockPos2.up();
			}

			return super.method_6348(blockPos2);
		}
	}

	@Override
	public Path method_6349(Entity entity) {
		return this.method_6348(new BlockPos(entity));
	}

	private int method_6362() {
		if (this.entity.isInsideWater() && this.canSwim()) {
			int i = MathHelper.floor(this.entity.method_5829().minY);
			Block block = this.field_6677.method_8320(new BlockPos(this.entity.x, (double)i, this.entity.z)).getBlock();
			int j = 0;

			while (block == Blocks.field_10382) {
				block = this.field_6677.method_8320(new BlockPos(this.entity.x, (double)(++i), this.entity.z)).getBlock();
				if (++j > 16) {
					return MathHelper.floor(this.entity.method_5829().minY);
				}
			}

			return i;
		} else {
			return MathHelper.floor(this.entity.method_5829().minY + 0.5);
		}
	}

	@Override
	protected void method_6359() {
		super.method_6359();
		if (this.avoidSunlight) {
			if (this.field_6677.isSkyVisible(new BlockPos(this.entity.x, this.entity.method_5829().minY + 0.5, this.entity.z))) {
				return;
			}

			for (int i = 0; i < this.field_6681.getLength(); i++) {
				PathNode pathNode = this.field_6681.getNode(i);
				if (this.field_6677.isSkyVisible(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) {
					this.field_6681.setLength(i);
					return;
				}
			}
		}
	}

	@Override
	protected boolean method_6341(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k) {
		int l = MathHelper.floor(vec3d.x);
		int m = MathHelper.floor(vec3d.z);
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.z - vec3d.z;
		double f = d * d + e * e;
		if (f < 1.0E-8) {
			return false;
		} else {
			double g = 1.0 / Math.sqrt(f);
			d *= g;
			e *= g;
			i += 2;
			k += 2;
			if (!this.method_6364(l, MathHelper.floor(vec3d.y), m, i, j, k, vec3d, d, e)) {
				return false;
			} else {
				i -= 2;
				k -= 2;
				double h = 1.0 / Math.abs(d);
				double n = 1.0 / Math.abs(e);
				double o = (double)l - vec3d.x;
				double p = (double)m - vec3d.z;
				if (d >= 0.0) {
					o++;
				}

				if (e >= 0.0) {
					p++;
				}

				o /= d;
				p /= e;
				int q = d < 0.0 ? -1 : 1;
				int r = e < 0.0 ? -1 : 1;
				int s = MathHelper.floor(vec3d2.x);
				int t = MathHelper.floor(vec3d2.z);
				int u = s - l;
				int v = t - m;

				while (u * q > 0 || v * r > 0) {
					if (o < p) {
						o += h;
						l += q;
						u = s - l;
					} else {
						p += n;
						m += r;
						v = t - m;
					}

					if (!this.method_6364(l, MathHelper.floor(vec3d.y), m, i, j, k, vec3d, d, e)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private boolean method_6364(int i, int j, int k, int l, int m, int n, Vec3d vec3d, double d, double e) {
		int o = i - l / 2;
		int p = k - n / 2;
		if (!this.method_6367(o, j, p, l, m, n, vec3d, d, e)) {
			return false;
		} else {
			for (int q = o; q < o + l; q++) {
				for (int r = p; r < p + n; r++) {
					double f = (double)q + 0.5 - vec3d.x;
					double g = (double)r + 0.5 - vec3d.z;
					if (!(f * d + g * e < 0.0)) {
						PathNodeType pathNodeType = this.field_6678.getPathNodeType(this.field_6677, q, j - 1, r, this.entity, l, m, n, true, true);
						if (pathNodeType == PathNodeType.field_18) {
							return false;
						}

						if (pathNodeType == PathNodeType.field_14) {
							return false;
						}

						if (pathNodeType == PathNodeType.field_7) {
							return false;
						}

						pathNodeType = this.field_6678.getPathNodeType(this.field_6677, q, j, r, this.entity, l, m, n, true, true);
						float h = this.entity.method_5944(pathNodeType);
						if (h < 0.0F || h >= 8.0F) {
							return false;
						}

						if (pathNodeType == PathNodeType.field_3 || pathNodeType == PathNodeType.field_9 || pathNodeType == PathNodeType.field_17) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	private boolean method_6367(int i, int j, int k, int l, int m, int n, Vec3d vec3d, double d, double e) {
		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(i, j, k), new BlockPos(i + l - 1, j + m - 1, k + n - 1))) {
			double f = (double)blockPos.getX() + 0.5 - vec3d.x;
			double g = (double)blockPos.getZ() + 0.5 - vec3d.z;
			if (!(f * d + g * e < 0.0) && !this.field_6677.method_8320(blockPos).method_11609(this.field_6677, blockPos, BlockPlacementEnvironment.field_50)) {
				return false;
			}
		}

		return true;
	}

	public void setCanPathThroughDoors(boolean bl) {
		this.field_6678.setCanPathThroughDoors(bl);
	}

	public boolean canEnterOpenDoors() {
		return this.field_6678.canEnterOpenDoors();
	}

	public void setAvoidSunlight(boolean bl) {
		this.avoidSunlight = bl;
	}
}
