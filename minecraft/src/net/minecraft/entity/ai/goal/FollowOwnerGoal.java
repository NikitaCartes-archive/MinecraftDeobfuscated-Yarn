package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public class FollowOwnerGoal extends Goal {
	protected final TameableEntity tameable;
	private LivingEntity owner;
	protected final ViewableWorld field_6445;
	private final double field_6442;
	private final EntityNavigation navigation;
	private int field_6443;
	private final float maxDistance;
	private final float minDistance;
	private float field_6447;

	public FollowOwnerGoal(TameableEntity tameableEntity, double d, float f, float g) {
		this.tameable = tameableEntity;
		this.field_6445 = tameableEntity.field_6002;
		this.field_6442 = d;
		this.navigation = tameableEntity.getNavigation();
		this.minDistance = f;
		this.maxDistance = g;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		if (!(tameableEntity.getNavigation() instanceof MobNavigation) && !(tameableEntity.getNavigation() instanceof BirdNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.tameable.getOwner();
		if (livingEntity == null) {
			return false;
		} else if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).isSpectator()) {
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
		return !this.navigation.isIdle() && this.tameable.squaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance) && !this.tameable.isSitting();
	}

	@Override
	public void start() {
		this.field_6443 = 0;
		this.field_6447 = this.tameable.method_5944(PathNodeType.field_18);
		this.tameable.method_5941(PathNodeType.field_18, 0.0F);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		this.tameable.method_5941(PathNodeType.field_18, this.field_6447);
	}

	@Override
	public void tick() {
		this.tameable.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameable.getLookPitchSpeed());
		if (!this.tameable.isSitting()) {
			if (--this.field_6443 <= 0) {
				this.field_6443 = 10;
				if (!this.navigation.startMovingTo(this.owner, this.field_6442)) {
					if (!this.tameable.isLeashed() && !this.tameable.hasVehicle()) {
						if (!(this.tameable.squaredDistanceTo(this.owner) < 144.0)) {
							int i = MathHelper.floor(this.owner.x) - 2;
							int j = MathHelper.floor(this.owner.z) - 2;
							int k = MathHelper.floor(this.owner.method_5829().minY);

							for (int l = 0; l <= 4; l++) {
								for (int m = 0; m <= 4; m++) {
									if ((l < 1 || m < 1 || l > 3 || m > 3) && this.method_6263(new BlockPos(i + l, k - 1, j + m))) {
										this.tameable
											.setPositionAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + m) + 0.5F), this.tameable.yaw, this.tameable.pitch);
										this.navigation.stop();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean method_6263(BlockPos blockPos) {
		BlockState blockState = this.field_6445.method_8320(blockPos);
		return blockState.allowsSpawning(this.field_6445, blockPos, this.tameable.getType())
			&& this.field_6445.isAir(blockPos.up())
			&& this.field_6445.isAir(blockPos.up(2));
	}
}
