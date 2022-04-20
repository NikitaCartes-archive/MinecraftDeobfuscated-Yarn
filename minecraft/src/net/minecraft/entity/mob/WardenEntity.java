package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.pathing.PathNodeType;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.AbstractRandom;
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
	private static final int field_38860 = 200;
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
	private static final int field_38155 = 35;
	private static final int field_38156 = 10;
	private static final int field_38157 = 100;
	private static final int field_38158 = 20;
	private static final int field_38159 = 30;
	private static final float field_38160 = 4.5F;
	private static final float field_38161 = 0.7F;
	private int field_38162;
	private int field_38163;
	private int field_38164;
	private int field_38165;
	public AnimationState roaringAnimationState = new AnimationState();
	public AnimationState sniffingAnimationState = new AnimationState();
	public AnimationState emergingAnimationState = new AnimationState();
	public AnimationState diggingAnimationState = new AnimationState();
	public AnimationState attackingAnimationState = new AnimationState();
	public AnimationState chargingSonicBoomAnimationState = new AnimationState();
	private final EntityGameEventHandler<SculkSensorListener> gameEventHandler;
	private WardenAngerManager angerManager = new WardenAngerManager(Collections.emptyList());

	public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.gameEventHandler = new EntityGameEventHandler<>(
			new SculkSensorListener(new EntityPositionSource(this, this.getStandingEyeHeight()), 16, this, null, 0, 0)
		);
		this.experiencePoints = 5;
		this.getNavigation().setCanSwim(true);
		this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket((LivingEntity)this, this.isInPose(EntityPose.EMERGING) ? 1 : 0);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		if (packet.getEntityData() == 1) {
			this.setPose(EntityPose.EMERGING);
		}
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return super.canSpawn(world) && world.isSpaceEmpty(this, this.getType().getDimensions().getBoxAt(this.getPos()));
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return 0.0F;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.isDiggingOrEmerging() && !damageSource.isOutOfWorld() ? true : super.isInvulnerableTo(damageSource);
	}

	private boolean isDiggingOrEmerging() {
		return this.isInPose(EntityPose.DIGGING) || this.isInPose(EntityPose.EMERGING);
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
	protected float getSoundVolume() {
		return 4.0F;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return !this.isInPose(EntityPose.ROARING) && !this.isDiggingOrEmerging() ? this.getAngriness().getSound() : null;
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
		SonicBoomTask.cooldown(this, 100);
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
			this.gameEventHandler.getListener().tick(serverWorld);
			if (this.hasCustomName()) {
				WardenBrain.resetDigCooldown(this);
			}
		}

		super.tick();
		if (this.world.isClient()) {
			if (this.age % this.getHeartRate() == 0) {
				this.field_38164 = 10;
				if (!this.isSilent()) {
					this.world
						.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, this.getSoundCategory(), 5.0F, this.getSoundPitch(), false);
				}
			}

			this.field_38163 = this.field_38162;
			if (this.field_38162 > 0) {
				this.field_38162--;
			}

			this.field_38165 = this.field_38164;
			if (this.field_38164 > 0) {
				this.field_38164--;
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
		ServerWorld serverWorld = (ServerWorld)this.world;
		serverWorld.getProfiler().push("wardenBrain");
		this.getBrain().tick(serverWorld, this);
		this.world.getProfiler().pop();
		super.mobTick();
		if ((this.age + this.getId()) % 120 == 0) {
			addDarknessToClosePlayers(serverWorld, this.getPos(), this, 20);
		}

		if (this.age % 20 == 0) {
			this.angerManager.tick(serverWorld, this::isValidTarget);
		}

		this.updateAnger();
		WardenBrain.tick(this);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.attackingAnimationState.start();
		} else if (status == EntityStatuses.EARS_TWITCH) {
			this.field_38162 = 10;
		} else if (status == EntityStatuses.SONIC_BOOM) {
			this.chargingSonicBoomAnimationState.start();
		} else {
			super.handleStatus(status);
		}
	}

	private int getHeartRate() {
		float f = (float)this.getAnger() / (float)Angriness.ANGRY.getThreshold();
		return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0F, 1.0F) * 30.0F);
	}

	public float getTendrilPitch(float tickDelta) {
		return MathHelper.lerp(tickDelta, (float)this.field_38163, (float)this.field_38162) / 10.0F;
	}

	public float getHeartPitch(float tickDelta) {
		return MathHelper.lerp(tickDelta, (float)this.field_38165, (float)this.field_38164) / 10.0F;
	}

	private void addDigParticles(AnimationState animationState) {
		if ((float)(Util.getMeasuringTimeMs() - animationState.getStartTime()) < 4500.0F) {
			AbstractRandom abstractRandom = this.getRandom();
			BlockState blockState = this.world.getBlockState(this.getBlockPos().down());
			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				for (int i = 0; i < 30; i++) {
					double d = this.getX() + (double)MathHelper.nextBetween(abstractRandom, -0.7F, 0.7F);
					double e = this.getY();
					double f = this.getZ() + (double)MathHelper.nextBetween(abstractRandom, -0.7F, 0.7F);
					this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
				}
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
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return WardenBrain.create(this, dynamic);
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
	public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> biConsumer) {
		if (this.world instanceof ServerWorld serverWorld) {
			biConsumer.accept(this.gameEventHandler, serverWorld);
		}
	}

	@Override
	public TagKey<GameEvent> getTag() {
		return GameEventTags.WARDEN_CAN_LISTEN;
	}

	public boolean isValidTarget(@Nullable Entity entity) {
		if (entity instanceof LivingEntity livingEntity
			&& EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity)
			&& !this.isTeammate(entity)
			&& livingEntity.getType() != EntityType.ARMOR_STAND
			&& livingEntity.getType() != EntityType.WARDEN
			&& !livingEntity.isInvulnerable()
			&& !livingEntity.isDead()
			&& this.world.getWorldBorder().contains(livingEntity.getBoundingBox())) {
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
		SculkSensorListener.createCodec(this)
			.encodeStart(NbtOps.INSTANCE, this.gameEventHandler.getListener())
			.resultOrPartial(field_38138::error)
			.ifPresent(nbtElement -> nbt.put("listener", nbtElement));
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

		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			SculkSensorListener.createCodec(this)
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
				.resultOrPartial(field_38138::error)
				.ifPresent(sculkSensorListener -> this.gameEventHandler.setListener(sculkSensorListener, this.world));
		}
	}

	private void playListeningSound() {
		if (!this.isInPose(EntityPose.ROARING)) {
			this.playSound(this.getAngriness().getListeningSound(), 10.0F, this.getSoundPitch());
		}
	}

	public Angriness getAngriness() {
		return Angriness.getForAnger(this.angerManager.getPrimeSuspectAnger());
	}

	public void removeSuspect(Entity entity) {
		this.angerManager.removeSuspect(entity);
	}

	public void increaseAngerAt(@Nullable Entity entity) {
		this.increaseAngerAt(entity, 35, true);
	}

	@VisibleForTesting
	public void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening) {
		if (!this.isAiDisabled() && this.isValidTarget(entity)) {
			WardenBrain.resetDigCooldown(this);
			boolean bl = this.getPrimeSuspect().filter(entityx -> !(entityx instanceof PlayerEntity)).isPresent();
			int i = this.angerManager.increaseAngerAt(entity, amount);
			if (entity instanceof PlayerEntity && bl && Angriness.getForAnger(i) == Angriness.ANGRY) {
				this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
			}

			if (listening) {
				this.playListeningSound();
			}
		}
	}

	public Optional<LivingEntity> getPrimeSuspect() {
		return this.getAngriness() == Angriness.ANGRY ? this.angerManager.getPrimeSuspect() : Optional.empty();
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isPersistent();
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.getBrain().remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
		if (spawnReason == SpawnReason.TRIGGERED) {
			this.setPose(EntityPose.EMERGING);
			this.getBrain().remember(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long)WardenBrain.EMERGE_DURATION);
			this.setPersistent();
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && !this.isAiDisabled()) {
				Entity entity = source.getAttacker();
				this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20, false);
				if (this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
					&& entity instanceof LivingEntity livingEntity
					&& (!(source instanceof ProjectileDamageSource) || this.isInRange(livingEntity, 5.0))) {
					this.updateAttackTarget(livingEntity);
				}
			}

			return bl;
		}
	}

	public void updateAttackTarget(LivingEntity entity) {
		UpdateAttackTargetTask.updateAttackTarget(this, entity);
		SonicBoomTask.cooldown(this, 200);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		EntityDimensions entityDimensions = super.getDimensions(pose);
		return this.isDiggingOrEmerging() ? EntityDimensions.fixed(entityDimensions.width, 1.0F) : entityDimensions;
	}

	@Override
	public boolean isPushable() {
		return !this.isDiggingOrEmerging() && super.isPushable();
	}

	@Override
	protected void pushAway(Entity entity) {
		if (!this.isAiDisabled() && !this.getBrain().hasMemoryModule(MemoryModuleType.TOUCH_COOLDOWN)) {
			this.getBrain().remember(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
			this.increaseAngerAt(entity);
			WardenBrain.lookAtDisturbance(this, entity.getBlockPos());
		}

		super.pushAway(entity);
	}

	@Override
	public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
		if (!this.isAiDisabled()
			&& !this.getBrain().hasMemoryModule(MemoryModuleType.VIBRATION_COOLDOWN)
			&& !this.isDiggingOrEmerging()
			&& world.getWorldBorder().contains(pos)) {
			if (emitter.sourceEntity() instanceof LivingEntity livingEntity && !this.isValidTarget(livingEntity)) {
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void accept(
		ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay
	) {
		this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
		world.sendEntityStatus(this, EntityStatuses.EARS_TWITCH);
		this.playSound(SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, this.getSoundPitch());
		BlockPos blockPos = pos;
		if (sourceEntity != null) {
			if (this.isInRange(sourceEntity, 30.0)) {
				if (this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
					if (this.isValidTarget(sourceEntity)) {
						blockPos = sourceEntity.getBlockPos();
					}

					this.increaseAngerAt(sourceEntity);
				} else {
					this.increaseAngerAt(sourceEntity, 10, true);
				}
			}

			this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
		} else {
			this.increaseAngerAt(entity);
		}

		if (this.getAngriness() != Angriness.ANGRY
			&& (sourceEntity != null || (Boolean)this.angerManager.getPrimeSuspect().map(suspect -> suspect == entity).orElse(true))) {
			WardenBrain.lookAtDisturbance(this, blockPos);
		}
	}

	@VisibleForTesting
	public WardenAngerManager getAngerManager() {
		return this.angerManager;
	}
}
