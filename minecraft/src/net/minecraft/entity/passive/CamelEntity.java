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
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
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
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class CamelEntity extends AbstractHorseEntity implements JumpingMount, Saddleable {
	public static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CACTUS);
	public static final int field_40132 = 55;
	private static final float field_40146 = 0.1F;
	private static final float field_40147 = 1.4285F;
	private static final float field_40148 = 22.2222F;
	private static final int field_40149 = 40;
	private static final int field_40133 = 52;
	private static final int field_40134 = 80;
	private static final float field_40135 = 1.43F;
	public static final TrackedData<Boolean> DASHING = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Long> LAST_POSE_TICK = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.LONG);
	public final AnimationState walkingAnimationState = new AnimationState();
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
		this.stepHeight = 1.5F;
		MobNavigation mobNavigation = (MobNavigation)this.getNavigation();
		mobNavigation.setCanSwim(true);
		mobNavigation.setCanWalkOverFences(true);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsSitting", this.getPose() == EntityPose.SITTING);
		nbt.putLong("LastPoseTick", this.dataTracker.get(LAST_POSE_TICK));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.getBoolean("IsSitting")) {
			this.setPose(EntityPose.SITTING);
		}

		this.setLastPoseTick(nbt.getLong("LastPoseTick"));
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
		this.dataTracker.startTracking(LAST_POSE_TICK, -52L);
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		CamelBrain.method_45367(this, world.getRandom());
		this.dataTracker.set(LAST_POSE_TICK, world.toServerWorld().getTime() - 52L);
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
		return dimensions.height - 0.1F;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("camelBrain");
		Brain<?> brain = this.getBrain();
		((Brain<CamelEntity>)brain).tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("camelActivityUpdate");
		CamelBrain.updateActivities(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isDashing() && this.dashCooldown < 55 && (this.onGround || this.isTouchingWater())) {
			this.setDashing(false);
		}

		if (this.dashCooldown > 0) {
			this.dashCooldown--;
			if (this.dashCooldown == 0) {
				this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_CAMEL_DASH_READY, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}

		if (this.world.isClient()) {
			this.updateAnimations();
		}
	}

	private void updateAnimations() {
		if (this.idleAnimationCooldown <= 0) {
			this.idleAnimationCooldown = this.random.nextInt(40) + 80;
			this.idlingAnimationState.start(this.age);
		} else {
			this.idleAnimationCooldown--;
		}

		switch (this.getPose()) {
			case STANDING:
				this.sittingTransitionAnimationState.stop();
				this.sittingAnimationState.stop();
				this.dashingAnimationState.setRunning(this.isDashing(), this.age);
				this.standingTransitionAnimationState.setRunning(this.isChangingPose(), this.age);
				this.walkingAnimationState.setRunning((this.onGround || this.hasPrimaryPassenger()) && this.getVelocity().horizontalLengthSquared() > 1.0E-6, this.age);
				break;
			case SITTING:
				this.walkingAnimationState.stop();
				this.standingTransitionAnimationState.stop();
				this.dashingAnimationState.stop();
				if (this.shouldPlaySittingTransitionAnimation()) {
					this.sittingTransitionAnimationState.startIfNotRunning(this.age);
					this.sittingAnimationState.stop();
				} else {
					this.sittingTransitionAnimationState.stop();
					this.sittingAnimationState.startIfNotRunning(this.age);
				}
				break;
			default:
				this.walkingAnimationState.stop();
				this.sittingTransitionAnimationState.stop();
				this.sittingAnimationState.stop();
				this.standingTransitionAnimationState.stop();
				this.dashingAnimationState.stop();
		}
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.isAlive()) {
			if (this.isStationary() && this.isOnGround()) {
				this.setVelocity(this.getVelocity().multiply(0.0, 1.0, 0.0));
				movementInput = movementInput.multiply(0.0, 1.0, 0.0);
			}

			super.travel(movementInput);
		}
	}

	public boolean isStationary() {
		return this.isSitting() || this.isChangingPose();
	}

	@Override
	protected float getHorsebackMovementSpeed(LivingEntity passenger) {
		float f = passenger.isSprinting() && this.getJumpCooldown() == 0 ? 0.1F : 0.0F;
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + f;
	}

	@Override
	protected boolean ignoresMovementInput(LivingEntity passenger) {
		boolean bl = this.isChangingPose();
		if (this.isSitting() && !bl && passenger.forwardSpeed > 0.0F) {
			this.startStanding();
		}

		return this.isStationary() || super.ignoresMovementInput(passenger);
	}

	@Override
	public boolean canJump(PlayerEntity player) {
		return !this.isStationary() && this.getPrimaryPassenger() == player && super.canJump(player);
	}

	@Override
	public void setJumpStrength(int strength) {
		if (this.isSaddled() && this.dashCooldown <= 0 && this.isOnGround()) {
			super.setJumpStrength(strength);
		}
	}

	@Override
	protected void jump(float strength, float sidewaysSpeed, float forwardSpeed) {
		double d = this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) * (double)this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier();
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

	public boolean isPanicking() {
		return this.getBrain().isMemoryInState(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_PRESENT);
	}

	@Override
	public void startJumping(int height) {
		this.playSound(SoundEvents.ENTITY_CAMEL_DASH, 1.0F, 1.0F);
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
		if (state.getSoundGroup() == BlockSoundGroup.SAND) {
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
		if (player.shouldCancelInteraction()) {
			this.openInventory(player);
			return ActionResult.success(this.world.isClient);
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

				return ActionResult.success(this.world.isClient);
			}
		}
	}

	@Override
	protected void updateForLeashLength(float leashLength) {
		if (leashLength > 6.0F && this.isSitting() && !this.isChangingPose()) {
			this.startStanding();
		}
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
				this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
				if (!this.world.isClient) {
					this.growUp(10);
				}
			}

			if (!bl && !bl2 && !bl3) {
				return false;
			} else {
				if (!this.isSilent()) {
					SoundEvent soundEvent = this.getEatSound();
					if (soundEvent != null) {
						this.world
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
	public void updatePassengerPosition(Entity passenger) {
		int i = this.getPassengerList().indexOf(passenger);
		if (i >= 0) {
			boolean bl = i == 0;
			float f = 0.5F;
			float g = (float)(this.isRemoved() ? 0.01F : this.method_45346(bl, 0.0F) + passenger.getHeightOffset());
			if (this.getPassengerList().size() > 1) {
				if (!bl) {
					f = -0.7F;
				}

				if (passenger instanceof AnimalEntity) {
					f += 0.2F;
				}
			}

			Vec3d vec3d = new Vec3d(0.0, 0.0, (double)f).rotateY(-this.bodyYaw * (float) (Math.PI / 180.0));
			passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
			this.clampPassengerYaw(passenger);
		}
	}

	private double method_45346(boolean bl, float f) {
		double d = this.getMountedHeightOffset();
		float g = this.getScaleFactor() * 1.43F;
		float h = g - this.getScaleFactor() * 0.2F;
		float i = g - h;
		boolean bl2 = this.isChangingPose();
		boolean bl3 = this.getPose() == EntityPose.SITTING;
		if (bl2) {
			int j = bl3 ? 40 : 52;
			int k;
			float l;
			if (bl3) {
				k = 28;
				l = bl ? 0.5F : 0.1F;
			} else {
				k = bl ? 24 : 32;
				l = bl ? 0.6F : 0.35F;
			}

			float m = (float)this.getLastPoseTickDelta() + f;
			boolean bl4 = m < (float)k;
			float n = bl4 ? m / (float)k : (m - (float)k) / (float)(j - k);
			float o = g - l * h;
			d += bl3 ? (double)MathHelper.lerp(n, bl4 ? g : o, bl4 ? o : i) : (double)MathHelper.lerp(n, bl4 ? i - g : i - o, bl4 ? i - o : 0.0F);
		}

		if (bl3 && !bl2) {
			d += (double)i;
		}

		return d;
	}

	@Override
	public Vec3d getLeashOffset(float tickDelta) {
		return new Vec3d(0.0, this.method_45346(true, tickDelta) - (double)(0.2F * this.getScaleFactor()), (double)(this.getWidth() * 0.56F));
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)(this.getDimensions(this.getPose()).height - (this.isBaby() ? 0.35F : 0.6F));
	}

	@Override
	public void onPassengerLookAround(Entity passenger) {
		if (this.getPrimaryPassenger() != passenger) {
			this.clampPassengerYaw(passenger);
		}
	}

	private void clampPassengerYaw(Entity passenger) {
		passenger.setBodyYaw(this.getYaw());
		float f = passenger.getYaw();
		float g = MathHelper.wrapDegrees(f - this.getYaw());
		float h = MathHelper.clamp(g, -160.0F, 160.0F);
		passenger.prevYaw += h - g;
		float i = f + h - g;
		passenger.setYaw(i);
		passenger.setHeadYaw(i);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() <= 2;
	}

	@Nullable
	@Override
	public LivingEntity getPrimaryPassenger() {
		if (!this.getPassengerList().isEmpty() && this.isSaddled()) {
			Entity entity = (Entity)this.getPassengerList().get(0);
			if (entity instanceof LivingEntity) {
				return (LivingEntity)entity;
			}
		}

		return null;
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public boolean isSitting() {
		return this.getPose() == EntityPose.SITTING;
	}

	public boolean isChangingPose() {
		long l = this.getLastPoseTickDelta();

		return switch (this.getPose()) {
			case STANDING -> l < 52L;
			case SITTING -> l < 40L;
			default -> false;
		};
	}

	private boolean shouldPlaySittingTransitionAnimation() {
		return this.getPose() == EntityPose.SITTING && this.getLastPoseTickDelta() < 40L;
	}

	public void startSitting() {
		if (!this.isInPose(EntityPose.SITTING)) {
			this.playSound(SoundEvents.ENTITY_CAMEL_SIT, 1.0F, 1.0F);
			this.setPose(EntityPose.SITTING);
			this.setLastPoseTick(this.world.getTime());
		}
	}

	public void startStanding() {
		if (!this.isInPose(EntityPose.STANDING)) {
			this.playSound(SoundEvents.ENTITY_CAMEL_STAND, 1.0F, 1.0F);
			this.setPose(EntityPose.STANDING);
			this.setLastPoseTick(this.world.getTime());
		}
	}

	public void setStanding() {
		this.setPose(EntityPose.STANDING);
		this.setLastPoseTick(this.world.getTime() - 52L);
	}

	@VisibleForTesting
	public void setLastPoseTick(long lastPoseTick) {
		this.dataTracker.set(LAST_POSE_TICK, lastPoseTick);
	}

	public long getLastPoseTickDelta() {
		return this.world.getTime() - this.dataTracker.get(LAST_POSE_TICK);
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
	protected BodyControl createBodyControl() {
		return new CamelEntity.CamelBodyControl(this);
	}

	@Override
	public boolean isTame() {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		if (!this.world.isClient) {
			player.openHorseInventory(this, this.items);
		}
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
}
