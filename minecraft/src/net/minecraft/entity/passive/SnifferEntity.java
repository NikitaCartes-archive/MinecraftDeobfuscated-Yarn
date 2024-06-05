package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
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
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
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
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SnifferEntity extends AnimalEntity {
	private static final int field_42656 = 1700;
	private static final int field_42657 = 6000;
	private static final int field_42658 = 30;
	private static final int field_42659 = 120;
	private static final int field_42661 = 48000;
	private static final float field_44785 = 0.4F;
	private static final EntityDimensions DIMENSIONS = EntityDimensions.changing(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - 0.4F)
		.withEyeHeight(0.81F);
	private static final TrackedData<SnifferEntity.State> STATE = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.SNIFFER_STATE);
	private static final TrackedData<Integer> FINISH_DIG_TIME = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public final AnimationState feelingHappyAnimationState = new AnimationState();
	public final AnimationState scentingAnimationState = new AnimationState();
	public final AnimationState sniffingAnimationState = new AnimationState();
	public final AnimationState diggingAnimationState = new AnimationState();
	public final AnimationState risingAnimationState = new AnimationState();

	public static DefaultAttributeContainer.Builder createSnifferAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F).add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0);
	}

	public SnifferEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.getNavigation().setCanSwim(true);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, -1.0F);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(STATE, SnifferEntity.State.IDLING);
		builder.add(FINISH_DIG_TIME, 0);
	}

	@Override
	public void onStartPathfinding() {
		super.onStartPathfinding();
		if (this.isOnFire() || this.isTouchingWater()) {
			this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		}
	}

	@Override
	public void onFinishPathfinding() {
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return this.getState() == SnifferEntity.State.DIGGING ? DIMENSIONS.scaled(this.getScaleFactor()) : super.getBaseDimensions(pose);
	}

	public boolean isSearching() {
		return this.getState() == SnifferEntity.State.SEARCHING;
	}

	public boolean isTempted() {
		return (Boolean)this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
	}

	public boolean canTryToDig() {
		return !this.isTempted()
			&& !this.isPanicking()
			&& !this.isTouchingWater()
			&& !this.isInLove()
			&& this.isOnGround()
			&& !this.hasVehicle()
			&& !this.isLeashed();
	}

	public boolean isDiggingOrSearching() {
		return this.getState() == SnifferEntity.State.DIGGING || this.getState() == SnifferEntity.State.SEARCHING;
	}

	private BlockPos getDigPos() {
		Vec3d vec3d = this.getDigLocation();
		return BlockPos.ofFloored(vec3d.getX(), this.getY() + 0.2F, vec3d.getZ());
	}

	private Vec3d getDigLocation() {
		return this.getPos().add(this.getRotationVecClient().multiply(2.25));
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
				case FEELING_HAPPY:
					this.feelingHappyAnimationState.startIfNotRunning(this.age);
					break;
				case SCENTING:
					this.scentingAnimationState.startIfNotRunning(this.age);
					break;
				case SNIFFING:
					this.sniffingAnimationState.startIfNotRunning(this.age);
				case SEARCHING:
				default:
					break;
				case DIGGING:
					this.diggingAnimationState.startIfNotRunning(this.age);
					break;
				case RISING:
					this.risingAnimationState.startIfNotRunning(this.age);
			}

			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
	}

	private void stopAnimations() {
		this.diggingAnimationState.stop();
		this.sniffingAnimationState.stop();
		this.risingAnimationState.stop();
		this.feelingHappyAnimationState.stop();
		this.scentingAnimationState.stop();
	}

	public SnifferEntity startState(SnifferEntity.State state) {
		switch (state) {
			case IDLING:
				this.setState(SnifferEntity.State.IDLING);
				break;
			case FEELING_HAPPY:
				this.playSound(SoundEvents.ENTITY_SNIFFER_HAPPY, 1.0F, 1.0F);
				this.setState(SnifferEntity.State.FEELING_HAPPY);
				break;
			case SCENTING:
				this.setState(SnifferEntity.State.SCENTING).playScentingSound();
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
		}

		return this;
	}

	private SnifferEntity playScentingSound() {
		this.playSound(SoundEvents.ENTITY_SNIFFER_SCENTING, 1.0F, this.isBaby() ? 1.3F : 1.0F);
		return this;
	}

	private SnifferEntity setDigging() {
		this.dataTracker.set(FINISH_DIG_TIME, this.age + 120);
		this.getWorld().sendEntityStatus(this, EntityStatuses.START_DIGGING);
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
			.map(BlockPos::ofFloored)
			.filter(pos -> this.getWorld().getWorldBorder().contains(pos))
			.map(BlockPos::down)
			.filter(this::isDiggable)
			.findFirst();
	}

	boolean canDig() {
		return !this.isPanicking()
			&& !this.isTempted()
			&& !this.isBaby()
			&& !this.isTouchingWater()
			&& this.isOnGround()
			&& !this.hasVehicle()
			&& this.isDiggable(this.getDigPos().down());
	}

	private boolean isDiggable(BlockPos pos) {
		return this.getWorld().getBlockState(pos).isIn(BlockTags.SNIFFER_DIGGABLE_BLOCK)
			&& this.getExploredPositions().noneMatch(globalPos -> GlobalPos.create(this.getWorld().getRegistryKey(), pos).equals(globalPos))
			&& (Boolean)Optional.ofNullable(this.getNavigation().findPathTo(pos, 1)).map(Path::reachesTarget).orElse(false);
	}

	private void dropSeeds() {
		if (!this.getWorld().isClient() && this.dataTracker.get(FINISH_DIG_TIME) == this.age) {
			ServerWorld serverWorld = (ServerWorld)this.getWorld();
			LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(LootTables.SNIFFER_DIGGING_GAMEPLAY);
			LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
				.add(LootContextParameters.ORIGIN, this.getDigLocation())
				.add(LootContextParameters.THIS_ENTITY, this)
				.build(LootContextTypes.GIFT);
			List<ItemStack> list = lootTable.generateLoot(lootContextParameterSet);
			BlockPos blockPos = this.getDigPos();

			for (ItemStack itemStack : list) {
				ItemEntity itemEntity = new ItemEntity(serverWorld, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
				itemEntity.setToDefaultPickupDelay();
				serverWorld.spawnEntity(itemEntity);
			}

			this.playSound(SoundEvents.ENTITY_SNIFFER_DROP_SEED, 1.0F, 1.0F);
		}
	}

	private SnifferEntity spawnDiggingParticles(AnimationState diggingAnimationState) {
		boolean bl = diggingAnimationState.getTimeRunning() > 1700L && diggingAnimationState.getTimeRunning() < 6000L;
		if (bl) {
			BlockPos blockPos = this.getDigPos();
			BlockState blockState = this.getWorld().getBlockState(blockPos.down());
			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				for (int i = 0; i < 30; i++) {
					Vec3d vec3d = Vec3d.ofCenter(blockPos).add(0.0, -0.65F, 0.0);
					this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
				}

				if (this.age % 10 == 0) {
					this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), blockState.getSoundGroup().getHitSound(), this.getSoundCategory(), 0.5F, 0.5F, false);
				}
			}
		}

		if (this.age % 10 == 0) {
			this.getWorld().emitGameEvent(GameEvent.ENTITY_ACTION, this.getDigPos(), GameEvent.Emitter.of(this));
		}

		return this;
	}

	private SnifferEntity addExploredPosition(BlockPos pos) {
		List<GlobalPos> list = (List<GlobalPos>)this.getExploredPositions().limit(20L).collect(Collectors.toList());
		list.add(0, GlobalPos.create(this.getWorld().getRegistryKey(), pos));
		this.getBrain().remember(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
		return this;
	}

	private Stream<GlobalPos> getExploredPositions() {
		return this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
	}

	@Override
	public void jump() {
		super.jump();
		double d = this.moveControl.getSpeed();
		if (d > 0.0) {
			double e = this.getVelocity().horizontalLengthSquared();
			if (e < 0.01) {
				this.updateVelocity(0.1F, new Vec3d(0.0, 0.0, 1.0));
			}
		}
	}

	@Override
	public void breed(ServerWorld world, AnimalEntity other) {
		ItemStack itemStack = new ItemStack(Items.SNIFFER_EGG);
		ItemEntity itemEntity = new ItemEntity(world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), itemStack);
		itemEntity.setToDefaultPickupDelay();
		this.breed(world, other, null);
		this.playSound(SoundEvents.BLOCK_SNIFFER_EGG_PLOP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
		world.spawnEntity(itemEntity);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		this.startState(SnifferEntity.State.IDLING);
		super.onDeath(damageSource);
	}

	@Override
	public void tick() {
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
		boolean bl = this.isBreedingItem(itemStack);
		ActionResult actionResult = super.interactMob(player, hand);
		if (actionResult.isAccepted() && bl) {
			this.getWorld()
				.playSoundFromEntity(null, this, this.getEatSound(itemStack), SoundCategory.NEUTRAL, 1.0F, MathHelper.nextBetween(this.getWorld().random, 0.8F, 1.2F));
		}

		return actionResult;
	}

	private void playSearchingSound() {
		if (this.getWorld().isClient() && this.age % 20 == 0) {
			this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SNIFFER_SEARCHING, this.getSoundCategory(), 1.0F, 1.0F, false);
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
	public int getMaxHeadRotation() {
		return 50;
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
	public Box getVisibilityBoundingBox() {
		return super.getVisibilityBoundingBox().expand(0.6F);
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
		this.getWorld().getProfiler().push("snifferBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().swap("snifferActivityUpdate");
		SnifferBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public static enum State {
		IDLING(0),
		FEELING_HAPPY(1),
		SCENTING(2),
		SNIFFING(3),
		SEARCHING(4),
		DIGGING(5),
		RISING(6);

		public static final IntFunction<SnifferEntity.State> INDEX_TO_VALUE = ValueLists.createIdToValueFunction(
			SnifferEntity.State::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		public static final PacketCodec<ByteBuf, SnifferEntity.State> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, SnifferEntity.State::getIndex);
		private final int index;

		private State(final int index) {
			this.index = index;
		}

		public int getIndex() {
			return this.index;
		}
	}
}
