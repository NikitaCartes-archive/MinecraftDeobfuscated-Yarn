package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class HorseBaseEntity extends AnimalEntity implements InventoryListener, JumpingMount {
	private static final Predicate<LivingEntity> IS_BRED_HORSE = livingEntity -> livingEntity instanceof HorseBaseEntity
			&& ((HorseBaseEntity)livingEntity).isBred();
	private static final TargetPredicate PARENT_HORSE_PREDICATE = new TargetPredicate()
		.setBaseMaxDistance(16.0)
		.includeInvulnerable()
		.includeTeammates()
		.includeHidden()
		.setPredicate(IS_BRED_HORSE);
	protected static final EntityAttribute JUMP_STRENGTH = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0)
		.setName("Jump Strength")
		.setTracked(true);
	private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private int eatingGrassTicks;
	private int eatingTicks;
	private int angryTicks;
	public int field_6957;
	public int field_6958;
	protected boolean inAir;
	protected BasicInventory items;
	protected int temper;
	protected float jumpStrength;
	private boolean jumping;
	private float eatingGrassAnimationProgress;
	private float lastEatingGrassAnimationProgress;
	private float angryAnimationProgress;
	private float lastAngryAnimationProgress;
	private float eatingAnimationProgress;
	private float lastEatingAnimationProgress;
	protected boolean field_6964 = true;
	protected int soundTicks;

	protected HorseBaseEntity(EntityType<? extends HorseBaseEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.method_6721();
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
		this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, HorseBaseEntity.class));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HORSE_FLAGS, (byte)0);
		this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
	}

	protected boolean getHorseFlag(int bitmask) {
		return (this.dataTracker.get(HORSE_FLAGS) & bitmask) != 0;
	}

	protected void setHorseFlag(int bitmask, boolean flag) {
		byte b = this.dataTracker.get(HORSE_FLAGS);
		if (flag) {
			this.dataTracker.set(HORSE_FLAGS, (byte)(b | bitmask));
		} else {
			this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~bitmask));
		}
	}

	public boolean isTame() {
		return this.getHorseFlag(2);
	}

	@Nullable
	public UUID getOwnerUuid() {
		return (UUID)this.dataTracker.get(OWNER_UUID).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uuid) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
	}

	public boolean isInAir() {
		return this.inAir;
	}

	public void setTame(boolean tame) {
		this.setHorseFlag(2, tame);
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	@Override
	protected void updateForLeashLength(float leashLength) {
		if (leashLength > 6.0F && this.isEatingGrass()) {
			this.setEatingGrass(false);
		}
	}

	public boolean isEatingGrass() {
		return this.getHorseFlag(16);
	}

	public boolean isAngry() {
		return this.getHorseFlag(32);
	}

	public boolean isBred() {
		return this.getHorseFlag(8);
	}

	public void setBred(boolean bl) {
		this.setHorseFlag(8, bl);
	}

	public void setSaddled(boolean bl) {
		this.setHorseFlag(4, bl);
	}

	public int getTemper() {
		return this.temper;
	}

	public void setTemper(int temper) {
		this.temper = temper;
	}

	public int addTemper(int difference) {
		int i = MathHelper.clamp(this.getTemper() + difference, 0, this.getMaxTemper());
		this.setTemper(i);
		return i;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		Entity entity = source.getAttacker();
		return this.hasPassengers() && entity != null && this.hasPassengerDeep(entity) ? false : super.damage(source, amount);
	}

	@Override
	public boolean isPushable() {
		return !this.hasPassengers();
	}

	private void playEatingAnimation() {
		this.setEating();
		if (!this.isSilent()) {
			this.world
				.playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.ENTITY_HORSE_EAT,
					this.getSoundCategory(),
					1.0F,
					1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
		if (fallDistance > 1.0F) {
			this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
		}

		int i = this.computeFallDamage(fallDistance, damageMultiplier);
		if (i <= 0) {
			return false;
		} else {
			this.damage(DamageSource.FALL, (float)i);
			if (this.hasPassengers()) {
				for (Entity entity : this.getPassengersDeep()) {
					entity.damage(DamageSource.FALL, (float)i);
				}
			}

			this.playBlockFallSound();
			return true;
		}
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		return MathHelper.ceil((fallDistance * 0.5F - 3.0F) * damageMultiplier);
	}

	protected int getInventorySize() {
		return 2;
	}

	protected void method_6721() {
		BasicInventory basicInventory = this.items;
		this.items = new BasicInventory(this.getInventorySize());
		if (basicInventory != null) {
			basicInventory.removeListener(this);
			int i = Math.min(basicInventory.getInvSize(), this.items.getInvSize());

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = basicInventory.getInvStack(j);
				if (!itemStack.isEmpty()) {
					this.items.setInvStack(j, itemStack.copy());
				}
			}
		}

		this.items.addListener(this);
		this.updateSaddle();
	}

	protected void updateSaddle() {
		if (!this.world.isClient) {
			this.setSaddled(!this.items.getInvStack(0).isEmpty() && this.canBeSaddled());
		}
	}

	@Override
	public void onInvChange(Inventory inventory) {
		boolean bl = this.isSaddled();
		this.updateSaddle();
		if (this.age > 20 && !bl && this.isSaddled()) {
			this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
		}
	}

	public double getJumpStrength() {
		return this.getAttributeInstance(JUMP_STRENGTH).getValue();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		if (this.random.nextInt(3) == 0) {
			this.updateAnger();
		}

		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
			this.updateAnger();
		}

		return null;
	}

	public boolean canBeSaddled() {
		return true;
	}

	public boolean isSaddled() {
		return this.getHorseFlag(4);
	}

	@Nullable
	protected SoundEvent getAngrySound() {
		this.updateAnger();
		return null;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (!state.getMaterial().isLiquid()) {
			BlockState blockState = this.world.getBlockState(pos.up());
			BlockSoundGroup blockSoundGroup = state.getSoundGroup();
			if (blockState.getBlock() == Blocks.SNOW) {
				blockSoundGroup = blockState.getSoundGroup();
			}

			if (this.hasPassengers() && this.field_6964) {
				this.soundTicks++;
				if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
					this.playWalkSound(blockSoundGroup);
				} else if (this.soundTicks <= 5) {
					this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
				}
			} else if (blockSoundGroup == BlockSoundGroup.WOOD) {
				this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			} else {
				this.playSound(SoundEvents.ENTITY_HORSE_STEP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			}
		}
	}

	protected void playWalkSound(BlockSoundGroup group) {
		this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, group.getVolume() * 0.15F, group.getPitch());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributes().register(JUMP_STRENGTH);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(53.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.225F);
	}

	@Override
	public int getLimitPerChunk() {
		return 6;
	}

	public int getMaxTemper() {
		return 100;
	}

	@Override
	protected float getSoundVolume() {
		return 0.8F;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 400;
	}

	public void openInventory(PlayerEntity player) {
		if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTame()) {
			player.openHorseInventory(this, this.items);
		}
	}

	protected boolean receiveFood(PlayerEntity player, ItemStack item) {
		boolean bl = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		Item item2 = item.getItem();
		if (item2 == Items.WHEAT) {
			f = 2.0F;
			i = 20;
			j = 3;
		} else if (item2 == Items.SUGAR) {
			f = 1.0F;
			i = 30;
			j = 3;
		} else if (item2 == Blocks.HAY_BLOCK.asItem()) {
			f = 20.0F;
			i = 180;
		} else if (item2 == Items.APPLE) {
			f = 3.0F;
			i = 60;
			j = 3;
		} else if (item2 == Items.GOLDEN_CARROT) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(player);
			}
		} else if (item2 == Items.GOLDEN_APPLE || item2 == Items.ENCHANTED_GOLDEN_APPLE) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(player);
			}
		}

		if (this.getHealth() < this.getMaximumHealth() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isBaby() && i > 0) {
			this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.growUp(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
			bl = true;
			if (!this.world.isClient) {
				this.addTemper(j);
			}
		}

		if (bl) {
			this.playEatingAnimation();
		}

		return bl;
	}

	protected void putPlayerOnBack(PlayerEntity player) {
		this.setEatingGrass(false);
		this.setAngry(false);
		if (!this.world.isClient) {
			player.yaw = this.yaw;
			player.pitch = this.pitch;
			player.startRiding(this);
		}
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	private void method_6759() {
		this.field_6957 = 1;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.items != null) {
			for (int i = 0; i < this.items.getInvSize(); i++) {
				ItemStack itemStack = this.items.getInvStack(i);
				if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
					this.dropStack(itemStack);
				}
			}
		}
	}

	@Override
	public void tickMovement() {
		if (this.random.nextInt(200) == 0) {
			this.method_6759();
		}

		super.tickMovement();
		if (!this.world.isClient && this.isAlive()) {
			if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}

			if (this.eatsGrass()) {
				if (!this.isEatingGrass()
					&& !this.hasPassengers()
					&& this.random.nextInt(300) == 0
					&& this.world.getBlockState(this.getSenseCenterPos().down()).getBlock() == Blocks.GRASS_BLOCK) {
					this.setEatingGrass(true);
				}

				if (this.isEatingGrass() && ++this.eatingGrassTicks > 50) {
					this.eatingGrassTicks = 0;
					this.setEatingGrass(false);
				}
			}

			this.walkToParent();
		}
	}

	protected void walkToParent() {
		if (this.isBred() && this.isBaby() && !this.isEatingGrass()) {
			LivingEntity livingEntity = this.world
				.getClosestEntity(HorseBaseEntity.class, PARENT_HORSE_PREDICATE, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().expand(16.0));
			if (livingEntity != null && this.squaredDistanceTo(livingEntity) > 4.0) {
				this.navigation.findPathTo(livingEntity, 0);
			}
		}
	}

	public boolean eatsGrass() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
			this.eatingTicks = 0;
			this.setHorseFlag(64, false);
		}

		if ((this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) && this.angryTicks > 0 && ++this.angryTicks > 20) {
			this.angryTicks = 0;
			this.setAngry(false);
		}

		if (this.field_6957 > 0 && ++this.field_6957 > 8) {
			this.field_6957 = 0;
		}

		if (this.field_6958 > 0) {
			this.field_6958++;
			if (this.field_6958 > 300) {
				this.field_6958 = 0;
			}
		}

		this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
		if (this.isEatingGrass()) {
			this.eatingGrassAnimationProgress = this.eatingGrassAnimationProgress + (1.0F - this.eatingGrassAnimationProgress) * 0.4F + 0.05F;
			if (this.eatingGrassAnimationProgress > 1.0F) {
				this.eatingGrassAnimationProgress = 1.0F;
			}
		} else {
			this.eatingGrassAnimationProgress = this.eatingGrassAnimationProgress + ((0.0F - this.eatingGrassAnimationProgress) * 0.4F - 0.05F);
			if (this.eatingGrassAnimationProgress < 0.0F) {
				this.eatingGrassAnimationProgress = 0.0F;
			}
		}

		this.lastAngryAnimationProgress = this.angryAnimationProgress;
		if (this.isAngry()) {
			this.eatingGrassAnimationProgress = 0.0F;
			this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
			this.angryAnimationProgress = this.angryAnimationProgress + (1.0F - this.angryAnimationProgress) * 0.4F + 0.05F;
			if (this.angryAnimationProgress > 1.0F) {
				this.angryAnimationProgress = 1.0F;
			}
		} else {
			this.jumping = false;
			this.angryAnimationProgress = this.angryAnimationProgress
				+ ((0.8F * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress) * 0.6F - 0.05F);
			if (this.angryAnimationProgress < 0.0F) {
				this.angryAnimationProgress = 0.0F;
			}
		}

		this.lastEatingAnimationProgress = this.eatingAnimationProgress;
		if (this.getHorseFlag(64)) {
			this.eatingAnimationProgress = this.eatingAnimationProgress + (1.0F - this.eatingAnimationProgress) * 0.7F + 0.05F;
			if (this.eatingAnimationProgress > 1.0F) {
				this.eatingAnimationProgress = 1.0F;
			}
		} else {
			this.eatingAnimationProgress = this.eatingAnimationProgress + ((0.0F - this.eatingAnimationProgress) * 0.7F - 0.05F);
			if (this.eatingAnimationProgress < 0.0F) {
				this.eatingAnimationProgress = 0.0F;
			}
		}
	}

	private void setEating() {
		if (!this.world.isClient) {
			this.eatingTicks = 1;
			this.setHorseFlag(64, true);
		}
	}

	public void setEatingGrass(boolean eatingGrass) {
		this.setHorseFlag(16, eatingGrass);
	}

	public void setAngry(boolean angry) {
		if (angry) {
			this.setEatingGrass(false);
		}

		this.setHorseFlag(32, angry);
	}

	private void updateAnger() {
		if (this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) {
			this.angryTicks = 1;
			this.setAngry(true);
		}
	}

	public void playAngrySound() {
		this.updateAnger();
		SoundEvent soundEvent = this.getAngrySound();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public boolean bondWithPlayer(PlayerEntity player) {
		this.setOwnerUuid(player.getUuid());
		this.setTame(true);
		if (player instanceof ServerPlayerEntity) {
			Criterions.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
		}

		this.world.sendEntityStatus(this, (byte)7);
		return true;
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.isAlive()) {
			if (this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled()) {
				LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
				this.yaw = livingEntity.yaw;
				this.prevYaw = this.yaw;
				this.pitch = livingEntity.pitch * 0.5F;
				this.setRotation(this.yaw, this.pitch);
				this.bodyYaw = this.yaw;
				this.headYaw = this.bodyYaw;
				float f = livingEntity.sidewaysSpeed * 0.5F;
				float g = livingEntity.forwardSpeed;
				if (g <= 0.0F) {
					g *= 0.25F;
					this.soundTicks = 0;
				}

				if (this.onGround && this.jumpStrength == 0.0F && this.isAngry() && !this.jumping) {
					f = 0.0F;
					g = 0.0F;
				}

				if (this.jumpStrength > 0.0F && !this.isInAir() && this.onGround) {
					double d = this.getJumpStrength() * (double)this.jumpStrength * (double)this.getJumpVelocityMultiplier();
					double e;
					if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
						e = d + (double)((float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
					} else {
						e = d;
					}

					Vec3d vec3d = this.getVelocity();
					this.setVelocity(vec3d.x, e, vec3d.z);
					this.setInAir(true);
					this.velocityDirty = true;
					if (g > 0.0F) {
						float h = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
						float i = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
						this.setVelocity(this.getVelocity().add((double)(-0.4F * h * this.jumpStrength), 0.0, (double)(0.4F * i * this.jumpStrength)));
						this.playJumpSound();
					}

					this.jumpStrength = 0.0F;
				}

				this.flyingSpeed = this.getMovementSpeed() * 0.1F;
				if (this.isLogicalSideForUpdatingMovement()) {
					this.setMovementSpeed((float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
					super.travel(new Vec3d((double)f, movementInput.y, (double)g));
				} else if (livingEntity instanceof PlayerEntity) {
					this.setVelocity(Vec3d.ZERO);
				}

				if (this.onGround) {
					this.jumpStrength = 0.0F;
					this.setInAir(false);
				}

				this.lastLimbDistance = this.limbDistance;
				double dx = this.getX() - this.prevX;
				double ex = this.getZ() - this.prevZ;
				float j = MathHelper.sqrt(dx * dx + ex * ex) * 4.0F;
				if (j > 1.0F) {
					j = 1.0F;
				}

				this.limbDistance = this.limbDistance + (j - this.limbDistance) * 0.4F;
				this.limbAngle = this.limbAngle + this.limbDistance;
			} else {
				this.flyingSpeed = 0.02F;
				super.travel(movementInput);
			}
		}
	}

	protected void playJumpSound() {
		this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("EatingHaystack", this.isEatingGrass());
		tag.putBoolean("Bred", this.isBred());
		tag.putInt("Temper", this.getTemper());
		tag.putBoolean("Tame", this.isTame());
		if (this.getOwnerUuid() != null) {
			tag.putString("OwnerUUID", this.getOwnerUuid().toString());
		}

		if (!this.items.getInvStack(0).isEmpty()) {
			tag.put("SaddleItem", this.items.getInvStack(0).toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setEatingGrass(tag.getBoolean("EatingHaystack"));
		this.setBred(tag.getBoolean("Bred"));
		this.setTemper(tag.getInt("Temper"));
		this.setTame(tag.getBoolean("Tame"));
		String string;
		if (tag.contains("OwnerUUID", 8)) {
			string = tag.getString("OwnerUUID");
		} else {
			String string2 = tag.getString("Owner");
			string = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string2);
		}

		if (!string.isEmpty()) {
			this.setOwnerUuid(UUID.fromString(string));
		}

		EntityAttributeInstance entityAttributeInstance = this.getAttributes().get("Speed");
		if (entityAttributeInstance != null) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
		}

		if (tag.contains("SaddleItem", 10)) {
			ItemStack itemStack = ItemStack.fromTag(tag.getCompound("SaddleItem"));
			if (itemStack.getItem() == Items.SADDLE) {
				this.items.setInvStack(0, itemStack);
			}
		}

		this.updateSaddle();
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return false;
	}

	protected boolean canBreed() {
		return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaximumHealth() && this.isInLove();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return null;
	}

	protected void setChildAttributes(PassiveEntity mate, HorseBaseEntity child) {
		double d = this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue()
			+ mate.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue()
			+ (double)this.getChildHealthBonus();
		child.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(d / 3.0);
		double e = this.getAttributeInstance(JUMP_STRENGTH).getBaseValue()
			+ mate.getAttributeInstance(JUMP_STRENGTH).getBaseValue()
			+ this.getChildJumpStrengthBonus();
		child.getAttributeInstance(JUMP_STRENGTH).setBaseValue(e / 3.0);
		double f = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue()
			+ mate.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue()
			+ this.getChildMovementSpeedBonus();
		child.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(f / 3.0);
	}

	@Override
	public boolean canBeControlledByRider() {
		return this.getPrimaryPassenger() instanceof LivingEntity;
	}

	@Environment(EnvType.CLIENT)
	public float getEatingGrassAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getAngryAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastAngryAnimationProgress, this.angryAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getEatingAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setJumpStrength(int strength) {
		if (this.isSaddled()) {
			if (strength < 0) {
				strength = 0;
			} else {
				this.jumping = true;
				this.updateAnger();
			}

			if (strength >= 90) {
				this.jumpStrength = 1.0F;
			} else {
				this.jumpStrength = 0.4F + 0.4F * (float)strength / 90.0F;
			}
		}
	}

	@Override
	public boolean canJump() {
		return this.isSaddled();
	}

	@Override
	public void startJumping(int height) {
		this.jumping = true;
		this.updateAnger();
	}

	@Override
	public void stopJumping() {
	}

	@Environment(EnvType.CLIENT)
	protected void spawnPlayerReactionParticles(boolean positive) {
		ParticleEffect particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 7) {
			this.spawnPlayerReactionParticles(true);
		} else if (status == 6) {
			this.spawnPlayerReactionParticles(false);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		super.updatePassengerPosition(passenger);
		if (passenger instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)passenger;
			this.bodyYaw = mobEntity.bodyYaw;
		}

		if (this.lastAngryAnimationProgress > 0.0F) {
			float f = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.7F * this.lastAngryAnimationProgress;
			float i = 0.15F * this.lastAngryAnimationProgress;
			passenger.updatePosition(
				this.getX() + (double)(h * f), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double)i, this.getZ() - (double)(h * g)
			);
			if (passenger instanceof LivingEntity) {
				((LivingEntity)passenger).bodyYaw = this.bodyYaw;
			}
		}
	}

	protected float getChildHealthBonus() {
		return 15.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
	}

	protected double getChildJumpStrengthBonus() {
		return 0.4F + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
	}

	protected double getChildMovementSpeedBonus() {
		return (0.45F + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
	}

	@Override
	public boolean isClimbing() {
		return false;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.95F;
	}

	public boolean canEquip() {
		return false;
	}

	public boolean canEquip(ItemStack item) {
		return false;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		int i = slot - 400;
		if (i >= 0 && i < 2 && i < this.items.getInvSize()) {
			if (i == 0 && item.getItem() != Items.SADDLE) {
				return false;
			} else if (i != 1 || this.canEquip() && this.canEquip(item)) {
				this.items.setInvStack(i, item);
				this.updateSaddle();
				return true;
			} else {
				return false;
			}
		} else {
			int j = slot - 500 + 2;
			if (j >= 2 && j < this.items.getInvSize()) {
				this.items.setInvStack(j, item);
				return true;
			} else {
				return false;
			}
		}
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Override
	public Vec3d method_24829(LivingEntity livingEntity) {
		Vec3d vec3d = method_24826((double)this.getWidth(), (double)livingEntity.getWidth(), this.yaw + (livingEntity.getMainArm() == Arm.RIGHT ? 90.0F : -90.0F));
		double d = this.getX() + vec3d.x;
		double e = this.getBoundingBox().y1;
		double f = this.getZ() + vec3d.z;
		EntityContext entityContext = EntityContext.of(livingEntity);
		Box box = livingEntity.method_24833(EntityPose.SWIMMING).offset(d, e, f);
		BlockPos.Mutable mutable = new BlockPos.Mutable(d, e, f);
		double g = this.getBoundingBox().y2 + 0.75;

		do {
			double h = method_24827(this.world, mutable, entityContext);
			if ((double)mutable.getY() + h > g) {
				break;
			}

			if (!Double.isInfinite(h) && h < 1.0) {
				Box box2 = box.offset(d, (double)mutable.getY() + h, f);
				if (this.world.getBlockCollisions(livingEntity, box2).allMatch(VoxelShape::isEmpty)) {
					return new Vec3d(d, (double)mutable.getY() + h, f);
				}
			}

			mutable.setOffset(Direction.UP);
		} while ((double)mutable.getY() < g);

		return new Vec3d(this.getX(), this.getY(), this.getZ());
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData();
			((PassiveEntity.PassiveData)entityData).setBabyChance(0.2F);
		}

		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}
}
