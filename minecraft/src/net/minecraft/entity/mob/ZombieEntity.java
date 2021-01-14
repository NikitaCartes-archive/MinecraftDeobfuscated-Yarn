package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.StepAndDestroyBlockGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ZombieEntity extends HostileEntity {
	private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final EntityAttributeModifier BABY_SPEED_BONUS = new EntityAttributeModifier(
		BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	/**
	 * Unused tracked data, left over from 1.10 when zombies, zombie villagers and husks were all the same type of entity.
	 */
	private static final TrackedData<Integer> ZOMBIE_TYPE = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CONVERTING_IN_WATER = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = difficulty -> difficulty == Difficulty.HARD;
	private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER);
	private boolean canBreakDoors;
	private int inWaterTime;
	private int ticksUntilWaterConversion;

	public ZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
		super(entityType, world);
	}

	public ZombieEntity(World world) {
		this(EntityType.ZOMBIE, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(4, new ZombieEntity.DestroyEggGoal(this, 1.0, 3));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
		this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, MerchantEntity.class, false));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	public static DefaultAttributeContainer.Builder createZombieAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
			.add(EntityAttributes.GENERIC_ARMOR, 2.0)
			.add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(BABY, false);
		this.getDataTracker().startTracking(ZOMBIE_TYPE, 0);
		this.getDataTracker().startTracking(CONVERTING_IN_WATER, false);
	}

	public boolean isConvertingInWater() {
		return this.getDataTracker().get(CONVERTING_IN_WATER);
	}

	public boolean canBreakDoors() {
		return this.canBreakDoors;
	}

	public void setCanBreakDoors(boolean canBreakDoors) {
		if (this.shouldBreakDoors() && NavigationConditions.hasMobNavigation(this)) {
			if (this.canBreakDoors != canBreakDoors) {
				this.canBreakDoors = canBreakDoors;
				((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
				if (canBreakDoors) {
					this.goalSelector.add(1, this.breakDoorsGoal);
				} else {
					this.goalSelector.remove(this.breakDoorsGoal);
				}
			}
		} else if (this.canBreakDoors) {
			this.goalSelector.remove(this.breakDoorsGoal);
			this.canBreakDoors = false;
		}
	}

	protected boolean shouldBreakDoors() {
		return true;
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(BABY);
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.isBaby()) {
			this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
		}

		return super.getXpToDrop(player);
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		if (this.world != null && !this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.removeModifier(BABY_SPEED_BONUS);
			if (baby) {
				entityAttributeInstance.addTemporaryModifier(BABY_SPEED_BONUS);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (BABY.equals(data)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
	}

	protected boolean canConvertInWater() {
		return true;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.isAlive() && !this.isAiDisabled()) {
			if (this.isConvertingInWater()) {
				this.ticksUntilWaterConversion--;
				if (this.ticksUntilWaterConversion < 0) {
					this.convertInWater();
				}
			} else if (this.canConvertInWater()) {
				if (this.isSubmergedIn(FluidTags.WATER)) {
					this.inWaterTime++;
					if (this.inWaterTime >= 600) {
						this.setTicksUntilWaterConversion(300);
					}
				} else {
					this.inWaterTime = -1;
				}
			}
		}

		super.tick();
	}

	@Override
	public void tickMovement() {
		if (this.isAlive()) {
			boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight();
			if (bl) {
				ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
				if (!itemStack.isEmpty()) {
					if (itemStack.isDamageable()) {
						itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
						if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
							this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
							this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
						}
					}

					bl = false;
				}

				if (bl) {
					this.setOnFireFor(8);
				}
			}
		}

		super.tickMovement();
	}

	private void setTicksUntilWaterConversion(int ticksUntilWaterConversion) {
		this.ticksUntilWaterConversion = ticksUntilWaterConversion;
		this.getDataTracker().set(CONVERTING_IN_WATER, true);
	}

	protected void convertInWater() {
		this.convertTo(EntityType.DROWNED);
		if (!this.isSilent()) {
			this.world.syncWorldEvent(null, 1040, this.getBlockPos(), 0);
		}
	}

	protected void convertTo(EntityType<? extends ZombieEntity> entityType) {
		ZombieEntity zombieEntity = this.method_29243(entityType, true);
		if (zombieEntity != null) {
			zombieEntity.applyAttributeModifiers(zombieEntity.world.getLocalDifficulty(zombieEntity.getBlockPos()).getClampedLocalDifficulty());
			zombieEntity.setCanBreakDoors(zombieEntity.shouldBreakDoors() && this.canBreakDoors());
		}
	}

	protected boolean burnsInDaylight() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (!super.damage(source, amount)) {
			return false;
		} else if (!(this.world instanceof ServerWorld)) {
			return false;
		} else {
			ServerWorld serverWorld = (ServerWorld)this.world;
			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
				livingEntity = (LivingEntity)source.getAttacker();
			}

			if (livingEntity != null
				&& this.world.getDifficulty() == Difficulty.HARD
				&& (double)this.random.nextFloat() < this.getAttributeValue(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
				&& this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
				int i = MathHelper.floor(this.getX());
				int j = MathHelper.floor(this.getY());
				int k = MathHelper.floor(this.getZ());
				ZombieEntity zombieEntity = new ZombieEntity(this.world);

				for (int l = 0; l < 50; l++) {
					int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					BlockPos blockPos = new BlockPos(m, n, o);
					EntityType<?> entityType = zombieEntity.getType();
					SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
					if (SpawnHelper.canSpawn(location, this.world, blockPos, entityType)
						&& SpawnRestriction.canSpawn(entityType, serverWorld, SpawnReason.REINFORCEMENT, blockPos, this.world.random)) {
						zombieEntity.setPosition((double)m, (double)n, (double)o);
						if (!this.world.isPlayerInRange((double)m, (double)n, (double)o, 7.0)
							&& this.world.intersectsEntities(zombieEntity)
							&& this.world.isSpaceEmpty(zombieEntity)
							&& !this.world.containsFluid(zombieEntity.getBoundingBox())) {
							zombieEntity.setTarget(livingEntity);
							zombieEntity.initialize(serverWorld, this.world.getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.REINFORCEMENT, null, null);
							serverWorld.spawnEntityAndPassengers(zombieEntity);
							this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
								.addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05F, EntityAttributeModifier.Operation.ADDITION));
							zombieEntity.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
								.addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05F, EntityAttributeModifier.Operation.ADDITION));
							break;
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			float f = this.world.getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
			if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				target.setOnFireFor(2 * (int)f);
			}
		}

		return bl;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOMBIE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_DEATH;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		if (this.random.nextFloat() < (this.world.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.random.nextInt(3);
			if (i == 0) {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			} else {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsBaby", this.isBaby());
		nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
		nbt.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
		nbt.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setBaby(nbt.getBoolean("IsBaby"));
		this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));
		this.inWaterTime = nbt.getInt("InWaterTime");
		if (nbt.contains("DrownedConversionTime", 99) && nbt.getInt("DrownedConversionTime") > -1) {
			this.setTicksUntilWaterConversion(nbt.getInt("DrownedConversionTime"));
		}
	}

	@Override
	public void onKilledOther(ServerWorld world, LivingEntity other) {
		super.onKilledOther(world, other);
		if ((world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity) {
			if (world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
				return;
			}

			VillagerEntity villagerEntity = (VillagerEntity)other;
			ZombieVillagerEntity zombieVillagerEntity = villagerEntity.method_29243(EntityType.ZOMBIE_VILLAGER, false);
			zombieVillagerEntity.initialize(
				world, world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null
			);
			zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
			zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
			zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
			zombieVillagerEntity.setXp(villagerEntity.getExperience());
			if (!this.isSilent()) {
				world.syncWorldEvent(null, 1026, this.getBlockPos(), 0);
			}
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	public boolean canPickupItem(ItemStack stack) {
		return stack.getItem() == Items.EGG && this.isBaby() && this.hasVehicle() ? false : super.canPickupItem(stack);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
		float f = difficulty.getClampedLocalDifficulty();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
		if (entityData == null) {
			entityData = new ZombieEntity.ZombieData(method_29936(world.getRandom()), true);
		}

		if (entityData instanceof ZombieEntity.ZombieData) {
			ZombieEntity.ZombieData zombieData = (ZombieEntity.ZombieData)entityData;
			if (zombieData.baby) {
				this.setBaby(true);
				if (zombieData.field_25607) {
					if ((double)world.getRandom().nextFloat() < 0.05) {
						List<ChickenEntity> list = world.getEntitiesByClass(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
						if (!list.isEmpty()) {
							ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
							chickenEntity.setHasJockey(true);
							this.startRiding(chickenEntity);
						}
					} else if ((double)world.getRandom().nextFloat() < 0.05) {
						ChickenEntity chickenEntity2 = EntityType.CHICKEN.create(this.world);
						chickenEntity2.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0F);
						chickenEntity2.initialize(world, difficulty, SpawnReason.JOCKEY, null, null);
						chickenEntity2.setHasJockey(true);
						this.startRiding(chickenEntity2);
						world.spawnEntity(chickenEntity2);
					}
				}
			}

			this.setCanBreakDoors(this.shouldBreakDoors() && this.random.nextFloat() < f * 0.1F);
			this.initEquipment(difficulty);
			this.updateEnchantments(difficulty);
		}

		if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
				this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
			}
		}

		this.applyAttributeModifiers(f);
		return entityData;
	}

	public static boolean method_29936(Random random) {
		return random.nextFloat() < 0.05F;
	}

	protected void applyAttributeModifiers(float chanceMultiplier) {
		this.initAttributes();
		this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
			.addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, EntityAttributeModifier.Operation.ADDITION));
		double d = this.random.nextDouble() * 1.5 * (double)chanceMultiplier;
		if (d > 1.0) {
			this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)
				.addPersistentModifier(new EntityAttributeModifier("Random zombie-spawn bonus", d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		}

		if (this.random.nextFloat() < chanceMultiplier * 0.05F) {
			this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
				.addPersistentModifier(
					new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADDITION)
				);
			this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
				.addPersistentModifier(
					new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
				);
			this.setCanBreakDoors(this.shouldBreakDoors());
		}
	}

	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * 0.1F);
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? 0.0 : -0.45;
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		Entity entity = source.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.shouldDropHead()) {
				ItemStack itemStack = this.getSkull();
				if (!itemStack.isEmpty()) {
					creeperEntity.onHeadDropped();
					this.dropStack(itemStack);
				}
			}
		}
	}

	/**
	 * Returns the item stack this entity will drop when killed by a charged creeper.
	 */
	protected ItemStack getSkull() {
		return new ItemStack(Items.ZOMBIE_HEAD);
	}

	class DestroyEggGoal extends StepAndDestroyBlockGoal {
		DestroyEggGoal(PathAwareEntity mob, double speed, int maxYDifference) {
			super(Blocks.TURTLE_EGG, mob, speed, maxYDifference);
		}

		@Override
		public void tickStepping(WorldAccess world, BlockPos pos) {
			world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.random.nextFloat() * 0.2F);
		}

		@Override
		public void onDestroyBlock(World world, BlockPos pos) {
			world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		}

		@Override
		public double getDesiredSquaredDistanceToTarget() {
			return 1.14;
		}
	}

	public static class ZombieData implements EntityData {
		public final boolean baby;
		public final boolean field_25607;

		public ZombieData(boolean baby, boolean bl) {
			this.baby = baby;
			this.field_25607 = bl;
		}
	}
}
