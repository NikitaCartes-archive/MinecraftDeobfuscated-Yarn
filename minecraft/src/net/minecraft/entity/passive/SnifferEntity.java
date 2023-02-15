package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNodeType;
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
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SnifferEntity extends AnimalEntity {
	private static final int field_42656 = 1700;
	private static final int field_42657 = 6000;
	private static final int field_42658 = 30;
	private static final int field_42659 = 120;
	private static final int field_42660 = 10;
	private static final int field_42661 = 48000;
	private static final TrackedData<SnifferEntity.State> STATE = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.SNIFFER_STATE);
	private static final TrackedData<Integer> FINISH_DIG_TIME = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public final AnimationState walkingAnimationState = new AnimationState();
	public final AnimationState panickingAnimationState = new AnimationState();
	public final AnimationState feelingHappyAnimationState = new AnimationState();
	public final AnimationState scentingAnimationState = new AnimationState();
	public final AnimationState sniffingAnimationState = new AnimationState();
	public final AnimationState searchingAnimationState = new AnimationState();
	public final AnimationState diggingAnimationState = new AnimationState();
	public final AnimationState risingAnimationState = new AnimationState();

	public static DefaultAttributeContainer.Builder createSnifferAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F).add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0);
	}

	public SnifferEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.dataTracker.startTracking(STATE, SnifferEntity.State.IDLING);
		this.dataTracker.startTracking(FINISH_DIG_TIME, 0);
		this.getNavigation().setCanSwim(true);
		this.setPathfindingPenalty(PathNodeType.WATER, -2.0F);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.getDimensions(pose).height * 0.6F;
	}

	private boolean isMoving() {
		boolean bl = this.onGround || this.isInsideWaterOrBubbleColumn();
		return bl && this.getVelocity().horizontalLengthSquared() > 1.0E-6;
	}

	public boolean isPanicking() {
		return this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_PANICKING).isPresent();
	}

	public boolean isDiggingOrSearching() {
		return this.getState() == SnifferEntity.State.DIGGING || this.getState() == SnifferEntity.State.SEARCHING;
	}

	private BlockPos getDigPos() {
		Vec3d vec3d = this.getPos().add(this.getRotationVecClient().multiply(2.25));
		return new BlockPos(vec3d.getX(), this.getY(), vec3d.getZ());
	}

	private SnifferEntity.State getState() {
		return this.dataTracker.get(STATE);
	}

	private SnifferEntity setState(SnifferEntity.State state) {
		this.dataTracker.set(STATE, state);
		return this;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (STATE.equals(data)) {
			SnifferEntity.State state = this.getState();
			this.stopAnimations();
			switch (state) {
				case SCENTING:
					this.scentingAnimationState.startIfNotRunning(this.age);
					break;
				case SNIFFING:
					this.sniffingAnimationState.startIfNotRunning(this.age);
					break;
				case SEARCHING:
					this.searchingAnimationState.startIfNotRunning(this.age);
					break;
				case DIGGING:
					this.diggingAnimationState.startIfNotRunning(this.age);
					break;
				case RISING:
					this.risingAnimationState.startIfNotRunning(this.age);
					break;
				case FEELING_HAPPY:
					this.feelingHappyAnimationState.startIfNotRunning(this.age);
			}
		}

		super.onTrackedDataSet(data);
	}

	private void stopAnimations() {
		this.searchingAnimationState.stop();
		this.diggingAnimationState.stop();
		this.sniffingAnimationState.stop();
		this.risingAnimationState.stop();
		this.feelingHappyAnimationState.stop();
		this.scentingAnimationState.stop();
	}

	public SnifferEntity startState(SnifferEntity.State state) {
		switch (state) {
			case SCENTING:
				this.playSound(SoundEvents.ENTITY_SNIFFER_SCENTING, 1.0F, 1.0F);
				this.setState(SnifferEntity.State.SCENTING);
				break;
			case SNIFFING:
				this.playSound(SoundEvents.ENTITY_SNIFFER_SNIFFING, 1.0F, 1.0F);
				this.setState(SnifferEntity.State.SNIFFING);
				break;
			case SEARCHING:
				this.setState(SnifferEntity.State.SEARCHING);
				break;
			case DIGGING:
				this.setState(SnifferEntity.State.DIGGING).setDigging();
				break;
			case RISING:
				this.playSound(SoundEvents.ENTITY_SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
				this.setState(SnifferEntity.State.RISING);
				break;
			case FEELING_HAPPY:
				this.playSound(SoundEvents.ENTITY_SNIFFER_HAPPY, 1.0F, 1.0F);
				this.setState(SnifferEntity.State.FEELING_HAPPY);
				break;
			case IDLING:
				this.setState(SnifferEntity.State.IDLING);
		}

		return this;
	}

	private SnifferEntity setDigging() {
		this.dataTracker.set(FINISH_DIG_TIME, this.age + 120);
		this.world.sendEntityStatus(this, (byte)63);
		return this;
	}

	public SnifferEntity finishDigging(boolean explored) {
		if (explored) {
			this.addExploredPosition(this.getSteppingPos());
		}

		return this;
	}

	Optional<BlockPos> findSniffingTargetPos() {
		return IntStream.range(0, 5)
			.mapToObj(i -> FuzzyTargeting.find(this, 10 + 2 * i, 3))
			.filter(Objects::nonNull)
			.map(BlockPos::new)
			.map(BlockPos::down)
			.filter(this::isDiggable)
			.findFirst();
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	boolean canDig() {
		return !this.isPanicking() && !this.isBaby() && !this.isTouchingWater() && this.isDiggable(this.getDigPos().down());
	}

	private boolean isDiggable(BlockPos pos) {
		return this.world.getBlockState(pos).isIn(BlockTags.SNIFFER_DIGGABLE_BLOCK)
			&& this.world.getBlockState(pos.up()).isAir()
			&& this.getExploredPositions().noneMatch(pos::equals);
	}

	private void dropSeeds() {
		if (!this.world.isClient() && this.dataTracker.get(FINISH_DIG_TIME) == this.age) {
			ItemStack itemStack = new ItemStack(Items.TORCHFLOWER_SEEDS);
			BlockPos blockPos = this.getDigPos();
			ItemEntity itemEntity = new ItemEntity(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
			itemEntity.setToDefaultPickupDelay();
			this.world.spawnEntity(itemEntity);
			this.playSound(SoundEvents.ENTITY_SNIFFER_DROP_SEED, 1.0F, 1.0F);
		}
	}

	private SnifferEntity spawnDiggingParticles(AnimationState diggingAnimationState) {
		boolean bl = diggingAnimationState.getTimeRunning() > 1700L && diggingAnimationState.getTimeRunning() < 6000L;
		if (bl) {
			BlockState blockState = this.getSteppingBlockState();
			BlockPos blockPos = this.getDigPos();
			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				for (int i = 0; i < 30; i++) {
					Vec3d vec3d = Vec3d.ofCenter(blockPos);
					this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
				}

				if (this.age % 10 == 0) {
					this.world.playSound(this.getX(), this.getY(), this.getZ(), blockState.getSoundGroup().getHitSound(), this.getSoundCategory(), 0.5F, 0.5F, false);
				}
			}
		}

		return this;
	}

	private SnifferEntity addExploredPosition(BlockPos pos) {
		List<BlockPos> list = (List<BlockPos>)this.getExploredPositions().limit(20L).collect(Collectors.toList());
		list.add(0, pos);
		this.getBrain().remember(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
		return this;
	}

	private Stream<BlockPos> getExploredPositions() {
		return this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
	}

	@Override
	public void tick() {
		this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.isTouchingWater() ? 0.2F : 0.1F);
		if (!this.isMoving() && !this.isTouchingWater()) {
			this.panickingAnimationState.stop();
			this.walkingAnimationState.stop();
		} else if (this.isPanicking()) {
			this.walkingAnimationState.stop();
			this.panickingAnimationState.startIfNotRunning(this.age);
		} else {
			this.panickingAnimationState.stop();
			this.walkingAnimationState.startIfNotRunning(this.age);
		}

		switch (this.getState()) {
			case SEARCHING:
				this.playSearchingSound();
				break;
			case DIGGING:
				this.spawnDiggingParticles(this.diggingAnimationState).dropSeeds();
		}

		super.tick();
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		ActionResult actionResult = super.interactMob(player, hand);
		if (actionResult.isAccepted() && this.isBreedingItem(itemStack)) {
			this.world.playSoundFromEntity(null, this, this.getEatSound(itemStack), SoundCategory.NEUTRAL, 1.0F, MathHelper.nextBetween(this.world.random, 0.8F, 1.2F));
		}

		return actionResult;
	}

	private void playSearchingSound() {
		if (this.world.isClient() && this.age % 20 == 0) {
			this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SNIFFER_SEARCHING, this.getSoundCategory(), 1.0F, 1.0F, false);
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_SNIFFER_STEP, 0.15F, 1.0F);
	}

	@Override
	public SoundEvent getEatSound(ItemStack stack) {
		return SoundEvents.ENTITY_SNIFFER_EAT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return Set.of(SnifferEntity.State.DIGGING, SnifferEntity.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.ENTITY_SNIFFER_IDLE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SNIFFER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SNIFFER_DEATH;
	}

	@Override
	public void setBaby(boolean baby) {
		this.setBreedingAge(baby ? -48000 : 0);
	}

	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.SNIFFER.create(world);
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (!(other instanceof SnifferEntity snifferEntity)) {
			return false;
		} else {
			Set<SnifferEntity.State> set = Set.of(SnifferEntity.State.IDLING, SnifferEntity.State.SCENTING, SnifferEntity.State.FEELING_HAPPY);
			return set.contains(this.getState()) && set.contains(snifferEntity.getState()) && super.canBreedWith(other);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.SNIFFER_FOOD);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return SnifferBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<SnifferEntity> getBrain() {
		return (Brain<SnifferEntity>)super.getBrain();
	}

	@Override
	protected Brain.Profile<SnifferEntity> createBrainProfile() {
		return Brain.createProfile(SnifferBrain.MEMORY_MODULES, SnifferBrain.SENSORS);
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("snifferBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().swap("snifferActivityUpdate");
		SnifferBrain.updateActivities(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public static enum State {
		IDLING,
		FEELING_HAPPY,
		SCENTING,
		SNIFFING,
		SEARCHING,
		DIGGING,
		RISING;
	}
}
