package net.minecraft.entity.raid;

import java.util.EnumSet;
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
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class RaiderEntity extends PatrolEntity {
	private static final Predicate<ItemEntity> OBTAINABLE_ILLAGER_BANNER_ITEM = itemEntity -> !itemEntity.cannotPickup()
			&& itemEntity.isValid()
			&& ItemStack.areEqual(itemEntity.method_6983(), Raid.field_16609);
	@Nullable
	protected Raid raid;
	private int wave;
	private boolean hasRaidGoal;
	private int outOfRaidCounter;

	protected RaiderEntity(EntityType<? extends RaiderEntity> entityType, World world) {
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
		if (this.field_6002 instanceof ServerWorld && this.isValid()) {
			Raid raid = this.getRaid();
			if (raid == null) {
				if (this.field_6002.getTime() % 20L == 0L) {
					Raid raid2 = ((ServerWorld)this.field_6002).method_19502(new BlockPos(this));
					if (raid2 != null && RaidManager.isValidRaiderFor(this, raid2)) {
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
		if (this.field_6002 instanceof ServerWorld) {
			if (this.getRaid() != null) {
				if (this.isPatrolLeader()) {
					this.getRaid().removeLeader(this.getWave());
				}

				this.getRaid().method_16510(this, false);
			}

			if (this.isPatrolLeader() && this.getRaid() == null && ((ServerWorld)this.field_6002).method_19502(new BlockPos(this)) == null) {
				ItemStack itemStack = this.method_6118(EquipmentSlot.HEAD);
				PlayerEntity playerEntity = null;
				Entity entity = damageSource.method_5529();
				if (entity instanceof PlayerEntity) {
					playerEntity = (PlayerEntity)entity;
				} else if (entity instanceof WolfEntity) {
					WolfEntity wolfEntity = (WolfEntity)entity;
					LivingEntity livingEntity = wolfEntity.getOwner();
					if (wolfEntity.isTamed() && livingEntity instanceof PlayerEntity) {
						playerEntity = (PlayerEntity)livingEntity;
					}
				}

				if (!itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.field_16609) && playerEntity != null) {
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

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(2, new RaiderEntity.PickupBannerAsLeaderGoal<>(this));
		this.field_6201.add(3, new MoveToRaidCenterGoal<>(this));
	}

	@Override
	public boolean hasNoRaid() {
		return !this.hasActiveRaid();
	}

	public void setRaid(Raid raid) {
		this.raid = raid;
	}

	@Nullable
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
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("Wave", this.wave);
		compoundTag.putBoolean("HasRaidGoal", this.hasRaidGoal);
		if (this.raid != null) {
			compoundTag.putInt("RaidId", this.raid.getRaidId());
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.wave = compoundTag.getInt("Wave");
		this.hasRaidGoal = compoundTag.getBoolean("HasRaidGoal");
		if (compoundTag.containsKey("RaidId", 3)) {
			if (this.field_6002 instanceof ServerWorld) {
				this.raid = ((ServerWorld)this.field_6002).method_19495().getRaid(compoundTag.getInt("RaidId"));
			}

			if (this.raid != null) {
				this.raid.method_16487(this.wave, this, false);
				if (this.isPatrolLeader()) {
					this.raid.method_16491(this.wave, this);
				}
			}
		}
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.method_6983();
		boolean bl = this.hasActiveRaid() && this.getRaid().method_16496(this.getWave()) != null;
		if (this.hasActiveRaid() && !bl && ItemStack.areEqual(itemStack, Raid.field_16609)) {
			EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
			ItemStack itemStack2 = this.method_6118(equipmentSlot);
			double d = (double)this.method_5929(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < d) {
				this.method_5775(itemStack2);
			}

			this.method_5673(equipmentSlot, itemStack);
			this.pickUpEntity(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
			this.getRaid().method_16491(this.getWave(), this);
			this.setPatrolLeader(true);
		} else {
			super.method_5949(itemEntity);
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
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			Raid raid = this.field_16603.getRaid();
			if (!RaiderEntity.this.hasActiveRaid()
				|| !this.field_16603.canLead()
				|| ItemStack.areEqual(this.field_16603.method_6118(EquipmentSlot.HEAD), Raid.field_16609)) {
				return false;
			} else if (raid.method_16496(this.field_16603.getWave()) != null && raid.method_16496(this.field_16603.getWave()).isValid()) {
				return false;
			} else {
				List<ItemEntity> list = this.field_16603
					.field_6002
					.method_8390(ItemEntity.class, this.field_16603.method_5829().expand(16.0, 8.0, 16.0), RaiderEntity.OBTAINABLE_ILLAGER_BANNER_ITEM);
				if (!list.isEmpty()) {
					this.field_16603.method_5942().startMovingTo((Entity)list.get(0), 1.2F);
				}

				return !list.isEmpty();
			}
		}

		@Override
		public void tick() {
			if (this.field_16603.method_5831(this.field_16603.method_5942().method_6355()) < 2.0) {
				List<ItemEntity> list = this.field_16603
					.field_6002
					.method_8390(ItemEntity.class, this.field_16603.method_5829().expand(4.0, 4.0, 4.0), RaiderEntity.OBTAINABLE_ILLAGER_BANNER_ITEM);
				if (!list.isEmpty()) {
					this.field_16603.method_5949((ItemEntity)list.get(0));
				}
			}
		}
	}
}
