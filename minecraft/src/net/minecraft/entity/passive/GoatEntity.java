package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GoatEntity extends AnimalEntity {
	public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.changing(0.9F, 1.3F).scaled(0.7F);
	private static final int DEFAULT_ATTACK_DAMAGE = 2;
	private static final int BABY_ATTACK_DAMAGE = 1;
	protected static final ImmutableList<SensorType<? extends Sensor<? super GoatEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES,
		SensorType.NEAREST_PLAYERS,
		SensorType.NEAREST_ITEMS,
		SensorType.NEAREST_ADULT,
		SensorType.HURT_BY,
		SensorType.GOAT_TEMPTATIONS
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.ATE_RECENTLY,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.LONG_JUMP_COOLING_DOWN,
		MemoryModuleType.LONG_JUMP_MID_JUMP,
		MemoryModuleType.TEMPTING_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_ADULT,
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED,
		MemoryModuleType.RAM_COOLDOWN_TICKS,
		MemoryModuleType.RAM_TARGET,
		MemoryModuleType.IS_PANICKING
	);
	public static final int FALL_DAMAGE_SUBTRACTOR = 10;
	public static final double SCREAMING_CHANCE = 0.02;
	public static final double field_39046 = 0.1F;
	private static final TrackedData<Boolean> SCREAMING = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> LEFT_HORN = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> RIGHT_HORN = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private boolean preparingRam;
	private int headPitch;

	public GoatEntity(EntityType<? extends GoatEntity> entityType, World world) {
		super(entityType, world);
		this.getNavigation().setCanSwim(true);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
	}

	public ItemStack getGoatHornStack() {
		Random random = Random.create((long)this.getUuid().hashCode());
		TagKey<Instrument> tagKey = this.isScreaming() ? InstrumentTags.SCREAMING_GOAT_HORNS : InstrumentTags.REGULAR_GOAT_HORNS;
		RegistryEntryList<Instrument> registryEntryList = Registries.INSTRUMENT.getOrCreateEntryList(tagKey);
		return GoatHornItem.getStackForInstrument(Items.GOAT_HORN, (RegistryEntry<Instrument>)registryEntryList.getRandom(random).get());
	}

	@Override
	protected Brain.Profile<GoatEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return GoatBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	public static DefaultAttributeContainer.Builder createGoatAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
	}

	@Override
	protected void onGrowUp() {
		if (this.isBaby()) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
			this.removeHorns();
		} else {
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0);
			this.addHorns();
		}
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		return super.computeFallDamage(fallDistance, damageMultiplier) - 10;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_AMBIENT : SoundEvents.ENTITY_GOAT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_HURT : SoundEvents.ENTITY_GOAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_DEATH : SoundEvents.ENTITY_GOAT_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_GOAT_STEP, 0.15F, 1.0F);
	}

	protected SoundEvent getMilkingSound() {
		return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_MILK : SoundEvents.ENTITY_GOAT_MILK;
	}

	@Nullable
	public GoatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		GoatEntity goatEntity = EntityType.GOAT.create(serverWorld);
		if (goatEntity != null) {
			GoatBrain.resetLongJumpCooldown(goatEntity, serverWorld.getRandom());
			PassiveEntity passiveEntity2 = (PassiveEntity)(serverWorld.getRandom().nextBoolean() ? this : passiveEntity);
			boolean bl = passiveEntity2 instanceof GoatEntity goatEntity2 && goatEntity2.isScreaming() || serverWorld.getRandom().nextDouble() < 0.02;
			goatEntity.setScreaming(bl);
		}

		return goatEntity;
	}

	@Override
	public Brain<GoatEntity> getBrain() {
		return (Brain<GoatEntity>)super.getBrain();
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("goatBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("goatActivityUpdate");
		GoatBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	@Override
	public int getMaxHeadRotation() {
		return 15;
	}

	@Override
	public void setHeadYaw(float headYaw) {
		int i = this.getMaxHeadRotation();
		float f = MathHelper.subtractAngles(this.bodyYaw, headYaw);
		float g = MathHelper.clamp(f, (float)(-i), (float)i);
		super.setHeadYaw(this.bodyYaw + g);
	}

	@Override
	public SoundEvent getEatSound(ItemStack stack) {
		return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_EAT : SoundEvents.ENTITY_GOAT_EAT;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.GOAT_FOOD);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.BUCKET) && !this.isBaby()) {
			player.playSound(this.getMilkingSound(), 1.0F, 1.0F);
			ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
			player.setStackInHand(hand, itemStack2);
			return ActionResult.success(this.getWorld().isClient);
		} else {
			ActionResult actionResult = super.interactMob(player, hand);
			if (actionResult.isAccepted() && this.isBreedingItem(itemStack)) {
				this.getWorld()
					.playSoundFromEntity(null, this, this.getEatSound(itemStack), SoundCategory.NEUTRAL, 1.0F, MathHelper.nextBetween(this.getWorld().random, 0.8F, 1.2F));
			}

			return actionResult;
		}
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		Random random = world.getRandom();
		GoatBrain.resetLongJumpCooldown(this, random);
		this.setScreaming(random.nextDouble() < 0.02);
		this.onGrowUp();
		if (!this.isBaby() && (double)random.nextFloat() < 0.1F) {
			TrackedData<Boolean> trackedData = random.nextBoolean() ? LEFT_HORN : RIGHT_HORN;
			this.dataTracker.set(trackedData, false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return pose == EntityPose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scaled(this.getScaleFactor()) : super.getBaseDimensions(pose);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsScreamingGoat", this.isScreaming());
		nbt.putBoolean("HasLeftHorn", this.hasLeftHorn());
		nbt.putBoolean("HasRightHorn", this.hasRightHorn());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setScreaming(nbt.getBoolean("IsScreamingGoat"));
		this.dataTracker.set(LEFT_HORN, nbt.getBoolean("HasLeftHorn"));
		this.dataTracker.set(RIGHT_HORN, nbt.getBoolean("HasRightHorn"));
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PREPARE_RAM) {
			this.preparingRam = true;
		} else if (status == EntityStatuses.FINISH_RAM) {
			this.preparingRam = false;
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void tickMovement() {
		if (this.preparingRam) {
			this.headPitch++;
		} else {
			this.headPitch -= 2;
		}

		this.headPitch = MathHelper.clamp(this.headPitch, 0, 20);
		super.tickMovement();
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SCREAMING, false);
		builder.add(LEFT_HORN, true);
		builder.add(RIGHT_HORN, true);
	}

	public boolean hasLeftHorn() {
		return this.dataTracker.get(LEFT_HORN);
	}

	public boolean hasRightHorn() {
		return this.dataTracker.get(RIGHT_HORN);
	}

	public boolean dropHorn() {
		boolean bl = this.hasLeftHorn();
		boolean bl2 = this.hasRightHorn();
		if (!bl && !bl2) {
			return false;
		} else {
			TrackedData<Boolean> trackedData;
			if (!bl) {
				trackedData = RIGHT_HORN;
			} else if (!bl2) {
				trackedData = LEFT_HORN;
			} else {
				trackedData = this.random.nextBoolean() ? LEFT_HORN : RIGHT_HORN;
			}

			this.dataTracker.set(trackedData, false);
			Vec3d vec3d = this.getPos();
			ItemStack itemStack = this.getGoatHornStack();
			double d = (double)MathHelper.nextBetween(this.random, -0.2F, 0.2F);
			double e = (double)MathHelper.nextBetween(this.random, 0.3F, 0.7F);
			double f = (double)MathHelper.nextBetween(this.random, -0.2F, 0.2F);
			ItemEntity itemEntity = new ItemEntity(this.getWorld(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack, d, e, f);
			this.getWorld().spawnEntity(itemEntity);
			return true;
		}
	}

	public void addHorns() {
		this.dataTracker.set(LEFT_HORN, true);
		this.dataTracker.set(RIGHT_HORN, true);
	}

	public void removeHorns() {
		this.dataTracker.set(LEFT_HORN, false);
		this.dataTracker.set(RIGHT_HORN, false);
	}

	public boolean isScreaming() {
		return this.dataTracker.get(SCREAMING);
	}

	public void setScreaming(boolean screaming) {
		this.dataTracker.set(SCREAMING, screaming);
	}

	public float getHeadPitch() {
		return (float)this.headPitch / 20.0F * 30.0F * (float) (Math.PI / 180.0);
	}

	public static boolean canSpawn(EntityType<? extends AnimalEntity> entityType, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.GOATS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}
}
