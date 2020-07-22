package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
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
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PillagerEntity extends IllagerEntity implements CrossbowUser {
	private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final SimpleInventory inventory = new SimpleInventory(5);

	public PillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
		this.goalSelector.add(3, new CrossbowAttackGoal<>(this, 1.0, 8.0F));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0F));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	public static DefaultAttributeContainer.Builder createPillagerAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CHARGING, false);
	}

	@Override
	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return weapon == Items.CROSSBOW;
	}

	@Environment(EnvType.CLIENT)
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
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.toTag(new CompoundTag()));
			}
		}

		tag.put("Inventory", listTag);
	}

	@Environment(EnvType.CLIENT)
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
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		ListTag listTag = tag.getList("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompound(i));
			if (!itemStack.isEmpty()) {
				this.inventory.addStack(itemStack);
			}
		}

		this.setCanPickUpLoot(true);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		BlockState blockState = world.getBlockState(pos.down());
		return !blockState.isOf(Blocks.GRASS_BLOCK) && !blockState.isOf(Blocks.SAND) ? 0.5F - world.getBrightness(pos) : 10.0F;
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess serverWorldAccess, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		return super.initialize(serverWorldAccess, difficulty, spawnReason, entityData, entityTag);
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
	}

	@Override
	protected void method_30759(float f) {
		super.method_30759(f);
		if (this.random.nextInt(300) == 0) {
			ItemStack itemStack = this.getMainHandStack();
			if (itemStack.getItem() == Items.CROSSBOW) {
				Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack);
				map.putIfAbsent(Enchantments.PIERCING, 1);
				EnchantmentHelper.set(map, itemStack);
				this.equipStack(EquipmentSlot.MAINHAND, itemStack);
			}
		}
	}

	@Override
	public boolean isTeammate(Entity other) {
		if (super.isTeammate(other)) {
			return true;
		} else {
			return other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER
				? this.getScoreboardTeam() == null && other.getScoreboardTeam() == null
				: false;
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
	public void attack(LivingEntity target, float pullProgress) {
		this.shoot(this, 1.6F);
	}

	@Override
	public void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
		this.shoot(this, target, projectile, multiShotSpray, 1.6F);
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (itemStack.getItem() instanceof BannerItem) {
			super.loot(item);
		} else {
			Item item2 = itemStack.getItem();
			if (this.method_7111(item2)) {
				this.method_29499(item);
				ItemStack itemStack2 = this.inventory.addStack(itemStack);
				if (itemStack2.isEmpty()) {
					item.remove();
				} else {
					itemStack.setCount(itemStack2.getCount());
				}
			}
		}
	}

	private boolean method_7111(Item item) {
		return this.hasActiveRaid() && item == Items.WHITE_BANNER;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (super.equip(slot, item)) {
			return true;
		} else {
			int i = slot - 300;
			if (i >= 0 && i < this.inventory.size()) {
				this.inventory.setStack(i, item);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void addBonusForWave(int wave, boolean unused) {
		Raid raid = this.getRaid();
		boolean bl = this.random.nextFloat() <= raid.getEnchantmentChance();
		if (bl) {
			ItemStack itemStack = new ItemStack(Items.CROSSBOW);
			Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newHashMap();
			if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
				map.put(Enchantments.QUICK_CHARGE, 2);
			} else if (wave > raid.getMaxWaves(Difficulty.EASY)) {
				map.put(Enchantments.QUICK_CHARGE, 1);
			}

			map.put(Enchantments.MULTISHOT, 1);
			EnchantmentHelper.set(map, itemStack);
			this.equipStack(EquipmentSlot.MAINHAND, itemStack);
		}
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
	}
}
