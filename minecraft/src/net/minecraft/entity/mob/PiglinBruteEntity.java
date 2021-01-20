package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class PiglinBruteEntity extends AbstractPiglinEntity {
	protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinBruteEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.DOORS_TO_CLOSE,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
		MemoryModuleType.NEARBY_ADULT_PIGLINS,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.ATTACK_COOLING_DOWN,
		MemoryModuleType.INTERACTION_TARGET,
		MemoryModuleType.PATH,
		MemoryModuleType.ANGRY_AT,
		MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
		MemoryModuleType.HOME
	);

	public PiglinBruteEntity(EntityType<? extends PiglinBruteEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 20;
	}

	public static DefaultAttributeContainer.Builder createPiglinBruteAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		PiglinBruteBrain.setCurrentPosAsHome(this);
		this.initEquipment(difficulty);
		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
	}

	@Override
	protected Brain.Profile<PiglinBruteEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return PiglinBruteBrain.create(this, this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<PiglinBruteEntity> getBrain() {
		return (Brain<PiglinBruteEntity>)super.getBrain();
	}

	@Override
	public boolean canHunt() {
		return false;
	}

	@Override
	public boolean canGather(ItemStack stack) {
		return stack.isOf(Items.GOLDEN_AXE) ? super.canGather(stack) : false;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("piglinBruteBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		PiglinBruteBrain.method_30256(this);
		PiglinBruteBrain.method_30258(this);
		super.mobTick();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public PiglinActivity getActivity() {
		return this.isAttacking() && this.isHoldingTool() ? PiglinActivity.ATTACKING_WITH_MELEE_WEAPON : PiglinActivity.DEFAULT;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				PiglinBruteBrain.tryRevenge(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PIGLIN_BRUTE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PIGLIN_BRUTE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PIGLIN_BRUTE_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
	}

	protected void playAngrySound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_ANGRY, 1.0F, this.getSoundPitch());
	}

	@Override
	protected void playZombificationSound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getSoundPitch());
	}
}
