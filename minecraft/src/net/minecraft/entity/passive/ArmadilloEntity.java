package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityStatuses;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
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
	public static final int field_47779 = 80;
	private static final double field_48333 = 7.0;
	private static final double field_48334 = 2.0;
	private static final TrackedData<ArmadilloEntity.State> STATE = DataTracker.registerData(ArmadilloEntity.class, TrackedDataHandlerRegistry.ARMADILLO_STATE);
	private long currentStateTicks = 0L;
	public final AnimationState unrollingAnimationState = new AnimationState();
	public final AnimationState rollingAnimationState = new AnimationState();
	public final AnimationState scaredAnimationState = new AnimationState();
	private int nextScuteShedCooldown;
	private boolean peeking = false;

	public ArmadilloEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.getNavigation().setCanSwim(true);
		this.nextScuteShedCooldown = this.getNextScuteShedCooldown();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.ARMADILLO.create(world, SpawnReason.BREEDING);
	}

	public static DefaultAttributeContainer.Builder createArmadilloAttributes() {
		return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 12.0).add(EntityAttributes.MOVEMENT_SPEED, 0.14);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(STATE, ArmadilloEntity.State.IDLE);
	}

	public boolean isNotIdle() {
		return this.dataTracker.get(STATE) != ArmadilloEntity.State.IDLE;
	}

	public boolean isRolledUp() {
		return this.getState().isRolledUp(this.currentStateTicks);
	}

	public boolean shouldSwitchToScaredState() {
		return this.getState() == ArmadilloEntity.State.ROLLING && this.currentStateTicks > (long)ArmadilloEntity.State.ROLLING.getLengthInTicks();
	}

	public ArmadilloEntity.State getState() {
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
				this.unrollingAnimationState.stop();
				this.rollingAnimationState.stop();
				this.scaredAnimationState.stop();
				break;
			case ROLLING:
				this.unrollingAnimationState.stop();
				this.rollingAnimationState.startIfNotRunning(this.age);
				this.scaredAnimationState.stop();
				break;
			case SCARED:
				this.unrollingAnimationState.stop();
				this.rollingAnimationState.stop();
				if (this.peeking) {
					this.scaredAnimationState.stop();
					this.peeking = false;
				}

				if (this.currentStateTicks == 0L) {
					this.scaredAnimationState.start(this.age);
					this.scaredAnimationState.skip(ArmadilloEntity.State.SCARED.getLengthInTicks(), 1.0F);
				} else {
					this.scaredAnimationState.startIfNotRunning(this.age);
				}
				break;
			case UNROLLING:
				this.unrollingAnimationState.startIfNotRunning(this.age);
				this.rollingAnimationState.stop();
				this.scaredAnimationState.stop();
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PEEKING && this.getWorld().isClient) {
			this.peeking = true;
			this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMADILLO_PEEK, this.getSoundCategory(), 1.0F, 1.0F, false);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.ARMADILLO_FOOD);
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
		nbt.putInt("scute_time", this.nextScuteShedCooldown);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setState(ArmadilloEntity.State.fromName(nbt.getString("state")));
		if (nbt.contains("scute_time")) {
			this.nextScuteShedCooldown = nbt.getInt("scute_time");
		}
	}

	public void startRolling() {
		if (!this.isNotIdle()) {
			this.stopMovement();
			this.resetLoveTicks();
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.playSound(SoundEvents.ENTITY_ARMADILLO_ROLL);
			this.setState(ArmadilloEntity.State.ROLLING);
		}
	}

	public void unroll() {
		if (this.isNotIdle()) {
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			this.playSound(SoundEvents.ENTITY_ARMADILLO_UNROLL_FINISH);
			this.setState(ArmadilloEntity.State.IDLE);
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
		if (!this.isAiDisabled() && !this.isDead()) {
			if (source.getAttacker() instanceof LivingEntity) {
				this.getBrain().remember(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, 80L);
				if (this.canRollUp()) {
					this.startRolling();
				}
			} else if (source.isIn(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) {
				this.unroll();
			}
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.BRUSH) && this.brushScute()) {
			itemStack.damage(16, player, getSlotForHand(hand));
			return ActionResult.SUCCESS;
		} else {
			return (ActionResult)(this.isNotIdle() ? ActionResult.FAIL : super.interactMob(player, hand));
		}
	}

	public boolean brushScute() {
		if (this.isBaby()) {
			return false;
		} else {
			this.dropStack(new ItemStack(Items.ARMADILLO_SCUTE));
			this.emitGameEvent(GameEvent.ENTITY_INTERACT);
			this.playSoundIfNotSilent(SoundEvents.ENTITY_ARMADILLO_BRUSH);
			return true;
		}
	}

	public boolean canRollUp() {
		return !this.isPanicking() && !this.isInFluid() && !this.isLeashed() && !this.hasVehicle() && !this.hasPassengers();
	}

	@Override
	public boolean canEat() {
		return super.canEat() && !this.isNotIdle();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isNotIdle() ? null : SoundEvents.ENTITY_ARMADILLO_AMBIENT;
	}

	@Override
	protected void playEatSound() {
		this.playSound(SoundEvents.ENTITY_ARMADILLO_EAT);
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
		IDLE("idle", false, 0, 0) {
			@Override
			public boolean isRolledUp(long currentStateTicks) {
				return false;
			}
		},
		ROLLING("rolling", true, 10, 1) {
			@Override
			public boolean isRolledUp(long currentStateTicks) {
				return currentStateTicks > 5L;
			}
		},
		SCARED("scared", true, 50, 2) {
			@Override
			public boolean isRolledUp(long currentStateTicks) {
				return true;
			}
		},
		UNROLLING("unrolling", true, 30, 3) {
			@Override
			public boolean isRolledUp(long currentStateTicks) {
				return currentStateTicks < 26L;
			}
		};

		private static final StringIdentifiable.EnumCodec<ArmadilloEntity.State> CODEC = StringIdentifiable.createCodec(ArmadilloEntity.State::values);
		private static final IntFunction<ArmadilloEntity.State> INDEX_TO_VALUE = ValueLists.createIdToValueFunction(
			ArmadilloEntity.State::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		public static final PacketCodec<ByteBuf, ArmadilloEntity.State> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, ArmadilloEntity.State::getIndex);
		private final String name;
		private final boolean runRollUpTask;
		private final int lengthInTicks;
		private final int index;

		State(final String name, final boolean runRollUpTask, final int lengthInTicks, final int index) {
			this.name = name;
			this.runRollUpTask = runRollUpTask;
			this.lengthInTicks = lengthInTicks;
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

		public abstract boolean isRolledUp(long currentStateTicks);

		public boolean shouldRunRollUpTask() {
			return this.runRollUpTask;
		}

		public int getLengthInTicks() {
			return this.lengthInTicks;
		}
	}
}
