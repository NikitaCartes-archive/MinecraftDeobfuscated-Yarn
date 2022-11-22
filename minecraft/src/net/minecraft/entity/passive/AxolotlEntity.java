package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.AngledModelEntity;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.joml.Vector3f;
import org.slf4j.Logger;

/**
 * Represents an axolotl, the cutest predator.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Relations of this entity class with the codebase</caption>
 * <tr>
 *   <th><b>Relation</b></th><th><b>Class</b></th>
 * </tr>
 * <tr>
 *   <td>Brain</td><td>{@link AxolotlBrain}</td>
 * </tr>
 * <tr>
 *   <td>EntityData</td><td>{@link AxolotlData}</td>
 * </tr>
 * <tr>
 *   <td>Look Control</td><td>{@link AxolotlLookControl}</td>
 * </tr>
 * <tr>
 *   <td>Model</td><td>{@link net.minecraft.client.render.entity.model.AxolotlEntityModel}</td>
 * </tr>
 * <tr>
 *   <td>Move Control</td><td>{@link AxolotlMoveControl}</td>
 * </tr>
 * <tr>
 *   <td>Renderer</td><td>{@link net.minecraft.client.render.entity.AxolotlEntityRenderer}</td>
 * </tr>
 * <tr>
 *   <td>Variants</td><td>{@link Variant}</td>
 * </tr>
 * </table>
 * </div>
 */
public class AxolotlEntity extends AnimalEntity implements AngledModelEntity, VariantHolder<AxolotlEntity.Variant>, Bucketable {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int PLAY_DEAD_TICKS = 200;
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super AxolotlEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
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
		MemoryModuleType.NEAREST_VISIBLE_ADULT,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.PLAY_DEAD_TICKS,
		MemoryModuleType.NEAREST_ATTACKABLE,
		MemoryModuleType.TEMPTING_PLAYER,
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED,
		MemoryModuleType.HAS_HUNTING_COOLDOWN,
		MemoryModuleType.IS_PANICKING
	);
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> PLAYING_DEAD = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final double BUFF_RANGE = 20.0;
	public static final int BLUE_BABY_CHANCE = 1200;
	private static final int MAX_AIR = 6000;
	public static final String VARIANT_KEY = "Variant";
	private static final int HYDRATION_BY_POTION = 1800;
	private static final int MAX_REGENERATION_BUFF_DURATION = 2400;
	private final Map<String, Vector3f> modelAngles = Maps.<String, Vector3f>newHashMap();
	private static final int BUFF_DURATION = 100;

	public AxolotlEntity(EntityType<? extends AxolotlEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.moveControl = new AxolotlEntity.AxolotlMoveControl(this);
		this.lookControl = new AxolotlEntity.AxolotlLookControl(this, 20);
		this.stepHeight = 1.0F;
	}

	@Override
	public Map<String, Vector3f> getModelAngles() {
		return this.modelAngles;
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return 0.0F;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, 0);
		this.dataTracker.startTracking(PLAYING_DEAD, false);
		this.dataTracker.startTracking(FROM_BUCKET, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getVariant().getId());
		nbt.putBoolean("FromBucket", this.isFromBucket());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setVariant(AxolotlEntity.Variant.VARIANTS[nbt.getInt("Variant")]);
		this.setFromBucket(nbt.getBoolean("FromBucket"));
	}

	@Override
	public void playAmbientSound() {
		if (!this.isPlayingDead()) {
			super.playAmbientSound();
		}
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		boolean bl = false;
		if (spawnReason == SpawnReason.BUCKET) {
			return entityData;
		} else {
			Random random = world.getRandom();
			if (entityData instanceof AxolotlEntity.AxolotlData) {
				if (((AxolotlEntity.AxolotlData)entityData).getSpawnedCount() >= 2) {
					bl = true;
				}
			} else {
				entityData = new AxolotlEntity.AxolotlData(AxolotlEntity.Variant.getRandomNatural(random), AxolotlEntity.Variant.getRandomNatural(random));
			}

			this.setVariant(((AxolotlEntity.AxolotlData)entityData).getRandomVariant(random));
			if (bl) {
				this.setBreedingAge(-24000);
			}

			return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
		}
	}

	@Override
	public void baseTick() {
		int i = this.getAir();
		super.baseTick();
		if (!this.isAiDisabled()) {
			this.tickAir(i);
		}
	}

	protected void tickAir(int air) {
		if (this.isAlive() && !this.isWet()) {
			this.setAir(air - 1);
			if (this.getAir() == -20) {
				this.setAir(0);
				this.damage(DamageSource.DRYOUT, 2.0F);
			}
		} else {
			this.setAir(this.getMaxAir());
		}
	}

	public void hydrateFromPotion() {
		int i = this.getAir() + 1800;
		this.setAir(Math.min(i, this.getMaxAir()));
	}

	@Override
	public int getMaxAir() {
		return 6000;
	}

	public AxolotlEntity.Variant getVariant() {
		return AxolotlEntity.Variant.VARIANTS[this.dataTracker.get(VARIANT)];
	}

	public void setVariant(AxolotlEntity.Variant variant) {
		this.dataTracker.set(VARIANT, variant.getId());
	}

	private static boolean shouldBabyBeDifferent(Random random) {
		return random.nextInt(1200) == 0;
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.doesNotIntersectEntities(this);
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public boolean isPushedByFluids() {
		return false;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.AQUATIC;
	}

	public void setPlayingDead(boolean playingDead) {
		this.dataTracker.set(PLAYING_DEAD, playingDead);
	}

	public boolean isPlayingDead() {
		return this.dataTracker.get(PLAYING_DEAD);
	}

	@Override
	public boolean isFromBucket() {
		return this.dataTracker.get(FROM_BUCKET);
	}

	@Override
	public void setFromBucket(boolean fromBucket) {
		this.dataTracker.set(FROM_BUCKET, fromBucket);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		AxolotlEntity axolotlEntity = EntityType.AXOLOTL.create(world);
		if (axolotlEntity != null) {
			AxolotlEntity.Variant variant;
			if (shouldBabyBeDifferent(this.random)) {
				variant = AxolotlEntity.Variant.getRandomUnnatural(this.random);
			} else {
				variant = this.random.nextBoolean() ? this.getVariant() : ((AxolotlEntity)entity).getVariant();
			}

			axolotlEntity.setVariant(variant);
			axolotlEntity.setPersistent();
		}

		return axolotlEntity;
	}

	@Override
	public double squaredAttackRange(LivingEntity target) {
		return 1.5 + (double)target.getWidth() * 2.0;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.AXOLOTL_TEMPT_ITEMS);
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return true;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("axolotlBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("axolotlActivityUpdate");
		AxolotlBrain.updateActivities(this);
		this.world.getProfiler().pop();
		if (!this.isAiDisabled()) {
			Optional<Integer> optional = this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.PLAY_DEAD_TICKS);
			this.setPlayingDead(optional.isPresent() && (Integer)optional.get() > 0);
		}
	}

	public static DefaultAttributeContainer.Builder createAxolotlAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new AmphibiousSwimNavigation(this, world);
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
		if (bl) {
			this.applyDamageEffects(this, target);
			this.playSound(SoundEvents.ENTITY_AXOLOTL_ATTACK, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		float f = this.getHealth();
		if (!this.world.isClient
			&& !this.isAiDisabled()
			&& this.world.random.nextInt(3) == 0
			&& ((float)this.world.random.nextInt(3) < amount || f / this.getMaxHealth() < 0.5F)
			&& amount < f
			&& this.isTouchingWater()
			&& (source.getAttacker() != null || source.getSource() != null)
			&& !this.isPlayingDead()) {
			this.brain.remember(MemoryModuleType.PLAY_DEAD_TICKS, 200);
		}

		return super.damage(source, amount);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.655F;
	}

	@Override
	public int getMaxLookPitchChange() {
		return 1;
	}

	@Override
	public int getMaxHeadRotation() {
		return 1;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
	}

	@Override
	public void copyDataToStack(ItemStack stack) {
		Bucketable.copyDataToStack(this, stack);
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putInt("Variant", this.getVariant().getId());
		nbtCompound.putInt("Age", this.getBreedingAge());
		Brain<?> brain = this.getBrain();
		if (brain.hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
			nbtCompound.putLong("HuntingCooldown", brain.getMemoryExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
		}
	}

	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Bucketable.copyDataFromNbt(this, nbt);
		int i = nbt.getInt("Variant");
		if (i >= 0 && i < AxolotlEntity.Variant.VARIANTS.length) {
			this.setVariant(AxolotlEntity.Variant.VARIANTS[i]);
		} else {
			LOGGER.error("Invalid variant: {}", i);
		}

		if (nbt.contains("Age")) {
			this.setBreedingAge(nbt.getInt("Age"));
		}

		if (nbt.contains("HuntingCooldown")) {
			this.getBrain().remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, nbt.getLong("HuntingCooldown"));
		}
	}

	@Override
	public ItemStack getBucketItem() {
		return new ItemStack(Items.AXOLOTL_BUCKET);
	}

	@Override
	public SoundEvent getBucketFillSound() {
		return SoundEvents.ITEM_BUCKET_FILL_AXOLOTL;
	}

	@Override
	public boolean canTakeDamage() {
		return !this.isPlayingDead() && super.canTakeDamage();
	}

	public static void appreciatePlayer(AxolotlEntity axolotl, LivingEntity entity) {
		World world = axolotl.world;
		if (entity.isDead()) {
			DamageSource damageSource = entity.getRecentDamageSource();
			if (damageSource != null) {
				Entity entity2 = damageSource.getAttacker();
				if (entity2 != null && entity2.getType() == EntityType.PLAYER) {
					PlayerEntity playerEntity = (PlayerEntity)entity2;
					List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, axolotl.getBoundingBox().expand(20.0));
					if (list.contains(playerEntity)) {
						axolotl.buffPlayer(playerEntity);
					}
				}
			}
		}
	}

	public void buffPlayer(PlayerEntity player) {
		StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.REGENERATION);
		int i = statusEffectInstance != null ? statusEffectInstance.getDuration() : 0;
		if (i < 2400) {
			i = Math.min(2400, 100 + i);
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, i, 0), this);
		}

		player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
	}

	@Override
	public boolean cannotDespawn() {
		return super.cannotDespawn() || this.isFromBucket();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_AXOLOTL_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_AXOLOTL_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTouchingWater() ? SoundEvents.ENTITY_AXOLOTL_IDLE_WATER : SoundEvents.ENTITY_AXOLOTL_IDLE_AIR;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_AXOLOTL_SPLASH;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_AXOLOTL_SWIM;
	}

	@Override
	protected Brain.Profile<AxolotlEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return AxolotlBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<AxolotlEntity> getBrain() {
		return (Brain<AxolotlEntity>)super.getBrain();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
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
	protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
		if (stack.isOf(Items.TROPICAL_FISH_BUCKET)) {
			player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
		} else {
			super.eat(player, hand, stack);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isFromBucket() && !this.hasCustomName();
	}

	public static boolean canSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.AXOLOTLS_SPAWNABLE_ON);
	}

	public static class AxolotlData extends PassiveEntity.PassiveData {
		public final AxolotlEntity.Variant[] variants;

		public AxolotlData(AxolotlEntity.Variant... variants) {
			super(false);
			this.variants = variants;
		}

		public AxolotlEntity.Variant getRandomVariant(Random random) {
			return this.variants[random.nextInt(this.variants.length)];
		}
	}

	class AxolotlLookControl extends YawAdjustingLookControl {
		public AxolotlLookControl(AxolotlEntity axolotl, int yawAdjustThreshold) {
			super(axolotl, yawAdjustThreshold);
		}

		@Override
		public void tick() {
			if (!AxolotlEntity.this.isPlayingDead()) {
				super.tick();
			}
		}
	}

	static class AxolotlMoveControl extends AquaticMoveControl {
		private final AxolotlEntity axolotl;

		public AxolotlMoveControl(AxolotlEntity axolotl) {
			super(axolotl, 85, 10, 0.1F, 0.5F, false);
			this.axolotl = axolotl;
		}

		@Override
		public void tick() {
			if (!this.axolotl.isPlayingDead()) {
				super.tick();
			}
		}
	}

	public static enum Variant implements StringIdentifiable {
		LUCY(0, "lucy", true),
		WILD(1, "wild", true),
		GOLD(2, "gold", true),
		CYAN(3, "cyan", true),
		BLUE(4, "blue", false);

		public static final AxolotlEntity.Variant[] VARIANTS = (AxolotlEntity.Variant[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(AxolotlEntity.Variant::getId))
			.toArray(AxolotlEntity.Variant[]::new);
		public static final com.mojang.serialization.Codec<AxolotlEntity.Variant> CODEC = StringIdentifiable.createCodec(AxolotlEntity.Variant::values);
		private final int id;
		private final String name;
		private final boolean natural;

		private Variant(int id, String name, boolean natural) {
			this.id = id;
			this.name = name;
			this.natural = natural;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public static AxolotlEntity.Variant getRandomNatural(Random random) {
			return getRandom(random, true);
		}

		public static AxolotlEntity.Variant getRandomUnnatural(Random random) {
			return getRandom(random, false);
		}

		private static AxolotlEntity.Variant getRandom(Random random, boolean natural) {
			AxolotlEntity.Variant[] variants = (AxolotlEntity.Variant[])Arrays.stream(VARIANTS)
				.filter(variant -> variant.natural == natural)
				.toArray(AxolotlEntity.Variant[]::new);
			return Util.getRandom(variants, random);
		}
	}
}
