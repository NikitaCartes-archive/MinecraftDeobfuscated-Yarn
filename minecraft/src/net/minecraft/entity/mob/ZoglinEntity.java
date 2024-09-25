package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class ZoglinEntity extends HostileEntity implements Hoglin {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final int field_30514 = 40;
	private static final int field_30505 = 1;
	private static final float DEFAULT_KNOCKBACK_RESISTANCE = 0.6F;
	private static final int DEFAULT_ATTACK_DAMAGE = 6;
	private static final float BABY_ATTACK_DAMAGE = 0.5F;
	private static final int ADULT_MELEE_ATTACK_COOLDOWN = 40;
	private static final int BABY_MELEE_ATTACK_COOLDOWN = 15;
	private static final int ATTACK_TARGET_DURATION = 200;
	private static final float DEFAULT_MOVEMENT_SPEED = 0.3F;
	private static final float field_30513 = 0.4F;
	private int movementCooldownTicks;
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super ZoglinEntity>>> USED_SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> USED_MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.ATTACK_COOLING_DOWN
	);

	public ZoglinEntity(EntityType<? extends ZoglinEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	protected Brain.Profile<ZoglinEntity> createBrainProfile() {
		return Brain.createProfile(USED_MEMORY_MODULES, USED_SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		Brain<ZoglinEntity> brain = this.createBrainProfile().deserialize(dynamic);
		addCoreTasks(brain);
		addIdleTasks(brain);
		addFightTasks(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreTasks(Brain<ZoglinEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new MoveToTargetTask()));
	}

	private static void addIdleTasks(Brain<ZoglinEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				UpdateAttackTargetTask.create((world, target) -> target.getHoglinTarget(world)),
				LookAtMobWithIntervalTask.follow(8.0F, UniformIntProvider.create(30, 60)),
				new RandomTask<>(
					ImmutableList.of(Pair.of(StrollTask.create(0.4F), 2), Pair.of(GoTowardsLookTargetTask.create(0.4F, 3), 2), Pair.of(new WaitTask(30, 60), 1))
				)
			)
		);
	}

	private static void addFightTasks(Brain<ZoglinEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				RangedApproachTask.create(1.0F),
				TaskTriggerer.runIf(ZoglinEntity::isAdult, MeleeAttackTask.create(40)),
				TaskTriggerer.runIf(ZoglinEntity::isBaby, MeleeAttackTask.create(15)),
				ForgetAttackTargetTask.create()
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private Optional<? extends LivingEntity> getHoglinTarget(ServerWorld world) {
		return ((LivingTargetCache)this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty()))
			.findFirst(target -> this.shouldAttack(world, target));
	}

	private boolean shouldAttack(ServerWorld world, LivingEntity target) {
		EntityType<?> entityType = target.getType();
		return entityType != EntityType.ZOGLIN && entityType != EntityType.CREEPER && Sensor.testAttackableTargetPredicate(world, this, target);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(BABY, false);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (BABY.equals(data)) {
			this.calculateDimensions();
		}
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		if (world.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	public static DefaultAttributeContainer.Builder createZoglinAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.MAX_HEALTH, 40.0)
			.add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.6F)
			.add(EntityAttributes.ATTACK_KNOCKBACK, 1.0)
			.add(EntityAttributes.ATTACK_DAMAGE, 6.0);
	}

	public boolean isAdult() {
		return !this.isBaby();
	}

	@Override
	public boolean tryAttack(ServerWorld world, Entity target) {
		if (target instanceof LivingEntity livingEntity) {
			this.movementCooldownTicks = 10;
			world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
			this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK);
			return Hoglin.tryAttack(world, this, livingEntity);
		} else {
			return false;
		}
	}

	@Override
	public boolean canBeLeashed() {
		return true;
	}

	@Override
	protected void knockback(LivingEntity target) {
		if (!this.isBaby()) {
			Hoglin.knockback(this, target);
		}
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		boolean bl = super.damage(world, source, amount);
		if (bl && source.getAttacker() instanceof LivingEntity livingEntity) {
			if (this.canTarget(livingEntity) && !LookTargetUtil.isNewTargetTooFar(this, livingEntity, 4.0)) {
				this.setAttackTarget(livingEntity);
			}

			return true;
		} else {
			return bl;
		}
	}

	private void setAttackTarget(LivingEntity entity) {
		this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		this.brain.remember(MemoryModuleType.ATTACK_TARGET, entity, 200L);
	}

	@Override
	public Brain<ZoglinEntity> getBrain() {
		return (Brain<ZoglinEntity>)super.getBrain();
	}

	protected void tickBrain() {
		Activity activity = (Activity)this.brain.getFirstPossibleNonCoreActivity().orElse(null);
		this.brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
		Activity activity2 = (Activity)this.brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity2 == Activity.FIGHT && activity != Activity.FIGHT) {
			this.playAngrySound();
		}

		this.setAttacking(this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
	}

	@Override
	protected void mobTick(ServerWorld world) {
		Profiler profiler = Profilers.get();
		profiler.push("zoglinBrain");
		this.getBrain().tick(world, this);
		profiler.pop();
		this.tickBrain();
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		if (!this.getWorld().isClient && baby) {
			this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(0.5);
		}
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(BABY);
	}

	@Override
	public void tickMovement() {
		if (this.movementCooldownTicks > 0) {
			this.movementCooldownTicks--;
		}

		super.tickMovement();
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.movementCooldownTicks = 10;
			this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public int getMovementCooldownTicks() {
		return this.movementCooldownTicks;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if (this.getWorld().isClient) {
			return null;
		} else {
			return this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET) ? SoundEvents.ENTITY_ZOGLIN_ANGRY : SoundEvents.ENTITY_ZOGLIN_AMBIENT;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOGLIN_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_ZOGLIN_STEP, 0.15F, 1.0F);
	}

	protected void playAngrySound() {
		this.playSound(SoundEvents.ENTITY_ZOGLIN_ANGRY);
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return this.getTargetInBrain();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isBaby()) {
			nbt.putBoolean("IsBaby", true);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.getBoolean("IsBaby")) {
			this.setBaby(true);
		}
	}
}
