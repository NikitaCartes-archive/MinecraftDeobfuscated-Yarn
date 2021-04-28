package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockState;
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
	protected PathNodeNavigator createPathNodeNavigator(int range) {
		this.nodeMaker = new LandPathNodeMaker();
		this.nodeMaker.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.nodeMaker, range);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.entity.isOnGround() || this.isInLiquid() || this.entity.hasVehicle();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.entity.getX(), (double)this.getPathfindingY(), this.entity.getZ());
	}

	@Override
	public Path findPathTo(BlockPos target, int distance) {
		if (this.world.getBlockState(target).isAir()) {
			BlockPos blockPos = target.down();

			while (blockPos.getY() > this.world.getBottomY() && this.world.getBlockState(blockPos).isAir()) {
				blockPos = blockPos.down();
			}

			if (blockPos.getY() > this.world.getBottomY()) {
				return super.findPathTo(blockPos.up(), distance);
			}

			while (blockPos.getY() < this.world.getTopY() && this.world.getBlockState(blockPos).isAir()) {
				blockPos = blockPos.up();
			}

			target = blockPos;
		}

		if (!this.world.getBlockState(target).getMaterial().isSolid()) {
			return super.findPathTo(target, distance);
		} else {
			BlockPos blockPos = target.up();

			while (blockPos.getY() < this.world.getTopY() && this.world.getBlockState(blockPos).getMaterial().isSolid()) {
				blockPos = blockPos.up();
			}

			return super.findPathTo(blockPos, distance);
		}
	}

	@Override
	public Path findPathTo(Entity entity, int distance) {
		return this.findPathTo(entity.getBlockPos(), distance);
	}

	/**
	 * The y-position to act as if the entity is at for pathfinding purposes
	 */
	private int getPathfindingY() {
		if (this.entity.isTouchingWater() && this.canSwim()) {
			int i = this.entity.getBlockY();
			BlockState blockState = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)i, this.entity.getZ()));
			int j = 0;

			while (blockState.isOf(Blocks.WATER)) {
				blockState = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)(++i), this.entity.getZ()));
				if (++j > 16) {
					return this.entity.getBlockY();
				}
			}

			return i;
		} else {
			return MathHelper.floor(this.entity.getY() + 0.5);
		}
	}

	@Override
	protected void adjustPath() {
		super.adjustPath();
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
			if (!this.allVisibleAreSafe(i, MathHelper.floor(origin.y), j, sizeX, sizeY, sizeZ, origin, d, e)) {
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

					if (!this.allVisibleAreSafe(i, MathHelper.floor(origin.y), j, sizeX, sizeY, sizeZ, origin, d, e)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private boolean allVisibleAreSafe(int centerX, int centerY, int centerZ, int sizeX, int sizeY, int sizeZ, Vec3d entityPos, double lookVecX, double lookVecZ) {
		int i = centerX - sizeX / 2;
		int j = centerZ - sizeZ / 2;
		if (!this.allVisibleArePassable(i, centerY, j, sizeX, sizeY, sizeZ, entityPos, lookVecX, lookVecZ)) {
			return false;
		} else {
			for (int k = i; k < i + sizeX; k++) {
				for (int l = j; l < j + sizeZ; l++) {
					double d = (double)k + 0.5 - entityPos.x;
					double e = (double)l + 0.5 - entityPos.z;
					if (!(d * lookVecX + e * lookVecZ < 0.0)) {
						PathNodeType pathNodeType = this.nodeMaker.getNodeType(this.world, k, centerY - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);
						if (!this.canWalkOnPath(pathNodeType)) {
							return false;
						}

						pathNodeType = this.nodeMaker.getNodeType(this.world, k, centerY, l, this.entity, sizeX, sizeY, sizeZ, true, true);
						float f = this.entity.getPathfindingPenalty(pathNodeType);
						if (f < 0.0F || f >= 8.0F) {
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

	protected boolean canWalkOnPath(PathNodeType pathType) {
		if (pathType == PathNodeType.WATER) {
			return false;
		} else {
			return pathType == PathNodeType.LAVA ? false : pathType != PathNodeType.OPEN;
		}
	}

	/**
	 * Checks whether all blocks in the box which are visible (in front of) the mob can be pathed through
	 */
	private boolean allVisibleArePassable(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d entityPos, double lookVecX, double lookVecZ) {
		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
			double d = (double)blockPos.getX() + 0.5 - entityPos.x;
			double e = (double)blockPos.getZ() + 0.5 - entityPos.z;
			if (!(d * lookVecX + e * lookVecZ < 0.0) && !this.world.getBlockState(blockPos).canPathfindThrough(this.world, blockPos, NavigationType.LAND)) {
				return false;
			}
		}

		return true;
	}

	public void setCanPathThroughDoors(boolean canPathThroughDoors) {
		this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
	}

	public boolean method_35140() {
		return this.nodeMaker.canEnterOpenDoors();
	}

	public void setCanEnterOpenDoors(boolean canEnterOpenDoors) {
		this.nodeMaker.setCanEnterOpenDoors(canEnterOpenDoors);
	}

	public boolean canEnterOpenDoors() {
		return this.nodeMaker.canEnterOpenDoors();
	}

	public void setAvoidSunlight(boolean avoidSunlight) {
		this.avoidSunlight = avoidSunlight;
	}
}
