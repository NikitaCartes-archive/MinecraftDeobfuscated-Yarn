package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CrossbowUser;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4836 extends HostileEntity implements CrossbowUser {
	private static final Logger field_22382 = LogManager.getLogger();
	private static final TrackedData<Boolean> field_22377 = DataTracker.registerData(class_4836.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_22378 = DataTracker.registerData(class_4836.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UUID field_22379 = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
	private static final EntityAttributeModifier field_22380 = new EntityAttributeModifier(
		field_22379, "Baby speed boost", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private int field_22370 = 0;
	private final BasicInventory field_22371 = new BasicInventory(8);
	private static int field_22372 = 0;
	private static int field_22373 = 0;
	private static int field_22374 = 0;
	private static int field_22375 = 0;
	protected static final ImmutableList<SensorType<? extends Sensor<? super class_4836>>> field_22376 = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES,
		SensorType.NEAREST_PLAYERS,
		SensorType.NEAREST_ITEMS,
		SensorType.HURT_BY,
		SensorType.INTERACTABLE_DOORS,
		SensorType.PIGLIN_SPECIFIC_SENSOR
	);
	protected static final ImmutableList<MemoryModuleType<?>> field_22381 = ImmutableList.of(
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
		MemoryModuleType.INTERACTION_TARGET,
		MemoryModuleType.PATH,
		MemoryModuleType.ANGRY_AT,
		MemoryModuleType.AVOID_TARGET,
		MemoryModuleType.ADMIRING_ITEM,
		MemoryModuleType.WAS_HIT_BY_PLAYER,
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
		MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM
	);

	public class_4836(EntityType<? extends HostileEntity> entityType, World world) {
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

		tag.put("Inventory", method_24692(this.field_22371));
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setBaby(tag.getBoolean("IsBaby"));
		method_24693(this.field_22371, tag.getList("Inventory", 10));
	}

	private static ListTag method_24692(Inventory inventory) {
		ListTag listTag = new ListTag();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.toTag(new CompoundTag()));
			}
		}

		return listTag;
	}

	private static void method_24693(BasicInventory basicInventory, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompound(i));
			if (!itemStack.isEmpty()) {
				basicInventory.add(itemStack);
			}
		}
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		this.field_22371.method_24514().forEach(this::dropStack);
		ItemStack itemStack = this.getStackInHand(Hand.OFF_HAND);
		if (!itemStack.isEmpty()) {
			this.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
			this.dropStack(itemStack);
		}
	}

	protected ItemStack method_24711(ItemStack itemStack) {
		return this.field_22371.add(itemStack);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_22377, false);
		this.dataTracker.startTracking(field_22378, false);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (field_22377.equals(data)) {
			this.calculateDimensions();
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(16.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
	}

	public static boolean method_24695(EntityType<class_4836> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getBlockState(blockPos.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		if (world.getRandom().nextFloat() < 0.2F) {
			this.setBaby(true);
		}

		this.initEquipment(difficulty);
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	@Override
	protected boolean method_23734() {
		return false;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isPersistent();
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		if (this.method_24712()) {
			this.equipStack(EquipmentSlot.MAINHAND, this.method_24702());
			this.method_24696(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
			this.method_24696(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
			this.method_24696(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
			this.method_24696(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
		}
	}

	private void method_24696(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		if (this.world.random.nextFloat() < 0.1F) {
			this.equipStack(equipmentSlot, itemStack);
		}
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return class_4838.method_24732(this, dynamic);
	}

	@Override
	public Brain<class_4836> getBrain() {
		return (Brain<class_4836>)super.getBrain();
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		if (super.interactMob(player, hand)) {
			return true;
		} else {
			return this.world.isClient ? false : class_4838.method_24728(this, player, hand);
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	public void setBaby(boolean bl) {
		this.getDataTracker().set(field_22377, bl);
		if (!this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
			entityAttributeInstance.removeModifier(field_22380);
			if (bl) {
				entityAttributeInstance.addModifier(field_22380);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(field_22377);
	}

	public boolean method_24712() {
		return !this.isBaby();
	}

	public boolean method_24704() {
		return this.world.getDimension().getType() == DimensionType.OVERWORLD;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("piglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		class_4838.method_24722(this);
		class_4838.method_24749(this);
		if (class_4838.method_24770(this)) {
			this.setPersistent();
		}

		if (this.world.dimension.getType() == DimensionType.OVERWORLD) {
			this.field_22370++;
		} else {
			this.field_22370 = 0;
		}

		if (this.field_22370 > 300) {
			this.method_24701();
			this.method_24694((ServerWorld)this.world);
		}
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return this.experiencePoints;
	}

	private void method_24694(ServerWorld serverWorld) {
		ZombiePigmanEntity zombiePigmanEntity = EntityType.ZOMBIE_PIGMAN.create(serverWorld);
		zombiePigmanEntity.copyPositionAndRotation(this);
		zombiePigmanEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(zombiePigmanEntity)), SpawnType.CONVERSION, null, null);
		zombiePigmanEntity.setBaby(this.isBaby());
		this.remove();
		zombiePigmanEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			zombiePigmanEntity.setCustomName(this.getCustomName());
			zombiePigmanEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		serverWorld.spawnEntity(zombiePigmanEntity);
		zombiePigmanEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return (LivingEntity)this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	private ItemStack method_24702() {
		return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
	}

	@Environment(EnvType.CLIENT)
	private boolean method_24703() {
		return this.dataTracker.get(field_22378);
	}

	@Override
	public void setCharging(boolean charging) {
		this.dataTracker.set(field_22378, charging);
	}

	@Override
	public void method_24651() {
		this.despawnCounter = 0;
	}

	@Environment(EnvType.CLIENT)
	public class_4836.class_4837 method_24705() {
		if (this.isHandSwinging) {
			return class_4836.class_4837.field_22386;
		} else if (class_4838.method_24735(this.getOffHandStack().getItem())) {
			return class_4836.class_4837.field_22385;
		} else if (this.method_24703()) {
			return class_4836.class_4837.field_22384;
		} else {
			return this.method_24518(Items.CROSSBOW) && this.isAttacking() ? class_4836.class_4837.field_22383 : class_4836.class_4837.field_22386;
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				class_4838.method_24724(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
	}

	@Override
	public void attack(LivingEntity target, float f) {
		this.method_24654(this, 1.6F);
	}

	@Override
	public void shoot(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray) {
		this.method_24652(this, target, projectile, multiShotSpray, 1.6F);
	}

	@Override
	public boolean canGather(ItemStack itemStack) {
		return class_4838.method_24730(this, itemStack);
	}

	@Override
	protected boolean isBetterItemFor(ItemStack current, ItemStack previous, EquipmentSlot slot) {
		if (class_4838.method_24735(current.getItem()) && !class_4838.method_24735(previous.getItem())) {
			return true;
		} else {
			return !class_4838.method_24735(current.getItem()) && class_4838.method_24735(previous.getItem()) ? false : super.isBetterItemFor(current, previous, slot);
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		class_4838.method_24726(this, item);
	}

	protected boolean method_24706() {
		return this.getOffHandStack().isEmpty();
	}

	public boolean method_24707() {
		return this.getVehicle() != null;
	}

	protected float method_24708() {
		return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
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

	void method_24709() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, 1.0F, this.getSoundPitch());
	}

	@Override
	public void playAmbientSound() {
		if (class_4838.method_24766(this)) {
			super.playAmbientSound();
		}
	}

	void method_24710() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_ANGRY, 1.0F, this.getSoundPitch());
	}

	void method_24697() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_CELEBRATE, 1.0F, this.getSoundPitch());
	}

	void method_24698() {
		this.method_24699();
	}

	void method_24699() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_RETREAT, 1.0F, this.getSoundPitch());
	}

	void method_24700() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_JEALOUS, 1.0F, this.getSoundPitch());
	}

	void method_24701() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED, 1.0F, 1.0F);
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendVillagerAiDebugData(this);
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4837 {
		field_22383,
		field_22384,
		field_22385,
		field_22386;
	}
}
