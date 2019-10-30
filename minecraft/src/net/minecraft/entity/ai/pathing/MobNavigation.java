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

	public MobNavigation(MobEntity entity, World world) {
		super(entity, world);
	}

	@Override
	protected PathNodeNavigator createPathNodeNavigator(int i) {
		this.nodeMaker = new LandPathNodeMaker();
		this.nodeMaker.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.nodeMaker, i);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.entity.onGround || this.isInLiquid() || this.entity.hasVehicle();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.entity.getX(), (double)this.method_6362(), this.entity.getZ());
	}

	@Override
	public Path findPathTo(BlockPos target, int distance) {
		if (this.world.getBlockState(target).isAir()) {
			BlockPos blockPos = target.method_10074();

			while (blockPos.getY() > 0 && this.world.getBlockState(blockPos).isAir()) {
				blockPos = blockPos.method_10074();
			}

			if (blockPos.getY() > 0) {
				return super.findPathTo(blockPos.up(), distance);
			}

			while (blockPos.getY() < this.world.getHeight() && this.world.getBlockState(blockPos).isAir()) {
				blockPos = blockPos.up();
			}

			target = blockPos;
		}

		if (!this.world.getBlockState(target).getMaterial().isSolid()) {
			return super.findPathTo(target, distance);
		} else {
			BlockPos blockPos = target.up();

			while (blockPos.getY() < this.world.getHeight() && this.world.getBlockState(blockPos).getMaterial().isSolid()) {
				blockPos = blockPos.up();
			}

			return super.findPathTo(blockPos, distance);
		}
	}

	@Override
	public Path findPathTo(Entity entity, int distance) {
		return this.findPathTo(new BlockPos(entity), distance);
	}

	private int method_6362() {
		if (this.entity.isInsideWater() && this.canSwim()) {
			int i = MathHelper.floor(this.entity.getY());
			Block block = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)i, this.entity.getZ())).getBlock();
			int j = 0;

			while (block == Blocks.WATER) {
				block = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)(++i), this.entity.getZ())).getBlock();
				if (++j > 16) {
					return MathHelper.floor(this.entity.getY());
				}
			}

			return i;
		} else {
			return MathHelper.floor(this.entity.getY() + 0.5);
		}
	}

	@Override
	protected void method_6359() {
		super.method_6359();
		if (this.avoidSunlight) {
			if (this.world.isSkyVisible(new BlockPos(this.entity.getX(), this.entity.getY() + 0.5, this.entity.getZ()))) {
				return;
			}

			for (int i = 0; i < this.currentPath.getLength(); i++) {
				PathNode pathNode = this.currentPath.getNode(i);
				if (this.world.isSkyVisible(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) {
					this.currentPath.setLength(i);
					return;
				}
			}
		}
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		int i = MathHelper.floor(origin.x);
		int j = MathHelper.floor(origin.z);
		double d = target.x - origin.x;
		double e = target.z - origin.z;
		double f = d * d + e * e;
		if (f < 1.0E-8) {
			return false;
		} else {
			double g = 1.0 / Math.sqrt(f);
			d *= g;
			e *= g;
			sizeX += 2;
			sizeZ += 2;
			if (!this.method_6364(i, MathHelper.floor(origin.y), j, sizeX, sizeY, sizeZ, origin, d, e)) {
				return false;
			} else {
				sizeX -= 2;
				sizeZ -= 2;
				double h = 1.0 / Math.abs(d);
				double k = 1.0 / Math.abs(e);
				double l = (double)i - origin.x;
				double m = (double)j - origin.z;
				if (d >= 0.0) {
					l++;
				}

				if (e >= 0.0) {
					m++;
				}

				l /= d;
				m /= e;
				int n = d < 0.0 ? -1 : 1;
				int o = e < 0.0 ? -1 : 1;
				int p = MathHelper.floor(target.x);
				int q = MathHelper.floor(target.z);
				int r = p - i;
				int s = q - j;

				while (r * n > 0 || s * o > 0) {
					if (l < m) {
						l += h;
						i += n;
						r = p - i;
					} else {
						m += k;
						j += o;
						s = q - j;
					}

					if (!this.method_6364(i, MathHelper.floor(origin.y), j, sizeX, sizeY, sizeZ, origin, d, e)) {
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
						PathNodeType pathNodeType = this.nodeMaker.getNodeType(this.world, q, j - 1, r, this.entity, l, m, n, true, true);
						if (pathNodeType == PathNodeType.WATER) {
							return false;
						}

						if (pathNodeType == PathNodeType.LAVA) {
							return false;
						}

						if (pathNodeType == PathNodeType.OPEN) {
							return false;
						}

						pathNodeType = this.nodeMaker.getNodeType(this.world, q, j, r, this.entity, l, m, n, true, true);
						float h = this.entity.getPathfindingPenalty(pathNodeType);
						if (h < 0.0F || h >= 8.0F) {
							return false;
						}

						if (pathNodeType == PathNodeType.DAMAGE_FIRE || pathNodeType == PathNodeType.DANGER_FIRE || pathNodeType == PathNodeType.DAMAGE_OTHER) {
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
			if (!(f * d + g * e < 0.0) && !this.world.getBlockState(blockPos).canPlaceAtSide(this.world, blockPos, BlockPlacementEnvironment.LAND)) {
				return false;
			}
		}

		return true;
	}

	public void setCanPathThroughDoors(boolean bl) {
		this.nodeMaker.setCanOpenDoors(bl);
	}

	public boolean canEnterOpenDoors() {
		return this.nodeMaker.canEnterOpenDoors();
	}

	public void setAvoidSunlight(boolean avoidSunlight) {
		this.avoidSunlight = avoidSunlight;
	}
}
