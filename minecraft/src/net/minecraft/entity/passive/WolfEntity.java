package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.attribute.Attributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WolfEntity extends TameableEntity {
	private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final Predicate<LivingEntity> FOLLOW_TAMED_PREDICATE = livingEntity -> {
		EntityType<?> entityType = livingEntity.getType();
		return entityType == EntityType.SHEEP || entityType == EntityType.RABBIT || entityType == EntityType.FOX;
	};
	private float begAnimationProgress;
	private float lastBegAnimationProgress;
	private boolean furWet;
	private boolean canShakeWaterOff;
	private float shakeProgress;
	private float lastShakeProgress;

	public WolfEntity(EntityType<? extends WolfEntity> entityType, World world) {
		super(entityType, world);
		this.setTamed(false);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(3, new WolfEntity.AvoidLlamaGoal(this, LlamaEntity.class, 24.0F, 1.5, 1.5));
		this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
		this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(9, new WolfBegGoal(this, 8.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
		this.targetSelector.add(2, new AttackWithOwnerGoal(this));
		this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(4, new FollowTargetIfTamedGoal(this, AnimalEntity.class, false, FOLLOW_TAMED_PREDICATE));
		this.targetSelector.add(4, new FollowTargetIfTamedGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
		this.targetSelector.add(5, new FollowTargetGoal(this, AbstractSkeletonEntity.class, false));
	}

	public static DefaultAttributeContainer.Builder createWolfAttributes() {
		return MobEntity.createMobAttributes()
			.add(Attributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(Attributes.GENERIC_MAX_HEALTH, 8.0)
			.add(Attributes.GENERIC_ATTACK_DAMAGE, 2.0);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
		if (target == null) {
			this.setAngry(false);
		} else if (!this.isTamed()) {
			this.setAngry(true);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BEGGING, false);
		this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("Angry", this.isAngry());
		tag.putByte("CollarColor", (byte)this.getCollarColor().getId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setAngry(tag.getBoolean("Angry"));
		if (tag.contains("CollarColor", 99)) {
			this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isAngry()) {
			return SoundEvents.ENTITY_WOLF_GROWL;
		} else if (this.random.nextInt(3) == 0) {
			return this.isTamed() && this.getHealth() < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
		} else {
			return SoundEvents.ENTITY_WOLF_AMBIENT;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient && this.furWet && !this.canShakeWaterOff && !this.isNavigating() && this.onGround) {
			this.canShakeWaterOff = true;
			this.shakeProgress = 0.0F;
			this.lastShakeProgress = 0.0F;
			this.world.sendEntityStatus(this, (byte)8);
		}

		if (!this.world.isClient && this.getTarget() == null && this.isAngry()) {
			this.setAngry(false);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isAlive()) {
			this.lastBegAnimationProgress = this.begAnimationProgress;
			if (this.isBegging()) {
				this.begAnimationProgress = this.begAnimationProgress + (1.0F - this.begAnimationProgress) * 0.4F;
			} else {
				this.begAnimationProgress = this.begAnimationProgress + (0.0F - this.begAnimationProgress) * 0.4F;
			}

			if (this.isWet()) {
				this.furWet = true;
				this.canShakeWaterOff = false;
				this.shakeProgress = 0.0F;
				this.lastShakeProgress = 0.0F;
			} else if ((this.furWet || this.canShakeWaterOff) && this.canShakeWaterOff) {
				if (this.shakeProgress == 0.0F) {
					this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				this.lastShakeProgress = this.shakeProgress;
				this.shakeProgress += 0.05F;
				if (this.lastShakeProgress >= 2.0F) {
					this.furWet = false;
					this.canShakeWaterOff = false;
					this.lastShakeProgress = 0.0F;
					this.shakeProgress = 0.0F;
				}

				if (this.shakeProgress > 0.4F) {
					float f = (float)this.getY();
					int i = (int)(MathHelper.sin((this.shakeProgress - 0.4F) * (float) Math.PI) * 7.0F);
					Vec3d vec3d = this.getVelocity();

					for (int j = 0; j < i; j++) {
						float g = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
						float h = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
						this.world.addParticle(ParticleTypes.SPLASH, this.getX() + (double)g, (double)(f + 0.8F), this.getZ() + (double)h, vec3d.x, vec3d.y, vec3d.z);
					}
				}
			}
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		this.furWet = false;
		this.canShakeWaterOff = false;
		this.lastShakeProgress = 0.0F;
		this.shakeProgress = 0.0F;
		super.onDeath(source);
	}

	/**
	 * Returns whether this wolf's fur is wet.
	 * <p>
	 * The wolf's fur will remain wet until the wolf shakes.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isFurWet() {
		return this.furWet;
	}

	/**
	 * Returns this wolf's brightness multiplier based on the fur wetness.
	 * <p>
	 * The brightness multiplier represents how much darker the wolf gets while its fur is wet. The multiplier changes (from 0.75 to 1.0 incrementally) when a wolf shakes.
	 * 
	 * @param tickDelta Progress for linearly interpolating between the previous and current game state.
	 * @return Brightness as a float value between 0.75 and 1.0.
	 * @see net.minecraft.client.render.entity.model.TintableAnimalModel#setColorMultiplier(float, float, float)
	 */
	@Environment(EnvType.CLIENT)
	public float getFurWetBrightnessMultiplier(float tickDelta) {
		return 0.75F + MathHelper.lerp(tickDelta, this.lastShakeProgress, this.shakeProgress) / 2.0F * 0.25F;
	}

	@Environment(EnvType.CLIENT)
	public float getShakeAnimationProgress(float tickDelta, float f) {
		float g = (MathHelper.lerp(tickDelta, this.lastShakeProgress, this.shakeProgress) + f) / 1.8F;
		if (g < 0.0F) {
			g = 0.0F;
		} else if (g > 1.0F) {
			g = 1.0F;
		}

		return MathHelper.sin(g * (float) Math.PI) * MathHelper.sin(g * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
	}

	@Environment(EnvType.CLIENT)
	public float getBegAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastBegAnimationProgress, this.begAnimationProgress) * 0.15F * (float) Math.PI;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.8F;
	}

	@Override
	public int getLookPitchSpeed() {
		return this.isSitting() ? 20 : super.getLookPitchSpeed();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getAttacker();
			this.method_24346(false);
			if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof PersistentProjectileEntity)) {
				amount = (amount + 1.0F) / 2.0F;
			}

			return super.damage(source, amount);
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.method_26825(Attributes.GENERIC_ATTACK_DAMAGE)));
		if (bl) {
			this.dealDamage(this, target);
		}

		return bl;
	}

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		if (tamed) {
			this.getAttributeInstance(Attributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
			this.setHealth(20.0F);
		} else {
			this.getAttributeInstance(Attributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
		}

		this.getAttributeInstance(Attributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(player, hand);
		} else if (this.world.isClient) {
			return this.isOwner(player) || item == Items.BONE && !this.isTamed() && !this.isAngry();
		} else {
			if (this.isTamed()) {
				if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaximumHealth()) {
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					this.heal((float)item.getFoodComponent().getHunger());
					return true;
				}

				if (!(item instanceof DyeItem)) {
					boolean bl = super.interactMob(player, hand);
					if ((!bl || this.isBaby()) && this.isOwner(player) && !this.isBreedingItem(itemStack)) {
						this.method_24346(!this.method_24345());
						this.jumping = false;
						this.navigation.stop();
						this.setTarget(null);
					}

					return bl;
				}

				DyeColor dyeColor = ((DyeItem)item).getColor();
				if (dyeColor != this.getCollarColor()) {
					this.setCollarColor(dyeColor);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}
			} else if (item == Items.BONE && !this.isAngry()) {
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}

				if (this.random.nextInt(3) == 0) {
					this.setOwner(player);
					this.navigation.stop();
					this.setTarget(null);
					this.method_24346(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.world.sendEntityStatus(this, (byte)6);
				}

				return true;
			}

			return super.interactMob(player, hand);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 8) {
			this.canShakeWaterOff = true;
			this.shakeProgress = 0.0F;
			this.lastShakeProgress = 0.0F;
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	public float getTailAngle() {
		if (this.isAngry()) {
			return 1.5393804F;
		} else {
			return this.isTamed() ? (0.55F - (this.getMaximumHealth() - this.getHealth()) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		Item item = stack.getItem();
		return item.isFood() && item.getFoodComponent().isMeat();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	public boolean isAngry() {
		return (this.dataTracker.get(TAMEABLE_FLAGS) & 2) != 0;
	}

	public void setAngry(boolean angry) {
		byte b = this.dataTracker.get(TAMEABLE_FLAGS);
		if (angry) {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 2));
		} else {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -3));
		}
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
	}

	public void setCollarColor(DyeColor color) {
		this.dataTracker.set(COLLAR_COLOR, color.getId());
	}

	public WolfEntity createChild(PassiveEntity passiveEntity) {
		WolfEntity wolfEntity = EntityType.WOLF.create(this.world);
		UUID uUID = this.getOwnerUuid();
		if (uUID != null) {
			wolfEntity.setOwnerUuid(uUID);
			wolfEntity.setTamed(true);
		}

		return wolfEntity;
	}

	public void setBegging(boolean begging) {
		this.dataTracker.set(BEGGING, begging);
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(other instanceof WolfEntity)) {
			return false;
		} else {
			WolfEntity wolfEntity = (WolfEntity)other;
			if (!wolfEntity.isTamed()) {
				return false;
			} else {
				return wolfEntity.isSitting() ? false : this.isInLove() && wolfEntity.isInLove();
			}
		}
	}

	public boolean isBegging() {
		return this.dataTracker.get(BEGGING);
	}

	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (target instanceof CreeperEntity || target instanceof GhastEntity) {
			return false;
		} else if (target instanceof WolfEntity) {
			WolfEntity wolfEntity = (WolfEntity)target;
			return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
		} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) {
			return false;
		} else {
			return target instanceof HorseBaseEntity && ((HorseBaseEntity)target).isTame()
				? false
				: !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return !this.isAngry() && super.canBeLeashedBy(player);
	}

	class AvoidLlamaGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final WolfEntity wolf;

		public AvoidLlamaGoal(WolfEntity wolf, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
			super(wolf, fleeFromType, distance, slowSpeed, fastSpeed);
			this.wolf = wolf;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.targetEntity instanceof LlamaEntity ? !this.wolf.isTamed() && this.isScaredOf((LlamaEntity)this.targetEntity) : false;
		}

		private boolean isScaredOf(LlamaEntity llama) {
			return llama.getStrength() >= WolfEntity.this.random.nextInt(5);
		}

		@Override
		public void start() {
			WolfEntity.this.setTarget(null);
			super.start();
		}

		@Override
		public void tick() {
			WolfEntity.this.setTarget(null);
			super.tick();
		}
	}
}
