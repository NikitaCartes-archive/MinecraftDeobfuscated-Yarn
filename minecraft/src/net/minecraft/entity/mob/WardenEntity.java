package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;

public class WardenEntity extends HostileEntity implements VibrationListener.Callback {
	public static final Map<EntityType<?>, Integer> ANGRINESS_BONUSES = new Builder<EntityType<?>, Integer>()
		.put(EntityType.PLAYER, 35)
		.put(EntityType.SNOWBALL, 10)
		.build();
	private static final int field_36752 = 120;
	private static final int field_36753 = 640;
	private static final int field_36754 = 20;
	public static final int field_36743 = 200;
	private static final int field_36755 = 16;
	private static final int field_36756 = 500;
	private static final int field_36757 = 0;
	private static final float field_36758 = 1.25F;
	private static final int field_36759 = 30;
	private static final int field_36760 = 1;
	private static final int field_36761 = 150;
	public static final int field_36777 = 80;
	private static final int field_36762 = 40;
	private static final int field_36763 = 20;
	private static final float field_36764 = 0.3F;
	private static final int field_36765 = 30;
	private static final float field_36766 = 90.0F;
	private static final float field_36767 = 0.7F;
	private static final int field_36768 = 100;
	private static final int field_36769 = 120;
	private static final int field_36770 = 20;
	private static final int field_36771 = 40;
	private float field_36772;
	private float field_36773;
	private float field_36774;
	private float field_36775;
	private int field_36776;
	private int field_36744 = 80;
	private static final TrackedData<Integer> ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Nullable
	private SoundEvent listenSound;
	private final EntityGameEventHandler gameEventHandler;
	private final VibrationListener vibrationListener;
	private static final float field_36749 = 4.5F;
	public AnimationState roaringAnimation = new AnimationState();
	public AnimationState sniffingAnimation = new AnimationState();
	public AnimationState emergingAnimation = new AnimationState();
	public AnimationState diggingAnimation = new AnimationState();
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super WardenEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.WARDEN_SPECIFIC_SENSOR, SensorType.HURT_BY
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
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
		MemoryModuleType.DISTURBANCE_LOCATION,
		MemoryModuleType.ROAR_TARGET,
		MemoryModuleType.IS_SNIFFING,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.IS_EMERGING,
		MemoryModuleType.IS_DIGGING,
		MemoryModuleType.LAST_DISTURBANCE,
		MemoryModuleType.LAST_SNIFF,
		MemoryModuleType.NEAREST_ATTACKABLE,
		MemoryModuleType.RECENT_PROJECTILE,
		MemoryModuleType.LAST_ROAR,
		MemoryModuleType.CURRENT_ROAR_STARTED,
		MemoryModuleType.IN_VIBRATION_COOLDOWN
	);
	private final WardenAngerManager angerManager;

	public WardenEntity(EntityType<? extends WardenEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.vibrationListener = new VibrationListener(new EntityPositionSource(this, this.getStandingEyeHeight()), 16, this);
		this.gameEventHandler = new EntityGameEventHandler(this.vibrationListener);
		this.getNavigation().setCanSwim(true);
		this.angerManager = new WardenAngerManager(1, 150, () -> this.age % 60 == 0 && !this.isInPose(EntityPose.ROARING) && this.getTarget() == null);
	}

	public WardenEntity(World world) {
		this(EntityType.WARDEN, world);
	}

	@Override
	public boolean occludeVibrationSignals() {
		return true;
	}

	@Override
	public EntityGameEventHandler getGameEventHandler() {
		return this.gameEventHandler;
	}

	public WardenAngerManager getAngerManager() {
		return this.angerManager;
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		WardenBrain.disturb(this);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
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
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ANGER, 0);
	}

	public static DefaultAttributeContainer.Builder addAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.25)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0);
	}

	@Override
	public Brain<WardenEntity> getBrain() {
		return (Brain<WardenEntity>)super.getBrain();
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("wardenBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.angerManager.tick();
		WardenBrain.tick(this);
		this.addDarknessToClosePlayers();
		this.field_36744++;
		if (this.field_36744 > 5 && this.listenSound != null) {
			if (!this.isInPose(EntityPose.ROARING)) {
				this.playSound(this.listenSound, 10.0F, this.getSoundPitch());
			}

			this.listenSound = null;
		}
	}

	private void addDarknessToClosePlayers() {
		if ((this.age + this.getId()) % 120 == 0) {
			StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.DARKNESS, 640, 0, false, false);
			StatusEffectUtil.addEffectToPlayersWithinDistance((ServerWorld)this.world, this, this.getPos(), 20.0, statusEffectInstance, 200);
		}
	}

	private SoundEvent getListenSound(WardenEntity.Angriness angriness) {
		return switch (angriness) {
			case AGITATED, ANGRY -> SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY;
			default -> SoundEvents.ENTITY_WARDEN_LISTENING;
		};
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (POSE.equals(data)) {
			if (this.isInPose(EntityPose.ROARING)) {
				this.roaringAnimation.start();
			}

			if (this.isInPose(EntityPose.SNIFFING)) {
				this.sniffingAnimation.start();
			}

			if (this.isInPose(EntityPose.EMERGING)) {
				this.emergingAnimation.start();
			}

			if (this.isInPose(EntityPose.DIGGING)) {
				this.diggingAnimation.start();
			}
		}

		super.onTrackedDataSet(data);
	}

	private void addDigParticles(WardenEntity warden, World world, AnimationState animationState) {
		if ((float)(Util.getMeasuringTimeMs() - animationState.getStartTime()) < 4500.0F) {
			Random random = warden.getRandom();
			BlockState blockState = world.getBlockState(warden.getBlockPos().down());

			for (int i = 0; i < 30; i++) {
				double d = warden.getX() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
				double e = warden.getY();
				double f = warden.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public void tick() {
		boolean bl = this.world.isClient();
		if (bl) {
			if (this.isInPose(EntityPose.EMERGING)) {
				this.addDigParticles(this, this.world, this.emergingAnimation);
			}

			if (this.isInPose(EntityPose.DIGGING)) {
				this.addDigParticles(this, this.world, this.diggingAnimation);
			}
		}

		this.vibrationListener.tick(this.world);
		if (!this.world.isClient()) {
			this.setAnger(this.angerManager.getPrimeSuspectAnger());
		}

		super.tick();
		if (bl && this.age % this.method_40677() == 0) {
			this.field_36774 = 0.0F;
			this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, this.getSoundCategory(), 5.0F, this.getSoundPitch(), false);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		EntityPose entityPose = this.getPose();
		return entityPose == EntityPose.DIGGING || entityPose == EntityPose.EMERGING || super.isInvulnerableTo(damageSource);
	}

	private int method_40677() {
		float f = (float)this.getAnger() / 80.0F;
		return 40 - MathHelper.clamp(MathHelper.floor(f * 30.0F), 0, 30);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.field_36776 > 0) {
			this.field_36776--;
		}

		this.field_36773 = this.field_36772;
		if (this.field_36772 < 1.0F) {
			this.field_36772 = (float)((double)this.field_36772 + 0.1);
		}

		this.field_36775 = this.field_36774;
		if (this.field_36774 < 1.0F) {
			this.field_36774 = (float)((double)this.field_36774 + 0.1);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.field_36776 = 15;
		} else if (status == 61) {
			this.field_36772 = 0.0F;
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.ENTITY_WARDEN_ATTACK_IMPACT, 10.0F, this.getSoundPitch());
		WardenBrain.disturb(this);
		return super.tryAttack(target);
	}

	public void increaseAngerFor(Entity entity) {
		int i = (Integer)ANGRINESS_BONUSES.getOrDefault(entity.getType(), 20);
		this.angerManager.increaseAngerAt(entity.getUuid(), i);
		float f = (float)this.getAnger();
		this.setAnger(this.angerManager.getPrimeSuspectAnger());
		float g = (float)this.getAnger();
		WardenEntity.Angriness angriness = this.getAngriness();
		int j = angriness == WardenEntity.Angriness.CALM ? 20 : 120;
		if (this.field_36744 > j || f < 40.0F && g >= 40.0F) {
			this.listenSound = this.getListenSound(angriness);
			this.field_36744 = 0;
		}
	}

	public WardenEntity.Angriness getAngriness() {
		int i = this.getAnger();
		if (i >= 80) {
			return WardenEntity.Angriness.ANGRY;
		} else {
			return i >= 40 ? WardenEntity.Angriness.AGITATED : WardenEntity.Angriness.CALM;
		}
	}

	public int getAnger() {
		return this.dataTracker.get(ANGER);
	}

	public void setAnger(int anger) {
		this.dataTracker.set(ANGER, anger);
	}

	@Override
	public Tag.Identified<GameEvent> getTag() {
		return GameEventTags.WARDEN_EVENTS_CAN_LISTEN;
	}

	@Override
	public boolean accepts(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity) {
		Brain<WardenEntity> brain = this.getBrain();
		if (brain.getOptionalMemory(MemoryModuleType.IN_VIBRATION_COOLDOWN).isPresent()) {
			return false;
		} else {
			EntityPose entityPose = this.getPose();
			if (entityPose == EntityPose.DIGGING || entityPose == EntityPose.EMERGING) {
				return false;
			} else {
				return sourceEntity instanceof LivingEntity && !isValidTarget(sourceEntity) ? false : sourceEntity == null || sourceEntity.getType() != EntityType.WARDEN;
			}
		}
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

	@Override
	public void accept(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, int delay) {
		Brain<WardenEntity> brain = this.getBrain();
		world.sendEntityStatus(this, (byte)61);
		this.playSound(SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, this.getSoundPitch());
		brain.remember(MemoryModuleType.IN_VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
		BlockPos blockPos = pos;
		if (sourceEntity instanceof ProjectileEntity) {
			if (brain.getOptionalMemory(MemoryModuleType.RECENT_PROJECTILE).isPresent()) {
				Entity entity = ((ProjectileEntity)sourceEntity).getOwner();
				if (isValidTarget(entity)) {
					blockPos = entity.getBlockPos();
					this.increaseAngerFor(entity);
				}
			}
		} else if (sourceEntity != null && (!(sourceEntity instanceof LivingEntity) || !((LivingEntity)sourceEntity).isDead())) {
			this.increaseAngerFor(sourceEntity);
		}

		Optional<UUID> optional = this.angerManager.getPrimeSuspectUuid();
		WardenBrain.disturb(this);
		boolean bl = optional.isPresent() && sourceEntity != null && optional.get() == sourceEntity.getUuid();
		boolean bl2 = sourceEntity instanceof ProjectileEntity;
		if (optional.isEmpty() || bl || bl2) {
			WardenBrain.lookAtDisturbance(this, blockPos);
			if (bl2) {
				brain.remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.angerManager.writeToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.angerManager.readNbt(nbt);
		if (!this.world.isClient()) {
			this.setAnger(this.angerManager.getPrimeSuspectAnger());
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public float method_40672(float f, float g) {
		return this.method_40666(f, this.field_36773, this.field_36772);
	}

	public float method_40679(float f, float g) {
		return this.method_40666(f, this.field_36775, this.field_36774);
	}

	private float method_40666(float f, float g, float h) {
		float i = 1.0F - MathHelper.lerp(f, g, h);
		return Math.max(i, 0.0F);
	}

	public float method_40680(float f, float g) {
		return Math.max(0.0F, MathHelper.cos(g * 0.045F) * 0.25F);
	}

	public float method_40681(float f, float g) {
		return Math.max(0.0F, MathHelper.cos(g * 0.045F + (float) Math.PI) * 0.25F);
	}

	public int method_40675() {
		return this.field_36776;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		WardenEntity.Angriness angriness = this.getAngriness();
		if (angriness != WardenEntity.Angriness.CALM) {
			if (!((float)this.field_36744 <= 90.0F) && !this.isInPose(EntityPose.ROARING)) {
				return angriness == WardenEntity.Angriness.AGITATED ? SoundEvents.ENTITY_WARDEN_AGITATED : SoundEvents.ENTITY_WARDEN_ANGRY;
			} else {
				return null;
			}
		} else {
			return SoundEvents.ENTITY_WARDEN_AMBIENT;
		}
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
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected float getSoundVolume() {
		return 4.0F;
	}

	@Override
	protected float calculateNextStepSoundDistance() {
		return this.distanceTraveled + 0.55F;
	}

	@Override
	public boolean disablesShield() {
		return true;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.age % 20 == 0) {
			this.increaseAngerFor(player);
			WardenBrain.disturb(this);
			WardenBrain.lookAtDisturbance(this, player.getBlockPos());
		}
	}

	public static enum Angriness {
		CALM,
		AGITATED,
		ANGRY;
	}
}
