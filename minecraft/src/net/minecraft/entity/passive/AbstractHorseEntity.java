package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class AbstractHorseEntity extends AnimalEntity implements InventoryChangedListener, RideableInventory, JumpingMount, Saddleable {
	public static final int field_30413 = 400;
	public static final int field_30414 = 499;
	public static final int field_30415 = 500;
	private static final Predicate<LivingEntity> IS_BRED_HORSE = entity -> entity instanceof AbstractHorseEntity && ((AbstractHorseEntity)entity).isBred();
	private static final TargetPredicate PARENT_HORSE_PREDICATE = TargetPredicate.createNonAttackable()
		.setBaseMaxDistance(16.0)
		.ignoreVisibility()
		.setPredicate(IS_BRED_HORSE);
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(
		Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE
	);
	private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final int TAMED_FLAG = 2;
	private static final int SADDLED_FLAG = 4;
	private static final int BRED_FLAG = 8;
	private static final int EATING_GRASS_FLAG = 16;
	private static final int ANGRY_FLAG = 32;
	private static final int EATING_FLAG = 64;
	public static final int field_30416 = 0;
	public static final int field_30417 = 1;
	public static final int field_30418 = 2;
	private int eatingGrassTicks;
	private int eatingTicks;
	private int angryTicks;
	public int tailWagTicks;
	public int field_6958;
	protected boolean inAir;
	protected SimpleInventory items;
	protected int temper;
	protected float jumpStrength;
	private boolean jumping;
	private float eatingGrassAnimationProgress;
	private float lastEatingGrassAnimationProgress;
	private float angryAnimationProgress;
	private float lastAngryAnimationProgress;
	private float eatingAnimationProgress;
	private float lastEatingAnimationProgress;
	protected boolean playExtraHorseSounds = true;
	protected int soundTicks;

	protected AbstractHorseEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.onChestedStatusChanged();
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
		this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, AbstractHorseEntity.class));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), false));
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
		return this.getHorseFlag(TAMED_FLAG);
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
		this.setHorseFlag(TAMED_FLAG, tame);
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
		return this.getHorseFlag(EATING_GRASS_FLAG);
	}

	public boolean isAngry() {
		return this.getHorseFlag(ANGRY_FLAG);
	}

	public boolean isBred() {
		return this.getHorseFlag(BRED_FLAG);
	}

	public void setBred(boolean bred) {
		this.setHorseFlag(BRED_FLAG, bred);
	}

	@Override
	public boolean canBeSaddled() {
		return this.isAlive() && !this.isBaby() && this.isTame();
	}

	@Override
	public void saddle(@Nullable SoundCategory sound) {
		this.items.setStack(0, new ItemStack(Items.SADDLE));
		if (sound != null) {
			this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_HORSE_SADDLE, sound, 0.5F, 1.0F);
		}
	}

	@Override
	public boolean isSaddled() {
		return this.getHorseFlag(SADDLED_FLAG);
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
	public boolean isPushable() {
		return !this.hasPassengers();
	}

	private void playEatingAnimation() {
		this.setEating();
		if (!this.isSilent()) {
			SoundEvent soundEvent = this.getEatSound();
			if (soundEvent != null) {
				this.world
					.playSound(
						null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (fallDistance > 1.0F) {
			this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
		}

		int i = this.computeFallDamage(fallDistance, damageMultiplier);
		if (i <= 0) {
			return false;
		} else {
			this.damage(damageSource, (float)i);
			if (this.hasPassengers()) {
				for (Entity entity : this.getPassengersDeep()) {
					entity.damage(damageSource, (float)i);
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

	protected void onChestedStatusChanged() {
		SimpleInventory simpleInventory = this.items;
		this.items = new SimpleInventory(this.getInventorySize());
		if (simpleInventory != null) {
			simpleInventory.removeListener(this);
			int i = Math.min(simpleInventory.size(), this.items.size());

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = simpleInventory.getStack(j);
				if (!itemStack.isEmpty()) {
					this.items.setStack(j, itemStack.copy());
				}
			}
		}

		this.items.addListener(this);
		this.updateSaddle();
	}

	protected void updateSaddle() {
		if (!this.world.isClient) {
			this.setHorseFlag(SADDLED_FLAG, !this.items.getStack(0).isEmpty());
		}
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		boolean bl = this.isSaddled();
		this.updateSaddle();
		if (this.age > 20 && !bl && this.isSaddled()) {
			this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
		}
	}

	public double getJumpStrength() {
		return this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
	}

	@Nullable
	protected SoundEvent getEatSound() {
		return null;
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
			if (blockState.isOf(Blocks.SNOW)) {
				blockSoundGroup = blockState.getSoundGroup();
			}

			if (this.hasPassengers() && this.playExtraHorseSounds) {
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

	public static DefaultAttributeContainer.Builder createBaseHorseAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.HORSE_JUMP_STRENGTH)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 53.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225F);
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

	@Override
	public void openInventory(PlayerEntity player) {
		if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTame()) {
			player.openHorseInventory(this, this.items);
		}
	}

	public ActionResult interactHorse(PlayerEntity player, ItemStack stack) {
		boolean bl = this.receiveFood(player, stack);
		if (!player.getAbilities().creativeMode) {
			stack.decrement(1);
		}

		if (this.world.isClient) {
			return ActionResult.CONSUME;
		} else {
			return bl ? ActionResult.SUCCESS : ActionResult.PASS;
		}
	}

	protected boolean receiveFood(PlayerEntity player, ItemStack item) {
		boolean bl = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		if (item.isOf(Items.WHEAT)) {
			f = 2.0F;
			i = 20;
			j = 3;
		} else if (item.isOf(Items.SUGAR)) {
			f = 1.0F;
			i = 30;
			j = 3;
		} else if (item.isOf(Blocks.HAY_BLOCK.asItem())) {
			f = 20.0F;
			i = 180;
		} else if (item.isOf(Items.APPLE)) {
			f = 3.0F;
			i = 60;
			j = 3;
		} else if (item.isOf(Items.GOLDEN_CARROT)) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (!this.world.isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(player);
			}
		} else if (item.isOf(Items.GOLDEN_APPLE) || item.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (!this.world.isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(player);
			}
		}

		if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
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
			this.emitGameEvent(GameEvent.EAT);
		}

		return bl;
	}

	protected void putPlayerOnBack(PlayerEntity player) {
		this.setEatingGrass(false);
		this.setAngry(false);
		if (!this.world.isClient) {
			player.setYaw(this.getYaw());
			player.setPitch(this.getPitch());
			player.startRiding(this);
		}
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	private void wagTail() {
		this.tailWagTicks = 1;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.items != null) {
			for (int i = 0; i < this.items.size(); i++) {
				ItemStack itemStack = this.items.getStack(i);
				if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
					this.dropStack(itemStack);
				}
			}
		}
	}

	@Override
	public void tickMovement() {
		if (this.random.nextInt(200) == 0) {
			this.wagTail();
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
					&& this.world.getBlockState(this.getBlockPos().down()).isOf(Blocks.GRASS_BLOCK)) {
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
				.getClosestEntity(AbstractHorseEntity.class, PARENT_HORSE_PREDICATE, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().expand(16.0));
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
			this.setHorseFlag(EATING_FLAG, false);
		}

		if ((this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) && this.angryTicks > 0 && ++this.angryTicks > 20) {
			this.angryTicks = 0;
			this.setAngry(false);
		}

		if (this.tailWagTicks > 0 && ++this.tailWagTicks > 8) {
			this.tailWagTicks = 0;
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
		if (this.getHorseFlag(EATING_FLAG)) {
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
			this.setHorseFlag(EATING_FLAG, true);
		}
	}

	public void setEatingGrass(boolean eatingGrass) {
		this.setHorseFlag(EATING_GRASS_FLAG, eatingGrass);
	}

	public void setAngry(boolean angry) {
		if (angry) {
			this.setEatingGrass(false);
		}

		this.setHorseFlag(ANGRY_FLAG, angry);
	}

	private void updateAnger() {
		if (this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) {
			this.angryTicks = 1;
			this.setAngry(true);
		}
	}

	public void playAngrySound() {
		if (!this.isAngry()) {
			this.updateAnger();
			SoundEvent soundEvent = this.getAngrySound();
			if (soundEvent != null) {
				this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
			}
		}
	}

	public boolean bondWithPlayer(PlayerEntity player) {
		this.setOwnerUuid(player.getUuid());
		this.setTame(true);
		if (player instanceof ServerPlayerEntity) {
			Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
		}

		this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
		return true;
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.isAlive()) {
			LivingEntity livingEntity = this.getPrimaryPassenger();
			if (this.hasPassengers() && livingEntity != null) {
				this.setYaw(livingEntity.getYaw());
				this.prevYaw = this.getYaw();
				this.setPitch(livingEntity.getPitch() * 0.5F);
				this.setRotation(this.getYaw(), this.getPitch());
				this.bodyYaw = this.getYaw();
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
					double e = d + this.getJumpBoostVelocityModifier();
					Vec3d vec3d = this.getVelocity();
					this.setVelocity(vec3d.x, e, vec3d.z);
					this.setInAir(true);
					this.velocityDirty = true;
					if (g > 0.0F) {
						float h = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
						float i = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
						this.setVelocity(this.getVelocity().add((double)(-0.4F * h * this.jumpStrength), 0.0, (double)(0.4F * i * this.jumpStrength)));
					}

					this.jumpStrength = 0.0F;
				}

				this.airStrafingSpeed = this.getMovementSpeed() * 0.1F;
				if (this.isLogicalSideForUpdatingMovement()) {
					this.setMovementSpeed((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
					super.travel(new Vec3d((double)f, movementInput.y, (double)g));
				} else if (livingEntity instanceof PlayerEntity) {
					this.setVelocity(Vec3d.ZERO);
				}

				if (this.onGround) {
					this.jumpStrength = 0.0F;
					this.setInAir(false);
				}

				this.updateLimbs(this, false);
				this.tryCheckBlockCollision();
			} else {
				this.airStrafingSpeed = 0.02F;
				super.travel(movementInput);
			}
		}
	}

	protected void playJumpSound() {
		this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("EatingHaystack", this.isEatingGrass());
		nbt.putBoolean("Bred", this.isBred());
		nbt.putInt("Temper", this.getTemper());
		nbt.putBoolean("Tame", this.isTame());
		if (this.getOwnerUuid() != null) {
			nbt.putUuid("Owner", this.getOwnerUuid());
		}

		if (!this.items.getStack(0).isEmpty()) {
			nbt.put("SaddleItem", this.items.getStack(0).writeNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setEatingGrass(nbt.getBoolean("EatingHaystack"));
		this.setBred(nbt.getBoolean("Bred"));
		this.setTemper(nbt.getInt("Temper"));
		this.setTame(nbt.getBoolean("Tame"));
		UUID uUID;
		if (nbt.containsUuid("Owner")) {
			uUID = nbt.getUuid("Owner");
		} else {
			String string = nbt.getString("Owner");
			uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
		}

		if (uUID != null) {
			this.setOwnerUuid(uUID);
		}

		if (nbt.contains("SaddleItem", NbtElement.COMPOUND_TYPE)) {
			ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("SaddleItem"));
			if (itemStack.isOf(Items.SADDLE)) {
				this.items.setStack(0, itemStack);
			}
		}

		this.updateSaddle();
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return false;
	}

	protected boolean canBreed() {
		return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}

	protected void setChildAttributes(PassiveEntity mate, AbstractHorseEntity child) {
		double d = this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
			+ mate.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
			+ (double)this.getChildHealthBonus(this.random);
		child.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(d / 3.0);
		double e = this.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH)
			+ mate.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH)
			+ this.getChildJumpStrengthBonus(this.random);
		child.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(e / 3.0);
		double f = this.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
			+ mate.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
			+ this.getChildMovementSpeedBonus(this.random);
		child.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(f / 3.0);
	}

	public float getEatingGrassAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
	}

	public float getAngryAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastAngryAnimationProgress, this.angryAnimationProgress);
	}

	public float getEatingAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
	}

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
		this.playJumpSound();
	}

	@Override
	public void stopJumping() {
	}

	protected void spawnPlayerReactionParticles(boolean positive) {
		ParticleEffect particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES) {
			this.spawnPlayerReactionParticles(true);
		} else if (status == EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES) {
			this.spawnPlayerReactionParticles(false);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		super.updatePassengerPosition(passenger);
		if (passenger instanceof MobEntity mobEntity) {
			this.bodyYaw = mobEntity.bodyYaw;
		}

		if (this.lastAngryAnimationProgress > 0.0F) {
			float f = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.7F * this.lastAngryAnimationProgress;
			float i = 0.15F * this.lastAngryAnimationProgress;
			passenger.setPosition(
				this.getX() + (double)(h * f), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double)i, this.getZ() - (double)(h * g)
			);
			if (passenger instanceof LivingEntity) {
				((LivingEntity)passenger).bodyYaw = this.bodyYaw;
			}
		}
	}

	protected float getChildHealthBonus(AbstractRandom random) {
		return 15.0F + (float)random.nextInt(8) + (float)random.nextInt(9);
	}

	protected double getChildJumpStrengthBonus(AbstractRandom random) {
		return 0.4F + random.nextDouble() * 0.2 + random.nextDouble() * 0.2 + random.nextDouble() * 0.2;
	}

	protected double getChildMovementSpeedBonus(AbstractRandom random) {
		return (0.45F + random.nextDouble() * 0.3 + random.nextDouble() * 0.3 + random.nextDouble() * 0.3) * 0.25;
	}

	@Override
	public boolean isClimbing() {
		return false;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.95F;
	}

	/**
	 * Whether this horse has a slot for custom equipment besides a saddle.
	 * 
	 * <p>In the item slot argument type, the slot is referred to as <code>
	 * horse.armor</code>. In this horse's screen, it appears in the middle of
	 * the left side, and right below the saddle slot if this horse has a saddle
	 * slot.
	 * 
	 * <p>This is used by horse armors and llama carpets, but can be
	 * refitted to any purpose.
	 */
	public boolean hasArmorSlot() {
		return false;
	}

	/**
	 * Whether this horse already has an item stack in its horse armor slot.
	 * 
	 * @see #hasArmorSlot()
	 */
	public boolean hasArmorInSlot() {
		return !this.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
	}

	/**
	 * Whether the given item stack is valid for this horse's armor slot.
	 * 
	 * @see #hasArmorSlot()
	 */
	public boolean isHorseArmor(ItemStack item) {
		return false;
	}

	private StackReference createInventoryStackReference(int slot, Predicate<ItemStack> predicate) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return AbstractHorseEntity.this.items.getStack(slot);
			}

			@Override
			public boolean set(ItemStack stack) {
				if (!predicate.test(stack)) {
					return false;
				} else {
					AbstractHorseEntity.this.items.setStack(slot, stack);
					AbstractHorseEntity.this.updateSaddle();
					return true;
				}
			}
		};
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		int i = mappedIndex - 400;
		if (i >= 0 && i < 2 && i < this.items.size()) {
			if (i == 0) {
				return this.createInventoryStackReference(i, stack -> stack.isEmpty() || stack.isOf(Items.SADDLE));
			}

			if (i == 1) {
				if (!this.hasArmorSlot()) {
					return StackReference.EMPTY;
				}

				return this.createInventoryStackReference(i, stack -> stack.isEmpty() || this.isHorseArmor(stack));
			}
		}

		int j = mappedIndex - 500 + 2;
		return j >= 2 && j < this.items.size() ? StackReference.of(this.items, j) : super.getStackReference(mappedIndex);
	}

	@Nullable
	public LivingEntity getPrimaryPassenger() {
		if (this.isSaddled()) {
			Entity var2 = this.getFirstPassenger();
			if (var2 instanceof LivingEntity) {
				return (LivingEntity)var2;
			}
		}

		return null;
	}

	@Nullable
	private Vec3d locateSafeDismountingPos(Vec3d offset, LivingEntity passenger) {
		double d = this.getX() + offset.x;
		double e = this.getBoundingBox().minY;
		double f = this.getZ() + offset.z;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (EntityPose entityPose : passenger.getPoses()) {
			mutable.set(d, e, f);
			double g = this.getBoundingBox().maxY + 0.75;

			do {
				double h = this.world.getDismountHeight(mutable);
				if ((double)mutable.getY() + h > g) {
					break;
				}

				if (Dismounting.canDismountInBlock(h)) {
					Box box = passenger.getBoundingBox(entityPose);
					Vec3d vec3d = new Vec3d(d, (double)mutable.getY() + h, f);
					if (Dismounting.canPlaceEntityAt(this.world, passenger, box.offset(vec3d))) {
						passenger.setPose(entityPose);
						return vec3d;
					}
				}

				mutable.move(Direction.UP);
			} while (!((double)mutable.getY() < g));
		}

		return null;
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		Vec3d vec3d = getPassengerDismountOffset(
			(double)this.getWidth(), (double)passenger.getWidth(), this.getYaw() + (passenger.getMainArm() == Arm.RIGHT ? 90.0F : -90.0F)
		);
		Vec3d vec3d2 = this.locateSafeDismountingPos(vec3d, passenger);
		if (vec3d2 != null) {
			return vec3d2;
		} else {
			Vec3d vec3d3 = getPassengerDismountOffset(
				(double)this.getWidth(), (double)passenger.getWidth(), this.getYaw() + (passenger.getMainArm() == Arm.LEFT ? 90.0F : -90.0F)
			);
			Vec3d vec3d4 = this.locateSafeDismountingPos(vec3d3, passenger);
			return vec3d4 != null ? vec3d4 : this.getPos();
		}
	}

	protected void initAttributes(AbstractRandom random) {
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(0.2F);
		}

		this.initAttributes(world.getRandom());
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public boolean areInventoriesDifferent(Inventory inventory) {
		return this.items != inventory;
	}
}
