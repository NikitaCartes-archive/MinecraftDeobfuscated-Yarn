package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;

public class FrogEntity extends AnimalEntity {
	public static final Ingredient SLIME_BALL = Ingredient.ofItems(Items.SLIME_BALL);
	protected static final ImmutableList<SensorType<? extends Sensor<? super FrogEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.LONG_JUMP_COOLING_DOWN,
		MemoryModuleType.LONG_JUMP_MID_JUMP,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.TEMPTING_PLAYER,
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.NEAREST_ATTACKABLE,
		MemoryModuleType.IS_IN_WATER,
		MemoryModuleType.IS_PREGNANT,
		MemoryModuleType.IS_PANICKING,
		MemoryModuleType.UNREACHABLE_TONGUE_TARGETS
	);
	private static final TrackedData<FrogVariant> VARIANT = DataTracker.registerData(FrogEntity.class, TrackedDataHandlerRegistry.FROG_VARIANT);
	private static final TrackedData<OptionalInt> TARGET = DataTracker.registerData(FrogEntity.class, TrackedDataHandlerRegistry.OPTIONAL_INT);
	private static final int field_37459 = 5;
	public static final String VARIANT_KEY = "variant";
	public final AnimationState longJumpingAnimationState = new AnimationState();
	public final AnimationState croakingAnimationState = new AnimationState();
	public final AnimationState usingTongueAnimationState = new AnimationState();
	public final AnimationState walkingAnimationState = new AnimationState();
	public final AnimationState swimmingAnimationState = new AnimationState();
	public final AnimationState idlingInWaterAnimationState = new AnimationState();

	public FrogEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.lookControl = new FrogEntity.FrogLookControl(this);
		this.setPathfindingPenalty(PathNodeType.WATER, 4.0F);
		this.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1.0F);
		this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
		this.stepHeight = 1.0F;
	}

	@Override
	protected Brain.Profile<FrogEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return FrogBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<FrogEntity> getBrain() {
		return (Brain<FrogEntity>)super.getBrain();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, FrogVariant.TEMPERATE);
		this.dataTracker.startTracking(TARGET, OptionalInt.empty());
	}

	public void clearFrogTarget() {
		this.dataTracker.set(TARGET, OptionalInt.empty());
	}

	public Optional<Entity> getFrogTarget() {
		return this.dataTracker.get(TARGET).stream().mapToObj(this.world::getEntityById).filter(Objects::nonNull).findFirst();
	}

	public void setFrogTarget(Entity entity) {
		this.dataTracker.set(TARGET, OptionalInt.of(entity.getId()));
	}

	@Override
	public int getMaxLookYawChange() {
		return 35;
	}

	@Override
	public int getMaxHeadRotation() {
		return 5;
	}

	public FrogVariant getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	public void setVariant(FrogVariant variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("variant", Registry.FROG_VARIANT.getId(this.getVariant()).toString());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		FrogVariant frogVariant = Registry.FROG_VARIANT.get(Identifier.tryParse(nbt.getString("variant")));
		if (frogVariant != null) {
			this.setVariant(frogVariant);
		}
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	private boolean shouldWalk() {
		return this.onGround && this.getVelocity().horizontalLengthSquared() > 1.0E-6 && !this.isInsideWaterOrBubbleColumn();
	}

	private boolean shouldSwim() {
		return this.getVelocity().horizontalLengthSquared() > 1.0E-6 && this.isInsideWaterOrBubbleColumn();
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("frogBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("frogActivityUpdate");
		FrogBrain.updateActivities(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	public void tick() {
		if (this.world.isClient()) {
			if (this.shouldWalk()) {
				this.walkingAnimationState.startIfNotRunning(this.age);
			} else {
				this.walkingAnimationState.stop();
			}

			if (this.shouldSwim()) {
				this.idlingInWaterAnimationState.stop();
				this.swimmingAnimationState.startIfNotRunning(this.age);
			} else if (this.isInsideWaterOrBubbleColumn()) {
				this.swimmingAnimationState.stop();
				this.idlingInWaterAnimationState.startIfNotRunning(this.age);
			} else {
				this.swimmingAnimationState.stop();
				this.idlingInWaterAnimationState.stop();
			}
		}

		super.tick();
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (POSE.equals(data)) {
			EntityPose entityPose = this.getPose();
			if (entityPose == EntityPose.LONG_JUMPING) {
				this.longJumpingAnimationState.start(this.age);
			} else {
				this.longJumpingAnimationState.stop();
			}

			if (entityPose == EntityPose.CROAKING) {
				this.croakingAnimationState.start(this.age);
			} else {
				this.croakingAnimationState.stop();
			}

			if (entityPose == EntityPose.USING_TONGUE) {
				this.usingTongueAnimationState.start(this.age);
			} else {
				this.usingTongueAnimationState.stop();
			}
		}

		super.onTrackedDataSet(data);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		FrogEntity frogEntity = EntityType.FROG.create(world);
		if (frogEntity != null) {
			FrogBrain.coolDownLongJump(frogEntity, world.getRandom());
		}

		return frogEntity;
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	public void setBaby(boolean baby) {
	}

	@Override
	public void breed(ServerWorld world, AnimalEntity other) {
		ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
		if (serverPlayerEntity == null) {
			serverPlayerEntity = other.getLovingPlayer();
		}

		if (serverPlayerEntity != null) {
			serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
			Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, other, null);
		}

		this.setBreedingAge(6000);
		other.setBreedingAge(6000);
		this.resetLoveTicks();
		other.resetLoveTicks();
		this.getBrain().remember(MemoryModuleType.IS_PREGNANT, Unit.INSTANCE);
		world.sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
		if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
		}
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		RegistryEntry<Biome> registryEntry = world.getBiome(this.getBlockPos());
		if (registryEntry.isIn(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
			this.setVariant(FrogVariant.COLD);
		} else if (registryEntry.isIn(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
			this.setVariant(FrogVariant.WARM);
		} else {
			this.setVariant(FrogVariant.TEMPERATE);
		}

		FrogBrain.coolDownLongJump(this, world.getRandom());
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public static DefaultAttributeContainer.Builder createFrogAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_FROG_AMBIENT;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_FROG_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_FROG_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_FROG_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean isPushedByFluids() {
		return false;
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		return super.computeFallDamage(fallDistance, damageMultiplier) - 5;
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(this.getMovementSpeed(), movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
		} else {
			super.travel(movementInput);
		}
	}

	@Override
	public boolean canJumpToNextPathNode(PathNodeType type) {
		return super.canJumpToNextPathNode(type) && type != PathNodeType.WATER_BORDER;
	}

	public static boolean isValidFrogFood(LivingEntity entity) {
		if (entity instanceof SlimeEntity slimeEntity && slimeEntity.getSize() != 1) {
			return false;
		}

		return entity.getType().isIn(EntityTypeTags.FROG_FOOD);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new FrogEntity.FrogSwimNavigation(this, world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return SLIME_BALL.test(stack);
	}

	public static boolean canSpawn(EntityType<? extends AnimalEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.FROGS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}

	class FrogLookControl extends LookControl {
		FrogLookControl(MobEntity entity) {
			super(entity);
		}

		@Override
		protected boolean shouldStayHorizontal() {
			return FrogEntity.this.getFrogTarget().isEmpty();
		}
	}

	static class FrogSwimNavigation extends AxolotlSwimNavigation {
		FrogSwimNavigation(FrogEntity frog, World world) {
			super(frog, world);
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator(int range) {
			this.nodeMaker = new FrogEntity.FrogSwimPathNodeMaker(true);
			this.nodeMaker.setCanEnterOpenDoors(true);
			return new PathNodeNavigator(this.nodeMaker, range);
		}
	}

	static class FrogSwimPathNodeMaker extends AmphibiousPathNodeMaker {
		private final BlockPos.Mutable pos = new BlockPos.Mutable();

		public FrogSwimPathNodeMaker(boolean bl) {
			super(bl);
		}

		@Nullable
		@Override
		public PathNode getStart() {
			return this.getStart(
				new BlockPos(
					MathHelper.floor(this.entity.getBoundingBox().minX),
					MathHelper.floor(this.entity.getBoundingBox().minY),
					MathHelper.floor(this.entity.getBoundingBox().minZ)
				)
			);
		}

		@Override
		public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
			this.pos.set(x, y - 1, z);
			BlockState blockState = world.getBlockState(this.pos);
			return blockState.isIn(BlockTags.FROG_PREFER_JUMP_TO) ? PathNodeType.OPEN : super.getDefaultNodeType(world, x, y, z);
		}
	}
}
