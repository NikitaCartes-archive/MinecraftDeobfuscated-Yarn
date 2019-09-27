package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
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
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieEntity extends HostileEntity {
	protected static final EntityAttribute SPAWN_REINFORCEMENTS = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
		.setName("Spawn Reinforcements Chance");
	private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final EntityAttributeModifier BABY_SPEED_BONUS = new EntityAttributeModifier(
		BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_7427 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
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
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombiePigmanEntity.class));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(35.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23F);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
		this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(2.0);
		this.getAttributes().register(SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * 0.1F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(BABY, false);
		this.getDataTracker().startTracking(field_7427, 0);
		this.getDataTracker().startTracking(CONVERTING_IN_WATER, false);
	}

	public boolean isConvertingInWater() {
		return this.getDataTracker().get(CONVERTING_IN_WATER);
	}

	public boolean canBreakDoors() {
		return this.canBreakDoors;
	}

	public void setCanBreakDoors(boolean bl) {
		if (this.shouldBreakDoors()) {
			if (this.canBreakDoors != bl) {
				this.canBreakDoors = bl;
				((MobNavigation)this.getNavigation()).setCanPathThroughDoors(bl);
				if (bl) {
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
	protected int getCurrentExperience(PlayerEntity playerEntity) {
		if (this.isBaby()) {
			this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
		}

		return super.getCurrentExperience(playerEntity);
	}

	public void setBaby(boolean bl) {
		this.getDataTracker().set(BABY, bl);
		if (this.world != null && !this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
			entityAttributeInstance.removeModifier(BABY_SPEED_BONUS);
			if (bl) {
				entityAttributeInstance.addModifier(BABY_SPEED_BONUS);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (BABY.equals(trackedData)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(trackedData);
	}

	protected boolean canConvertInWater() {
		return true;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.isAlive()) {
			if (this.isConvertingInWater()) {
				this.ticksUntilWaterConversion--;
				if (this.ticksUntilWaterConversion < 0) {
					this.convertInWater();
				}
			} else if (this.canConvertInWater()) {
				if (this.isInFluid(FluidTags.WATER)) {
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
			boolean bl = this.burnsInDaylight() && this.isInDaylight();
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

	private void setTicksUntilWaterConversion(int i) {
		this.ticksUntilWaterConversion = i;
		this.getDataTracker().set(CONVERTING_IN_WATER, true);
	}

	protected void convertInWater() {
		this.convertTo(EntityType.DROWNED);
		this.world.playLevelEvent(null, 1040, new BlockPos(this), 0);
	}

	protected void convertTo(EntityType<? extends ZombieEntity> entityType) {
		if (!this.removed) {
			ZombieEntity zombieEntity = entityType.create(this.world);
			zombieEntity.copyPositionAndRotation(this);
			zombieEntity.setCanPickUpLoot(this.canPickUpLoot());
			zombieEntity.setCanBreakDoors(zombieEntity.shouldBreakDoors() && this.canBreakDoors());
			zombieEntity.applyAttributeModifiers(zombieEntity.world.getLocalDifficulty(new BlockPos(zombieEntity)).getClampedLocalDifficulty());
			zombieEntity.setBaby(this.isBaby());
			zombieEntity.setAiDisabled(this.isAiDisabled());

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack = this.getEquippedStack(equipmentSlot);
				if (!itemStack.isEmpty()) {
					zombieEntity.equipStack(equipmentSlot, itemStack.copy());
					zombieEntity.setEquipmentDropChance(equipmentSlot, this.getDropChance(equipmentSlot));
					itemStack.setCount(0);
				}
			}

			if (this.hasCustomName()) {
				zombieEntity.setCustomName(this.getCustomName());
				zombieEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.spawnEntity(zombieEntity);
			this.remove();
		}
	}

	protected boolean burnsInDaylight() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (super.damage(damageSource, f)) {
			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null && damageSource.getAttacker() instanceof LivingEntity) {
				livingEntity = (LivingEntity)damageSource.getAttacker();
			}

			if (livingEntity != null
				&& this.world.getDifficulty() == Difficulty.HARD
				&& (double)this.random.nextFloat() < this.getAttributeInstance(SPAWN_REINFORCEMENTS).getValue()
				&& this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
				int i = MathHelper.floor(this.x);
				int j = MathHelper.floor(this.y);
				int k = MathHelper.floor(this.z);
				ZombieEntity zombieEntity = new ZombieEntity(this.world);

				for (int l = 0; l < 50; l++) {
					int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					BlockPos blockPos = new BlockPos(m, n - 1, o);
					if (this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, zombieEntity) && this.world.getLightLevel(new BlockPos(m, n, o)) < 10) {
						zombieEntity.setPosition((double)m, (double)n, (double)o);
						if (!this.world.isPlayerInRange((double)m, (double)n, (double)o, 7.0)
							&& this.world.intersectsEntities(zombieEntity)
							&& this.world.doesNotCollide(zombieEntity)
							&& !this.world.containsFluid(zombieEntity.getBoundingBox())) {
							this.world.spawnEntity(zombieEntity);
							zombieEntity.setTarget(livingEntity);
							zombieEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.REINFORCEMENT, null, null);
							this.getAttributeInstance(SPAWN_REINFORCEMENTS)
								.addModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05F, EntityAttributeModifier.Operation.ADDITION));
							zombieEntity.getAttributeInstance(SPAWN_REINFORCEMENTS)
								.addModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05F, EntityAttributeModifier.Operation.ADDITION));
							break;
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean tryAttack(Entity entity) {
		boolean bl = super.tryAttack(entity);
		if (bl) {
			float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
			if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				entity.setOnFireFor(2 * (int)f);
			}
		}

		return bl;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
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
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		super.initEquipment(localDifficulty);
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
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.isBaby()) {
			compoundTag.putBoolean("IsBaby", true);
		}

		compoundTag.putBoolean("CanBreakDoors", this.canBreakDoors());
		compoundTag.putInt("InWaterTime", this.isInsideWater() ? this.inWaterTime : -1);
		compoundTag.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.getBoolean("IsBaby")) {
			this.setBaby(true);
		}

		this.setCanBreakDoors(compoundTag.getBoolean("CanBreakDoors"));
		this.inWaterTime = compoundTag.getInt("InWaterTime");
		if (compoundTag.contains("DrownedConversionTime", 99) && compoundTag.getInt("DrownedConversionTime") > -1) {
			this.setTicksUntilWaterConversion(compoundTag.getInt("DrownedConversionTime"));
		}
	}

	@Override
	public void onKilledOther(LivingEntity livingEntity) {
		super.onKilledOther(livingEntity);
		if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && livingEntity instanceof VillagerEntity) {
			if (this.world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
				return;
			}

			VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
			ZombieVillagerEntity zombieVillagerEntity = EntityType.ZOMBIE_VILLAGER.create(this.world);
			zombieVillagerEntity.copyPositionAndRotation(villagerEntity);
			villagerEntity.remove();
			zombieVillagerEntity.initialize(
				this.world, this.world.getLocalDifficulty(new BlockPos(zombieVillagerEntity)), SpawnType.CONVERSION, new ZombieEntity.Data(false), null
			);
			zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
			zombieVillagerEntity.method_21649(villagerEntity.method_21651().serialize(NbtOps.INSTANCE).getValue());
			zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toTag());
			zombieVillagerEntity.setXp(villagerEntity.getExperience());
			zombieVillagerEntity.setBaby(villagerEntity.isBaby());
			zombieVillagerEntity.setAiDisabled(villagerEntity.isAiDisabled());
			if (villagerEntity.hasCustomName()) {
				zombieVillagerEntity.setCustomName(villagerEntity.getCustomName());
				zombieVillagerEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
			}

			this.world.spawnEntity(zombieVillagerEntity);
			this.world.playLevelEvent(null, 1026, new BlockPos(this), 0);
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	protected boolean canPickupItem(ItemStack itemStack) {
		return itemStack.getItem() == Items.EGG && this.isBaby() && this.hasVehicle() ? false : super.canPickupItem(itemStack);
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		float f = localDifficulty.getClampedLocalDifficulty();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
		if (entityData == null) {
			entityData = new ZombieEntity.Data(iWorld.getRandom().nextFloat() < 0.05F);
		}

		if (entityData instanceof ZombieEntity.Data) {
			ZombieEntity.Data data = (ZombieEntity.Data)entityData;
			if (data.baby) {
				this.setBaby(true);
				if ((double)iWorld.getRandom().nextFloat() < 0.05) {
					List<ChickenEntity> list = iWorld.getEntities(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
					if (!list.isEmpty()) {
						ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
						chickenEntity.setHasJockey(true);
						this.startRiding(chickenEntity);
					}
				} else if ((double)iWorld.getRandom().nextFloat() < 0.05) {
					ChickenEntity chickenEntity2 = EntityType.CHICKEN.create(this.world);
					chickenEntity2.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					chickenEntity2.initialize(iWorld, localDifficulty, SpawnType.JOCKEY, null, null);
					chickenEntity2.setHasJockey(true);
					iWorld.spawnEntity(chickenEntity2);
					this.startRiding(chickenEntity2);
				}
			}

			this.setCanBreakDoors(this.shouldBreakDoors() && this.random.nextFloat() < f * 0.1F);
			this.initEquipment(localDifficulty);
			this.updateEnchantments(localDifficulty);
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

	protected void applyAttributeModifiers(float f) {
		this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE)
			.addModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, EntityAttributeModifier.Operation.ADDITION));
		double d = this.random.nextDouble() * 1.5 * (double)f;
		if (d > 1.0) {
			this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE)
				.addModifier(new EntityAttributeModifier("Random zombie-spawn bonus", d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		}

		if (this.random.nextFloat() < f * 0.05F) {
			this.getAttributeInstance(SPAWN_REINFORCEMENTS)
				.addModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADDITION));
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH)
				.addModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
			this.setCanBreakDoors(this.shouldBreakDoors());
		}
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? 0.0 : -0.45;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				ItemStack itemStack = this.getSkull();
				if (!itemStack.isEmpty()) {
					this.dropStack(itemStack);
				}
			}
		}
	}

	protected ItemStack getSkull() {
		return new ItemStack(Items.ZOMBIE_HEAD);
	}

	public class Data implements EntityData {
		public final boolean baby;

		private Data(boolean bl) {
			this.baby = bl;
		}
	}

	class DestroyEggGoal extends StepAndDestroyBlockGoal {
		DestroyEggGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
			super(Blocks.TURTLE_EGG, mobEntityWithAi, d, i);
		}

		@Override
		public void tickStepping(IWorld iWorld, BlockPos blockPos) {
			iWorld.playSound(null, blockPos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.random.nextFloat() * 0.2F);
		}

		@Override
		public void onDestroyBlock(World world, BlockPos blockPos) {
			world.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		}

		@Override
		public double getDesiredSquaredDistanceToTarget() {
			return 1.14;
		}
	}
}
