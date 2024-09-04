package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TadpoleEntity extends FishEntity {
	@VisibleForTesting
	public static int MAX_TADPOLE_AGE = Math.abs(-24000);
	public static final float WIDTH = 0.4F;
	public static final float HEIGHT = 0.3F;
	private int tadpoleAge;
	protected static final ImmutableList<SensorType<? extends Sensor<? super TadpoleEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.FROG_TEMPTATIONS
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.NEAREST_VISIBLE_ADULT,
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED,
		MemoryModuleType.TEMPTING_PLAYER,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.IS_PANICKING
	);

	public TadpoleEntity(EntityType<? extends FishEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
		this.lookControl = new YawAdjustingLookControl(this, 10);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	protected Brain.Profile<TadpoleEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return TadpoleBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<TadpoleEntity> getBrain() {
		return (Brain<TadpoleEntity>)super.getBrain();
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.ENTITY_TADPOLE_FLOP;
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("tadpoleBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("tadpoleActivityUpdate");
		TadpoleBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	public static DefaultAttributeContainer.Builder createTadpoleAttributes() {
		return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MOVEMENT_SPEED, 1.0).add(EntityAttributes.MAX_HEALTH, 6.0);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.getWorld().isClient) {
			this.setTadpoleAge(this.tadpoleAge + 1);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Age", this.tadpoleAge);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setTadpoleAge(nbt.getInt("Age"));
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_TADPOLE_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_TADPOLE_DEATH;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.isFrogFood(itemStack)) {
			this.eatSlimeBall(player, itemStack);
			return ActionResult.SUCCESS;
		} else {
			return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
		}
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public boolean isFromBucket() {
		return true;
	}

	@Override
	public void setFromBucket(boolean fromBucket) {
	}

	@Override
	public void copyDataToStack(ItemStack stack) {
		Bucketable.copyDataToStack(this, stack);
		NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, nbtCompound -> nbtCompound.putInt("Age", this.getTadpoleAge()));
	}

	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Bucketable.copyDataFromNbt(this, nbt);
		if (nbt.contains("Age")) {
			this.setTadpoleAge(nbt.getInt("Age"));
		}
	}

	@Override
	public ItemStack getBucketItem() {
		return new ItemStack(Items.TADPOLE_BUCKET);
	}

	@Override
	public SoundEvent getBucketFillSound() {
		return SoundEvents.ITEM_BUCKET_FILL_TADPOLE;
	}

	private boolean isFrogFood(ItemStack stack) {
		return stack.isIn(ItemTags.FROG_FOOD);
	}

	private void eatSlimeBall(PlayerEntity player, ItemStack stack) {
		this.decrementItem(player, stack);
		this.increaseAge(PassiveEntity.toGrowUpAge(this.getTicksUntilGrowth()));
		this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
	}

	private void decrementItem(PlayerEntity player, ItemStack stack) {
		stack.decrementUnlessCreative(1, player);
	}

	private int getTadpoleAge() {
		return this.tadpoleAge;
	}

	private void increaseAge(int seconds) {
		this.setTadpoleAge(this.tadpoleAge + seconds * 20);
	}

	private void setTadpoleAge(int tadpoleAge) {
		this.tadpoleAge = tadpoleAge;
		if (this.tadpoleAge >= MAX_TADPOLE_AGE) {
			this.growUp();
		}
	}

	private void growUp() {
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			this.convertTo(EntityType.FROG, EntityConversionContext.create(this, false, false), frog -> {
				frog.initialize(serverWorld, this.getWorld().getLocalDifficulty(frog.getBlockPos()), SpawnReason.CONVERSION, null);
				frog.setPersistent();
				frog.recalculateDimensions(this.getDimensions(this.getPose()));
				this.playSound(SoundEvents.ENTITY_TADPOLE_GROW_UP, 0.15F, 1.0F);
			});
		}
	}

	private int getTicksUntilGrowth() {
		return Math.max(0, MAX_TADPOLE_AGE - this.tadpoleAge);
	}

	@Override
	public boolean shouldDropXp() {
		return false;
	}
}
