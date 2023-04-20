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

		if (!this.world.getBlockState(target).isSolid()) {
			return super.findPathTo(target, distance);
		} else {
			BlockPos blockPos = target.up();

			while (blockPos.getY() < this.world.getTopY() && this.world.getBlockState(blockPos).isSolid()) {
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
			BlockState blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), (double)i, this.entity.getZ()));
			int j = 0;

			while (blockState.isOf(Blocks.WATER)) {
				blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), (double)(++i), this.entity.getZ()));
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
			if (this.world.isSkyVisible(BlockPos.ofFloored(this.entity.getX(), this.entity.getY() + 0.5, this.entity.getZ()))) {
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

	protected boolean canWalkOnPath(PathNodeType pathType) {
		if (pathType == PathNodeType.WATER) {
			return false;
		} else {
			return pathType == PathNodeType.LAVA ? false : pathType != PathNodeType.OPEN;
		}
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

	public void setCanWalkOverFences(boolean canWalkOverFences) {
		this.nodeMaker.setCanWalkOverFences(canWalkOverFences);
	}
}
