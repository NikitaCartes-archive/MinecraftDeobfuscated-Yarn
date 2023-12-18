package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
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
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ArmadilloEntity extends AnimalEntity {
	public static final float field_47778 = 0.6F;
	private static final int field_47782 = 5;
	private static final int field_47783 = 8;
	public static final int field_47779 = 60;
	private static final double field_47784 = 7.0;
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

	public boolean isEntityThreatening(LivingEntity entity) {
		if (!new Box(this.getPos(), this.getPos()).expand(7.0).contains(entity.getPos())) {
			return false;
		} else if (entity.getType().isIn(EntityTypeTags.UNDEAD)) {
			return true;
		} else {
			if (entity instanceof PlayerEntity playerEntity && (playerEntity.isSprinting() || playerEntity.hasVehicle())) {
				return true;
			}

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
			this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_ROLL, this.getSoundCategory(), 1.0F, 1.0F);
			this.setRolling(true);
		}
	}

	public void unroll(boolean bl) {
		if (this.isNotIdle()) {
			this.emitGameEvent(GameEvent.ENTITY_ACTION);
			if (!bl) {
				this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_UNROLL, this.getSoundCategory(), 1.0F, 1.0F);
			}

			this.setRolling(false);
		}
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		this.unroll(true);
		super.applyDamage(source, amount);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.getWorld().isClient) {
			boolean bl = itemStack.isOf(Items.BRUSH);
			return bl ? ActionResult.CONSUME : ActionResult.PASS;
		} else if (itemStack.isOf(Items.BRUSH)) {
			if (!player.getAbilities().creativeMode) {
				itemStack.damage(16, player, playerx -> playerx.sendToolBreakStatus(hand));
			}

			this.brushScute();
			return ActionResult.SUCCESS;
		} else {
			return super.interactMob(player, hand);
		}
	}

	public void brushScute() {
		this.dropStack(new ItemStack(Items.ARMADILLO_SCUTE));
		this.emitGameEvent(GameEvent.ENTITY_INTERACT);
		this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_BRUSH, this.getSoundCategory(), 1.0F, 1.0F);
	}

	public boolean canRollUp() {
		return !this.isPanicking() && !this.isInFluid() && !this.isLeashed();
	}

	@Override
	public void lovePlayer(@Nullable PlayerEntity player) {
		super.lovePlayer(player);
		this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_EAT, this.getSoundCategory(), 1.0F, 1.0F);
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
		return SoundEvents.ENTITY_ARMADILLO_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_ARMADILLO_STEP, 0.15F, 1.0F);
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
		IDLE("idle"),
		ROLLING("rolling"),
		SCARED("scared");

		private static StringIdentifiable.EnumCodec<ArmadilloEntity.State> CODEC = StringIdentifiable.createCodec(ArmadilloEntity.State::values);
		final String name;

		private State(String name) {
			this.name = name;
		}

		public static ArmadilloEntity.State fromName(String name) {
			return (ArmadilloEntity.State)CODEC.byId(name, IDLE);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
