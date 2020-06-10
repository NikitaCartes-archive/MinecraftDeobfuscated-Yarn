package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HoglinEntity extends AnimalEntity implements Monster, Hoglin {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(HoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int movementCooldownTicks;
	private int timeInOverworld = 0;
	private boolean cannotBeHunted = false;
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
		MemoryModuleType.PACIFIED
	);

	public HoglinEntity(EntityType<? extends HoglinEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return !this.isLeashed();
	}

	public static DefaultAttributeContainer.Builder createHoglinAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (!(target instanceof LivingEntity)) {
			return false;
		} else {
			this.movementCooldownTicks = 10;
			this.world.sendEntityStatus(this, (byte)4);
			this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0F, this.getSoundPitch());
			HoglinBrain.onAttacking(this, (LivingEntity)target);
			return Hoglin.tryAttack(this, (LivingEntity)target);
		}
	}

	@Override
	protected void knockback(LivingEntity target) {
		if (this.isAdult()) {
			Hoglin.knockback(this, target);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				HoglinBrain.onAttacked(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
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
	protected void mobTick() {
		this.world.getProfiler().push("hoglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		HoglinBrain.refreshActivities(this);
		HoglinBrain.playSoundAtChance(this);
		if (this.canConvert()) {
			this.timeInOverworld++;
			if (this.timeInOverworld > 300) {
				this.playZombifySound();
				this.zombify((ServerWorld)this.world);
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
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
		} else {
			this.experiencePoints = 5;
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6.0);
		}
	}

	public static boolean canSpawn(EntityType<HoglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
	}

	@Nullable
	@Override
	public EntityData initialize(
		WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (world.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
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
	public double getMountedHeightOffset() {
		return (double)this.getHeight() - (this.isBaby() ? 0.2 : 0.15);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ActionResult actionResult = super.interactMob(player, hand);
		if (actionResult.isAccepted()) {
			this.setPersistent();
		}

		return actionResult;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 4) {
			this.movementCooldownTicks = 10;
			this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0F, this.getSoundPitch());
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
	protected boolean canDropLootAndXp() {
		return true;
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return this.experiencePoints;
	}

	private void zombify(ServerWorld word) {
		ZoglinEntity zoglinEntity = this.method_29243(EntityType.ZOGLIN);
		zoglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.CRIMSON_FUNGUS;
	}

	public boolean isAdult() {
		return !this.isBaby();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BABY, false);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		if (this.isImmuneToZombification()) {
			tag.putBoolean("IsImmuneToZombification", true);
		}

		tag.putInt("TimeInOverworld", this.timeInOverworld);
		if (this.cannotBeHunted) {
			tag.putBoolean("CannotBeHunted", true);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setImmuneToZombification(tag.getBoolean("IsImmuneToZombification"));
		this.timeInOverworld = tag.getInt("TimeInOverworld");
		this.setCannotBeHunted(tag.getBoolean("CannotBeHunted"));
	}

	public void setImmuneToZombification(boolean immuneToZombification) {
		this.getDataTracker().set(BABY, immuneToZombification);
	}

	private boolean isImmuneToZombification() {
		return this.getDataTracker().get(BABY);
	}

	public boolean canConvert() {
		return !this.world.getDimension().isPiglinSafe() && !this.isImmuneToZombification() && !this.isAiDisabled();
	}

	private void setCannotBeHunted(boolean cannotBeHunted) {
		this.cannotBeHunted = cannotBeHunted;
	}

	public boolean canBeHunted() {
		return this.isAdult() && !this.cannotBeHunted;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		HoglinEntity hoglinEntity = EntityType.HOGLIN.create(this.world);
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
		return SoundEvents.ENTITY_HOGLIN_AMBIENT;
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
	public void playAmbientSound() {
		if (HoglinBrain.hasIdleActivity(this)) {
			super.playAmbientSound();
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_HOGLIN_STEP, 0.15F, 1.0F);
	}

	protected void playFightSound() {
		this.playSound(SoundEvents.ENTITY_HOGLIN_ANGRY, 1.0F, this.getSoundPitch());
	}

	protected void playRetreatSound() {
		this.playSound(SoundEvents.ENTITY_HOGLIN_RETREAT, 1.0F, this.getSoundPitch());
	}

	private void playZombifySound() {
		this.playSound(SoundEvents.ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getSoundPitch());
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}
}
