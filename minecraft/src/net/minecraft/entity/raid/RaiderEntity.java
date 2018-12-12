package net.minecraft.entity.raid;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldVillageManager;

public abstract class RaiderEntity extends PatrolEntity {
	private static final Predicate<ItemEntity> OBTAINABLE_ILLAGER_BANNER_ITEM = itemEntity -> !itemEntity.cannotPickup()
			&& itemEntity.isValid()
			&& ItemStack.areEqual(itemEntity.getStack(), Raid.ILLAGER_BANNER);
	protected Raid raid;
	private int wave;
	private boolean hasRaidGoal;
	private int outOfRaidCounter = 0;

	protected RaiderEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	public abstract void addBonusForWave(int i, boolean bl);

	public boolean hasRaidGoal() {
		return this.hasRaidGoal;
	}

	public void setHasRaidGoal(boolean bl) {
		this.hasRaidGoal = bl;
	}

	@Override
	public void updateMovement() {
		if (!this.world.isClient) {
			Raid raid = this.getRaid();
			if (raid == null) {
				if (this.world.getTime() % 20L == 0L) {
					Raid raid2 = this.getOnGoingRaid();
					if (raid2 != null && RaidManager.isValidRaiderFor(this, this.raid)) {
						raid2.method_16516(raid2.getGroupsSpawned(), this, null, true);
					}
				}
			} else {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity instanceof PlayerEntity || livingEntity instanceof GolemEntity) {
					this.despawnCounter = 0;
				}
			}
		}

		super.updateMovement();
	}

	@Override
	protected void method_16827() {
		this.despawnCounter += 2;
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!this.world.isClient) {
			if (this.getRaid() != null) {
				if (this.isPatrolLeader()) {
					this.getRaid().removeLeader(this.getWave());
				}

				this.getRaid().method_16510(this, false);
			}

			if (this.isPatrolLeader() && this.getRaid() == null && this.getOnGoingRaid() == null) {
				ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
				if (!itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.ILLAGER_BANNER) && damageSource.getAttacker() instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)damageSource.getAttacker();
					StatusEffectInstance statusEffectInstance = playerEntity.getPotionEffect(StatusEffects.field_16595);
					int i = Raid.getBadOmenLevel(this.random, this.isRaidCenterSet());
					if (statusEffectInstance != null) {
						i += statusEffectInstance.getAmplifier();
						playerEntity.removePotionEffect(StatusEffects.field_16595);
					} else {
						i--;
					}

					i = MathHelper.clamp(i, 0, 5);
					StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.field_16595, 120000, i, false, false, true);
					playerEntity.addPotionEffect(statusEffectInstance2);
				}
			}
		}

		super.onDeath(damageSource);
	}

	@Nullable
	private Raid getOnGoingRaid() {
		WorldVillageManager worldVillageManager = this.world.getVillageManager();
		Raid raid = null;
		if (worldVillageManager != null) {
			VillageProperties villageProperties = worldVillageManager.getNearestVillage(new BlockPos(this.x, this.y, this.z), 0);
			if (villageProperties != null) {
				raid = villageProperties.method_16469();
			}
		}

		return raid != null && raid.isOnGoing() ? raid : null;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(2, new RaiderEntity.PickupBannerAsLeaderGoal<>(this));
		this.goalSelector.add(3, new MoveToRaidCenterGoal<>(this));
	}

	@Override
	public boolean hasNoRaid() {
		return !this.hasActiveRaid();
	}

	public void setRaid(Raid raid) {
		this.raid = raid;
	}

	public Raid getRaid() {
		return this.raid;
	}

	public boolean hasActiveRaid() {
		return this.getRaid() != null && this.getRaid().isActive();
	}

	public void setWave(int i) {
		this.wave = i;
	}

	public int getWave() {
		return this.wave;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Wave", this.wave);
		compoundTag.putBoolean("HasRaidGoal", this.hasRaidGoal);
		if (this.raid != null) {
			compoundTag.putInt("RaidId", this.raid.getRaidId());
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.wave = compoundTag.getInt("Wave");
		this.hasRaidGoal = compoundTag.getBoolean("HasRaidGoal");
		if (compoundTag.containsKey("RaidId", 3)) {
			this.raid = this.world.getRaidManager().getRaid(compoundTag.getInt("RaidId"));
			if (this.raid != null) {
				this.raid.method_16487(this.wave, this, false);
				if (this.isPatrolLeader()) {
					this.raid.method_16491(this.wave, this);
				}
			}
		}
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		boolean bl = this.hasActiveRaid() && this.getRaid().method_16496(this.getWave()) != null;
		if (this.hasActiveRaid() && !bl && ItemStack.areEqual(itemStack, Raid.ILLAGER_BANNER)) {
			EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
			ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
			double d = (double)this.method_5929(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < d) {
				this.dropStack(itemStack2);
			}

			this.setEquippedStack(equipmentSlot, itemStack);
			this.method_6103(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
			this.getRaid().method_16491(this.getWave(), this);
			this.setPatrolLeader(true);
		} else {
			super.pickupItem(itemEntity);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return this.getRaid() != null || super.canImmediatelyDespawn(d);
	}

	@Override
	public boolean cannotDespawn() {
		return this.getRaid() != null;
	}

	public int getOutOfRaidCounter() {
		return this.outOfRaidCounter;
	}

	public void setOutOfRaidCounter(int i) {
		this.outOfRaidCounter = i;
	}

	public class PickupBannerAsLeaderGoal<T extends RaiderEntity> extends Goal {
		private final T field_16603;

		public PickupBannerAsLeaderGoal(T raiderEntity2) {
			this.field_16603 = raiderEntity2;
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			Raid raid = this.field_16603.getRaid();
			if (!RaiderEntity.this.hasActiveRaid()
				|| !this.field_16603.canLead()
				|| ItemStack.areEqual(this.field_16603.getEquippedStack(EquipmentSlot.HEAD), Raid.ILLAGER_BANNER)) {
				return false;
			} else if (raid.method_16496(this.field_16603.getWave()) != null && raid.method_16496(this.field_16603.getWave()).isValid()) {
				return false;
			} else {
				List<ItemEntity> list = this.field_16603
					.world
					.getEntities(ItemEntity.class, this.field_16603.getBoundingBox().expand(16.0, 8.0, 16.0), RaiderEntity.OBTAINABLE_ILLAGER_BANNER_ITEM);
				if (!list.isEmpty()) {
					this.field_16603.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
				}

				return !list.isEmpty();
			}
		}

		@Override
		public void tick() {
			if (this.field_16603.squaredDistanceTo(this.field_16603.getNavigation().method_6355()) < 2.0) {
				List<ItemEntity> list = this.field_16603
					.world
					.getEntities(ItemEntity.class, this.field_16603.getBoundingBox().expand(4.0, 4.0, 4.0), RaiderEntity.OBTAINABLE_ILLAGER_BANNER_ITEM);
				if (!list.isEmpty()) {
					this.field_16603.pickupItem((ItemEntity)list.get(0));
				}
			}
		}
	}
}
