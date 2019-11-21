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
	private final TameableEntity tameable;
	private LivingEntity owner;
	private final WorldView world;
	private final double field_6442;
	private final EntityNavigation navigation;
	private int field_6443;
	private final float maxDistance;
	private final float minDistance;
	private float field_6447;
	private final boolean field_21078;

	public FollowOwnerGoal(TameableEntity tameable, double speed, float minDistance, float maxDistance, boolean bl) {
		this.tameable = tameable;
		this.world = tameable.world;
		this.field_6442 = speed;
		this.navigation = tameable.getNavigation();
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.field_21078 = bl;
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
		this.field_6443 = 0;
		this.field_6447 = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
		this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.field_6447);
	}

	@Override
	public void tick() {
		this.tameable.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameable.getLookPitchSpeed());
		if (--this.field_6443 <= 0) {
			this.field_6443 = 10;
			if (!this.tameable.isLeashed() && !this.tameable.hasVehicle()) {
				if (this.tameable.squaredDistanceTo(this.owner) >= 144.0) {
					this.method_23345();
				} else {
					this.navigation.startMovingTo(this.owner, this.field_6442);
				}
			}
		}
	}

	private void method_23345() {
		BlockPos blockPos = new BlockPos(this.owner);

		for (int i = 0; i < 10; i++) {
			int j = this.method_23342(-3, 3);
			int k = this.method_23342(-1, 1);
			int l = this.method_23342(-3, 3);
			boolean bl = this.method_23343(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (bl) {
				return;
			}
		}
	}

	private boolean method_23343(int i, int j, int k) {
		if (Math.abs((double)i - this.owner.getX()) < 2.0 && Math.abs((double)k - this.owner.getZ()) < 2.0) {
			return false;
		} else if (!this.method_23344(new BlockPos(i, j, k))) {
			return false;
		} else {
			this.tameable.setPositionAndAngles((double)((float)i + 0.5F), (double)j, (double)((float)k + 0.5F), this.tameable.yaw, this.tameable.pitch);
			this.navigation.stop();
			return true;
		}
	}

	private boolean method_23344(BlockPos blockPos) {
		PathNodeType pathNodeType = LandPathNodeMaker.method_23476(this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
		if (pathNodeType != PathNodeType.WALKABLE) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(blockPos.down());
			if (!this.field_21078 && blockState.getBlock() instanceof LeavesBlock) {
				return false;
			} else {
				BlockPos blockPos2 = blockPos.subtract(new BlockPos(this.tameable));
				return this.world.doesNotCollide(this.tameable, this.tameable.getBoundingBox().offset(blockPos2));
			}
		}
	}

	private int method_23342(int i, int j) {
		return this.tameable.getRandom().nextInt(j - i + 1) + i;
	}
}
