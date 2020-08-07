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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PiglinEntity extends AbstractPiglinEntity implements CrossbowUser {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> DANCING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
	private static final EntityAttributeModifier BABY_SPEED_BOOST = new EntityAttributeModifier(
		BABY_SPEED_BOOST_ID, "Baby speed boost", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private final SimpleInventory inventory = new SimpleInventory(8);
	private boolean cannotHunt = false;
	protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.field_18466, SensorType.field_18467, SensorType.field_22358, SensorType.field_18469, SensorType.field_22359
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.field_18446,
		MemoryModuleType.field_26389,
		MemoryModuleType.field_18441,
		MemoryModuleType.field_18442,
		MemoryModuleType.field_18444,
		MemoryModuleType.field_22354,
		MemoryModuleType.field_22343,
		MemoryModuleType.field_25755,
		MemoryModuleType.field_22332,
		MemoryModuleType.field_18451,
		MemoryModuleType.field_18452,
		MemoryModuleType.field_18445,
		MemoryModuleType.field_19293,
		MemoryModuleType.field_22355,
		MemoryModuleType.field_22475,
		MemoryModuleType.field_18447,
		MemoryModuleType.field_18449,
		MemoryModuleType.field_22333,
		MemoryModuleType.field_25361,
		MemoryModuleType.field_22357,
		MemoryModuleType.field_22334,
		MemoryModuleType.field_25813,
		MemoryModuleType.field_22473,
		MemoryModuleType.field_25814,
		MemoryModuleType.field_22337,
		MemoryModuleType.field_25159,
		MemoryModuleType.field_22336,
		MemoryModuleType.field_22340,
		MemoryModuleType.field_25360,
		MemoryModuleType.field_22346,
		MemoryModuleType.field_22356,
		MemoryModuleType.field_22347,
		MemoryModuleType.field_22348,
		MemoryModuleType.field_22339,
		MemoryModuleType.field_22342,
		MemoryModuleType.field_22349,
		MemoryModuleType.field_22350,
		MemoryModuleType.field_22474
	);

	public PiglinEntity(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		if (this.isBaby()) {
			tag.putBoolean("IsBaby", true);
		}

		if (this.cannotHunt) {
			tag.putBoolean("CannotHunt", true);
		}

		tag.put("Inventory", this.inventory.getTags());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setBaby(tag.getBoolean("IsBaby"));
		this.setCannotHunt(tag.getBoolean("CannotHunt"));
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
		this.dataTracker.startTracking(DANCING, false);
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
			.add(EntityAttributes.field_23716, 16.0)
			.add(EntityAttributes.field_23719, 0.35F)
			.add(EntityAttributes.field_23721, 5.0);
	}

	public static boolean canSpawn(EntityType<PiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.method_10074()).isOf(Blocks.field_10541);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess serverWorldAccess, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (spawnReason != SpawnReason.field_16474) {
			if (serverWorldAccess.getRandom().nextFloat() < 0.2F) {
				this.setBaby(true);
			} else if (this.isAdult()) {
				this.equipStack(EquipmentSlot.field_6173, this.makeInitialWeapon());
			}
		}

		PiglinBrain.setHuntedRecently(this);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		return super.initialize(serverWorldAccess, difficulty, spawnReason, entityData, entityTag);
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
			this.equipAtChance(EquipmentSlot.field_6169, new ItemStack(Items.field_8862));
			this.equipAtChance(EquipmentSlot.field_6174, new ItemStack(Items.field_8678));
			this.equipAtChance(EquipmentSlot.field_6172, new ItemStack(Items.field_8416));
			this.equipAtChance(EquipmentSlot.field_6166, new ItemStack(Items.field_8753));
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
			boolean bl = PiglinBrain.isWillingToTrade(this, player.getStackInHand(hand)) && this.getActivity() != PiglinActivity.field_22385;
			return bl ? ActionResult.SUCCESS : ActionResult.PASS;
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getHeight() * 0.92;
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		if (!this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.field_23719);
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

	private void setCannotHunt(boolean cannotHunt) {
		this.cannotHunt = cannotHunt;
	}

	@Override
	protected boolean canHunt() {
		return !this.cannotHunt;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("piglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		PiglinBrain.tickActivities(this);
		super.mobTick();
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return this.experiencePoints;
	}

	@Override
	protected void zombify(ServerWorld world) {
		PiglinBrain.pickupItemWithOffHand(this);
		this.inventory.clearToList().forEach(this::dropStack);
		super.zombify(world);
	}

	private ItemStack makeInitialWeapon() {
		return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.field_8399) : new ItemStack(Items.field_8845);
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

	@Override
	public PiglinActivity getActivity() {
		if (this.isDancing()) {
			return PiglinActivity.field_25166;
		} else if (PiglinBrain.isGoldenItem(this.getOffHandStack().getItem())) {
			return PiglinActivity.field_22385;
		} else if (this.isAttacking() && this.isHoldingTool()) {
			return PiglinActivity.field_25165;
		} else if (this.isCharging()) {
			return PiglinActivity.field_22384;
		} else {
			return this.isAttacking() && this.isHolding(Items.field_8399) ? PiglinActivity.field_22383 : PiglinActivity.field_22386;
		}
	}

	public boolean isDancing() {
		return this.dataTracker.get(DANCING);
	}

	public void setDancing(boolean dancing) {
		this.dataTracker.set(DANCING, dancing);
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
		return weapon == Items.field_8399;
	}

	protected void equipToMainHand(ItemStack stack) {
		this.equipLootStack(EquipmentSlot.field_6173, stack);
	}

	protected void equipToOffHand(ItemStack stack) {
		if (stack.getItem() == PiglinBrain.BARTERING_ITEM) {
			this.equipStack(EquipmentSlot.field_6171, stack);
			this.updateDropChances(EquipmentSlot.field_6171);
		} else {
			this.equipLootStack(EquipmentSlot.field_6171, stack);
		}
	}

	@Override
	public boolean canGather(ItemStack stack) {
		return this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.canPickUpLoot() && PiglinBrain.canGather(this, stack);
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
			boolean bl = PiglinBrain.isGoldenItem(newStack.getItem()) || newStack.getItem() == Items.field_8399;
			boolean bl2 = PiglinBrain.isGoldenItem(oldStack.getItem()) || oldStack.getItem() == Items.field_8399;
			if (bl && !bl2) {
				return true;
			} else if (!bl && bl2) {
				return false;
			} else {
				return this.isAdult() && newStack.getItem() != Items.field_8399 && oldStack.getItem() == Items.field_8399
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
		if (this.isBaby() && entity.getType() == EntityType.field_21973) {
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
		return SoundEvents.field_22269;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_22267;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.field_22271, 0.15F, 1.0F);
	}

	protected void playSound(SoundEvent sound) {
		this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected void playZombificationSound() {
		this.playSound(SoundEvents.field_22272);
	}
}
