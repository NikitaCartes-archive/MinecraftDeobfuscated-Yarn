package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HoglinEntity extends AnimalEntity implements Monster, Hoglin {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(HoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final int MAX_HEALTH = 40;
	private static final float MOVEMENT_SPEED = 0.3F;
	private static final int ATTACK_KNOCKBACK = 1;
	private static final float KNOCKBACK_RESISTANCE = 0.6F;
	private static final int ATTACK_DAMAGE = 6;
	private static final float BABY_ATTACK_DAMAGE = 0.5F;
	public static final int CONVERSION_TIME = 300;
	private int movementCooldownTicks;
	private int timeInOverworld;
	private boolean cannotBeHunted;
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super HoglinEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ADULT, SensorType.HOGLIN_SPECIFIC_SENSOR
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.ATTACK_COOLING_DOWN,
		MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN,
		MemoryModuleType.AVOID_TARGET,
		MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
		MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
		MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
		MemoryModuleType.NEAREST_VISIBLE_ADULT,
		MemoryModuleType.NEAREST_REPELLENT,
		MemoryModuleType.PACIFIED,
		MemoryModuleType.IS_PANICKING
	);

	public HoglinEntity(EntityType<? extends HoglinEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@VisibleForTesting
	public void setTimeInOverworld(int timeInOverworld) {
		this.timeInOverworld = timeInOverworld;
	}

	@Override
	public boolean canBeLeashed() {
		return true;
	}

	public static DefaultAttributeContainer.Builder createHoglinAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.MAX_HEALTH, 40.0)
			.add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.6F)
			.add(EntityAttributes.ATTACK_KNOCKBACK, 1.0)
			.add(EntityAttributes.ATTACK_DAMAGE, 6.0);
	}

	@Override
	public boolean tryAttack(ServerWorld world, Entity target) {
		if (target instanceof LivingEntity livingEntity) {
			this.movementCooldownTicks = 10;
			this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
			this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK);
			HoglinBrain.onAttacking(this, livingEntity);
			return Hoglin.tryAttack(world, this, livingEntity);
		} else {
			return false;
		}
	}

	@Override
	protected void knockback(LivingEntity target) {
		if (this.isAdult()) {
			Hoglin.knockback(this, target);
		}
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		boolean bl = super.damage(world, source, amount);
		if (bl && source.getAttacker() instanceof LivingEntity livingEntity) {
			HoglinBrain.onAttacked(world, this, livingEntity);
		}

		return bl;
	}

	@Override
	protected Brain.Profile<HoglinEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return HoglinBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<HoglinEntity> getBrain() {
		return (Brain<HoglinEntity>)super.getBrain();
	}

	@Override
	protected void mobTick(ServerWorld world) {
		Profiler profiler = Profilers.get();
		profiler.push("hoglinBrain");
		this.getBrain().tick(world, this);
		profiler.pop();
		HoglinBrain.refreshActivities(this);
		if (this.canConvert()) {
			this.timeInOverworld++;
			if (this.timeInOverworld > 300) {
				this.playSound(SoundEvents.ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED);
				this.zombify();
			}
		} else {
			this.timeInOverworld = 0;
		}
	}

	@Override
	public void tickMovement() {
		if (this.movementCooldownTicks > 0) {
			this.movementCooldownTicks--;
		}

		super.tickMovement();
	}

	@Override
	protected void onGrowUp() {
		if (this.isBaby()) {
			this.experiencePoints = 3;
			this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(0.5);
		} else {
			this.experiencePoints = 5;
			this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
		}
	}

	public static boolean canSpawn(EntityType<HoglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		if (world.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isPersistent();
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		if (HoglinBrain.isWarpedFungusAround(this, pos)) {
			return -1.0F;
		} else {
			return world.getBlockState(pos.down()).isOf(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F;
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ActionResult actionResult = super.interactMob(player, hand);
		if (actionResult.isAccepted()) {
			this.setPersistent();
		}

		return actionResult;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.movementCooldownTicks = 10;
			this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public int getMovementCooldownTicks() {
		return this.movementCooldownTicks;
	}

	@Override
	public boolean shouldDropXp() {
		return true;
	}

	@Override
	protected int getXpToDrop(ServerWorld world) {
		return this.experiencePoints;
	}

	private void zombify() {
		this.convertTo(
			EntityType.ZOGLIN,
			EntityConversionContext.create(this, true, false),
			zoglin -> zoglin.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0))
		);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.HOGLIN_FOOD);
	}

	public boolean isAdult() {
		return !this.isBaby();
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(BABY, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isImmuneToZombification()) {
			nbt.putBoolean("IsImmuneToZombification", true);
		}

		nbt.putInt("TimeInOverworld", this.timeInOverworld);
		if (this.cannotBeHunted) {
			nbt.putBoolean("CannotBeHunted", true);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setImmuneToZombification(nbt.getBoolean("IsImmuneToZombification"));
		this.timeInOverworld = nbt.getInt("TimeInOverworld");
		this.setCannotBeHunted(nbt.getBoolean("CannotBeHunted"));
	}

	public void setImmuneToZombification(boolean immuneToZombification) {
		this.getDataTracker().set(BABY, immuneToZombification);
	}

	private boolean isImmuneToZombification() {
		return this.getDataTracker().get(BABY);
	}

	public boolean canConvert() {
		return !this.getWorld().getDimension().piglinSafe() && !this.isImmuneToZombification() && !this.isAiDisabled();
	}

	private void setCannotBeHunted(boolean cannotBeHunted) {
		this.cannotBeHunted = cannotBeHunted;
	}

	public boolean canBeHunted() {
		return this.isAdult() && !this.cannotBeHunted;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		HoglinEntity hoglinEntity = EntityType.HOGLIN.create(world, SpawnReason.BREEDING);
		if (hoglinEntity != null) {
			hoglinEntity.setPersistent();
		}

		return hoglinEntity;
	}

	@Override
	public boolean canEat() {
		return !HoglinBrain.isNearPlayer(this) && super.canEat();
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.getWorld().isClient ? null : (SoundEvent)HoglinBrain.getSoundEvent(this).orElse(null);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_HOGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_HOGLIN_DEATH;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_HOGLIN_STEP, 0.15F, 1.0F);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return this.getTargetInBrain();
	}
}
