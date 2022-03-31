package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class AllayEntity extends PathAwareEntity implements InventoryOwner, GameEventListener {
	private static final boolean field_38404 = false;
	private static final int field_38405 = 16;
	private static final Vec3i field_38399 = new Vec3i(1, 1, 1);
	protected static final ImmutableList<SensorType<? extends Sensor<? super AllayEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.PATH,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
		MemoryModuleType.LIKED_PLAYER,
		MemoryModuleType.LIKED_NOTEBLOCK,
		MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
		MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS
	);
	private final EntityPositionSource positionSource = new EntityPositionSource(this, this.getStandingEyeHeight());
	private final EntityGameEventHandler gameEventHandler;
	private final SimpleInventory inventory = new SimpleInventory(1);

	public AllayEntity(EntityType<? extends AllayEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
		this.setCanPickUpLoot(this.canPickUpLoot());
		this.gameEventHandler = new EntityGameEventHandler(this);
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
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6F)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
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
	public void travel(Vec3d movementInput) {
		if (this.isTouchingWater()) {
			this.updateVelocity(0.02F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.8F));
		} else if (this.isInLava()) {
			this.updateVelocity(0.02F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.5));
		} else {
			float f;
			if (this.onGround) {
				f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.91F;
			} else {
				f = 0.91F;
			}

			float g = MathHelper.magnitude(0.6F) * MathHelper.magnitude(0.91F) / MathHelper.magnitude(f);
			this.updateVelocity(this.onGround ? 0.1F * g : 0.02F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply((double)f));
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
		AllayBrain.method_42661(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
	}

	@Override
	public boolean canPickUpLoot() {
		return !this.isItemPickupCoolingDown() && !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
	}

	private boolean isItemPickupCoolingDown() {
		return this.getBrain().isMemoryInState(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT);
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
		if (itemStack2.isEmpty() && !itemStack.isEmpty()) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(1);
			this.setStackInHand(Hand.MAIN_HAND, itemStack3);
			if (!player.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

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

	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@Override
	protected Vec3i getItemPickUpRangeExpander() {
		return field_38399;
	}

	@Override
	public boolean canGather(ItemStack stack) {
		ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
		return !itemStack.isEmpty() && itemStack.isItemEqual(stack) && this.inventory.canInsert(itemStack);
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (this.canGather(itemStack)) {
			SimpleInventory simpleInventory = this.getInventory();
			boolean bl = simpleInventory.canInsert(itemStack);
			if (!bl) {
				return;
			}

			this.triggerItemPickedUpByEntityCriteria(item);
			this.sendPickup(item, itemStack.getCount());
			ItemStack itemStack2 = simpleInventory.addStack(itemStack);
			if (itemStack2.isEmpty()) {
				item.discard();
			} else {
				itemStack.setCount(itemStack2.getCount());
			}
		}
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
	public PositionSource getPositionSource() {
		return this.positionSource;
	}

	@Override
	public int getRange() {
		return 16;
	}

	@Override
	public void updateEventHandler(BiConsumer<EntityGameEventHandler, ServerWorld> biConsumer) {
		if (this.world instanceof ServerWorld serverWorld) {
			biConsumer.accept(this.gameEventHandler, serverWorld);
		}
	}

	@Override
	public boolean listen(ServerWorld world, GameEvent event, @Nullable Entity entity, Vec3d pos) {
		if (event != GameEvent.NOTE_BLOCK_PLAY) {
			return false;
		} else {
			AllayBrain.method_42659(this, new BlockPos(pos));
			return true;
		}
	}
}
