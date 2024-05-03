package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AmbientStandGoal;
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
import net.minecraft.entity.attribute.EntityAttribute;
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
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class AbstractHorseEntity extends AnimalEntity implements InventoryChangedListener, RideableInventory, Tameable, JumpingMount, Saddleable {
	public static final int field_30413 = 400;
	public static final int field_30414 = 499;
	public static final int field_30415 = 500;
	public static final double field_42647 = 0.15;
	private static final float MIN_MOVEMENT_SPEED_BONUS = (float)getChildMovementSpeedBonus(() -> 0.0);
	private static final float MAX_MOVEMENT_SPEED_BONUS = (float)getChildMovementSpeedBonus(() -> 1.0);
	private static final float MIN_JUMP_STRENGTH_BONUS = (float)getChildJumpStrengthBonus(() -> 0.0);
	private static final float MAX_JUMP_STRENGTH_BONUS = (float)getChildJumpStrengthBonus(() -> 1.0);
	private static final float MIN_HEALTH_BONUS = getChildHealthBonus(max -> 0);
	private static final float MAX_HEALTH_BONUS = getChildHealthBonus(max -> max - 1);
	private static final float field_42979 = 0.25F;
	private static final float field_42980 = 0.5F;
	private static final Predicate<LivingEntity> IS_BRED_HORSE = entity -> entity instanceof AbstractHorseEntity && ((AbstractHorseEntity)entity).isBred();
	private static final TargetPredicate PARENT_HORSE_PREDICATE = TargetPredicate.createNonAttackable()
		.setBaseMaxDistance(16.0)
		.ignoreVisibility()
		.setPredicate(IS_BRED_HORSE);
	private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int TAMED_FLAG = 2;
	private static final int SADDLED_FLAG = 4;
	private static final int BRED_FLAG = 8;
	private static final int EATING_GRASS_FLAG = 16;
	private static final int ANGRY_FLAG = 32;
	private static final int EATING_FLAG = 64;
	public static final int field_30416 = 0;
	public static final int field_30418 = 1;
	private int eatingGrassTicks;
	private int eatingTicks;
	private int angryTicks;
	public int tailWagTicks;
	public int field_6958;
	protected boolean inAir;
	protected SimpleInventory items;
	protected int temper;
	protected float jumpStrength;
	protected boolean jumping;
	private float eatingGrassAnimationProgress;
	private float lastEatingGrassAnimationProgress;
	private float angryAnimationProgress;
	private float lastAngryAnimationProgress;
	private float eatingAnimationProgress;
	private float lastEatingAnimationProgress;
	protected boolean playExtraHorseSounds = true;
	protected int soundTicks;
	@Nullable
	private UUID ownerUuid;
	private final Inventory inventory = new SingleStackInventory() {
		@Override
		public ItemStack getStack() {
			return AbstractHorseEntity.this.getBodyArmor();
		}

		@Override
		public void setStack(ItemStack stack) {
			AbstractHorseEntity.this.equipBodyArmor(stack);
		}

		@Override
		public void markDirty() {
		}

		@Override
		public boolean canPlayerUse(PlayerEntity player) {
			return player.getVehicle() == AbstractHorseEntity.this || player.canInteractWithEntity(AbstractHorseEntity.this, 4.0);
		}
	};

	protected AbstractHorseEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
		super(entityType, world);
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
		if (this.shouldAmbientStand()) {
			this.goalSelector.add(9, new AmbientStandGoal(this));
		}

		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(3, new TemptGoal(this, 1.25, stack -> stack.isIn(ItemTags.HORSE_TEMPT_ITEMS), false));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(HORSE_FLAGS, (byte)0);
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
	@Override
	public UUID getOwnerUuid() {
		return this.ownerUuid;
	}

	public void setOwnerUuid(@Nullable UUID ownerUuid) {
		this.ownerUuid = ownerUuid;
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
	}

	public void equipHorseArmor(PlayerEntity player, ItemStack stack) {
		if (this.isHorseArmor(stack)) {
			this.equipBodyArmor(stack.copyWithCount(1));
			stack.decrementUnlessCreative(1, player);
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
				this.getWorld()
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

	protected int getInventorySize() {
		return 1;
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
		this.updateSaddledFlag();
	}

	protected void updateSaddledFlag() {
		if (!this.getWorld().isClient) {
			this.setHorseFlag(SADDLED_FLAG, !this.items.getStack(0).isEmpty());
		}
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		boolean bl = this.isSaddled();
		this.updateSaddledFlag();
		if (this.age > 20 && !bl && this.isSaddled()) {
			this.playSound(this.getSaddleSound(), 0.5F, 1.0F);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (bl && this.random.nextInt(3) == 0) {
			this.updateAnger();
		}

		return bl;
	}

	protected boolean shouldAmbientStand() {
		return true;
	}

	@Nullable
	protected SoundEvent getEatSound() {
		return null;
	}

	@Nullable
	protected SoundEvent getAngrySound() {
		return null;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (!state.isLiquid()) {
			BlockState blockState = this.getWorld().getBlockState(pos.up());
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
			} else if (this.isWooden(blockSoundGroup)) {
				this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			} else {
				this.playSound(SoundEvents.ENTITY_HORSE_STEP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			}
		}
	}

	private boolean isWooden(BlockSoundGroup soundGroup) {
		return soundGroup == BlockSoundGroup.WOOD
			|| soundGroup == BlockSoundGroup.NETHER_WOOD
			|| soundGroup == BlockSoundGroup.NETHER_STEM
			|| soundGroup == BlockSoundGroup.CHERRY_WOOD
			|| soundGroup == BlockSoundGroup.BAMBOO_WOOD;
	}

	protected void playWalkSound(BlockSoundGroup group) {
		this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, group.getVolume() * 0.15F, group.getPitch());
	}

	public static DefaultAttributeContainer.Builder createBaseHorseAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_JUMP_STRENGTH, 0.7)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 53.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225F)
			.add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0)
			.add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, 6.0)
			.add(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, 0.5);
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
		if (!this.getWorld().isClient && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTame()) {
			player.openHorseInventory(this, this.items);
		}
	}

	public ActionResult interactHorse(PlayerEntity player, ItemStack stack) {
		boolean bl = this.receiveFood(player, stack);
		if (bl) {
			stack.decrementUnlessCreative(1, player);
		}

		if (this.getWorld().isClient) {
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
			if (!this.getWorld().isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(player);
			}
		} else if (item.isOf(Items.GOLDEN_APPLE) || item.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (!this.getWorld().isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(player);
			}
		}

		if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isBaby() && i > 0) {
			this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
			if (!this.getWorld().isClient) {
				this.growUp(i);
				bl = true;
			}
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper() && !this.getWorld().isClient) {
			this.addTemper(j);
			bl = true;
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
		if (!this.getWorld().isClient) {
			player.setYaw(this.getYaw());
			player.setPitch(this.getPitch());
			player.startRiding(this);
		}
	}

	@Override
	public boolean isImmobile() {
		return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.HORSE_FOOD);
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
				if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
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
		if (!this.getWorld().isClient && this.isAlive()) {
			if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}

			if (this.eatsGrass()) {
				if (!this.isEatingGrass()
					&& !this.hasPassengers()
					&& this.random.nextInt(300) == 0
					&& this.getWorld().getBlockState(this.getBlockPos().down()).isOf(Blocks.GRASS_BLOCK)) {
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
			LivingEntity livingEntity = this.getWorld()
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

		if (this.canMoveVoluntarily() && this.angryTicks > 0 && ++this.angryTicks > 20) {
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

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (this.hasPassengers() || this.isBaby()) {
			return super.interactMob(player, hand);
		} else if (this.isTame() && player.shouldCancelInteraction()) {
			this.openInventory(player);
			return ActionResult.success(this.getWorld().isClient);
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!itemStack.isEmpty()) {
				ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
				if (actionResult.isAccepted()) {
					return actionResult;
				}

				if (this.hasArmorSlot() && this.isHorseArmor(itemStack) && !this.isWearingBodyArmor()) {
					this.equipHorseArmor(player, itemStack);
					return ActionResult.success(this.getWorld().isClient);
				}
			}

			this.putPlayerOnBack(player);
			return ActionResult.success(this.getWorld().isClient);
		}
	}

	private void setEating() {
		if (!this.getWorld().isClient) {
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

	@Nullable
	public SoundEvent getAmbientStandSound() {
		return this.getAmbientSound();
	}

	public void updateAnger() {
		if (this.shouldAmbientStand() && this.canMoveVoluntarily()) {
			this.angryTicks = 1;
			this.setAngry(true);
		}
	}

	public void playAngrySound() {
		if (!this.isAngry()) {
			this.updateAnger();
			this.playSound(this.getAngrySound());
		}
	}

	public boolean bondWithPlayer(PlayerEntity player) {
		this.setOwnerUuid(player.getUuid());
		this.setTame(true);
		if (player instanceof ServerPlayerEntity) {
			Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
		}

		this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
		return true;
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);
		Vec2f vec2f = this.getControlledRotation(controllingPlayer);
		this.setRotation(vec2f.y, vec2f.x);
		this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
		if (this.isLogicalSideForUpdatingMovement()) {
			if (movementInput.z <= 0.0) {
				this.soundTicks = 0;
			}

			if (this.isOnGround()) {
				this.setInAir(false);
				if (this.jumpStrength > 0.0F && !this.isInAir()) {
					this.jump(this.jumpStrength, movementInput);
				}

				this.jumpStrength = 0.0F;
			}
		}
	}

	protected Vec2f getControlledRotation(LivingEntity controllingPassenger) {
		return new Vec2f(controllingPassenger.getPitch() * 0.5F, controllingPassenger.getYaw());
	}

	@Override
	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
		if (this.isOnGround() && this.jumpStrength == 0.0F && this.isAngry() && !this.jumping) {
			return Vec3d.ZERO;
		} else {
			float f = controllingPlayer.sidewaysSpeed * 0.5F;
			float g = controllingPlayer.forwardSpeed;
			if (g <= 0.0F) {
				g *= 0.25F;
			}

			return new Vec3d((double)f, 0.0, (double)g);
		}
	}

	@Override
	protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
	}

	protected void jump(float strength, Vec3d movementInput) {
		double d = (double)this.getJumpVelocity(strength);
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, d, vec3d.z);
		this.setInAir(true);
		this.velocityDirty = true;
		if (movementInput.z > 0.0) {
			float f = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
			this.setVelocity(this.getVelocity().add((double)(-0.4F * f * strength), 0.0, (double)(0.4F * g * strength)));
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
			nbt.put("SaddleItem", this.items.getStack(0).encode(this.getRegistryManager()));
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
			ItemStack itemStack = (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
			if (itemStack.isOf(Items.SADDLE)) {
				this.items.setStack(0, itemStack);
			}
		}

		this.updateSaddledFlag();
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

	protected void setChildAttributes(PassiveEntity other, AbstractHorseEntity child) {
		this.setChildAttribute(other, child, EntityAttributes.GENERIC_MAX_HEALTH, (double)MIN_HEALTH_BONUS, (double)MAX_HEALTH_BONUS);
		this.setChildAttribute(other, child, EntityAttributes.GENERIC_JUMP_STRENGTH, (double)MIN_JUMP_STRENGTH_BONUS, (double)MAX_JUMP_STRENGTH_BONUS);
		this.setChildAttribute(other, child, EntityAttributes.GENERIC_MOVEMENT_SPEED, (double)MIN_MOVEMENT_SPEED_BONUS, (double)MAX_MOVEMENT_SPEED_BONUS);
	}

	private void setChildAttribute(PassiveEntity other, AbstractHorseEntity child, RegistryEntry<EntityAttribute> attribute, double min, double max) {
		double d = calculateAttributeBaseValue(this.getAttributeBaseValue(attribute), other.getAttributeBaseValue(attribute), min, max, this.random);
		child.getAttributeInstance(attribute).setBaseValue(d);
	}

	static double calculateAttributeBaseValue(double parentBase, double otherParentBase, double min, double max, Random random) {
		if (max <= min) {
			throw new IllegalArgumentException("Incorrect range for an attribute");
		} else {
			parentBase = MathHelper.clamp(parentBase, min, max);
			otherParentBase = MathHelper.clamp(otherParentBase, min, max);
			double d = 0.15 * (max - min);
			double e = Math.abs(parentBase - otherParentBase) + d * 2.0;
			double f = (parentBase + otherParentBase) / 2.0;
			double g = (random.nextDouble() + random.nextDouble() + random.nextDouble()) / 3.0 - 0.5;
			double h = f + e * g;
			if (h > max) {
				double i = h - max;
				return max - i;
			} else if (h < min) {
				double i = min - h;
				return min + i;
			} else {
				return h;
			}
		}
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
			this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
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
	protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		super.updatePassengerPosition(passenger, positionUpdater);
		if (passenger instanceof LivingEntity) {
			((LivingEntity)passenger).bodyYaw = this.bodyYaw;
		}
	}

	protected static float getChildHealthBonus(IntUnaryOperator randomIntGetter) {
		return 15.0F + (float)randomIntGetter.applyAsInt(8) + (float)randomIntGetter.applyAsInt(9);
	}

	protected static double getChildJumpStrengthBonus(DoubleSupplier randomDoubleGetter) {
		return 0.4F + randomDoubleGetter.getAsDouble() * 0.2 + randomDoubleGetter.getAsDouble() * 0.2 + randomDoubleGetter.getAsDouble() * 0.2;
	}

	protected static double getChildMovementSpeedBonus(DoubleSupplier randomDoubleGetter) {
		return (0.45F + randomDoubleGetter.getAsDouble() * 0.3 + randomDoubleGetter.getAsDouble() * 0.3 + randomDoubleGetter.getAsDouble() * 0.3) * 0.25;
	}

	@Override
	public boolean isClimbing() {
		return false;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		int i = mappedIndex - 400;
		if (i == 0) {
			return new StackReference() {
				@Override
				public ItemStack get() {
					return AbstractHorseEntity.this.items.getStack(0);
				}

				@Override
				public boolean set(ItemStack stack) {
					if (!stack.isEmpty() && !stack.isOf(Items.SADDLE)) {
						return false;
					} else {
						AbstractHorseEntity.this.items.setStack(0, stack);
						AbstractHorseEntity.this.updateSaddledFlag();
						return true;
					}
				}
			};
		} else {
			int j = mappedIndex - 500 + 1;
			return j >= 1 && j < this.items.size() ? StackReference.of(this.items, j) : super.getStackReference(mappedIndex);
		}
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		if (this.isSaddled()) {
			Entity var2 = this.getFirstPassenger();
			if (var2 instanceof PlayerEntity) {
				return (PlayerEntity)var2;
			}
		}

		return super.getControllingPassenger();
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
				double h = this.getWorld().getDismountHeight(mutable);
				if ((double)mutable.getY() + h > g) {
					break;
				}

				if (Dismounting.canDismountInBlock(h)) {
					Box box = passenger.getBoundingBox(entityPose);
					Vec3d vec3d = new Vec3d(d, (double)mutable.getY() + h, f);
					if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) {
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

	protected void initAttributes(Random random) {
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(0.2F);
		}

		this.initAttributes(world.getRandom());
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	public boolean areInventoriesDifferent(Inventory inventory) {
		return this.items != inventory;
	}

	public int getMinAmbientStandDelay() {
		return this.getMinAmbientSoundDelay();
	}

	@Override
	protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor)
			.add(
				new Vec3d(0.0, 0.15 * (double)this.lastAngryAnimationProgress * (double)scaleFactor, -0.7 * (double)this.lastAngryAnimationProgress * (double)scaleFactor)
					.rotateY(-this.getYaw() * (float) (Math.PI / 180.0))
			);
	}

	public final Inventory getInventory() {
		return this.inventory;
	}
}
