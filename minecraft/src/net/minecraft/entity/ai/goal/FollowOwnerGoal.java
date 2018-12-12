package net.minecraft.entity.ai.goal;

import net.minecraft.class_1407;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public class FollowOwnerGoal extends Goal {
	private final TameableEntity caller;
	private LivingEntity owner;
	protected final ViewableWorld world;
	private final double field_6442;
	private final EntityNavigation field_6446;
	private int field_6443;
	private final float field_6450;
	private final float minDistance;
	private float field_6447;

	public FollowOwnerGoal(TameableEntity tameableEntity, double d, float f, float g) {
		this.caller = tameableEntity;
		this.world = tameableEntity.world;
		this.field_6442 = d;
		this.field_6446 = tameableEntity.getNavigation();
		this.minDistance = f;
		this.field_6450 = g;
		this.setControlBits(3);
		if (!(tameableEntity.getNavigation() instanceof EntityMobNavigation) && !(tameableEntity.getNavigation() instanceof class_1407)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.caller.getOwner();
		if (livingEntity == null) {
			return false;
		} else if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).isSpectator()) {
			return false;
		} else if (this.caller.isSitting()) {
			return false;
		} else if (this.caller.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
			return false;
		} else {
			this.owner = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6446.method_6357() && this.caller.squaredDistanceTo(this.owner) > (double)(this.field_6450 * this.field_6450) && !this.caller.isSitting();
	}

	@Override
	public void start() {
		this.field_6443 = 0;
		this.field_6447 = this.caller.getPathNodeTypeWeight(PathNodeType.WATER);
		this.caller.setPathNodeTypeWeight(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void onRemove() {
		this.owner = null;
		this.field_6446.method_6340();
		this.caller.setPathNodeTypeWeight(PathNodeType.WATER, this.field_6447);
	}

	@Override
	public void tick() {
		this.caller.getLookControl().lookAt(this.owner, 10.0F, (float)this.caller.method_5978());
		if (!this.caller.isSitting()) {
			if (--this.field_6443 <= 0) {
				this.field_6443 = 10;
				if (!this.field_6446.startMovingTo(this.owner, this.field_6442)) {
					if (!this.caller.isLeashed() && !this.caller.hasVehicle()) {
						if (!(this.caller.squaredDistanceTo(this.owner) < 144.0)) {
							int i = MathHelper.floor(this.owner.x) - 2;
							int j = MathHelper.floor(this.owner.z) - 2;
							int k = MathHelper.floor(this.owner.getBoundingBox().minY);

							for (int l = 0; l <= 4; l++) {
								for (int m = 0; m <= 4; m++) {
									if ((l < 1 || m < 1 || l > 3 || m > 3) && this.method_6263(i, j, k, l, m)) {
										this.caller.setPositionAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + m) + 0.5F), this.caller.yaw, this.caller.pitch);
										this.field_6446.method_6340();
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

	protected boolean method_6263(int i, int j, int k, int l, int m) {
		BlockPos blockPos = new BlockPos(i + l, k - 1, j + m);
		BlockState blockState = this.world.getBlockState(blockPos);
		return Block.isFaceFullCube(blockState.getCollisionShape(this.world, blockPos), Direction.DOWN)
			&& blockState.allowsSpawning(this.caller)
			&& this.world.isAir(blockPos.up())
			&& this.world.isAir(blockPos.up(2));
	}
}
