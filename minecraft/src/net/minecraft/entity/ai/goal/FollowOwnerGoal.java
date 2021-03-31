package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class FollowOwnerGoal extends Goal {
	public static final int field_30205 = 12;
	private static final int field_30206 = 2;
	private static final int field_30207 = 3;
	private static final int field_30208 = 1;
	private final TameableEntity tameable;
	private LivingEntity owner;
	private final WorldView world;
	private final double speed;
	private final EntityNavigation navigation;
	private int updateCountdownTicks;
	private final float maxDistance;
	private final float minDistance;
	private float oldWaterPathfindingPenalty;
	private final boolean leavesAllowed;

	public FollowOwnerGoal(TameableEntity tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
		this.tameable = tameable;
		this.world = tameable.world;
		this.speed = speed;
		this.navigation = tameable.getNavigation();
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.leavesAllowed = leavesAllowed;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		if (!(tameable.getNavigation() instanceof MobNavigation) && !(tameable.getNavigation() instanceof BirdNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.tameable.getOwner();
		if (livingEntity == null) {
			return false;
		} else if (livingEntity.isSpectator()) {
			return false;
		} else if (this.tameable.isSitting()) {
			return false;
		} else if (this.tameable.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
			return false;
		} else {
			this.owner = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.navigation.isIdle()) {
			return false;
		} else {
			return this.tameable.isSitting() ? false : !(this.tameable.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
		}
	}

	@Override
	public void start() {
		this.updateCountdownTicks = 0;
		this.oldWaterPathfindingPenalty = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
		this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
	}

	@Override
	public void tick() {
		this.tameable.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameable.getLookPitchSpeed());
		if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
			if (!this.tameable.isLeashed() && !this.tameable.hasVehicle()) {
				if (this.tameable.squaredDistanceTo(this.owner) >= 144.0) {
					this.tryTeleport();
				} else {
					this.navigation.startMovingTo(this.owner, this.speed);
				}
			}
		}
	}

	private void tryTeleport() {
		BlockPos blockPos = this.owner.getBlockPos();

		for (int i = 0; i < 10; i++) {
			int j = this.getRandomInt(-3, 3);
			int k = this.getRandomInt(-1, 1);
			int l = this.getRandomInt(-3, 3);
			boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (bl) {
				return;
			}
		}
	}

	private boolean tryTeleportTo(int x, int y, int z) {
		if (Math.abs((double)x - this.owner.getX()) < 2.0 && Math.abs((double)z - this.owner.getZ()) < 2.0) {
			return false;
		} else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
			return false;
		} else {
			this.tameable.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, this.tameable.yaw, this.tameable.pitch);
			this.navigation.stop();
			return true;
		}
	}

	private boolean canTeleportTo(BlockPos pos) {
		PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
		if (pathNodeType != PathNodeType.WALKABLE) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(pos.down());
			if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
				return false;
			} else {
				BlockPos blockPos = pos.subtract(this.tameable.getBlockPos());
				return this.world.isSpaceEmpty(this.tameable, this.tameable.getBoundingBox().offset(blockPos));
			}
		}
	}

	private int getRandomInt(int min, int max) {
		return this.tameable.getRandom().nextInt(max - min + 1) + min;
	}
}
