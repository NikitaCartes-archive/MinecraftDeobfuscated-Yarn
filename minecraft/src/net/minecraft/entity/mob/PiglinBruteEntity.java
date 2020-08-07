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
		SensorType.field_18466, SensorType.field_18467, SensorType.field_22358, SensorType.field_18469, SensorType.field_25757
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.field_18446,
		MemoryModuleType.field_26389,
		MemoryModuleType.field_18441,
		MemoryModuleType.field_18442,
		MemoryModuleType.field_18444,
		MemoryModuleType.field_22354,
		MemoryModuleType.field_22343,
		MemoryModuleType.field_25755,
		MemoryModuleType.field_18451,
		MemoryModuleType.field_18452,
		MemoryModuleType.field_18445,
		MemoryModuleType.field_19293,
		MemoryModuleType.field_22355,
		MemoryModuleType.field_22475,
		MemoryModuleType.field_18447,
		MemoryModuleType.field_18449,
		MemoryModuleType.field_22333,
		MemoryModuleType.field_25360,
		MemoryModuleType.field_18438
	);

	public PiglinBruteEntity(EntityType<? extends PiglinBruteEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 20;
	}

	public static DefaultAttributeContainer.Builder createPiglinBruteAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.field_23716, 50.0)
			.add(EntityAttributes.field_23719, 0.35F)
			.add(EntityAttributes.field_23721, 7.0);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess serverWorldAccess, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		PiglinBruteBrain.method_30250(this);
		this.initEquipment(difficulty);
		return super.initialize(serverWorldAccess, difficulty, spawnReason, entityData, entityTag);
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8825));
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
		return stack.getItem() == Items.field_8825 ? super.canGather(stack) : false;
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
		return this.isAttacking() && this.isHoldingTool() ? PiglinActivity.field_25165 : PiglinActivity.field_22386;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				PiglinBruteBrain.method_30251(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_25728;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.field_25731;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_25730;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.field_25732, 0.15F, 1.0F);
	}

	protected void playAngrySound() {
		this.playSound(SoundEvents.field_25729, 1.0F, this.getSoundPitch());
	}

	@Override
	protected void playZombificationSound() {
		this.playSound(SoundEvents.field_25733, 1.0F, this.getSoundPitch());
	}
}
