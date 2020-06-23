package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PiglinEntity extends HostileEntity implements CrossbowUser {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> IMMUNE_TO_ZOMBIFICATION = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> dancing = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
	private static final EntityAttributeModifier BABY_SPEED_BOOST = new EntityAttributeModifier(
		BABY_SPEED_BOOST_ID, "Baby speed boost", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private int conversionTicks = 0;
	private final SimpleInventory inventory = new SimpleInventory(8);
	private boolean cannotHunt = false;
	protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES,
		SensorType.NEAREST_PLAYERS,
		SensorType.NEAREST_ITEMS,
		SensorType.HURT_BY,
		SensorType.INTERACTABLE_DOORS,
		SensorType.PIGLIN_SPECIFIC_SENSOR
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.INTERACTABLE_DOORS,
		MemoryModuleType.OPENED_DOORS,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
		MemoryModuleType.NEAREST_ADULT_PIGLINS,
		MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.ATTACK_COOLING_DOWN,
		MemoryModuleType.INTERACTION_TARGET,
		MemoryModuleType.PATH,
		MemoryModuleType.ANGRY_AT,
		MemoryModuleType.UNIVERSAL_ANGER,
		MemoryModuleType.AVOID_TARGET,
		MemoryModuleType.ADMIRING_ITEM,
		MemoryModuleType.ADMIRING_DISABLED,
		MemoryModuleType.CELEBRATE_LOCATION,
		MemoryModuleType.DANCING,
		MemoryModuleType.HUNTED_RECENTLY,
		MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
		MemoryModuleType.NEAREST_VISIBLE_BABY_PIGLIN,
		MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
		MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED,
		MemoryModuleType.RIDE_TARGET,
		MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
		MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
		MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN,
		MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
		MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
		MemoryModuleType.ATE_RECENTLY,
		MemoryModuleType.NEAREST_REPELLENT
	);

	public PiglinEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.setCanPickUpLoot(true);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.experiencePoints = 5;
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		if (this.isBaby()) {
			tag.putBoolean("IsBaby", true);
		}

		if (this.isImmuneToZombification()) {
			tag.putBoolean("IsImmuneToZombification", true);
		}

		if (this.cannotHunt) {
			tag.putBoolean("CannotHunt", true);
		}

		tag.putInt("TimeInOverworld", this.conversionTicks);
		tag.put("Inventory", this.inventory.getTags());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setBaby(tag.getBoolean("IsBaby"));
		this.setImmuneToZombification(tag.getBoolean("IsImmuneToZombification"));
		this.setCannotHunt(tag.getBoolean("CannotHunt"));
		this.conversionTicks = tag.getInt("TimeInOverworld");
		this.inventory.readTags(tag.getList("Inventory", 10));
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		this.inventory.clearToList().forEach(this::dropStack);
	}

	protected ItemStack addItem(ItemStack stack) {
		return this.inventory.addStack(stack);
	}

	protected boolean canInsertIntoInventory(ItemStack stack) {
		return this.inventory.canInsert(stack);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BABY, false);
		this.dataTracker.startTracking(CHARGING, false);
		this.dataTracker.startTracking(IMMUNE_TO_ZOMBIFICATION, false);
		this.dataTracker.startTracking(dancing, false);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (BABY.equals(data)) {
			this.calculateDimensions();
		}
	}

	public static DefaultAttributeContainer.Builder createPiglinAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
	}

	public static boolean canSpawn(EntityType<PiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
	}

	@Nullable
	@Override
	public EntityData initialize(
		WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (spawnReason != SpawnReason.STRUCTURE) {
			if (world.getRandom().nextFloat() < 0.2F) {
				this.setBaby(true);
			} else if (this.isAdult()) {
				this.equipStack(EquipmentSlot.MAINHAND, this.makeInitialWeapon());
			}
		}

		PiglinBrain.setHuntedRecently(this);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return false;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isPersistent();
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		if (this.isAdult()) {
			this.equipAtChance(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
			this.equipAtChance(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
			this.equipAtChance(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
			this.equipAtChance(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
		}
	}

	private void equipAtChance(EquipmentSlot slot, ItemStack stack) {
		if (this.world.random.nextFloat() < 0.1F) {
			this.equipStack(slot, stack);
		}
	}

	@Override
	protected Brain.Profile<PiglinEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return PiglinBrain.create(this, this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<PiglinEntity> getBrain() {
		return (Brain<PiglinEntity>)super.getBrain();
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ActionResult actionResult = super.interactMob(player, hand);
		if (actionResult.isAccepted()) {
			return actionResult;
		} else if (!this.world.isClient) {
			return PiglinBrain.playerInteract(this, player, hand);
		} else {
			boolean bl = PiglinBrain.method_27086(this, player.getStackInHand(hand)) && this.getActivity() != PiglinEntity.Activity.ADMIRING_ITEM;
			return bl ? ActionResult.SUCCESS : ActionResult.PASS;
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? -0.1 : -0.45;
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getHeight() * 0.92;
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		if (!this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.removeModifier(BABY_SPEED_BOOST);
			if (baby) {
				entityAttributeInstance.addTemporaryModifier(BABY_SPEED_BOOST);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(BABY);
	}

	public boolean isAdult() {
		return !this.isBaby();
	}

	public void setImmuneToZombification(boolean immuneToZombification) {
		this.getDataTracker().set(IMMUNE_TO_ZOMBIFICATION, immuneToZombification);
	}

	private boolean isImmuneToZombification() {
		return this.getDataTracker().get(IMMUNE_TO_ZOMBIFICATION);
	}

	private void setCannotHunt(boolean cannotHunt) {
		this.cannotHunt = cannotHunt;
	}

	protected boolean canHunt() {
		return !this.cannotHunt;
	}

	public boolean canConvert() {
		return !this.world.getDimension().isPiglinSafe() && !this.isImmuneToZombification() && !this.isAiDisabled();
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("piglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		PiglinBrain.tickActivities(this);
		if (this.canConvert()) {
			this.conversionTicks++;
		} else {
			this.conversionTicks = 0;
		}

		if (this.conversionTicks > 300) {
			this.method_30086(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED);
			this.zombify((ServerWorld)this.world);
		}
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return this.experiencePoints;
	}

	private void zombify(ServerWorld serverWorld) {
		PiglinBrain.method_25948(this);
		this.inventory.clearToList().forEach(this::dropStack);
		ZombifiedPiglinEntity zombifiedPiglinEntity = this.method_29243(EntityType.ZOMBIFIED_PIGLIN);
		zombifiedPiglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	private ItemStack makeInitialWeapon() {
		return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
	}

	private boolean isCharging() {
		return this.dataTracker.get(CHARGING);
	}

	@Override
	public void setCharging(boolean charging) {
		this.dataTracker.set(CHARGING, charging);
	}

	@Override
	public void postShoot() {
		this.despawnCounter = 0;
	}

	public PiglinEntity.Activity getActivity() {
		if (this.isDancing()) {
			return PiglinEntity.Activity.DANCING;
		} else if (PiglinBrain.isGoldenItem(this.getOffHandStack().getItem())) {
			return PiglinEntity.Activity.ADMIRING_ITEM;
		} else if (this.isAttacking() && this.isHoldingTool()) {
			return PiglinEntity.Activity.ATTACKING_WITH_MELEE_WEAPON;
		} else if (this.isCharging()) {
			return PiglinEntity.Activity.CROSSBOW_CHARGE;
		} else {
			return this.isAttacking() && this.isHolding(Items.CROSSBOW) ? PiglinEntity.Activity.CROSSBOW_HOLD : PiglinEntity.Activity.DEFAULT;
		}
	}

	public boolean isDancing() {
		return this.dataTracker.get(dancing);
	}

	public void setDancing(boolean dancing) {
		this.dataTracker.set(PiglinEntity.dancing, dancing);
	}

	private boolean isHoldingTool() {
		return this.getMainHandStack().getItem() instanceof ToolItem;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				PiglinBrain.onAttacked(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		this.shoot(this, 1.6F);
	}

	@Override
	public void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
		this.shoot(this, target, projectile, multiShotSpray, 1.6F);
	}

	@Override
	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return weapon == Items.CROSSBOW;
	}

	protected void equipToMainHand(ItemStack stack) {
		this.equipLootStack(EquipmentSlot.MAINHAND, stack);
	}

	protected void equipToOffHand(ItemStack stack) {
		if (stack.getItem() == PiglinBrain.BARTERING_ITEM) {
			this.equipStack(EquipmentSlot.OFFHAND, stack);
			this.updateDropChances(EquipmentSlot.OFFHAND);
		} else {
			this.equipLootStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	@Override
	public boolean canGather(ItemStack stack) {
		return this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && PiglinBrain.canGather(this, stack);
	}

	protected boolean method_24846(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		ItemStack itemStack = this.getEquippedStack(equipmentSlot);
		return this.prefersNewEquipment(stack, itemStack);
	}

	@Override
	protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
		if (EnchantmentHelper.hasBindingCurse(oldStack)) {
			return false;
		} else {
			boolean bl = PiglinBrain.isGoldenItem(newStack.getItem()) || newStack.getItem() == Items.CROSSBOW;
			boolean bl2 = PiglinBrain.isGoldenItem(oldStack.getItem()) || oldStack.getItem() == Items.CROSSBOW;
			if (bl && !bl2) {
				return true;
			} else if (!bl && bl2) {
				return false;
			} else {
				return this.isAdult() && newStack.getItem() != Items.CROSSBOW && oldStack.getItem() == Items.CROSSBOW
					? false
					: super.prefersNewEquipment(newStack, oldStack);
			}
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		this.method_29499(item);
		PiglinBrain.loot(this, item);
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.isBaby() && entity.getType() == EntityType.HOGLIN) {
			entity = this.method_26089(entity, 3);
		}

		return super.startRiding(entity, force);
	}

	private Entity method_26089(Entity entity, int i) {
		List<Entity> list = entity.getPassengerList();
		return i != 1 && !list.isEmpty() ? this.method_26089((Entity)list.get(0), i - 1) : entity;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.world.isClient ? null : (SoundEvent)PiglinBrain.method_30091(this).orElse(null);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PIGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PIGLIN_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15F, 1.0F);
	}

	protected void method_30086(SoundEvent soundEvent) {
		this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	public static enum Activity {
		ATTACKING_WITH_MELEE_WEAPON,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		ADMIRING_ITEM,
		DANCING,
		DEFAULT;
	}
}
