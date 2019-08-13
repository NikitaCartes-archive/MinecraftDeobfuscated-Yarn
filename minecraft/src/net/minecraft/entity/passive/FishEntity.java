package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class FishEntity extends WaterCreatureEntity {
	private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public FishEntity(EntityType<? extends FishEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FishEntity.FishMoveControl(this);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.65F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
	}

	@Override
	public boolean cannotDespawn() {
		return this.isFromBucket();
	}

	public static boolean canSpawn(EntityType<? extends FishEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getBlockState(blockPos).getBlock() == Blocks.field_10382 && iWorld.getBlockState(blockPos.up()).getBlock() == Blocks.field_10382;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isFromBucket() && !this.hasCustomName();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(FROM_BUCKET, false);
	}

	private boolean isFromBucket() {
		return this.dataTracker.get(FROM_BUCKET);
	}

	public void setFromBucket(boolean bl) {
		this.dataTracker.set(FROM_BUCKET, bl);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("FromBucket", this.isFromBucket());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setFromBucket(compoundTag.getBoolean("FromBucket"));
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 1.6, 1.4, EntityPredicates.EXCEPT_SPECTATOR::test));
		this.goalSelector.add(4, new FishEntity.SwimToRandomPlaceGoal(this));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.canMoveVoluntarily() && this.isInsideWater()) {
			this.updateVelocity(0.01F, vec3d);
			this.move(MovementType.field_6308, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(vec3d);
		}
	}

	@Override
	public void tickMovement() {
		if (!this.isInsideWater() && this.onGround && this.verticalCollision) {
			this.setVelocity(
				this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F))
			);
			this.onGround = false;
			this.velocityDirty = true;
			this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getSoundPitch());
		}

		super.tickMovement();
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8705 && this.isAlive()) {
			this.playSound(SoundEvents.field_14568, 1.0F, 1.0F);
			itemStack.decrement(1);
			ItemStack itemStack2 = this.getFishBucketItem();
			this.copyDataToStack(itemStack2);
			if (!this.world.isClient) {
				Criterions.FILLED_BUCKET.handle((ServerPlayerEntity)playerEntity, itemStack2);
			}

			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, itemStack2);
			} else if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			this.remove();
			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	protected void copyDataToStack(ItemStack itemStack) {
		if (this.hasCustomName()) {
			itemStack.setCustomName(this.getCustomName());
		}
	}

	protected abstract ItemStack getFishBucketItem();

	protected boolean hasSelfControl() {
		return true;
	}

	protected abstract SoundEvent getFlopSound();

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.field_14591;
	}

	static class FishMoveControl extends MoveControl {
		private final FishEntity fish;

		FishMoveControl(FishEntity fishEntity) {
			super(fishEntity);
			this.fish = fishEntity;
		}

		@Override
		public void tick() {
			if (this.fish.isInFluid(FluidTags.field_15517)) {
				this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
			}

			if (this.state == MoveControl.State.field_6378 && !this.fish.getNavigation().isIdle()) {
				double d = this.targetX - this.fish.x;
				double e = this.targetY - this.fish.y;
				double f = this.targetZ - this.fish.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.fish.yaw = this.changeAngle(this.fish.yaw, h, 90.0F);
				this.fish.field_6283 = this.fish.yaw;
				float i = (float)(this.speed * this.fish.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), i));
				this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * e * 0.1, 0.0));
			} else {
				this.fish.setMovementSpeed(0.0F);
			}
		}
	}

	static class SwimToRandomPlaceGoal extends SwimAroundGoal {
		private final FishEntity fish;

		public SwimToRandomPlaceGoal(FishEntity fishEntity) {
			super(fishEntity, 1.0, 40);
			this.fish = fishEntity;
		}

		@Override
		public boolean canStart() {
			return this.fish.hasSelfControl() && super.canStart();
		}
	}
}
