package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PillagerEntity extends IllagerEntity implements CrossbowUser, InventoryOwner {
	private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final int field_30478 = 5;
	private static final int field_30476 = 300;
	private final SimpleInventory inventory = new SimpleInventory(5);

	public PillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new FleeEntityGoal(this, CreakingEntity.class, 8.0F, 1.0, 1.2));
		this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
		this.goalSelector.add(3, new CrossbowAttackGoal<>(this, 1.0, 8.0F));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0F));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
		this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
	}

	public static DefaultAttributeContainer.Builder createPillagerAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.MOVEMENT_SPEED, 0.35F)
			.add(EntityAttributes.MAX_HEALTH, 24.0)
			.add(EntityAttributes.ATTACK_DAMAGE, 5.0)
			.add(EntityAttributes.FOLLOW_RANGE, 32.0);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(CHARGING, false);
	}

	@Override
	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return weapon == Items.CROSSBOW;
	}

	public boolean isCharging() {
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
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.writeInventory(nbt, this.getRegistryManager());
	}

	@Override
	public IllagerEntity.State getState() {
		if (this.isCharging()) {
			return IllagerEntity.State.CROSSBOW_CHARGE;
		} else if (this.isHolding(Items.CROSSBOW)) {
			return IllagerEntity.State.CROSSBOW_HOLD;
		} else {
			return this.isAttacking() ? IllagerEntity.State.ATTACKING : IllagerEntity.State.NEUTRAL;
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.readInventory(nbt, this.getRegistryManager());
		this.setCanPickUpLoot(true);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return 0.0F;
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		Random random = world.getRandom();
		this.initEquipment(random, difficulty);
		this.updateEnchantments(world, random, difficulty);
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
	}

	@Override
	protected void enchantMainHandItem(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty) {
		super.enchantMainHandItem(world, random, localDifficulty);
		if (random.nextInt(300) == 0) {
			ItemStack itemStack = this.getMainHandStack();
			if (itemStack.isOf(Items.CROSSBOW)) {
				EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(), EnchantmentProviders.PILLAGER_SPAWN_CROSSBOW, localDifficulty, random);
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PILLAGER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PILLAGER_HURT;
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		this.shoot(this, 1.6F);
	}

	@Override
	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@Override
	protected void loot(ServerWorld world, ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		if (itemStack.getItem() instanceof BannerItem) {
			super.loot(world, itemEntity);
		} else if (this.isRaidCaptain(itemStack)) {
			this.triggerItemPickedUpByEntityCriteria(itemEntity);
			ItemStack itemStack2 = this.inventory.addStack(itemStack);
			if (itemStack2.isEmpty()) {
				itemEntity.discard();
			} else {
				itemStack.setCount(itemStack2.getCount());
			}
		}
	}

	private boolean isRaidCaptain(ItemStack stack) {
		return this.hasActiveRaid() && stack.isOf(Items.WHITE_BANNER);
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		int i = mappedIndex - 300;
		return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
	}

	@Override
	public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
		Raid raid = this.getRaid();
		boolean bl = this.random.nextFloat() <= raid.getEnchantmentChance();
		if (bl) {
			ItemStack itemStack = new ItemStack(Items.CROSSBOW);
			RegistryKey<EnchantmentProvider> registryKey;
			if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
				registryKey = EnchantmentProviders.PILLAGER_POST_WAVE_5_RAID;
			} else if (wave > raid.getMaxWaves(Difficulty.EASY)) {
				registryKey = EnchantmentProviders.PILLAGER_POST_WAVE_3_RAID;
			} else {
				registryKey = null;
			}

			if (registryKey != null) {
				EnchantmentHelper.applyEnchantmentProvider(
					itemStack, world.getRegistryManager(), registryKey, world.getLocalDifficulty(this.getBlockPos()), this.getRandom()
				);
				this.equipStack(EquipmentSlot.MAINHAND, itemStack);
			}
		}
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
	}
}
