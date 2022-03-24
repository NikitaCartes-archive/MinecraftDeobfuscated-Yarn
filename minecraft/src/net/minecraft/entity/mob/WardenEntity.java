package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.slf4j.Logger;

public class WardenEntity extends HostileEntity implements SculkSensorListener.Callback {
	private static final Logger field_38138 = LogUtils.getLogger();
	private static final int field_38139 = 16;
	private static final int field_38142 = 40;
	private static final int field_38143 = 500;
	private static final float field_38144 = 0.3F;
	private static final float field_38145 = 1.0F;
	private static final float field_38146 = 1.5F;
	private static final int field_38147 = 30;
	private static final TrackedData<Integer> ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final int field_38149 = 200;
	private static final int field_38150 = 260;
	private static final int field_38151 = 20;
	private static final int field_38152 = 120;
	private static final int field_38153 = 20;
	private static final int field_38154 = 70;
	private static final int field_38155 = 35;
	private static final int field_38156 = 20;
	private static final int field_38157 = 100;
	private static final int field_38158 = 20;
	protected static final List<SensorType<? extends Sensor<? super WardenEntity>>> SENSORS = List.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.WARDEN_ENTITY_SENSOR
	);
	protected static final List<MemoryModuleType<?>> MEMORY_MODULES = List.of(
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.ATTACK_COOLING_DOWN,
		MemoryModuleType.NEAREST_ATTACKABLE,
		MemoryModuleType.ROAR_TARGET,
		MemoryModuleType.DISTURBANCE_LOCATION,
		MemoryModuleType.RECENT_PROJECTILE,
		MemoryModuleType.IS_SNIFFING,
		MemoryModuleType.IS_EMERGING,
		MemoryModuleType.ROAR_SOUND_DELAY,
		MemoryModuleType.DIG_COOLDOWN,
		MemoryModuleType.ROAR_SOUND_COOLDOWN,
		MemoryModuleType.SNIFF_COOLDOWN,
		MemoryModuleType.TOUCH_COOLDOWN,
		MemoryModuleType.VIBRATION_COOLDOWN
	);
	private static final int field_38159 = 30;
	private static final float field_38160 = 4.5F;
	private static final float field_38161 = 0.7F;
	private float field_38162;
	private float field_38163;
	private float field_38164;
	private float field_38165;
	public AnimationState roaringAnimationState = new AnimationState();
	public AnimationState sniffingAnimationState = new AnimationState();
	public AnimationState emergingAnimationState = new AnimationState();
	public AnimationState diggingAnimationState = new AnimationState();
	public AnimationState attackingAnimationState = new AnimationState();
	private final EntityGameEventHandler gameEventHandler;
	private final SculkSensorListener vibrationListener;
	private WardenAngerManager angerManager = new WardenAngerManager(Collections.emptyMap());

	public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.vibrationListener = new SculkSensorListener(new EntityPositionSource(this, this.getStandingEyeHeight()), 16, this, null, 0, 0);
		this.gameEventHandler = new EntityGameEventHandler(this.vibrationListener);
		this.experiencePoints = 5;
		this.getNavigation().setCanSwim(true);
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.isSpaceEmpty(this);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return 0.0F;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.isInPose(EntityPose.DIGGING) || this.isInPose(EntityPose.EMERGING) || super.isInvulnerableTo(damageSource);
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean disablesShield() {
		return true;
	}

	@Override
	protected float calculateNextStepSoundDistance() {
		return this.distanceTraveled + 0.55F;
	}

	@Override
	public double getSwimHeight() {
		return (double)this.getStandingEyeHeight() * 0.9;
	}

	@Override
	protected void swimUpward(TagKey<Fluid> fluid) {
		this.setVelocity(this.getVelocity().add(0.0, 0.4, 0.0));
	}

	@Override
	protected float getBaseMovementSpeedMultiplier() {
		return 0.98F;
	}

	@Override
	public boolean isPushedByFluids() {
		return false;
	}

	public static DefaultAttributeContainer.Builder addAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0);
	}

	@Override
	public boolean occludeVibrationSignals() {
		return true;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected float getSoundVolume() {
		return 4.0F;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return this.isInPose(EntityPose.ROARING) ? null : this.getAngriness().getSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WARDEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WARDEN_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 10.0F, 1.0F);
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.ENTITY_WARDEN_ATTACK_IMPACT, 10.0F, this.getSoundPitch());
		return super.tryAttack(target);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ANGER, 0);
	}

	public int getAnger() {
		return this.dataTracker.get(ANGER);
	}

	private void updateAnger() {
		this.dataTracker.set(ANGER, this.angerManager.getPrimeSuspectAnger());
	}

	@Override
	public void tick() {
		if (this.world instanceof ServerWorld serverWorld) {
			this.vibrationListener.tick(serverWorld);
			if (this.hasCustomName()) {
				WardenBrain.resetDigCooldown(this);
			}
		}

		super.tick();
		if (this.world.isClient()) {
			if (this.age % this.getHeartRate() == 0) {
				this.field_38164 = 0.0F;
				this.world
					.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, this.getSoundCategory(), 5.0F, this.getSoundPitch(), false);
			}

			this.field_38163 = this.field_38162;
			if (this.field_38162 < 1.0F) {
				this.field_38162 += 0.1F;
			}

			this.field_38165 = this.field_38164;
			if (this.field_38164 < 1.0F) {
				this.field_38164 += 0.1F;
			}

			switch (this.getPose()) {
				case EMERGING:
					this.addDigParticles(this.emergingAnimationState);
					break;
				case DIGGING:
					this.addDigParticles(this.diggingAnimationState);
			}
		}
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("wardenBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		super.mobTick();
		if ((this.age + this.getId()) % 120 == 0) {
			addDarknessToClosePlayers((ServerWorld)this.world, this.getPos(), this, 20);
		}

		if (this.age % 20 == 0) {
			int i = this.angerManager.getPrimeSuspectAnger();
			this.angerManager.tick();
			this.playListeningSound(i, this.angerManager.getPrimeSuspectAnger());
		}

		this.updateAnger();
		WardenBrain.tick(this);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.attackingAnimationState.start();
		} else if (status == EntityStatuses.EARS_TWITCH) {
			this.field_38162 = 0.0F;
		} else {
			super.handleStatus(status);
		}
	}

	private int getHeartRate() {
		float f = (float)this.getAnger() / (float)Angriness.ANGRY.getThreshold();
		return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0F, 1.0F) * 30.0F);
	}

	public float getEarPitch(float tickDelta) {
		return Math.max(1.0F - MathHelper.lerp(tickDelta, this.field_38163, this.field_38162), 0.0F);
	}

	public float getHeartPitch(float tickDelta) {
		return Math.max(1.0F - MathHelper.lerp(tickDelta, this.field_38165, this.field_38164), 0.0F);
	}

	private void addDigParticles(AnimationState animationState) {
		if ((float)(Util.getMeasuringTimeMs() - animationState.getStartTime()) < 4500.0F) {
			Random random = this.getRandom();
			BlockState blockState = this.world.getBlockState(this.getBlockPos().down());

			for (int i = 0; i < 30; i++) {
				double d = this.getX() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
				double e = this.getY();
				double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
				this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (POSE.equals(data)) {
			switch (this.getPose()) {
				case EMERGING:
					this.emergingAnimationState.start();
					break;
				case DIGGING:
					this.diggingAnimationState.start();
					break;
				case ROARING:
					this.roaringAnimationState.start();
					break;
				case SNIFFING:
					this.sniffingAnimationState.start();
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected Brain.Profile<WardenEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return WardenBrain.create(this, this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<WardenEntity> getBrain() {
		return (Brain<WardenEntity>)super.getBrain();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public void updateEventHandler(BiConsumer<EntityGameEventHandler, ServerWorld> biConsumer) {
		if (this.world instanceof ServerWorld serverWorld) {
			biConsumer.accept(this.gameEventHandler, serverWorld);
		}
	}

	@Override
	public TagKey<GameEvent> getTag() {
		return GameEventTags.WARDEN_EVENTS_CAN_LISTEN;
	}

	public static boolean isValidTarget(@Nullable Entity entity) {
		if (entity instanceof LivingEntity livingEntity
			&& EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity)
			&& livingEntity.getType() != EntityType.ARMOR_STAND
			&& livingEntity.getType() != EntityType.WARDEN
			&& !livingEntity.isDead()) {
			return true;
		}

		return false;
	}

	public static void addDarknessToClosePlayers(ServerWorld world, Vec3d pos, @Nullable Entity entity, int range) {
		StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.DARKNESS, 260, 0, false, false);
		StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, pos, (double)range, statusEffectInstance, 200);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		WardenAngerManager.CODEC
			.encodeStart(NbtOps.INSTANCE, this.angerManager)
			.resultOrPartial(field_38138::error)
			.ifPresent(angerNbt -> nbt.put("anger", angerNbt));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("anger")) {
			WardenAngerManager.CODEC
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("anger")))
				.resultOrPartial(field_38138::error)
				.ifPresent(angerManager -> this.angerManager = angerManager);
			this.updateAnger();
		}
	}

	private void playListeningSound(int prevAnger, int anger) {
		if (prevAnger >= 70 && anger < 70) {
			this.playListeningSound();
		}
	}

	private void playListeningSound() {
		if (!this.isInPose(EntityPose.ROARING)) {
			SoundEvent soundEvent = this.getAngriness() == Angriness.CALM ? SoundEvents.ENTITY_WARDEN_LISTENING : SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY;
			this.playSound(soundEvent, 10.0F, this.getSoundPitch());
		}
	}

	public Angriness getAngriness() {
		return Angriness.getForAnger(this.angerManager.getPrimeSuspectAnger());
	}

	public void removeSuspect(Entity entity) {
		this.angerManager.removeSuspect(entity);
	}

	public void increaseAngerAt(@Nullable Entity entity) {
		this.increaseAngerAt(entity, entity instanceof ProjectileEntity ? 20 : 35);
	}

	@VisibleForTesting
	public void increaseAngerAt(@Nullable Entity entity, int amount) {
		if (isValidTarget(entity)) {
			WardenBrain.resetDigCooldown(this);
			boolean bl = this.getPrimeSuspect().filter(entityx -> !(entityx instanceof PlayerEntity)).isPresent();
			int i = this.angerManager.increaseAngerAt(entity, amount);
			if (entity instanceof PlayerEntity playerEntity && bl && Angriness.getForAnger(i) == Angriness.ANGRY) {
				this.getBrain().remember(MemoryModuleType.ATTACK_TARGET, playerEntity);
			}

			this.playListeningSound();
		}
	}

	public Optional<LivingEntity> getPrimeSuspect() {
		return this.getAngriness() == Angriness.ANGRY ? this.angerManager.getPrimeSuspect(this.world) : Optional.empty();
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.getBrain().remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public double squaredAttackRange(LivingEntity target) {
		return 8.0;
	}

	@Override
	public boolean isInAttackRange(LivingEntity entity) {
		double d = this.squaredDistanceTo(entity.getX(), entity.getY() - (double)(this.getHeight() / 2.0F), entity.getZ());
		return d <= this.squaredAttackRange(entity);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl) {
				Entity entity = source.getAttacker();
				this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20);
				if (this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
					&& entity instanceof LivingEntity livingEntity
					&& (!(source instanceof ProjectileDamageSource) || this.isInRange(livingEntity, 5.0))) {
					this.brain.remember(MemoryModuleType.ATTACK_TARGET, livingEntity);
				}
			}

			return bl;
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.getBrain().hasMemoryModule(MemoryModuleType.TOUCH_COOLDOWN)) {
			this.getBrain().remember(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
			this.increaseAngerAt(player);
			WardenBrain.lookAtDisturbance(this, player.getBlockPos());
		}
	}

	@Override
	public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
		if (this.getBrain().getOptionalMemory(MemoryModuleType.VIBRATION_COOLDOWN).isPresent()) {
			return false;
		} else {
			EntityPose entityPose = this.getPose();
			return entityPose != EntityPose.DIGGING && entityPose != EntityPose.EMERGING ? !(entity instanceof LivingEntity) || isValidTarget(entity) : false;
		}
	}

	@Override
	public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, int delay) {
		this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
		world.sendEntityStatus(this, EntityStatuses.EARS_TWITCH);
		this.playSound(SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, this.getSoundPitch());
		BlockPos blockPos = pos;
		if (entity instanceof ProjectileEntity projectileEntity) {
			if (this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
				Entity entity2 = projectileEntity.getOwner();
				if (isValidTarget(entity2) && this.isInRange(entity2, 30.0)) {
					blockPos = entity2.getBlockPos();
					this.increaseAngerAt(entity2);
				}
			}

			this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
		} else {
			this.increaseAngerAt(entity);
		}

		if (this.getAngriness() != Angriness.ANGRY
			&& (entity instanceof ProjectileEntity || (Boolean)this.angerManager.getPrimeSuspect(world).map(suspect -> suspect == entity).orElse(true))) {
			WardenBrain.lookAtDisturbance(this, blockPos);
		}
	}

	@VisibleForTesting
	public WardenAngerManager getAngerManager() {
		return this.angerManager;
	}
}
