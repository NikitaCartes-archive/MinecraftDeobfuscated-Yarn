package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AllayEntity extends PathAwareEntity implements InventoryOwner {
	private static final Logger field_39045 = LogUtils.getLogger();
	private static final int field_38405 = 16;
	private static final Vec3i ITEM_PICKUP_RANGE_EXPANDER = new Vec3i(1, 1, 1);
	private static final int field_39461 = 5;
	private static final float field_39462 = 55.0F;
	private static final float field_39463 = 15.0F;
	private static final float field_39451 = 0.5F;
	private static final Ingredient DUPLICATION_INGREDIENT = Ingredient.ofItems(Items.AMETHYST_SHARD);
	private static final int DUPLICATION_COOLDOWN = 6000;
	private static final int field_39679 = 3;
	private static final TrackedData<Boolean> DANCING = DataTracker.registerData(AllayEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> CAN_DUPLICATE = DataTracker.registerData(AllayEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final ImmutableList<SensorType<? extends Sensor<? super AllayEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.PATH,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
		MemoryModuleType.LIKED_PLAYER,
		MemoryModuleType.LIKED_NOTEBLOCK,
		MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
		MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
		MemoryModuleType.IS_PANICKING
	);
	public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(
		0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, 2.5F, 3.0F, 3.75F, 4.0F
	);
	private final EntityGameEventHandler<VibrationListener> gameEventHandler;
	private final VibrationListener.Callback listenerCallback;
	private final EntityGameEventHandler<AllayEntity.JukeboxEventListener> jukeboxEventHandler;
	private final SimpleInventory inventory = new SimpleInventory(1);
	@Nullable
	private BlockPos jukeboxPos;
	private long duplicationCooldown;
	private float field_38935;
	private float field_38936;
	private float field_39472;
	private float field_39473;
	private float field_39474;

	public AllayEntity(EntityType<? extends AllayEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
		this.setCanPickUpLoot(this.canPickUpLoot());
		PositionSource positionSource = new EntityPositionSource(this, this.getStandingEyeHeight());
		this.listenerCallback = new AllayEntity.VibrationListenerCallback();
		this.gameEventHandler = new EntityGameEventHandler<>(new VibrationListener(positionSource, 16, this.listenerCallback, null, 0.0F, 0));
		this.jukeboxEventHandler = new EntityGameEventHandler<>(new AllayEntity.JukeboxEventListener(positionSource, GameEvent.JUKEBOX_PLAY.getRange()));
	}

	@Override
	protected Brain.Profile<AllayEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return AllayBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<AllayEntity> getBrain() {
		return (Brain<AllayEntity>)super.getBrain();
	}

	public static DefaultAttributeContainer.Builder createAllayAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
			.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1F)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation birdNavigation = new BirdNavigation(this, world);
		birdNavigation.setCanPathThroughDoors(false);
		birdNavigation.setCanSwim(true);
		birdNavigation.setCanEnterOpenDoors(true);
		return birdNavigation;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(DANCING, false);
		this.dataTracker.startTracking(CAN_DUPLICATE, true);
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
			if (this.isTouchingWater()) {
				this.updateVelocity(0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				this.setVelocity(this.getVelocity().multiply(0.8F));
			} else if (this.isInLava()) {
				this.updateVelocity(0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				this.setVelocity(this.getVelocity().multiply(0.5));
			} else {
				this.updateVelocity(this.getMovementSpeed(), movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				this.setVelocity(this.getVelocity().multiply(0.91F));
			}
		}

		this.updateLimbs(this, false);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.6F;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source.getAttacker() instanceof PlayerEntity playerEntity) {
			Optional<UUID> optional = this.getBrain().getOptionalMemory(MemoryModuleType.LIKED_PLAYER);
			if (optional.isPresent() && playerEntity.getUuid().equals(optional.get())) {
				return false;
			}
		}

		return super.damage(source, amount);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.hasStackEquipped(EquipmentSlot.MAINHAND) ? SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ALLAY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ALLAY_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("allayBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("allayActivityUpdate");
		AllayBrain.updateActivities(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient && this.isAlive() && this.age % 10 == 0) {
			this.heal(1.0F);
		}

		if (this.isDancing() && this.shouldStopDancing() && this.age % 20 == 0) {
			this.setDancing(false);
			this.jukeboxPos = null;
		}

		this.tickDuplicationCooldown();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			this.field_38936 = this.field_38935;
			if (this.isHoldingItem()) {
				this.field_38935 = MathHelper.clamp(this.field_38935 + 1.0F, 0.0F, 5.0F);
			} else {
				this.field_38935 = MathHelper.clamp(this.field_38935 - 1.0F, 0.0F, 5.0F);
			}

			if (this.isDancing()) {
				this.field_39472++;
				this.field_39474 = this.field_39473;
				if (this.method_44360()) {
					this.field_39473++;
				} else {
					this.field_39473--;
				}

				this.field_39473 = MathHelper.clamp(this.field_39473, 0.0F, 15.0F);
			} else {
				this.field_39472 = 0.0F;
				this.field_39473 = 0.0F;
				this.field_39474 = 0.0F;
			}
		} else {
			this.gameEventHandler.getListener().tick(this.world);
		}
	}

	@Override
	public boolean canPickUpLoot() {
		return !this.isItemPickupCoolingDown() && this.isHoldingItem();
	}

	public boolean isHoldingItem() {
		return !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
	}

	@Override
	public boolean canEquip(ItemStack stack) {
		return false;
	}

	private boolean isItemPickupCoolingDown() {
		return this.getBrain().isMemoryInState(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT);
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
		if (this.isDancing() && this.matchesDuplicationIngredient(itemStack) && this.canDuplicate()) {
			this.duplicate();
			this.world.sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
			this.world.playSoundFromEntity(player, this, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.NEUTRAL, 2.0F, 1.0F);
			this.decrementStackUnlessInCreative(player, itemStack);
			return ActionResult.SUCCESS;
		} else if (itemStack2.isEmpty() && !itemStack.isEmpty()) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(1);
			this.setStackInHand(Hand.MAIN_HAND, itemStack3);
			this.decrementStackUnlessInCreative(player, itemStack);
			this.world.playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
			this.getBrain().remember(MemoryModuleType.LIKED_PLAYER, player.getUuid());
			return ActionResult.SUCCESS;
		} else if (!itemStack2.isEmpty() && hand == Hand.MAIN_HAND && itemStack.isEmpty()) {
			this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			this.world.playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
			this.swingHand(Hand.MAIN_HAND);

			for (ItemStack itemStack4 : this.getInventory().clearToList()) {
				LookTargetUtil.give(this, itemStack4, this.getPos());
			}

			this.getBrain().forget(MemoryModuleType.LIKED_PLAYER);
			player.giveItemStack(itemStack2);
			return ActionResult.SUCCESS;
		} else {
			return super.interactMob(player, hand);
		}
	}

	public void updateJukeboxPos(BlockPos jukeboxPos, boolean playing) {
		if (playing) {
			if (!this.isDancing()) {
				this.jukeboxPos = jukeboxPos;
				this.setDancing(true);
			}
		} else if (jukeboxPos.equals(this.jukeboxPos) || this.jukeboxPos == null) {
			this.jukeboxPos = null;
			this.setDancing(false);
		}
	}

	@Override
	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@Override
	protected Vec3i getItemPickUpRangeExpander() {
		return ITEM_PICKUP_RANGE_EXPANDER;
	}

	@Override
	public boolean canGather(ItemStack stack) {
		ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
		return !itemStack.isEmpty() && itemStack.isItemEqual(stack) && this.inventory.canInsert(stack);
	}

	@Override
	protected void loot(ItemEntity item) {
		InventoryOwner.pickUpItem(this, this, item);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public boolean hasWings() {
		return !this.isOnGround();
	}

	@Override
	public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
		if (this.world instanceof ServerWorld serverWorld) {
			callback.accept(this.gameEventHandler, serverWorld);
			callback.accept(this.jukeboxEventHandler, serverWorld);
		}
	}

	public boolean isDancing() {
		return this.dataTracker.get(DANCING);
	}

	public void setDancing(boolean dancing) {
		if (!this.world.isClient) {
			this.dataTracker.set(DANCING, dancing);
		}
	}

	private boolean shouldStopDancing() {
		return this.jukeboxPos == null
			|| !this.jukeboxPos.isWithinDistance(this.getPos(), (double)GameEvent.JUKEBOX_PLAY.getRange())
			|| !this.world.getBlockState(this.jukeboxPos).isOf(Blocks.JUKEBOX);
	}

	public float method_43397(float f) {
		return MathHelper.lerp(f, this.field_38936, this.field_38935) / 5.0F;
	}

	public boolean method_44360() {
		float f = this.field_39472 % 55.0F;
		return f < 15.0F;
	}

	public float method_44368(float f) {
		return MathHelper.lerp(f, this.field_39474, this.field_39473) / 15.0F;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		this.inventory.clearToList().forEach(this::dropStack);
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
		if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
			this.dropStack(itemStack);
			this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Inventory", this.inventory.toNbtList());
		VibrationListener.createCodec(this.listenerCallback)
			.encodeStart(NbtOps.INSTANCE, this.gameEventHandler.getListener())
			.resultOrPartial(field_39045::error)
			.ifPresent(nbtElement -> nbt.put("listener", nbtElement));
		nbt.putLong("DuplicationCooldown", this.duplicationCooldown);
		nbt.putBoolean("CanDuplicate", this.canDuplicate());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			VibrationListener.createCodec(this.listenerCallback)
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
				.resultOrPartial(field_39045::error)
				.ifPresent(vibrationListener -> this.gameEventHandler.setListener(vibrationListener, this.world));
		}

		this.duplicationCooldown = (long)nbt.getInt("DuplicationCooldown");
		this.dataTracker.set(CAN_DUPLICATE, nbt.getBoolean("CanDuplicate"));
	}

	@Override
	protected boolean shouldFollowLeash() {
		return false;
	}

	@Override
	public Iterable<BlockPos> getPotentialEscapePositions() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX - 0.5);
		int j = MathHelper.floor(box.maxX + 0.5);
		int k = MathHelper.floor(box.minZ - 0.5);
		int l = MathHelper.floor(box.maxZ + 0.5);
		int m = MathHelper.floor(box.minY - 0.5);
		int n = MathHelper.floor(box.maxY + 0.5);
		return BlockPos.iterate(i, m, k, j, n, l);
	}

	private void tickDuplicationCooldown() {
		if (this.duplicationCooldown > 0L) {
			this.duplicationCooldown--;
		}

		if (!this.world.isClient() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
			this.dataTracker.set(CAN_DUPLICATE, true);
		}
	}

	private boolean matchesDuplicationIngredient(ItemStack stack) {
		return DUPLICATION_INGREDIENT.test(stack);
	}

	private void duplicate() {
		AllayEntity allayEntity = EntityType.ALLAY.create(this.world);
		if (allayEntity != null) {
			allayEntity.refreshPositionAfterTeleport(this.getPos());
			allayEntity.setPersistent();
			allayEntity.startDuplicationCooldown();
			this.startDuplicationCooldown();
			this.world.spawnEntity(allayEntity);
		}
	}

	private void startDuplicationCooldown() {
		this.duplicationCooldown = 6000L;
		this.dataTracker.set(CAN_DUPLICATE, false);
	}

	private boolean canDuplicate() {
		return this.dataTracker.get(CAN_DUPLICATE);
	}

	private void decrementStackUnlessInCreative(PlayerEntity player, ItemStack stack) {
		if (!player.getAbilities().creativeMode) {
			stack.decrement(1);
		}
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)this.getStandingEyeHeight() * 0.6, (double)this.getWidth() * 0.1);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_BREEDING_PARTICLES) {
			for (int i = 0; i < 3; i++) {
				this.addHeartParticle();
			}
		} else {
			super.handleStatus(status);
		}
	}

	private void addHeartParticle() {
		double d = this.random.nextGaussian() * 0.02;
		double e = this.random.nextGaussian() * 0.02;
		double f = this.random.nextGaussian() * 0.02;
		this.world.addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
	}

	class JukeboxEventListener implements GameEventListener {
		private final PositionSource positionSource;
		private final int range;

		public JukeboxEventListener(PositionSource positionSource, int range) {
			this.positionSource = positionSource;
			this.range = range;
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public int getRange() {
			return this.range;
		}

		@Override
		public boolean listen(ServerWorld world, GameEvent.Message event) {
			if (event.getEvent() == GameEvent.JUKEBOX_PLAY) {
				AllayEntity.this.updateJukeboxPos(new BlockPos(event.getEmitterPos()), true);
				return true;
			} else if (event.getEvent() == GameEvent.JUKEBOX_STOP_PLAY) {
				AllayEntity.this.updateJukeboxPos(new BlockPos(event.getEmitterPos()), false);
				return true;
			} else {
				return false;
			}
		}
	}

	class VibrationListenerCallback implements VibrationListener.Callback {
		@Override
		public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
			if (AllayEntity.this.getWorld() == world && !AllayEntity.this.isRemoved() && !AllayEntity.this.isAiDisabled()) {
				Optional<GlobalPos> optional = AllayEntity.this.getBrain().getOptionalMemory(MemoryModuleType.LIKED_NOTEBLOCK);
				if (optional.isEmpty()) {
					return true;
				} else {
					GlobalPos globalPos = (GlobalPos)optional.get();
					return globalPos.getDimension().equals(world.getRegistryKey()) && globalPos.getPos().equals(pos);
				}
			} else {
				return false;
			}
		}

		@Override
		public void accept(
			ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance
		) {
			if (event == GameEvent.NOTE_BLOCK_PLAY) {
				AllayBrain.rememberNoteBlock(AllayEntity.this, new BlockPos(pos));
			}
		}

		@Override
		public TagKey<GameEvent> getTag() {
			return GameEventTags.ALLAY_CAN_LISTEN;
		}
	}
}
