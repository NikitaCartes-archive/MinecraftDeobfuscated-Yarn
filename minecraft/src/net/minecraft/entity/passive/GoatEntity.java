package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class GoatEntity extends AnimalEntity {
	public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.changing(0.9F, 1.3F).scaled(0.7F);
	protected static final ImmutableList<SensorType<? extends Sensor<? super GoatEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS
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
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED
	);
	public static final int field_30402 = 10;
	public static final double field_30403 = 0.02;
	private boolean screaming;

	public GoatEntity(EntityType<? extends GoatEntity> entityType, World world) {
		super(entityType, world);
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
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
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

	public GoatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.GOAT.create(serverWorld);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? dimensions.height * 0.95F : 1.3F;
	}

	@Override
	public Brain<GoatEntity> getBrain() {
		return (Brain<GoatEntity>)super.getBrain();
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("goatBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("goatActivityUpdate");
		GoatBrain.updateActivities(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	public int getBodyYawSpeed() {
		return 15;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.BUCKET) && !this.isBaby()) {
			player.playSound(this.getMilkingSound(), 1.0F, 1.0F);
			ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
			player.setStackInHand(hand, itemStack2);
			return ActionResult.success(this.world.isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		GoatBrain.resetLongJumpCooldown(this);
		this.screaming = world.getRandom().nextDouble() < 0.02;
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return pose == EntityPose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scaled(this.getScaleFactor()) : super.getDimensions(pose);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsScreamingGoat", this.screaming);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.screaming = nbt.getBoolean("IsScreamingGoat");
	}

	public boolean isScreaming() {
		return this.screaming;
	}
}
