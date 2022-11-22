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
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
		return doesNotCollide(this.entity, origin, target, true);
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
		if (this.inRecalculationCooldown) {
			this.recalculatePath();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.continueFollowingPath();
			} else if (this.currentPath != null && !this.currentPath.isFinished()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				if (this.entity.getBlockX() == MathHelper.floor(vec3d.x)
					&& this.entity.getBlockY() == MathHelper.floor(vec3d.y)
					&& this.entity.getBlockZ() == MathHelper.floor(vec3d.z)) {
					this.currentPath.next();
				}
			}

			DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
			if (!this.isIdle()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				this.entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
			}
		}
	}

	public void setCanPathThroughDoors(boolean canPathThroughDoors) {
		this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
	}

	public boolean canEnterOpenDoors() {
		return this.nodeMaker.canEnterOpenDoors();
	}

	public void setCanEnterOpenDoors(boolean canEnterOpenDoors) {
		this.nodeMaker.setCanEnterOpenDoors(canEnterOpenDoors);
	}

	public boolean method_35129() {
		return this.nodeMaker.canEnterOpenDoors();
	}

	@Override
	public boolean isValidPosition(BlockPos pos) {
		return this.world.getBlockState(pos).hasSolidTopSurface(this.world, pos, this.entity);
	}
}
