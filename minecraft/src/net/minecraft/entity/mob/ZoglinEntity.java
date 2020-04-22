package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.world.World;

public class ZoglinEntity extends HostileEntity implements Monster, Hoglin {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int movementCooldownTicks;
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super ZoglinEntity>>> field_23731 = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> field_23733 = ImmutableList.of(
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
	protected Brain<?> deserializeBrain(Dynamic<?> data) {
		Brain<ZoglinEntity> brain = new Brain<>(
			(Collection<MemoryModuleType<?>>)field_23733, (Collection<SensorType<? extends Sensor<? super ZoglinEntity>>>)field_23731, data
		);
		method_26928(brain);
		method_26929(brain);
		method_26930(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void method_26928(Brain<ZoglinEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(200)));
	}

	private static void method_26929(Brain<ZoglinEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				new UpdateAttackTargetTask<>(ZoglinEntity::method_26934),
				new TimeLimitedTask(new FollowMobTask(8.0F), IntRange.between(30, 60)),
				new RandomTask(ImmutableList.of(Pair.of(new StrollTask(0.4F), 2), Pair.of(new GoTowardsLookTarget(0.4F, 3), 2), Pair.of(new WaitTask(30, 60), 1)))
			)
		);
	}

	private static void method_26930(Brain<ZoglinEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				new RangedApproachTask(1.0F),
				new ConditionalTask<>(ZoglinEntity::isAdult, new MeleeAttackTask(40)),
				new ConditionalTask<>(ZoglinEntity::isBaby, new MeleeAttackTask(15)),
				new ForgetAttackTargetTask()
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private Optional<? extends LivingEntity> method_26934() {
		return ((List)this.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of()))
			.stream()
			.filter(ZoglinEntity::method_26936)
			.findFirst();
	}

	private static boolean method_26936(LivingEntity livingEntity) {
		EntityType<?> entityType = livingEntity.getType();
		return entityType != EntityType.ZOGLIN && entityType != EntityType.CREEPER && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BABY, false);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (BABY.equals(data)) {
			this.calculateDimensions();
		}
	}

	public static DefaultAttributeContainer.Builder createZoglinAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	public boolean isAdult() {
		return !this.isBaby();
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (!(target instanceof LivingEntity)) {
			return false;
		} else {
			this.movementCooldownTicks = 10;
			this.world.sendEntityStatus(this, (byte)4);
			this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0F, this.getSoundPitch());
			return Hoglin.tryAttack(this, (LivingEntity)target);
		}
	}

	@Override
	protected void knockback(LivingEntity target) {
		if (!this.isBaby()) {
			Hoglin.knockback(this, target);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else if (bl && source.getAttacker() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)source.getAttacker();
			if (EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity) && !LookTargetUtil.isNewTargetTooFar(this, livingEntity, 4.0)) {
				this.method_26938(livingEntity);
			}

			return bl;
		} else {
			return bl;
		}
	}

	private void method_26938(LivingEntity livingEntity) {
		this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		this.brain.remember(MemoryModuleType.ATTACK_TARGET, livingEntity, 200L);
	}

	@Override
	public Brain<ZoglinEntity> getBrain() {
		return (Brain<ZoglinEntity>)super.getBrain();
	}

	protected void method_26931() {
		Activity activity = (Activity)this.brain.getFirstPossibleNonCoreActivity().orElse(null);
		this.brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
		Activity activity2 = (Activity)this.brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			this.method_26935();
		}

		this.setAttacking(this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("zoglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.method_26931();
		this.method_26932();
	}

	private void method_26935() {
		if (this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
			this.playAngrySound();
		}
	}

	protected void method_26932() {
		if ((double)this.random.nextFloat() < 0.0125) {
			this.method_26935();
		}
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
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

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 4) {
			this.movementCooldownTicks = 10;
			this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0F, this.getSoundPitch());
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getMovementCooldownTicks() {
		return this.movementCooldownTicks;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOGLIN_AMBIENT;
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
		this.playSound(SoundEvents.ENTITY_ZOGLIN_ANGRY, 1.0F, this.getSoundPitch());
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}
}
