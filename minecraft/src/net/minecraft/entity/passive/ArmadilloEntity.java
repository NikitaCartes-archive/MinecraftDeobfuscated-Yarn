package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class ArmadilloEntity extends AnimalEntity {
	public static final float field_47778 = 0.6F;
	public static final float field_48332 = 32.5F;
	private static final int field_47782 = 5;
	private static final int field_47783 = 8;
	public static final int field_47779 = 60;
	private static final double field_48333 = 7.0;
	private static final double field_48334 = 2.0;
	private static final TrackedData<ArmadilloEntity.State> STATE = DataTracker.registerData(ArmadilloEntity.class, TrackedDataHandlerRegistry.ARMADILLO_STATE);
	private long currentStateTicks = 0L;
	public final AnimationState scaredAnimationState = new AnimationState();
	public final AnimationState rollingAnimationState = new AnimationState();
	private int nextScuteShedCooldown;

	public ArmadilloEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.getNavigation().setCanSwim(true);
		this.nextScuteShedCooldown = this.getNextScuteShedCooldown();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.ARMADILLO.create(world);
	}

	public static DefaultAttributeContainer.Builder createArmadilloAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.14);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(STATE, ArmadilloEntity.State.IDLE);
	}

	public boolean isNotIdle() {
		return this.dataTracker.get(STATE) != ArmadilloEntity.State.IDLE;
	}

	public boolean isRolledUp() {
		ArmadilloEntity.State state = this.getState();
		return state == ArmadilloEntity.State.SCARED || state == ArmadilloEntity.State.ROLLING && this.currentStateTicks > 5L;
	}

	public boolean shouldSwitchToScaredState() {
		return this.getState() == ArmadilloEntity.State.ROLLING && this.currentStateTicks > 8L;
	}

	private ArmadilloEntity.State getState() {
		return this.dataTracker.get(STATE);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public void setState(ArmadilloEntity.State state) {
		this.dataTracker.set(STATE, state);
	}

	private void setRolling(boolean rolling) {
		this.setState(rolling ? ArmadilloEntity.State.ROLLING : ArmadilloEntity.State.IDLE);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (STATE.equals(data)) {
			this.currentStateTicks = 0L;
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected Brain.Profile<ArmadilloEntity> createBrainProfile() {
		return ArmadilloBrain.createBrainProfile();
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return ArmadilloBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("armadilloBrain");
		((Brain<ArmadilloEntity>)this.brain).tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("armadilloActivityUpdate");
		ArmadilloBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		if (this.isAlive() && !this.isBaby() && --this.nextScuteShedCooldown <= 0) {
			this.playSound(SoundEvents.ENTITY_ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Items.ARMADILLO_SCUTE);
			this.emitGameEvent(GameEvent.ENTITY_PLACE);
			this.nextScuteShedCooldown = this.getNextScuteShedCooldown();
		}

		super.mobTick();
	}

	private int getNextScuteShedCooldown() {
		return this.random.nextInt(20 * TimeHelper.MINUTE_IN_SECONDS * 5) + 20 * TimeHelper.MINUTE_IN_SECONDS * 5;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient()) {
			this.updateAnimationStates();
		}

		if (this.isNotIdle()) {
			this.clampHeadYaw();
		}

		this.currentStateTicks++;
	}

	@Override
	public float getScaleFactor() {
		return this.isBaby() ? 0.6F : 1.0F;
	}

	private void updateAnimationStates() {
		switch (this.getState()) {
			case IDLE:
				this.scaredAnimationState.stop();
				this.rollingAnimationState.stop();
				break;
			case SCARED:
				this.scaredAnimationState.startIfNotRunning(this.age);
				this.rollingAnimationState.stop();
				break;
			case ROLLING:
				this.scaredAnimationState.stop();
				this.rollingAnimationState.startIfNotRunning(this.age);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return ArmadilloBrain.BREEDING_INGREDIENT.test(stack);
	}

	public static boolean canSpawn(EntityType<ArmadilloEntity> entityType, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.ARMADILLO_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}

	public boolean isEntityThreatening(LivingEntity entity) {
		if (!this.getBoundingBox().expand(7.0, 2.0, 7.0).intersects(entity.getBoundingBox())) {
			return false;
		} else if (entity.getType().isIn(EntityTypeTags.UNDEAD)) {
			return true;
		} else if (this.getAttacker() == entity) {
			return true;
		} else if (entity instanceof PlayerEntity playerEntity) {
			return playerEntity.isSpectator() ? false : playerEntity.isSprinting() || playerEntity.hasVehicle();
		} else {
			return false;
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("state", this.getState().asString());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setState(ArmadilloEntity.State.fromName(nbt.getString("state")));
	}

	public void startRolling() {
		if (!this.isNotIdle()) {
			this.stopMovement();
			this.resetLoveTicks();
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.playSound(SoundEvents.ENTITY_ARMADILLO_ROLL);
			this.setRolling(true);
		}
	}

	public void unroll() {
		if (this.isNotIdle()) {
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.playSound(SoundEvents.ENTITY_ARMADILLO_UNROLL);
			this.setRolling(false);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isNotIdle()) {
			amount = (amount - 1.0F) / 2.0F;
		}

		return super.damage(source, amount);
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		super.applyDamage(source, amount);
		if (source.getAttacker() instanceof LivingEntity) {
			this.getBrain().remember(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, 60L);
			if (this.canRollUp()) {
				this.startRolling();
			}
		} else {
			this.unroll();
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.BRUSH)) {
			if (!player.getAbilities().creativeMode) {
				itemStack.damage(16, player, getSlotForHand(hand));
			}

			this.brushScute();
			return ActionResult.success(this.getWorld().isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	public void brushScute() {
		this.dropStack(new ItemStack(Items.ARMADILLO_SCUTE));
		this.emitGameEvent(GameEvent.ENTITY_INTERACT);
		this.playSoundIfNotSilent(SoundEvents.ENTITY_ARMADILLO_BRUSH);
	}

	public boolean canRollUp() {
		return !this.isPanicking() && !this.isInFluid() && !this.isLeashed() && !this.hasVehicle() && !this.hasPassengers();
	}

	@Override
	public void lovePlayer(@Nullable PlayerEntity player) {
		super.lovePlayer(player);
		this.playSound(SoundEvents.ENTITY_ARMADILLO_EAT);
	}

	@Override
	public boolean canEat() {
		return super.canEat() && !this.isNotIdle();
	}

	@Override
	public SoundEvent getEatSound(ItemStack stack) {
		return SoundEvents.ENTITY_ARMADILLO_EAT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isNotIdle() ? null : SoundEvents.ENTITY_ARMADILLO_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ARMADILLO_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isNotIdle() ? SoundEvents.ENTITY_ARMADILLO_HURT_REDUCED : SoundEvents.ENTITY_ARMADILLO_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_ARMADILLO_STEP, 0.15F, 1.0F);
	}

	@Override
	public int getMaxHeadRotation() {
		return this.isNotIdle() ? 0 : 32;
	}

	@Override
	protected BodyControl createBodyControl() {
		return new BodyControl(this) {
			@Override
			public void tick() {
				if (!ArmadilloEntity.this.isNotIdle()) {
					super.tick();
				}
			}
		};
	}

	public static enum State implements StringIdentifiable {
		IDLE("idle", 0),
		ROLLING("rolling", 1),
		SCARED("scared", 2);

		private static final StringIdentifiable.EnumCodec<ArmadilloEntity.State> CODEC = StringIdentifiable.createCodec(ArmadilloEntity.State::values);
		private static final IntFunction<ArmadilloEntity.State> INDEX_TO_VALUE = ValueLists.createIdToValueFunction(
			ArmadilloEntity.State::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		public static final PacketCodec<ByteBuf, ArmadilloEntity.State> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, ArmadilloEntity.State::getIndex);
		private final String name;
		private final int index;

		private State(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public static ArmadilloEntity.State fromName(String name) {
			return (ArmadilloEntity.State)CODEC.byId(name, IDLE);
		}

		@Override
		public String asString() {
			return this.name;
		}

		private int getIndex() {
			return this.index;
		}
	}
}
