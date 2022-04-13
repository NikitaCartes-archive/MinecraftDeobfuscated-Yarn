package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.PowderSnowJumpGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;

public class FoxEntity extends AnimalEntity {
	private static final TrackedData<Integer> TYPE = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> FOX_FLAGS = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int SITTING_FLAG = 1;
	public static final int CROUCHING_FLAG = 4;
	public static final int ROLLING_HEAD_FLAG = 8;
	public static final int CHASING_FLAG = 16;
	private static final int SLEEPING_FLAG = 32;
	private static final int WALKING_FLAG = 64;
	private static final int AGGRESSIVE_FLAG = 128;
	private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Optional<UUID>> OTHER_TRUSTED = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = item -> !item.cannotPickup() && item.isAlive();
	private static final Predicate<Entity> JUST_ATTACKED_SOMETHING_FILTER = entity -> !(entity instanceof LivingEntity livingEntity)
			? false
			: livingEntity.getAttacking() != null && livingEntity.getLastAttackTime() < livingEntity.age + 600;
	static final Predicate<Entity> CHICKEN_AND_RABBIT_FILTER = entity -> entity instanceof ChickenEntity || entity instanceof RabbitEntity;
	private static final Predicate<Entity> NOTICEABLE_PLAYER_FILTER = entity -> !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);
	private static final int EATING_DURATION = 600;
	private Goal followChickenAndRabbitGoal;
	private Goal followBabyTurtleGoal;
	private Goal followFishGoal;
	private float headRollProgress;
	private float lastHeadRollProgress;
	float extraRollingHeight;
	float lastExtraRollingHeight;
	private int eatingTime;

	public FoxEntity(EntityType<? extends FoxEntity> entityType, World world) {
		super(entityType, world);
		this.lookControl = new FoxEntity.FoxLookControl();
		this.moveControl = new FoxEntity.FoxMoveControl();
		this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0F);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(OWNER, Optional.empty());
		this.dataTracker.startTracking(OTHER_TRUSTED, Optional.empty());
		this.dataTracker.startTracking(TYPE, 0);
		this.dataTracker.startTracking(FOX_FLAGS, (byte)0);
	}

	@Override
	protected void initGoals() {
		this.followChickenAndRabbitGoal = new ActiveTargetGoal(
			this, AnimalEntity.class, 10, false, false, entity -> entity instanceof ChickenEntity || entity instanceof RabbitEntity
		);
		this.followBabyTurtleGoal = new ActiveTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
		this.followFishGoal = new ActiveTargetGoal(this, FishEntity.class, 20, false, false, entity -> entity instanceof SchoolingFishEntity);
		this.goalSelector.add(0, new FoxEntity.FoxSwimGoal());
		this.goalSelector.add(0, new PowderSnowJumpGoal(this, this.world));
		this.goalSelector.add(1, new FoxEntity.StopWanderingGoal());
		this.goalSelector.add(2, new FoxEntity.EscapeWhenNotAggressiveGoal(2.2));
		this.goalSelector.add(3, new FoxEntity.MateGoal(1.0));
		this.goalSelector
			.add(
				4,
				new FleeEntityGoal(
					this, PlayerEntity.class, 16.0F, 1.6, 1.4, entity -> NOTICEABLE_PLAYER_FILTER.test(entity) && !this.canTrust(entity.getUuid()) && !this.isAggressive()
				)
			);
		this.goalSelector.add(4, new FleeEntityGoal(this, WolfEntity.class, 8.0F, 1.6, 1.4, entity -> !((WolfEntity)entity).isTamed() && !this.isAggressive()));
		this.goalSelector.add(4, new FleeEntityGoal(this, PolarBearEntity.class, 8.0F, 1.6, 1.4, entity -> !this.isAggressive()));
		this.goalSelector.add(5, new FoxEntity.MoveToHuntGoal());
		this.goalSelector.add(6, new FoxEntity.JumpChasingGoal());
		this.goalSelector.add(6, new FoxEntity.AvoidDaylightGoal(1.25));
		this.goalSelector.add(7, new FoxEntity.AttackGoal(1.2F, true));
		this.goalSelector.add(7, new FoxEntity.DelayedCalmDownGoal());
		this.goalSelector.add(8, new FoxEntity.FollowParentGoal(this, 1.25));
		this.goalSelector.add(9, new FoxEntity.GoToVillageGoal(32, 200));
		this.goalSelector.add(10, new FoxEntity.EatSweetBerriesGoal(1.2F, 12, 1));
		this.goalSelector.add(10, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(11, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(11, new FoxEntity.PickupItemGoal());
		this.goalSelector.add(12, new FoxEntity.LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
		this.goalSelector.add(13, new FoxEntity.SitDownAndLookAroundGoal());
		this.targetSelector
			.add(
				3,
				new FoxEntity.DefendFriendGoal(LivingEntity.class, false, false, entity -> JUST_ATTACKED_SOMETHING_FILTER.test(entity) && !this.canTrust(entity.getUuid()))
			);
	}

	@Override
	public SoundEvent getEatSound(ItemStack stack) {
		return SoundEvents.ENTITY_FOX_EAT;
	}

	@Override
	public void tickMovement() {
		if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
			this.eatingTime++;
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (this.canEat(itemStack)) {
				if (this.eatingTime > 600) {
					ItemStack itemStack2 = itemStack.finishUsing(this.world, this);
					if (!itemStack2.isEmpty()) {
						this.equipStack(EquipmentSlot.MAINHAND, itemStack2);
					}

					this.eatingTime = 0;
				} else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1F) {
					this.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
					this.world.sendEntityStatus(this, EntityStatuses.CREATE_EATING_PARTICLES);
				}
			}

			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null || !livingEntity.isAlive()) {
				this.setCrouching(false);
				this.setRollingHead(false);
			}
		}

		if (this.isSleeping() || this.isImmobile()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
		}

		super.tickMovement();
		if (this.isAggressive() && this.random.nextFloat() < 0.05F) {
			this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
		}
	}

	@Override
	protected boolean isImmobile() {
		return this.isDead();
	}

	private boolean canEat(ItemStack stack) {
		return stack.getItem().isFood() && this.getTarget() == null && this.onGround && !this.isSleeping();
	}

	@Override
	protected void initEquipment(AbstractRandom random, LocalDifficulty localDifficulty) {
		if (random.nextFloat() < 0.2F) {
			float f = random.nextFloat();
			ItemStack itemStack;
			if (f < 0.05F) {
				itemStack = new ItemStack(Items.EMERALD);
			} else if (f < 0.2F) {
				itemStack = new ItemStack(Items.EGG);
			} else if (f < 0.4F) {
				itemStack = random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
			} else if (f < 0.6F) {
				itemStack = new ItemStack(Items.WHEAT);
			} else if (f < 0.8F) {
				itemStack = new ItemStack(Items.LEATHER);
			} else {
				itemStack = new ItemStack(Items.FEATHER);
			}

			this.equipStack(EquipmentSlot.MAINHAND, itemStack);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.CREATE_EATING_PARTICLES) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				for (int i = 0; i < 8; i++) {
					Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
						.rotateX(-this.getPitch() * (float) (Math.PI / 180.0))
						.rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
					this.world
						.addParticle(
							new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack),
							this.getX() + this.getRotationVector().x / 2.0,
							this.getY(),
							this.getZ() + this.getRotationVector().z / 2.0,
							vec3d.x,
							vec3d.y + 0.05,
							vec3d.z
						);
				}
			}
		} else {
			super.handleStatus(status);
		}
	}

	public static DefaultAttributeContainer.Builder createFoxAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
	}

	public FoxEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		FoxEntity foxEntity = EntityType.FOX.create(serverWorld);
		foxEntity.setType(this.random.nextBoolean() ? this.getFoxType() : ((FoxEntity)passiveEntity).getFoxType());
		return foxEntity;
	}

	public static boolean canSpawn(EntityType<FoxEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, AbstractRandom random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.FOXES_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		RegistryEntry<Biome> registryEntry = world.getBiome(this.getBlockPos());
		FoxEntity.Type type = FoxEntity.Type.fromBiome(registryEntry);
		boolean bl = false;
		if (entityData instanceof FoxEntity.FoxData foxData) {
			type = foxData.type;
			if (foxData.getSpawnedCount() >= 2) {
				bl = true;
			}
		} else {
			entityData = new FoxEntity.FoxData(type);
		}

		this.setType(type);
		if (bl) {
			this.setBreedingAge(-24000);
		}

		if (world instanceof ServerWorld) {
			this.addTypeSpecificGoals();
		}

		this.initEquipment(world.getRandom(), difficulty);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	private void addTypeSpecificGoals() {
		if (this.getFoxType() == FoxEntity.Type.RED) {
			this.targetSelector.add(4, this.followChickenAndRabbitGoal);
			this.targetSelector.add(4, this.followBabyTurtleGoal);
			this.targetSelector.add(6, this.followFishGoal);
		} else {
			this.targetSelector.add(4, this.followFishGoal);
			this.targetSelector.add(6, this.followChickenAndRabbitGoal);
			this.targetSelector.add(6, this.followBabyTurtleGoal);
		}
	}

	@Override
	protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
		if (this.isBreedingItem(stack)) {
			this.playSound(this.getEatSound(stack), 1.0F, 1.0F);
		}

		super.eat(player, hand, stack);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? dimensions.height * 0.85F : 0.4F;
	}

	public FoxEntity.Type getFoxType() {
		return FoxEntity.Type.fromId(this.dataTracker.get(TYPE));
	}

	private void setType(FoxEntity.Type type) {
		this.dataTracker.set(TYPE, type.getId());
	}

	List<UUID> getTrustedUuids() {
		List<UUID> list = Lists.<UUID>newArrayList();
		list.add((UUID)this.dataTracker.get(OWNER).orElse(null));
		list.add((UUID)this.dataTracker.get(OTHER_TRUSTED).orElse(null));
		return list;
	}

	void addTrustedUuid(@Nullable UUID uuid) {
		if (this.dataTracker.get(OWNER).isPresent()) {
			this.dataTracker.set(OTHER_TRUSTED, Optional.ofNullable(uuid));
		} else {
			this.dataTracker.set(OWNER, Optional.ofNullable(uuid));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		List<UUID> list = this.getTrustedUuids();
		NbtList nbtList = new NbtList();

		for (UUID uUID : list) {
			if (uUID != null) {
				nbtList.add(NbtHelper.fromUuid(uUID));
			}
		}

		nbt.put("Trusted", nbtList);
		nbt.putBoolean("Sleeping", this.isSleeping());
		nbt.putString("Type", this.getFoxType().getKey());
		nbt.putBoolean("Sitting", this.isSitting());
		nbt.putBoolean("Crouching", this.isInSneakingPose());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		NbtList nbtList = nbt.getList("Trusted", NbtElement.INT_ARRAY_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			this.addTrustedUuid(NbtHelper.toUuid(nbtList.get(i)));
		}

		this.setSleeping(nbt.getBoolean("Sleeping"));
		this.setType(FoxEntity.Type.byName(nbt.getString("Type")));
		this.setSitting(nbt.getBoolean("Sitting"));
		this.setCrouching(nbt.getBoolean("Crouching"));
		if (this.world instanceof ServerWorld) {
			this.addTypeSpecificGoals();
		}
	}

	public boolean isSitting() {
		return this.getFoxFlag(SITTING_FLAG);
	}

	public void setSitting(boolean sitting) {
		this.setFoxFlag(SITTING_FLAG, sitting);
	}

	public boolean isWalking() {
		return this.getFoxFlag(WALKING_FLAG);
	}

	void setWalking(boolean walking) {
		this.setFoxFlag(WALKING_FLAG, walking);
	}

	boolean isAggressive() {
		return this.getFoxFlag(AGGRESSIVE_FLAG);
	}

	void setAggressive(boolean aggressive) {
		this.setFoxFlag(AGGRESSIVE_FLAG, aggressive);
	}

	@Override
	public boolean isSleeping() {
		return this.getFoxFlag(SLEEPING_FLAG);
	}

	void setSleeping(boolean sleeping) {
		this.setFoxFlag(SLEEPING_FLAG, sleeping);
	}

	private void setFoxFlag(int mask, boolean value) {
		if (value) {
			this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) | mask));
		} else {
			this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) & ~mask));
		}
	}

	private boolean getFoxFlag(int bitmask) {
		return (this.dataTracker.get(FOX_FLAGS) & bitmask) != 0;
	}

	@Override
	public boolean canEquip(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.MAINHAND && super.canEquip(stack);
	}

	@Override
	public boolean canPickupItem(ItemStack stack) {
		Item item = stack.getItem();
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
		return itemStack.isEmpty() || this.eatingTime > 0 && item.isFood() && !itemStack.getItem().isFood();
	}

	private void spit(ItemStack stack) {
		if (!stack.isEmpty() && !this.world.isClient) {
			ItemEntity itemEntity = new ItemEntity(
				this.world, this.getX() + this.getRotationVector().x, this.getY() + 1.0, this.getZ() + this.getRotationVector().z, stack
			);
			itemEntity.setPickupDelay(40);
			itemEntity.setThrower(this.getUuid());
			this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
			this.world.spawnEntity(itemEntity);
		}
	}

	private void dropItem(ItemStack stack) {
		ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), stack);
		this.world.spawnEntity(itemEntity);
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (this.canPickupItem(itemStack)) {
			int i = itemStack.getCount();
			if (i > 1) {
				this.dropItem(itemStack.split(i - 1));
			}

			this.spit(this.getEquippedStack(EquipmentSlot.MAINHAND));
			this.triggerItemPickedUpByEntityCriteria(item);
			this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
			this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0F;
			this.sendPickup(item, itemStack.getCount());
			item.discard();
			this.eatingTime = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.canMoveVoluntarily()) {
			boolean bl = this.isTouchingWater();
			if (bl || this.getTarget() != null || this.world.isThundering()) {
				this.stopSleeping();
			}

			if (bl || this.isSleeping()) {
				this.setSitting(false);
			}

			if (this.isWalking() && this.world.random.nextFloat() < 0.2F) {
				BlockPos blockPos = this.getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				this.world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
			}
		}

		this.lastHeadRollProgress = this.headRollProgress;
		if (this.isRollingHead()) {
			this.headRollProgress = this.headRollProgress + (1.0F - this.headRollProgress) * 0.4F;
		} else {
			this.headRollProgress = this.headRollProgress + (0.0F - this.headRollProgress) * 0.4F;
		}

		this.lastExtraRollingHeight = this.extraRollingHeight;
		if (this.isInSneakingPose()) {
			this.extraRollingHeight += 0.2F;
			if (this.extraRollingHeight > 3.0F) {
				this.extraRollingHeight = 3.0F;
			}
		} else {
			this.extraRollingHeight = 0.0F;
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.FOX_FOOD);
	}

	@Override
	protected void onPlayerSpawnedChild(PlayerEntity player, MobEntity child) {
		((FoxEntity)child).addTrustedUuid(player.getUuid());
	}

	public boolean isChasing() {
		return this.getFoxFlag(CHASING_FLAG);
	}

	public void setChasing(boolean chasing) {
		this.setFoxFlag(CHASING_FLAG, chasing);
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean isFullyCrouched() {
		return this.extraRollingHeight == 3.0F;
	}

	public void setCrouching(boolean crouching) {
		this.setFoxFlag(CROUCHING_FLAG, crouching);
	}

	@Override
	public boolean isInSneakingPose() {
		return this.getFoxFlag(CROUCHING_FLAG);
	}

	public void setRollingHead(boolean rollingHead) {
		this.setFoxFlag(ROLLING_HEAD_FLAG, rollingHead);
	}

	public boolean isRollingHead() {
		return this.getFoxFlag(ROLLING_HEAD_FLAG);
	}

	public float getHeadRoll(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastHeadRollProgress, this.headRollProgress) * 0.11F * (float) Math.PI;
	}

	public float getBodyRotationHeightOffset(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastExtraRollingHeight, this.extraRollingHeight);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		if (this.isAggressive() && target == null) {
			this.setAggressive(false);
		}

		super.setTarget(target);
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		return MathHelper.ceil((fallDistance - 5.0F) * damageMultiplier);
	}

	void stopSleeping() {
		this.setSleeping(false);
	}

	void stopActions() {
		this.setRollingHead(false);
		this.setCrouching(false);
		this.setSitting(false);
		this.setSleeping(false);
		this.setAggressive(false);
		this.setWalking(false);
	}

	boolean wantsToPickupItem() {
		return !this.isSleeping() && !this.isSitting() && !this.isWalking();
	}

	@Override
	public void playAmbientSound() {
		SoundEvent soundEvent = this.getAmbientSound();
		if (soundEvent == SoundEvents.ENTITY_FOX_SCREECH) {
			this.playSound(soundEvent, 2.0F, this.getSoundPitch());
		} else {
			super.playAmbientSound();
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isSleeping()) {
			return SoundEvents.ENTITY_FOX_SLEEP;
		} else {
			if (!this.world.isDay() && this.random.nextFloat() < 0.1F) {
				List<PlayerEntity> list = this.world
					.getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(16.0, 16.0, 16.0), EntityPredicates.EXCEPT_SPECTATOR);
				if (list.isEmpty()) {
					return SoundEvents.ENTITY_FOX_SCREECH;
				}
			}

			return SoundEvents.ENTITY_FOX_AMBIENT;
		}
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_FOX_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_FOX_DEATH;
	}

	boolean canTrust(UUID uuid) {
		return this.getTrustedUuids().contains(uuid);
	}

	@Override
	protected void drop(DamageSource source) {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
		if (!itemStack.isEmpty()) {
			this.dropStack(itemStack);
			this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		}

		super.drop(source);
	}

	public static boolean canJumpChase(FoxEntity fox, LivingEntity chasedEntity) {
		double d = chasedEntity.getZ() - fox.getZ();
		double e = chasedEntity.getX() - fox.getX();
		double f = d / e;
		int i = 6;

		for (int j = 0; j < 6; j++) {
			double g = f == 0.0 ? 0.0 : d * (double)((float)j / 6.0F);
			double h = f == 0.0 ? e * (double)((float)j / 6.0F) : g / f;

			for (int k = 1; k < 4; k++) {
				if (!fox.world.getBlockState(new BlockPos(fox.getX() + h, fox.getY() + (double)k, fox.getZ() + g)).getMaterial().isReplaceable()) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.55F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(double speed, boolean pauseWhenIdle) {
			super(FoxEntity.this, speed, pauseWhenIdle);
		}

		@Override
		protected void attack(LivingEntity target, double squaredDistance) {
			double d = this.getSquaredMaxAttackDistance(target);
			if (squaredDistance <= d && this.isCooledDown()) {
				this.resetCooldown();
				this.mob.tryAttack(target);
				FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0F, 1.0F);
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setRollingHead(false);
			super.start();
		}

		@Override
		public boolean canStart() {
			return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isWalking() && super.canStart();
		}
	}

	class AvoidDaylightGoal extends EscapeSunlightGoal {
		private int timer = toGoalTicks(100);

		public AvoidDaylightGoal(double speed) {
			super(FoxEntity.this, speed);
		}

		@Override
		public boolean canStart() {
			if (!FoxEntity.this.isSleeping() && this.mob.getTarget() == null) {
				if (FoxEntity.this.world.isThundering() && FoxEntity.this.world.isSkyVisible(this.mob.getBlockPos())) {
					return this.targetShadedPos();
				} else if (this.timer > 0) {
					this.timer--;
					return false;
				} else {
					this.timer = 100;
					BlockPos blockPos = this.mob.getBlockPos();
					return FoxEntity.this.world.isDay()
						&& FoxEntity.this.world.isSkyVisible(blockPos)
						&& !((ServerWorld)FoxEntity.this.world).isNearOccupiedPointOfInterest(blockPos)
						&& this.targetShadedPos();
				}
			} else {
				return false;
			}
		}

		@Override
		public void start() {
			FoxEntity.this.stopActions();
			super.start();
		}
	}

	abstract class CalmDownGoal extends Goal {
		private final TargetPredicate WORRIABLE_ENTITY_PREDICATE = TargetPredicate.createAttackable()
			.setBaseMaxDistance(12.0)
			.ignoreVisibility()
			.setPredicate(FoxEntity.this.new WorriableEntityFilter());

		protected boolean isAtFavoredLocation() {
			BlockPos blockPos = new BlockPos(FoxEntity.this.getX(), FoxEntity.this.getBoundingBox().maxY, FoxEntity.this.getZ());
			return !FoxEntity.this.world.isSkyVisible(blockPos) && FoxEntity.this.getPathfindingFavor(blockPos) >= 0.0F;
		}

		protected boolean canCalmDown() {
			return !FoxEntity.this.world
				.getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, FoxEntity.this, FoxEntity.this.getBoundingBox().expand(12.0, 6.0, 12.0))
				.isEmpty();
		}
	}

	class DefendFriendGoal extends ActiveTargetGoal<LivingEntity> {
		@Nullable
		private LivingEntity offender;
		@Nullable
		private LivingEntity friend;
		private int lastAttackedTime;

		public DefendFriendGoal(
			Class<LivingEntity> targetEntityClass, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate
		) {
			super(FoxEntity.this, targetEntityClass, 10, checkVisibility, checkCanNavigate, targetPredicate);
		}

		@Override
		public boolean canStart() {
			if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
				return false;
			} else {
				for (UUID uUID : FoxEntity.this.getTrustedUuids()) {
					if (uUID != null
						&& FoxEntity.this.world instanceof ServerWorld
						&& ((ServerWorld)FoxEntity.this.world).getEntity(uUID) instanceof LivingEntity livingEntity) {
						this.friend = livingEntity;
						this.offender = livingEntity.getAttacker();
						int i = livingEntity.getLastAttackedTime();
						return i != this.lastAttackedTime && this.canTrack(this.offender, this.targetPredicate);
					}
				}

				return false;
			}
		}

		@Override
		public void start() {
			this.setTargetEntity(this.offender);
			this.targetEntity = this.offender;
			if (this.friend != null) {
				this.lastAttackedTime = this.friend.getLastAttackedTime();
			}

			FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
			FoxEntity.this.setAggressive(true);
			FoxEntity.this.stopSleeping();
			super.start();
		}
	}

	class DelayedCalmDownGoal extends FoxEntity.CalmDownGoal {
		private static final int MAX_CALM_DOWN_TIME = toGoalTicks(140);
		private int timer = FoxEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);

		public DelayedCalmDownGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.sidewaysSpeed == 0.0F && FoxEntity.this.upwardSpeed == 0.0F && FoxEntity.this.forwardSpeed == 0.0F
				? this.canNotCalmDown() || FoxEntity.this.isSleeping()
				: false;
		}

		@Override
		public boolean shouldContinue() {
			return this.canNotCalmDown();
		}

		private boolean canNotCalmDown() {
			if (this.timer > 0) {
				this.timer--;
				return false;
			} else {
				return FoxEntity.this.world.isDay() && this.isAtFavoredLocation() && !this.canCalmDown() && !FoxEntity.this.inPowderSnow;
			}
		}

		@Override
		public void stop() {
			this.timer = FoxEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
			FoxEntity.this.stopActions();
		}

		@Override
		public void start() {
			FoxEntity.this.setSitting(false);
			FoxEntity.this.setCrouching(false);
			FoxEntity.this.setRollingHead(false);
			FoxEntity.this.setJumping(false);
			FoxEntity.this.setSleeping(true);
			FoxEntity.this.getNavigation().stop();
			FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.getX(), FoxEntity.this.getY(), FoxEntity.this.getZ(), 0.0);
		}
	}

	public class EatSweetBerriesGoal extends MoveToTargetPosGoal {
		private static final int EATING_TIME = 40;
		protected int timer;

		public EatSweetBerriesGoal(double speed, int range, int maxYDifference) {
			super(FoxEntity.this, speed, range, maxYDifference);
		}

		@Override
		public double getDesiredDistanceToTarget() {
			return 2.0;
		}

		@Override
		public boolean shouldResetPath() {
			return this.tryingTime % 100 == 0;
		}

		@Override
		protected boolean isTargetPos(WorldView world, BlockPos pos) {
			BlockState blockState = world.getBlockState(pos);
			return blockState.isOf(Blocks.SWEET_BERRY_BUSH) && (Integer)blockState.get(SweetBerryBushBlock.AGE) >= 2 || CaveVines.hasBerries(blockState);
		}

		@Override
		public void tick() {
			if (this.hasReached()) {
				if (this.timer >= 40) {
					this.eatSweetBerry();
				} else {
					this.timer++;
				}
			} else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05F) {
				FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
			}

			super.tick();
		}

		protected void eatSweetBerry() {
			if (FoxEntity.this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
				BlockState blockState = FoxEntity.this.world.getBlockState(this.targetPos);
				if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
					this.pickSweetBerries(blockState);
				} else if (CaveVines.hasBerries(blockState)) {
					this.pickGlowBerries(blockState);
				}
			}
		}

		private void pickGlowBerries(BlockState state) {
			CaveVines.pickBerries(state, FoxEntity.this.world, this.targetPos);
		}

		private void pickSweetBerries(BlockState state) {
			int i = (Integer)state.get(SweetBerryBushBlock.AGE);
			state.with(SweetBerryBushBlock.AGE, Integer.valueOf(1));
			int j = 1 + FoxEntity.this.world.random.nextInt(2) + (i == 3 ? 1 : 0);
			ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (itemStack.isEmpty()) {
				FoxEntity.this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
				j--;
			}

			if (j > 0) {
				Block.dropStack(FoxEntity.this.world, this.targetPos, new ItemStack(Items.SWEET_BERRIES, j));
			}

			FoxEntity.this.playSound(SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
			FoxEntity.this.world.setBlockState(this.targetPos, state.with(SweetBerryBushBlock.AGE, Integer.valueOf(1)), Block.NOTIFY_LISTENERS);
		}

		@Override
		public boolean canStart() {
			return !FoxEntity.this.isSleeping() && super.canStart();
		}

		@Override
		public void start() {
			this.timer = 0;
			FoxEntity.this.setSitting(false);
			super.start();
		}
	}

	class EscapeWhenNotAggressiveGoal extends EscapeDangerGoal {
		public EscapeWhenNotAggressiveGoal(double speed) {
			super(FoxEntity.this, speed);
		}

		@Override
		public boolean isInDanger() {
			return !FoxEntity.this.isAggressive() && super.isInDanger();
		}
	}

	class FollowParentGoal extends net.minecraft.entity.ai.goal.FollowParentGoal {
		private final FoxEntity fox;

		public FollowParentGoal(FoxEntity fox, double speed) {
			super(fox, speed);
			this.fox = fox;
		}

		@Override
		public boolean canStart() {
			return !this.fox.isAggressive() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.fox.isAggressive() && super.shouldContinue();
		}

		@Override
		public void start() {
			this.fox.stopActions();
			super.start();
		}
	}

	public static class FoxData extends PassiveEntity.PassiveData {
		public final FoxEntity.Type type;

		public FoxData(FoxEntity.Type type) {
			super(false);
			this.type = type;
		}
	}

	public class FoxLookControl extends LookControl {
		public FoxLookControl() {
			super(FoxEntity.this);
		}

		@Override
		public void tick() {
			if (!FoxEntity.this.isSleeping()) {
				super.tick();
			}
		}

		@Override
		protected boolean shouldStayHorizontal() {
			return !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.isWalking();
		}
	}

	class FoxMoveControl extends MoveControl {
		public FoxMoveControl() {
			super(FoxEntity.this);
		}

		@Override
		public void tick() {
			if (FoxEntity.this.wantsToPickupItem()) {
				super.tick();
			}
		}
	}

	class FoxSwimGoal extends SwimGoal {
		public FoxSwimGoal() {
			super(FoxEntity.this);
		}

		@Override
		public void start() {
			super.start();
			FoxEntity.this.stopActions();
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.isTouchingWater() && FoxEntity.this.getFluidHeight(FluidTags.WATER) > 0.25 || FoxEntity.this.isInLava();
		}
	}

	class GoToVillageGoal extends net.minecraft.entity.ai.goal.GoToVillageGoal {
		public GoToVillageGoal(int unused, int searchRange) {
			super(FoxEntity.this, searchRange);
		}

		@Override
		public void start() {
			FoxEntity.this.stopActions();
			super.start();
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.canGoToVillage();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.canGoToVillage();
		}

		private boolean canGoToVillage() {
			return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
		}
	}

	public class JumpChasingGoal extends DiveJumpingGoal {
		@Override
		public boolean canStart() {
			if (!FoxEntity.this.isFullyCrouched()) {
				return false;
			} else {
				LivingEntity livingEntity = FoxEntity.this.getTarget();
				if (livingEntity != null && livingEntity.isAlive()) {
					if (livingEntity.getMovementDirection() != livingEntity.getHorizontalFacing()) {
						return false;
					} else {
						boolean bl = FoxEntity.canJumpChase(FoxEntity.this, livingEntity);
						if (!bl) {
							FoxEntity.this.getNavigation().findPathTo(livingEntity, 0);
							FoxEntity.this.setCrouching(false);
							FoxEntity.this.setRollingHead(false);
						}

						return bl;
					}
				} else {
					return false;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null && livingEntity.isAlive()) {
				double d = FoxEntity.this.getVelocity().y;
				return (!(d * d < 0.05F) || !(Math.abs(FoxEntity.this.getPitch()) < 15.0F) || !FoxEntity.this.onGround) && !FoxEntity.this.isWalking();
			} else {
				return false;
			}
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public void start() {
			FoxEntity.this.setJumping(true);
			FoxEntity.this.setChasing(true);
			FoxEntity.this.setRollingHead(false);
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null) {
				FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0F, 30.0F);
				Vec3d vec3d = new Vec3d(
						livingEntity.getX() - FoxEntity.this.getX(), livingEntity.getY() - FoxEntity.this.getY(), livingEntity.getZ() - FoxEntity.this.getZ()
					)
					.normalize();
				FoxEntity.this.setVelocity(FoxEntity.this.getVelocity().add(vec3d.x * 0.8, 0.9, vec3d.z * 0.8));
			}

			FoxEntity.this.getNavigation().stop();
		}

		@Override
		public void stop() {
			FoxEntity.this.setCrouching(false);
			FoxEntity.this.extraRollingHeight = 0.0F;
			FoxEntity.this.lastExtraRollingHeight = 0.0F;
			FoxEntity.this.setRollingHead(false);
			FoxEntity.this.setChasing(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null) {
				FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0F, 30.0F);
			}

			if (!FoxEntity.this.isWalking()) {
				Vec3d vec3d = FoxEntity.this.getVelocity();
				if (vec3d.y * vec3d.y < 0.03F && FoxEntity.this.getPitch() != 0.0F) {
					FoxEntity.this.setPitch(MathHelper.lerpAngle(FoxEntity.this.getPitch(), 0.0F, 0.2F));
				} else {
					double d = vec3d.horizontalLength();
					double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 180.0F / (float)Math.PI;
					FoxEntity.this.setPitch((float)e);
				}
			}

			if (livingEntity != null && FoxEntity.this.distanceTo(livingEntity) <= 2.0F) {
				FoxEntity.this.tryAttack(livingEntity);
			} else if (FoxEntity.this.getPitch() > 0.0F
				&& FoxEntity.this.onGround
				&& (float)FoxEntity.this.getVelocity().y != 0.0F
				&& FoxEntity.this.world.getBlockState(FoxEntity.this.getBlockPos()).isOf(Blocks.SNOW)) {
				FoxEntity.this.setPitch(60.0F);
				FoxEntity.this.setTarget(null);
				FoxEntity.this.setWalking(true);
			}
		}
	}

	class LookAtEntityGoal extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
		public LookAtEntityGoal(MobEntity fox, Class<? extends LivingEntity> targetType, float range) {
			super(fox, targetType, range);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
		}
	}

	class MateGoal extends AnimalMateGoal {
		public MateGoal(double chance) {
			super(FoxEntity.this, chance);
		}

		@Override
		public void start() {
			((FoxEntity)this.animal).stopActions();
			((FoxEntity)this.mate).stopActions();
			super.start();
		}

		@Override
		protected void breed() {
			ServerWorld serverWorld = (ServerWorld)this.world;
			FoxEntity foxEntity = (FoxEntity)this.animal.createChild(serverWorld, this.mate);
			if (foxEntity != null) {
				ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
				ServerPlayerEntity serverPlayerEntity2 = this.mate.getLovingPlayer();
				ServerPlayerEntity serverPlayerEntity3 = serverPlayerEntity;
				if (serverPlayerEntity != null) {
					foxEntity.addTrustedUuid(serverPlayerEntity.getUuid());
				} else {
					serverPlayerEntity3 = serverPlayerEntity2;
				}

				if (serverPlayerEntity2 != null && serverPlayerEntity != serverPlayerEntity2) {
					foxEntity.addTrustedUuid(serverPlayerEntity2.getUuid());
				}

				if (serverPlayerEntity3 != null) {
					serverPlayerEntity3.incrementStat(Stats.ANIMALS_BRED);
					Criteria.BRED_ANIMALS.trigger(serverPlayerEntity3, this.animal, this.mate, foxEntity);
				}

				this.animal.setBreedingAge(6000);
				this.mate.setBreedingAge(6000);
				this.animal.resetLoveTicks();
				this.mate.resetLoveTicks();
				foxEntity.setBreedingAge(-24000);
				foxEntity.refreshPositionAndAngles(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
				serverWorld.spawnEntityAndPassengers(foxEntity);
				this.world.sendEntityStatus(this.animal, EntityStatuses.ADD_BREEDING_PARTICLES);
				if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
					this.world
						.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
				}
			}
		}
	}

	class MoveToHuntGoal extends Goal {
		public MoveToHuntGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			if (FoxEntity.this.isSleeping()) {
				return false;
			} else {
				LivingEntity livingEntity = FoxEntity.this.getTarget();
				return livingEntity != null
					&& livingEntity.isAlive()
					&& FoxEntity.CHICKEN_AND_RABBIT_FILTER.test(livingEntity)
					&& FoxEntity.this.squaredDistanceTo(livingEntity) > 36.0
					&& !FoxEntity.this.isInSneakingPose()
					&& !FoxEntity.this.isRollingHead()
					&& !FoxEntity.this.jumping;
			}
		}

		@Override
		public void start() {
			FoxEntity.this.setSitting(false);
			FoxEntity.this.setWalking(false);
		}

		@Override
		public void stop() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity)) {
				FoxEntity.this.setRollingHead(true);
				FoxEntity.this.setCrouching(true);
				FoxEntity.this.getNavigation().stop();
				FoxEntity.this.getLookControl().lookAt(livingEntity, (float)FoxEntity.this.getMaxHeadRotation(), (float)FoxEntity.this.getMaxLookPitchChange());
			} else {
				FoxEntity.this.setRollingHead(false);
				FoxEntity.this.setCrouching(false);
			}
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = FoxEntity.this.getTarget();
			if (livingEntity != null) {
				FoxEntity.this.getLookControl().lookAt(livingEntity, (float)FoxEntity.this.getMaxHeadRotation(), (float)FoxEntity.this.getMaxLookPitchChange());
				if (FoxEntity.this.squaredDistanceTo(livingEntity) <= 36.0) {
					FoxEntity.this.setRollingHead(true);
					FoxEntity.this.setCrouching(true);
					FoxEntity.this.getNavigation().stop();
				} else {
					FoxEntity.this.getNavigation().startMovingTo(livingEntity, 1.5);
				}
			}
		}
	}

	class PickupItemGoal extends Goal {
		public PickupItemGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (!FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
				return false;
			} else if (FoxEntity.this.getTarget() != null || FoxEntity.this.getAttacker() != null) {
				return false;
			} else if (!FoxEntity.this.wantsToPickupItem()) {
				return false;
			} else if (FoxEntity.this.getRandom().nextInt(toGoalTicks(10)) != 0) {
				return false;
			} else {
				List<ItemEntity> list = FoxEntity.this.world
					.getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
				return !list.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = FoxEntity.this.world
				.getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
			ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (itemStack.isEmpty() && !list.isEmpty()) {
				FoxEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = FoxEntity.this.world
				.getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
			if (!list.isEmpty()) {
				FoxEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}
	}

	class SitDownAndLookAroundGoal extends FoxEntity.CalmDownGoal {
		private double lookX;
		private double lookZ;
		private int timer;
		private int counter;

		public SitDownAndLookAroundGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.getAttacker() == null
				&& FoxEntity.this.getRandom().nextFloat() < 0.02F
				&& !FoxEntity.this.isSleeping()
				&& FoxEntity.this.getTarget() == null
				&& FoxEntity.this.getNavigation().isIdle()
				&& !this.canCalmDown()
				&& !FoxEntity.this.isChasing()
				&& !FoxEntity.this.isInSneakingPose();
		}

		@Override
		public boolean shouldContinue() {
			return this.counter > 0;
		}

		@Override
		public void start() {
			this.chooseNewAngle();
			this.counter = 2 + FoxEntity.this.getRandom().nextInt(3);
			FoxEntity.this.setSitting(true);
			FoxEntity.this.getNavigation().stop();
		}

		@Override
		public void stop() {
			FoxEntity.this.setSitting(false);
		}

		@Override
		public void tick() {
			this.timer--;
			if (this.timer <= 0) {
				this.counter--;
				this.chooseNewAngle();
			}

			FoxEntity.this.getLookControl()
				.lookAt(
					FoxEntity.this.getX() + this.lookX,
					FoxEntity.this.getEyeY(),
					FoxEntity.this.getZ() + this.lookZ,
					(float)FoxEntity.this.getMaxHeadRotation(),
					(float)FoxEntity.this.getMaxLookPitchChange()
				);
		}

		private void chooseNewAngle() {
			double d = (Math.PI * 2) * FoxEntity.this.getRandom().nextDouble();
			this.lookX = Math.cos(d);
			this.lookZ = Math.sin(d);
			this.timer = this.getTickCount(80 + FoxEntity.this.getRandom().nextInt(20));
		}
	}

	class StopWanderingGoal extends Goal {
		int timer;

		public StopWanderingGoal() {
			this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.JUMP, Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return FoxEntity.this.isWalking();
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart() && this.timer > 0;
		}

		@Override
		public void start() {
			this.timer = this.getTickCount(40);
		}

		@Override
		public void stop() {
			FoxEntity.this.setWalking(false);
		}

		@Override
		public void tick() {
			this.timer--;
		}
	}

	public static enum Type {
		RED(0, "red"),
		SNOW(1, "snow");

		private static final FoxEntity.Type[] TYPES = (FoxEntity.Type[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(FoxEntity.Type::getId))
			.toArray(FoxEntity.Type[]::new);
		private static final Map<String, FoxEntity.Type> NAME_TYPE_MAP = (Map<String, FoxEntity.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(FoxEntity.Type::getKey, type -> type));
		private final int id;
		private final String key;

		private Type(int id, String key) {
			this.id = id;
			this.key = key;
		}

		public String getKey() {
			return this.key;
		}

		public int getId() {
			return this.id;
		}

		public static FoxEntity.Type byName(String name) {
			return (FoxEntity.Type)NAME_TYPE_MAP.getOrDefault(name, RED);
		}

		public static FoxEntity.Type fromId(int id) {
			if (id < 0 || id > TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		public static FoxEntity.Type fromBiome(RegistryEntry<Biome> registryEntry) {
			return registryEntry.value().getPrecipitation() == Biome.Precipitation.SNOW ? SNOW : RED;
		}
	}

	public class WorriableEntityFilter implements Predicate<LivingEntity> {
		public boolean test(LivingEntity livingEntity) {
			if (livingEntity instanceof FoxEntity) {
				return false;
			} else if (livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity || livingEntity instanceof HostileEntity) {
				return true;
			} else if (livingEntity instanceof TameableEntity) {
				return !((TameableEntity)livingEntity).isTamed();
			} else if (!(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative()) {
				return FoxEntity.this.canTrust(livingEntity.getUuid()) ? false : !livingEntity.isSleeping() && !livingEntity.isSneaky();
			} else {
				return false;
			}
		}
	}
}
