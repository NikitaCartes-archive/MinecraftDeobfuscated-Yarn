package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
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
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PiglinEntity extends HostileEntity implements CrossbowUser {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> IMMUNE_TO_ZOMBIFICATION = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UUID BABY_SPEED_BOOST_MODIFIER_ID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
	private static final EntityAttributeModifier BABY_SPEED_BOOST_MODIFIER = new EntityAttributeModifier(
		BABY_SPEED_BOOST_MODIFIER_ID, "Baby speed boost", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private int conversionTicks = 0;
	private final BasicInventory inventory = new BasicInventory(8);
	private static int field_22372 = 0;
	private static int field_22373 = 0;
	private static int field_22374 = 0;
	private static int field_22375 = 0;
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
		MemoryModuleType.AVOID_TARGET,
		MemoryModuleType.ADMIRING_ITEM,
		MemoryModuleType.ADMIRING_DISABLED,
		MemoryModuleType.CELEBRATE_LOCATION,
		MemoryModuleType.HUNTED_RECENTLY,
		MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
		MemoryModuleType.NEAREST_VISIBLE_BABY_PIGLIN,
		MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN,
		MemoryModuleType.RIDE_TARGET,
		MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
		MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
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
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
	}

	@Override
	public void remove() {
		super.remove();
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

		tag.putInt("TimeInOverworld", this.conversionTicks);
		tag.put("Inventory", this.inventory.getTags());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setBaby(tag.getBoolean("IsBaby"));
		this.setImmuneToZombification(tag.getBoolean("IsImmuneToZombification"));
		this.conversionTicks = tag.getInt("TimeInOverworld");
		this.inventory.readTags(tag.getList("Inventory", 10));
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		this.inventory.clearToList().forEach(this::dropStack);
	}

	protected ItemStack addItem(ItemStack stack) {
		return this.inventory.add(stack);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BABY, false);
		this.dataTracker.startTracking(CHARGING, false);
		this.dataTracker.startTracking(IMMUNE_TO_ZOMBIFICATION, false);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (BABY.equals(data)) {
			this.calculateDimensions();
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(16.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35F);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
	}

	public static boolean canSpawn(EntityType<PiglinEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		if (world.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		PiglinBrain.setHuntedRecently(this);
		this.initEquipment(difficulty);
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
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
			this.equipStack(EquipmentSlot.MAINHAND, this.makeInitialWeapon());
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
	protected Brain<?> deserializeBrain(Dynamic<?> data) {
		return PiglinBrain.create(this, data);
	}

	@Override
	public Brain<PiglinEntity> getBrain() {
		return (Brain<PiglinEntity>)super.getBrain();
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		if (super.interactMob(player, hand)) {
			return true;
		} else {
			return this.world.isClient ? false : PiglinBrain.playerInteract(this, player, hand);
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		if (!this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
			entityAttributeInstance.removeModifier(BABY_SPEED_BOOST_MODIFIER);
			if (baby) {
				entityAttributeInstance.addModifier(BABY_SPEED_BOOST_MODIFIER);
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

	private void setImmuneToZombification(boolean immuneToZombification) {
		this.getDataTracker().set(IMMUNE_TO_ZOMBIFICATION, immuneToZombification);
	}

	private boolean isImmuneToZombification() {
		return this.getDataTracker().get(IMMUNE_TO_ZOMBIFICATION);
	}

	public boolean canConvert() {
		return this.world.getDimension().getType() == DimensionType.OVERWORLD && !this.isImmuneToZombification() && !this.isAiDisabled();
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("piglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		PiglinBrain.tickActivities(this);
		PiglinBrain.playSoundAtChance(this);
		if (this.canConvert()) {
			this.conversionTicks++;
		} else {
			this.conversionTicks = 0;
		}

		if (this.conversionTicks > 300) {
			this.playZombifySound();
			this.zombify((ServerWorld)this.world);
		}
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return this.experiencePoints;
	}

	private void zombify(ServerWorld serverWorld) {
		ZombifiedPiglinEntity zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.create(serverWorld);
		if (zombifiedPiglinEntity != null) {
			zombifiedPiglinEntity.copyPositionAndRotation(this);
			zombifiedPiglinEntity.initialize(
				serverWorld, serverWorld.getLocalDifficulty(zombifiedPiglinEntity.getBlockPos()), SpawnType.CONVERSION, new ZombieEntity.ZombieData(this.isBaby()), null
			);
			zombifiedPiglinEntity.setBaby(this.isBaby());
			zombifiedPiglinEntity.setAiDisabled(this.isAiDisabled());
			PiglinBrain.method_25948(this);

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				if (!this.isAdult() || equipmentSlot != EquipmentSlot.MAINHAND) {
					ItemStack itemStack = this.getEquippedStack(equipmentSlot);
					if (!itemStack.isEmpty()) {
						zombifiedPiglinEntity.equipStack(equipmentSlot, itemStack.copy());
						zombifiedPiglinEntity.setEquipmentDropChance(equipmentSlot, this.getDropChance(equipmentSlot));
						itemStack.setCount(0);
					}
				}
			}

			if (this.hasCustomName()) {
				zombifiedPiglinEntity.setCustomName(this.getCustomName());
				zombifiedPiglinEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			if (this.isPersistent()) {
				zombifiedPiglinEntity.setPersistent();
			}

			this.remove();
			serverWorld.spawnEntity(zombifiedPiglinEntity);
			zombifiedPiglinEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
		}
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	private ItemStack makeInitialWeapon() {
		return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public PiglinEntity.Activity getActivity() {
		if (this.isHandSwinging) {
			return PiglinEntity.Activity.DEFAULT;
		} else if (PiglinBrain.isGoldenItem(this.getOffHandStack().getItem())) {
			return PiglinEntity.Activity.ADMIRING_ITEM;
		} else if (this.isCharging()) {
			return PiglinEntity.Activity.CROSSBOW_CHARGE;
		} else {
			return this.isHolding(Items.CROSSBOW) && this.isAttacking() ? PiglinEntity.Activity.CROSSBOW_HOLD : PiglinEntity.Activity.DEFAULT;
		}
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
	public boolean method_25938(RangedWeaponItem rangedWeaponItem) {
		return rangedWeaponItem == Items.CROSSBOW;
	}

	protected void equipToMainHand(ItemStack stack) {
		this.equipLootStack(EquipmentSlot.MAINHAND, stack);
	}

	protected void equipToOffHand(ItemStack stack) {
		if (stack.getItem() == Items.GOLD_INGOT) {
			this.equipStack(EquipmentSlot.OFFHAND, stack);
			this.method_25939(EquipmentSlot.OFFHAND);
		} else {
			this.equipLootStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	@Override
	public boolean canGather(ItemStack stack) {
		return this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && PiglinBrain.canGather(this, stack);
	}

	protected boolean method_24846(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		ItemStack itemStack = this.getEquippedStack(equipmentSlot);
		return this.isBetterItemFor(stack, itemStack, equipmentSlot);
	}

	@Override
	protected boolean isBetterItemFor(ItemStack current, ItemStack previous, EquipmentSlot slot) {
		if (PiglinBrain.isGoldenItem(previous.getItem())) {
			return false;
		} else if (this.isAdult() && previous.getItem() == Items.CROSSBOW) {
			return false;
		} else {
			return PiglinBrain.isGoldenItem(current.getItem()) ? true : super.isBetterItemFor(current, previous, slot);
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		PiglinBrain.loot(this, item);
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		int i = 3;
		Entity entity2 = this.method_26089(entity, i);
		return super.startRiding(entity2, force);
	}

	private Entity method_26089(Entity entity, int i) {
		List<Entity> list = entity.getPassengerList();
		return i != 1 && !list.isEmpty() ? this.method_26089((Entity)list.get(0), i - 1) : entity;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PIGLIN_AMBIENT;
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

	protected void playAdmireItemSound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, 1.0F, this.getSoundPitch());
	}

	@Override
	public void playAmbientSound() {
		if (PiglinBrain.hasIdleActivity(this)) {
			super.playAmbientSound();
		}
	}

	protected void playAngrySound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_ANGRY, 1.0F, this.getSoundPitch());
	}

	protected void playCelebrateSound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_CELEBRATE, 1.0F, this.getSoundPitch());
	}

	protected void playRetreatSound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_RETREAT, 1.0F, this.getSoundPitch());
	}

	protected void playJealousSound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_JEALOUS, 1.0F, this.getSoundPitch());
	}

	private void playZombifySound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getSoundPitch());
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Environment(EnvType.CLIENT)
	public static enum Activity {
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		ADMIRING_ITEM,
		DEFAULT;
	}
}
