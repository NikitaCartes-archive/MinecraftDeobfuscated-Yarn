package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;

public class CamelEntity extends AbstractHorseEntity implements JumpingMount, Saddleable {
	public static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CACTUS);
	public static final float field_45127 = 0.45F;
	public static final int field_40132 = 55;
	public static final int field_41764 = 30;
	private static final float field_40146 = 0.1F;
	private static final float field_40147 = 1.4285F;
	private static final float field_40148 = 22.2222F;
	private static final int field_43388 = 5;
	private static final int field_40149 = 40;
	private static final int field_40133 = 52;
	private static final int field_40134 = 80;
	private static final float field_40135 = 1.43F;
	public static final TrackedData<Boolean> DASHING = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Long> LAST_POSE_TICK = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.LONG);
	public final AnimationState sittingTransitionAnimationState = new AnimationState();
	public final AnimationState sittingAnimationState = new AnimationState();
	public final AnimationState standingTransitionAnimationState = new AnimationState();
	public final AnimationState idlingAnimationState = new AnimationState();
	public final AnimationState dashingAnimationState = new AnimationState();
	private static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.changing(EntityType.CAMEL.getWidth(), EntityType.CAMEL.getHeight() - 1.43F);
	private int dashCooldown = 0;
	private int idleAnimationCooldown = 0;

	public CamelEntity(EntityType<? extends CamelEntity> entityType, World world) {
		super(entityType, world);
		this.setStepHeight(1.5F);
		this.moveControl = new CamelEntity.CamelMoveControl();
		this.lookControl = new CamelEntity.CamelLookControl();
		MobNavigation mobNavigation = (MobNavigation)this.getNavigation();
		mobNavigation.setCanSwim(true);
		mobNavigation.setCanWalkOverFences(true);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putLong("LastPoseTick", this.dataTracker.get(LAST_POSE_TICK));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		long l = nbt.getLong("LastPoseTick");
		if (l < 0L) {
			this.setPose(EntityPose.SITTING);
		}

		this.setLastPoseTick(l);
	}

	public static DefaultAttributeContainer.Builder createCamelAttributes() {
		return createBaseHorseAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 32.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.09F)
			.add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.42F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(DASHING, false);
		this.dataTracker.startTracking(LAST_POSE_TICK, 0L);
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		CamelBrain.initialize(this, world.getRandom());
		this.initLastPoseTick(world.toServerWorld().getTime());
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected Brain.Profile<CamelEntity> createBrainProfile() {
		return CamelBrain.createProfile();
	}

	@Override
	protected void initGoals() {
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return CamelBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return pose == EntityPose.SITTING ? SITTING_DIMENSIONS.scaled(this.getScaleFactor()) : super.getDimensions(pose);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height - 0.1F * this.getScaleFactor();
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("camelBrain");
		Brain<?> brain = this.getBrain();
		((Brain<CamelEntity>)brain).tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("camelActivityUpdate");
		CamelBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isDashing() && this.dashCooldown < 50 && (this.isOnGround() || this.isInFluid() || this.hasVehicle())) {
			this.setDashing(false);
		}

		if (this.dashCooldown > 0) {
			this.dashCooldown--;
			if (this.dashCooldown == 0) {
				this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_CAMEL_DASH_READY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			}
		}

		if (this.getWorld().isClient()) {
			this.updateAnimations();
		}

		if (this.isStationary()) {
			this.clampHeadYaw(this, 30.0F);
		}

		if (this.isSitting() && this.isTouchingWater()) {
			this.setStanding();
		}
	}

	private void updateAnimations() {
		if (this.idleAnimationCooldown <= 0) {
			this.idleAnimationCooldown = this.random.nextInt(40) + 80;
			this.idlingAnimationState.start(this.age);
		} else {
			this.idleAnimationCooldown--;
		}

		if (this.shouldUpdateSittingAnimations()) {
			this.standingTransitionAnimationState.stop();
			this.dashingAnimationState.stop();
			if (this.shouldPlaySittingTransitionAnimation()) {
				this.sittingTransitionAnimationState.startIfNotRunning(this.age);
				this.sittingAnimationState.stop();
			} else {
				this.sittingTransitionAnimationState.stop();
				this.sittingAnimationState.startIfNotRunning(this.age);
			}
		} else {
			this.sittingTransitionAnimationState.stop();
			this.sittingAnimationState.stop();
			this.dashingAnimationState.setRunning(this.isDashing(), this.age);
			this.standingTransitionAnimationState.setRunning(this.isChangingPose() && this.getLastPoseTickDelta() >= 0L, this.age);
		}
	}

	@Override
	protected void updateLimbs(float posDelta) {
		float f;
		if (this.getPose() == EntityPose.STANDING && !this.dashingAnimationState.isRunning()) {
			f = Math.min(posDelta * 6.0F, 1.0F);
		} else {
			f = 0.0F;
		}

		this.limbAnimator.updateLimbs(f, 0.2F);
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.isStationary() && this.isOnGround()) {
			this.setVelocity(this.getVelocity().multiply(0.0, 1.0, 0.0));
			movementInput = movementInput.multiply(0.0, 1.0, 0.0);
		}

		super.travel(movementInput);
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);
		if (controllingPlayer.forwardSpeed > 0.0F && this.isSitting() && !this.isChangingPose()) {
			this.startStanding();
		}
	}

	public boolean isStationary() {
		return this.isSitting() || this.isChangingPose();
	}

	@Override
	protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
		float f = controllingPlayer.isSprinting() && this.getJumpCooldown() == 0 ? 0.1F : 0.0F;
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + f;
	}

	@Override
	protected Vec2f getControlledRotation(LivingEntity controllingPassenger) {
		return this.isStationary() ? new Vec2f(this.getPitch(), this.getYaw()) : super.getControlledRotation(controllingPassenger);
	}

	@Override
	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
		return this.isStationary() ? Vec3d.ZERO : super.getControlledMovementInput(controllingPlayer, movementInput);
	}

	@Override
	public boolean canJump() {
		return !this.isStationary() && super.canJump();
	}

	@Override
	public void setJumpStrength(int strength) {
		if (this.isSaddled() && this.dashCooldown <= 0 && this.isOnGround()) {
			super.setJumpStrength(strength);
		}
	}

	@Override
	public boolean canSprintAsVehicle() {
		return true;
	}

	@Override
	protected void jump(float strength, Vec3d movementInput) {
		double d = this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) * (double)this.getJumpVelocityMultiplier()
			+ (double)this.getJumpBoostVelocityModifier();
		this.addVelocity(
			this.getRotationVector()
				.multiply(1.0, 0.0, 1.0)
				.normalize()
				.multiply((double)(22.2222F * strength) * this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (double)this.getVelocityMultiplier())
				.add(0.0, (double)(1.4285F * strength) * d, 0.0)
		);
		this.dashCooldown = 55;
		this.setDashing(true);
		this.velocityDirty = true;
	}

	public boolean isDashing() {
		return this.dataTracker.get(DASHING);
	}

	public void setDashing(boolean dashing) {
		this.dataTracker.set(DASHING, dashing);
	}

	@Override
	public void startJumping(int height) {
		this.playSound(SoundEvents.ENTITY_CAMEL_DASH, 1.0F, this.getSoundPitch());
		this.emitGameEvent(GameEvent.ENTITY_ACTION);
		this.setDashing(true);
	}

	@Override
	public void stopJumping() {
	}

	@Override
	public int getJumpCooldown() {
		return this.dashCooldown;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_CAMEL_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CAMEL_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_CAMEL_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (state.isIn(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS)) {
			this.playSound(SoundEvents.ENTITY_CAMEL_STEP_SAND, 1.0F, 1.0F);
		} else {
			this.playSound(SoundEvents.ENTITY_CAMEL_STEP, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (player.shouldCancelInteraction() && !this.isBaby()) {
			this.openInventory(player);
			return ActionResult.success(this.getWorld().isClient);
		} else {
			ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
			if (actionResult.isAccepted()) {
				return actionResult;
			} else if (this.isBreedingItem(itemStack)) {
				return this.interactHorse(player, itemStack);
			} else {
				if (this.getPassengerList().size() < 2 && !this.isBaby()) {
					this.putPlayerOnBack(player);
				}

				return ActionResult.success(this.getWorld().isClient);
			}
		}
	}

	@Override
	protected void updateForLeashLength(float leashLength) {
		if (leashLength > 6.0F && this.isSitting() && !this.isChangingPose() && this.canChangePose()) {
			this.startStanding();
		}
	}

	public boolean canChangePose() {
		return this.wouldNotSuffocateInPose(this.isSitting() ? EntityPose.STANDING : EntityPose.SITTING);
	}

	@Override
	protected boolean receiveFood(PlayerEntity player, ItemStack item) {
		if (!this.isBreedingItem(item)) {
			return false;
		} else {
			boolean bl = this.getHealth() < this.getMaxHealth();
			if (bl) {
				this.heal(2.0F);
			}

			boolean bl2 = this.isTame() && this.getBreedingAge() == 0 && this.canEat();
			if (bl2) {
				this.lovePlayer(player);
			}

			boolean bl3 = this.isBaby();
			if (bl3) {
				this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
				if (!this.getWorld().isClient) {
					this.growUp(10);
				}
			}

			if (!bl && !bl2 && !bl3) {
				return false;
			} else {
				if (!this.isSilent()) {
					SoundEvent soundEvent = this.getEatSound();
					if (soundEvent != null) {
						this.getWorld()
							.playSound(
								null,
								this.getX(),
								this.getY(),
								this.getZ(),
								soundEvent,
								this.getSoundCategory(),
								1.0F,
								1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
							);
					}
				}

				this.emitGameEvent(GameEvent.EAT);
				return true;
			}
		}
	}

	@Override
	protected boolean shouldAmbientStand() {
		return false;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other != this && other instanceof CamelEntity camelEntity && this.canBreed() && camelEntity.canBreed()) {
			return true;
		}

		return false;
	}

	@Nullable
	public CamelEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.CAMEL.create(serverWorld);
	}

	@Nullable
	@Override
	protected SoundEvent getEatSound() {
		return SoundEvents.ENTITY_CAMEL_EAT;
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		this.setStanding();
		super.applyDamage(source, amount);
	}

	@Override
	protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		int i = Math.max(this.getPassengerList().indexOf(passenger), 0);
		boolean bl = i == 0;
		float f = 0.5F;
		float g = (float)(this.isRemoved() ? 0.01F : this.getPassengerAttachmentY(bl, 0.0F, dimensions, scaleFactor));
		if (this.getPassengerList().size() > 1) {
			if (!bl) {
				f = -0.7F;
			}

			if (passenger instanceof AnimalEntity) {
				f += 0.2F;
			}
		}

		return new Vector3f(0.0F, g, f * scaleFactor);
	}

	@Override
	public float getScaleFactor() {
		return this.isBaby() ? 0.45F : 1.0F;
	}

	private double getPassengerAttachmentY(boolean primaryPassenger, float tickDelta, EntityDimensions dimensions, float scaleFactor) {
		double d = (double)(dimensions.height - 0.375F * scaleFactor);
		float f = scaleFactor * 1.43F;
		float g = f - scaleFactor * 0.2F;
		float h = f - g;
		boolean bl = this.isChangingPose();
		boolean bl2 = this.isSitting();
		if (bl) {
			int i = bl2 ? 40 : 52;
			int j;
			float k;
			if (bl2) {
				j = 28;
				k = primaryPassenger ? 0.5F : 0.1F;
			} else {
				j = primaryPassenger ? 24 : 32;
				k = primaryPassenger ? 0.6F : 0.35F;
			}

			float l = MathHelper.clamp((float)this.getLastPoseTickDelta() + tickDelta, 0.0F, (float)i);
			boolean bl3 = l < (float)j;
			float m = bl3 ? l / (float)j : (l - (float)j) / (float)(i - j);
			float n = f - k * g;
			d += bl2 ? (double)MathHelper.lerp(m, bl3 ? f : n, bl3 ? n : h) : (double)MathHelper.lerp(m, bl3 ? h - f : h - n, bl3 ? h - n : 0.0F);
		}

		if (bl2 && !bl) {
			d += (double)h;
		}

		return d;
	}

	@Override
	public Vec3d getLeashOffset(float tickDelta) {
		EntityDimensions entityDimensions = this.getDimensions(this.getPose());
		float f = this.getScaleFactor();
		return new Vec3d(0.0, this.getPassengerAttachmentY(true, tickDelta, entityDimensions, f) - (double)(0.2F * f), (double)(entityDimensions.width * 0.56F));
	}

	private void clampHeadYaw(Entity entity, float range) {
		float f = entity.getHeadYaw();
		float g = MathHelper.wrapDegrees(this.bodyYaw - f);
		float h = MathHelper.clamp(MathHelper.wrapDegrees(this.bodyYaw - f), -range, range);
		float i = f + g - h;
		entity.setHeadYaw(i);
	}

	@Override
	public int getMaxHeadRotation() {
		return 30;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() <= 2;
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public boolean isSitting() {
		return this.dataTracker.get(LAST_POSE_TICK) < 0L;
	}

	public boolean shouldUpdateSittingAnimations() {
		return this.getLastPoseTickDelta() < 0L != this.isSitting();
	}

	public boolean isChangingPose() {
		long l = this.getLastPoseTickDelta();
		return l < (long)(this.isSitting() ? 40 : 52);
	}

	private boolean shouldPlaySittingTransitionAnimation() {
		return this.isSitting() && this.getLastPoseTickDelta() < 40L && this.getLastPoseTickDelta() >= 0L;
	}

	public void startSitting() {
		if (!this.isSitting()) {
			this.playSound(SoundEvents.ENTITY_CAMEL_SIT, 1.0F, this.getSoundPitch());
			this.setPose(EntityPose.SITTING);
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.setLastPoseTick(-this.getWorld().getTime());
		}
	}

	public void startStanding() {
		if (this.isSitting()) {
			this.playSound(SoundEvents.ENTITY_CAMEL_STAND, 1.0F, this.getSoundPitch());
			this.setPose(EntityPose.STANDING);
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.setLastPoseTick(this.getWorld().getTime());
		}
	}

	public void setStanding() {
		this.setPose(EntityPose.STANDING);
		this.emitGameEvent(GameEvent.ENTITY_ACTION);
		this.initLastPoseTick(this.getWorld().getTime());
	}

	@VisibleForTesting
	public void setLastPoseTick(long lastPoseTick) {
		this.dataTracker.set(LAST_POSE_TICK, lastPoseTick);
	}

	private void initLastPoseTick(long time) {
		this.setLastPoseTick(Math.max(0L, time - 52L - 1L));
	}

	public long getLastPoseTickDelta() {
		return this.getWorld().getTime() - Math.abs(this.dataTracker.get(LAST_POSE_TICK));
	}

	@Override
	public SoundEvent getSaddleSound() {
		return SoundEvents.ENTITY_CAMEL_SADDLE;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (!this.firstUpdate && DASHING.equals(data)) {
			this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public boolean isTame() {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		if (!this.getWorld().isClient) {
			player.openHorseInventory(this, this.items);
		}
	}

	@Override
	protected BodyControl createBodyControl() {
		return new CamelEntity.CamelBodyControl(this);
	}

	class CamelBodyControl extends BodyControl {
		public CamelBodyControl(CamelEntity camel) {
			super(camel);
		}

		@Override
		public void tick() {
			if (!CamelEntity.this.isStationary()) {
				super.tick();
			}
		}
	}

	class CamelLookControl extends LookControl {
		CamelLookControl() {
			super(CamelEntity.this);
		}

		@Override
		public void tick() {
			if (!CamelEntity.this.hasControllingPassenger()) {
				super.tick();
			}
		}
	}

	class CamelMoveControl extends MoveControl {
		public CamelMoveControl() {
			super(CamelEntity.this);
		}

		@Override
		public void tick() {
			if (this.state == MoveControl.State.MOVE_TO
				&& !CamelEntity.this.isLeashed()
				&& CamelEntity.this.isSitting()
				&& !CamelEntity.this.isChangingPose()
				&& CamelEntity.this.canChangePose()) {
				CamelEntity.this.startStanding();
			}

			super.tick();
		}
	}
}
