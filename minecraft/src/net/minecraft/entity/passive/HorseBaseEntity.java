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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

	protected boolean getHorseFlag(int i) {
		return (this.dataTracker.get(HORSE_FLAGS) & i) != 0;
	}

	protected void setHorseFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(HORSE_FLAGS);
		if (bl) {
			this.dataTracker.set(HORSE_FLAGS, (byte)(b | i));
		} else {
			this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~i));
		}
	}

	public boolean isTame() {
		return this.getHorseFlag(2);
	}

	@Nullable
	public UUID getOwnerUuid() {
		return (UUID)this.dataTracker.get(OWNER_UUID).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uUID) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
	}

	public boolean isInAir() {
		return this.inAir;
	}

	public void setTame(boolean bl) {
		this.setHorseFlag(2, bl);
	}

	public void setInAir(boolean bl) {
		this.inAir = bl;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return super.canBeLeashedBy(playerEntity) && this.getGroup() != EntityGroup.UNDEAD;
	}

	@Override
	protected void updateForLeashLength(float f) {
		if (f > 6.0F && this.isEatingGrass()) {
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

	public void setTemper(int i) {
		this.temper = i;
	}

	public int addTemper(int i) {
		int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxTemper());
		this.setTemper(j);
		return j;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		Entity entity = damageSource.getAttacker();
		return this.hasPassengers() && entity != null && this.hasPassengerDeep(entity) ? false : super.damage(damageSource, f);
	}

	@Override
	public boolean isPushable() {
		return !this.hasPassengers();
	}

	private void playEatingAnimation() {
		this.setEating();
		if (!this.isSilent()) {
			this.field_6002
				.playSound(
					null, this.x, this.y, this.z, SoundEvents.field_15099, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (f > 1.0F) {
			this.playSound(SoundEvents.field_14783, 0.4F, 1.0F);
		}

		int i = MathHelper.ceil((f * 0.5F - 3.0F) * g);
		if (i > 0) {
			this.damage(DamageSource.FALL, (float)i);
			if (this.hasPassengers()) {
				for (Entity entity : this.getPassengersDeep()) {
					entity.damage(DamageSource.FALL, (float)i);
				}
			}

			BlockState blockState = this.field_6002.method_8320(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z));
			if (!blockState.isAir() && !this.isSilent()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.field_6002
					.playSound(
						null,
						this.x,
						this.y,
						this.z,
						blockSoundGroup.getStepSound(),
						this.getSoundCategory(),
						blockSoundGroup.getVolume() * 0.5F,
						blockSoundGroup.getPitch() * 0.75F
					);
			}
		}
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
		if (!this.field_6002.isClient) {
			this.setSaddled(!this.items.getInvStack(0).isEmpty() && this.canBeSaddled());
		}
	}

	@Override
	public void onInvChange(Inventory inventory) {
		boolean bl = this.isSaddled();
		this.updateSaddle();
		if (this.age > 20 && !bl && this.isSaddled()) {
			this.playSound(SoundEvents.field_14704, 0.5F, 1.0F);
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
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		if (this.random.nextInt(3) == 0) {
			this.updateAnger();
		}

		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.random.nextInt(10) == 0 && !this.cannotMove()) {
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
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		if (!blockState.method_11620().isLiquid()) {
			BlockState blockState2 = this.field_6002.method_8320(blockPos.up());
			BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
			if (blockState2.getBlock() == Blocks.field_10477) {
				blockSoundGroup = blockState2.getSoundGroup();
			}

			if (this.hasPassengers() && this.field_6964) {
				this.soundTicks++;
				if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
					this.method_6761(blockSoundGroup);
				} else if (this.soundTicks <= 5) {
					this.playSound(SoundEvents.field_15061, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
				}
			} else if (blockSoundGroup == BlockSoundGroup.WOOD) {
				this.playSound(SoundEvents.field_15061, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			} else {
				this.playSound(SoundEvents.field_14613, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			}
		}
	}

	protected void method_6761(BlockSoundGroup blockSoundGroup) {
		this.playSound(SoundEvents.field_14987, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeContainer().register(JUMP_STRENGTH);
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

	public void openInventory(PlayerEntity playerEntity) {
		if (!this.field_6002.isClient && (!this.hasPassengers() || this.hasPassenger(playerEntity)) && this.isTame()) {
			playerEntity.openHorseInventory(this, this.items);
		}
	}

	protected boolean receiveFood(PlayerEntity playerEntity, ItemStack itemStack) {
		boolean bl = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		Item item = itemStack.getItem();
		if (item == Items.field_8861) {
			f = 2.0F;
			i = 20;
			j = 3;
		} else if (item == Items.field_8479) {
			f = 1.0F;
			i = 30;
			j = 3;
		} else if (item == Blocks.field_10359.asItem()) {
			f = 20.0F;
			i = 180;
		} else if (item == Items.field_8279) {
			f = 3.0F;
			i = 60;
			j = 3;
		} else if (item == Items.field_8071) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(playerEntity);
			}
		} else if (item == Items.field_8463 || item == Items.field_8367) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(playerEntity);
			}
		}

		if (this.getHealth() < this.getHealthMaximum() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isBaby() && i > 0) {
			this.field_6002
				.addParticle(
					ParticleTypes.field_11211,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					0.0,
					0.0,
					0.0
				);
			if (!this.field_6002.isClient) {
				this.growUp(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
			bl = true;
			if (!this.field_6002.isClient) {
				this.addTemper(j);
			}
		}

		if (bl) {
			this.playEatingAnimation();
		}

		return bl;
	}

	protected void putPlayerOnBack(PlayerEntity playerEntity) {
		this.setEatingGrass(false);
		this.setAngry(false);
		if (!this.field_6002.isClient) {
			playerEntity.yaw = this.yaw;
			playerEntity.pitch = this.pitch;
			playerEntity.startRiding(this);
		}
	}

	@Override
	protected boolean cannotMove() {
		return super.cannotMove() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
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
				if (!itemStack.isEmpty()) {
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
		if (!this.field_6002.isClient && this.isAlive()) {
			if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}

			if (this.eatsGrass()) {
				if (!this.isEatingGrass()
					&& !this.hasPassengers()
					&& this.random.nextInt(300) == 0
					&& this.field_6002.method_8320(new BlockPos(this).down()).getBlock() == Blocks.field_10219) {
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
			LivingEntity livingEntity = this.field_6002
				.method_18465(HorseBaseEntity.class, PARENT_HORSE_PREDICATE, this, this.x, this.y, this.z, this.method_5829().expand(16.0));
			if (livingEntity != null && this.squaredDistanceTo(livingEntity) > 4.0) {
				this.navigation.method_6349(livingEntity);
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
		if (!this.field_6002.isClient) {
			this.eatingTicks = 1;
			this.setHorseFlag(64, true);
		}
	}

	public void setEatingGrass(boolean bl) {
		this.setHorseFlag(16, bl);
	}

	public void setAngry(boolean bl) {
		if (bl) {
			this.setEatingGrass(false);
		}

		this.setHorseFlag(32, bl);
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

	public boolean bondWithPlayer(PlayerEntity playerEntity) {
		this.setOwnerUuid(playerEntity.getUuid());
		this.setTame(true);
		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.TAME_ANIMAL.handle((ServerPlayerEntity)playerEntity, this);
		}

		this.field_6002.sendEntityStatus(this, (byte)7);
		return true;
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		if (this.isAlive()) {
			if (this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled()) {
				LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
				this.yaw = livingEntity.yaw;
				this.prevYaw = this.yaw;
				this.pitch = livingEntity.pitch * 0.5F;
				this.setRotation(this.yaw, this.pitch);
				this.field_6283 = this.yaw;
				this.headYaw = this.field_6283;
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
					double d = this.getJumpStrength() * (double)this.jumpStrength;
					double e;
					if (this.hasStatusEffect(StatusEffects.field_5913)) {
						e = d + (double)((float)(this.getStatusEffect(StatusEffects.field_5913).getAmplifier() + 1) * 0.1F);
					} else {
						e = d;
					}

					Vec3d vec3d2 = this.method_18798();
					this.setVelocity(vec3d2.x, e, vec3d2.z);
					this.setInAir(true);
					this.velocityDirty = true;
					if (g > 0.0F) {
						float h = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
						float i = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
						this.method_18799(this.method_18798().add((double)(-0.4F * h * this.jumpStrength), 0.0, (double)(0.4F * i * this.jumpStrength)));
						this.playJumpSound();
					}

					this.jumpStrength = 0.0F;
				}

				this.field_6281 = this.getMovementSpeed() * 0.1F;
				if (this.isLogicalSideForUpdatingMovement()) {
					this.setMovementSpeed((float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
					super.method_6091(new Vec3d((double)f, vec3d.y, (double)g));
				} else if (livingEntity instanceof PlayerEntity) {
					this.method_18799(Vec3d.ZERO);
				}

				if (this.onGround) {
					this.jumpStrength = 0.0F;
					this.setInAir(false);
				}

				this.lastLimbDistance = this.limbDistance;
				double dx = this.x - this.prevX;
				double ex = this.z - this.prevZ;
				float j = MathHelper.sqrt(dx * dx + ex * ex) * 4.0F;
				if (j > 1.0F) {
					j = 1.0F;
				}

				this.limbDistance = this.limbDistance + (j - this.limbDistance) * 0.4F;
				this.limbAngle = this.limbAngle + this.limbDistance;
			} else {
				this.field_6281 = 0.02F;
				super.method_6091(vec3d);
			}
		}
	}

	protected void playJumpSound() {
		this.playSound(SoundEvents.field_14831, 0.4F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("EatingHaystack", this.isEatingGrass());
		compoundTag.putBoolean("Bred", this.isBred());
		compoundTag.putInt("Temper", this.getTemper());
		compoundTag.putBoolean("Tame", this.isTame());
		if (this.getOwnerUuid() != null) {
			compoundTag.putString("OwnerUUID", this.getOwnerUuid().toString());
		}

		if (!this.items.getInvStack(0).isEmpty()) {
			compoundTag.put("SaddleItem", this.items.getInvStack(0).toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setEatingGrass(compoundTag.getBoolean("EatingHaystack"));
		this.setBred(compoundTag.getBoolean("Bred"));
		this.setTemper(compoundTag.getInt("Temper"));
		this.setTame(compoundTag.getBoolean("Tame"));
		String string;
		if (compoundTag.containsKey("OwnerUUID", 8)) {
			string = compoundTag.getString("OwnerUUID");
		} else {
			String string2 = compoundTag.getString("Owner");
			string = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string2);
		}

		if (!string.isEmpty()) {
			this.setOwnerUuid(UUID.fromString(string));
		}

		EntityAttributeInstance entityAttributeInstance = this.getAttributeContainer().get("Speed");
		if (entityAttributeInstance != null) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
		}

		if (compoundTag.containsKey("SaddleItem", 10)) {
			ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("SaddleItem"));
			if (itemStack.getItem() == Items.field_8175) {
				this.items.setInvStack(0, itemStack);
			}
		}

		this.updateSaddle();
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		return false;
	}

	protected boolean canBreed() {
		return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getHealthMaximum() && this.isInLove();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	protected void setChildAttributes(PassiveEntity passiveEntity, HorseBaseEntity horseBaseEntity) {
		double d = this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue()
			+ passiveEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue()
			+ (double)this.getChildHealthBonus();
		horseBaseEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(d / 3.0);
		double e = this.getAttributeInstance(JUMP_STRENGTH).getBaseValue()
			+ passiveEntity.getAttributeInstance(JUMP_STRENGTH).getBaseValue()
			+ this.getChildJumpStrengthBonus();
		horseBaseEntity.getAttributeInstance(JUMP_STRENGTH).setBaseValue(e / 3.0);
		double f = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue()
			+ passiveEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue()
			+ this.getChildMovementSpeedBonus();
		horseBaseEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(f / 3.0);
	}

	@Override
	public boolean canBeControlledByRider() {
		return this.getPrimaryPassenger() instanceof LivingEntity;
	}

	@Environment(EnvType.CLIENT)
	public float getEatingGrassAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getAngryAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastAngryAnimationProgress, this.angryAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getEatingAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setJumpStrength(int i) {
		if (this.isSaddled()) {
			if (i < 0) {
				i = 0;
			} else {
				this.jumping = true;
				this.updateAnger();
			}

			if (i >= 90) {
				this.jumpStrength = 1.0F;
			} else {
				this.jumpStrength = 0.4F + 0.4F * (float)i / 90.0F;
			}
		}
	}

	@Override
	public boolean canJump() {
		return this.isSaddled();
	}

	@Override
	public void startJumping(int i) {
		this.jumping = true;
		this.updateAnger();
	}

	@Override
	public void stopJumping() {
	}

	@Environment(EnvType.CLIENT)
	protected void spawnPlayerReactionParticles(boolean bl) {
		ParticleEffect particleEffect = bl ? ParticleTypes.field_11201 : ParticleTypes.field_11251;

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.field_6002
				.addParticle(
					particleEffect,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 7) {
			this.spawnPlayerReactionParticles(true);
		} else if (b == 6) {
			this.spawnPlayerReactionParticles(false);
		} else {
			super.handleStatus(b);
		}
	}

	@Override
	public void updatePassengerPosition(Entity entity) {
		super.updatePassengerPosition(entity);
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			this.field_6283 = mobEntity.field_6283;
		}

		if (this.lastAngryAnimationProgress > 0.0F) {
			float f = MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0));
			float h = 0.7F * this.lastAngryAnimationProgress;
			float i = 0.15F * this.lastAngryAnimationProgress;
			entity.setPosition(this.x + (double)(h * f), this.y + this.getMountedHeightOffset() + entity.getHeightOffset() + (double)i, this.z - (double)(h * g));
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).field_6283 = this.field_6283;
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
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.95F;
	}

	public boolean canEquip() {
		return false;
	}

	public boolean canEquip(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean equip(int i, ItemStack itemStack) {
		int j = i - 400;
		if (j >= 0 && j < 2 && j < this.items.getInvSize()) {
			if (j == 0 && itemStack.getItem() != Items.field_8175) {
				return false;
			} else if (j != 1 || this.canEquip() && this.canEquip(itemStack)) {
				this.items.setInvStack(j, itemStack);
				this.updateSaddle();
				return true;
			} else {
				return false;
			}
		} else {
			int k = i - 500 + 2;
			if (k >= 2 && k < this.items.getInvSize()) {
				this.items.setInvStack(k, itemStack);
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

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (this.random.nextInt(5) == 0) {
			this.setBreedingAge(-24000);
		}

		return entityData;
	}
}
