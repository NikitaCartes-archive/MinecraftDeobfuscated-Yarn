package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4835;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoglinEntity extends AnimalEntity implements Monster {
	private static final Logger field_22367 = LogManager.getLogger();
	private int field_22368;
	private static int field_22361 = 0;
	private static int field_22362 = 0;
	private static int field_22363 = 0;
	private static int field_22364 = 0;
	public static final ImmutableList<? extends SensorType<? extends Sensor<? super HoglinEntity>>> field_22365 = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HOGLIN_SPECIFIC_SENSOR
	);
	public static final ImmutableList<? extends MemoryModuleType<?>> field_22366 = ImmutableList.of(
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
		MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN,
		MemoryModuleType.AVOID_TARGET,
		MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
		MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
		MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
		MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI,
		MemoryModuleType.PACIFIED
	);

	public HoglinEntity(EntityType<? extends HoglinEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
	}

	@Override
	public void remove() {
		super.remove();
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.4F);
		this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
		this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
	}

	private float method_24656() {
		return (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.field_22368 = 10;
		this.world.sendEntityStatus(this, (byte)4);
		float f = this.isBaby() ? 0.5F : this.method_24656() / 2.0F + (float)this.random.nextInt((int)this.method_24656());
		boolean bl = target.damage(DamageSource.mob(this), f);
		if (bl) {
			this.dealDamage(this, target);
			if (this.method_24658()) {
				this.method_24655(target);
			}
		}

		this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0F, this.getSoundPitch());
		if (target instanceof LivingEntity) {
			class_4835.method_24665(this, (LivingEntity)target);
		}

		return bl;
	}

	private void method_24655(Entity entity) {
		entity.setVelocity(
			entity.getVelocity()
				.add((double)((this.random.nextFloat() - 0.5F) * 0.5F), (double)(this.random.nextFloat() * 0.5F), (double)(this.random.nextFloat() * -0.5F))
		);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				class_4835.method_24672(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return class_4835.method_24668(this, dynamic);
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
		class_4835.method_24664(this);
		class_4835.method_24671(this);
		if (class_4835.method_24677(this)) {
			this.setPersistent();
		}
	}

	@Override
	public void tickMovement() {
		if (this.field_22368 > 0) {
			this.field_22368--;
		}

		super.tickMovement();
	}

	public static boolean method_24349(EntityType<HoglinEntity> entityType, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		if (world.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isPersistent();
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView worldView) {
		if (class_4835.method_24669(this, pos)) {
			return -1.0F;
		} else {
			return worldView.getBlockState(pos.down()).getBlock() == Blocks.CRIMSON_NYLIUM ? 10.0F : 0.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 4) {
			this.field_22368 = 10;
			this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0F, this.getSoundPitch());
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_24657() {
		return this.field_22368;
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return this.experiencePoints;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.CRIMSON_FUNGI;
	}

	public boolean method_24658() {
		return !this.isBaby();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return EntityType.HOGLIN.create(this.world);
	}

	@Override
	public boolean canEat() {
		return !class_4835.method_24680(this) && super.canEat();
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
		if (class_4835.method_24682(this)) {
			super.playAmbientSound();
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_HOGLIN_STEP, 0.15F, 1.0F);
	}

	public void method_24659() {
		this.playSound(SoundEvents.ENTITY_HOGLIN_ANGRY, 1.0F, this.getSoundPitch());
	}

	public void method_24660() {
		this.method_24661();
	}

	public void method_24661() {
		this.playSound(SoundEvents.ENTITY_HOGLIN_RETREAT, 1.0F, this.getSoundPitch());
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendVillagerAiDebugData(this);
	}
}
