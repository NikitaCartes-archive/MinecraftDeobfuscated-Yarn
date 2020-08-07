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
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HoglinEntity extends AnimalEntity implements Monster, Hoglin {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(HoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int movementCooldownTicks;
	private int timeInOverworld = 0;
	private boolean cannotBeHunted = false;
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super HoglinEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.field_18466, SensorType.field_18467, SensorType.field_25362, SensorType.field_22360
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.field_18448,
		MemoryModuleType.field_18441,
		MemoryModuleType.field_18442,
		MemoryModuleType.field_18444,
		MemoryModuleType.field_22354,
		MemoryModuleType.field_18446,
		MemoryModuleType.field_18445,
		MemoryModuleType.field_19293,
		MemoryModuleType.field_18449,
		MemoryModuleType.field_22355,
		MemoryModuleType.field_22475,
		MemoryModuleType.field_22345,
		MemoryModuleType.field_22357,
		MemoryModuleType.field_22347,
		MemoryModuleType.field_22348,
		MemoryModuleType.field_22344,
		MemoryModuleType.field_25359,
		MemoryModuleType.field_22474,
		MemoryModuleType.field_22353
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
			.add(EntityAttributes.field_23716, 40.0)
			.add(EntityAttributes.field_23719, 0.3F)
			.add(EntityAttributes.field_23718, 0.6F)
			.add(EntityAttributes.field_23722, 1.0)
			.add(EntityAttributes.field_23721, 6.0);
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (!(target instanceof LivingEntity)) {
			return false;
		} else {
			this.movementCooldownTicks = 10;
			this.world.sendEntityStatus(this, (byte)4);
			this.playSound(SoundEvents.field_22258, 1.0F, this.getSoundPitch());
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
		if (this.canConvert()) {
			this.timeInOverworld++;
			if (this.timeInOverworld > 300) {
				this.method_30081(SoundEvents.field_23671);
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
			this.getAttributeInstance(EntityAttributes.field_23721).setBaseValue(0.5);
		} else {
			this.experiencePoints = 5;
			this.getAttributeInstance(EntityAttributes.field_23721).setBaseValue(6.0);
		}
	}

	public static boolean canSpawn(EntityType<HoglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.method_10074()).isOf(Blocks.field_10541);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess serverWorldAccess, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (serverWorldAccess.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		return super.initialize(serverWorldAccess, difficulty, spawnReason, entityData, entityTag);
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
			return world.getBlockState(pos.method_10074()).isOf(Blocks.field_22120) ? 10.0F : 0.0F;
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
			this.playSound(SoundEvents.field_22258, 1.0F, this.getSoundPitch());
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
		ZoglinEntity zoglinEntity = this.method_29243(EntityType.field_23696, true);
		if (zoglinEntity != null) {
			zoglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.field_5916, 200, 0));
		}
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
	public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		HoglinEntity hoglinEntity = EntityType.field_21973.create(serverWorld);
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
		return SoundCategory.field_15251;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.world.isClient ? null : (SoundEvent)HoglinBrain.method_30083(this).orElse(null);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.field_22260;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_22259;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.field_14630;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.field_14836;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.field_22262, 0.15F, 1.0F);
	}

	protected void method_30081(SoundEvent soundEvent) {
		this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}
}
