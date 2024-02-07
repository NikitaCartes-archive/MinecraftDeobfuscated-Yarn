package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FishEntity extends WaterCreatureEntity implements Bucketable {
	private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public FishEntity(EntityType<? extends FishEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FishEntity.FishMoveControl(this);
	}

	public static DefaultAttributeContainer.Builder createFishAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0);
	}

	@Override
	public boolean cannotDespawn() {
		return super.cannotDespawn() || this.isFromBucket();
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isFromBucket() && !this.hasCustomName();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(FROM_BUCKET, false);
	}

	@Override
	public boolean isFromBucket() {
		return this.dataTracker.get(FROM_BUCKET);
	}

	@Override
	public void setFromBucket(boolean fromBucket) {
		this.dataTracker.set(FROM_BUCKET, fromBucket);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("FromBucket", this.isFromBucket());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setFromBucket(nbt.getBoolean("FromBucket"));
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
	public void travel(Vec3d movementInput) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(0.01F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(movementInput);
		}
	}

	@Override
	public void tickMovement() {
		if (!this.isTouchingWater() && this.isOnGround() && this.verticalCollision) {
			this.setVelocity(
				this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F))
			);
			this.setOnGround(false);
			this.velocityDirty = true;
			this.playSound(this.getFlopSound());
		}

		super.tickMovement();
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
	}

	@Override
	public void copyDataToStack(ItemStack stack) {
		Bucketable.copyDataToStack(this, stack);
	}

	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Bucketable.copyDataFromNbt(this, nbt);
	}

	@Override
	public SoundEvent getBucketFillSound() {
		return SoundEvents.ITEM_BUCKET_FILL_FISH;
	}

	protected boolean hasSelfControl() {
		return true;
	}

	protected abstract SoundEvent getFlopSound();

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_FISH_SWIM;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
	}

	static class FishMoveControl extends MoveControl {
		private final FishEntity fish;

		FishMoveControl(FishEntity owner) {
			super(owner);
			this.fish = owner;
		}

		@Override
		public void tick() {
			if (this.fish.isSubmergedIn(FluidTags.WATER)) {
				this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
			}

			if (this.state == MoveControl.State.MOVE_TO && !this.fish.getNavigation().isIdle()) {
				float f = (float)(this.speed * this.fish.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
				this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), f));
				double d = this.targetX - this.fish.getX();
				double e = this.targetY - this.fish.getY();
				double g = this.targetZ - this.fish.getZ();
				if (e != 0.0) {
					double h = Math.sqrt(d * d + e * e + g * g);
					this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * (e / h) * 0.1, 0.0));
				}

				if (d != 0.0 || g != 0.0) {
					float i = (float)(MathHelper.atan2(g, d) * 180.0F / (float)Math.PI) - 90.0F;
					this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), i, 90.0F));
					this.fish.bodyYaw = this.fish.getYaw();
				}
			} else {
				this.fish.setMovementSpeed(0.0F);
			}
		}
	}

	static class SwimToRandomPlaceGoal extends SwimAroundGoal {
		private final FishEntity fish;

		public SwimToRandomPlaceGoal(FishEntity fish) {
			super(fish, 1.0, 40);
			this.fish = fish;
		}

		@Override
		public boolean canStart() {
			return this.fish.hasSelfControl() && super.canStart();
		}
	}
}
