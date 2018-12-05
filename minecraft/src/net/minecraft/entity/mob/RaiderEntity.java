package net.minecraft.entity.mob;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3759;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.Raid;
import net.minecraft.sortme.RaidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldVillageManager;

public abstract class RaiderEntity extends PatrolEntity {
	private static final Predicate<ItemEntity> field_16600 = itemEntity -> !itemEntity.cannotPickup()
			&& itemEntity.isValid()
			&& ItemStack.areEqual(itemEntity.getStack(), Raid.illagerBanner);
	protected Raid raid;
	private int wave;
	private boolean hasRaidGoal;
	private int field_16997 = 0;

	protected RaiderEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	public abstract void method_16484(int i, boolean bl);

	public boolean hasRaidGoal() {
		return this.hasRaidGoal;
	}

	public void setHasRaidGoal(boolean bl) {
		this.hasRaidGoal = bl;
	}

	@Override
	public void updateMovement() {
		if (!this.world.isRemote) {
			Raid raid = this.getRaid();
			if (raid == null) {
				if (this.world.getTime() % 20L == 0L) {
					Raid raid2 = this.method_16837();
					if (raid2 != null && RaidState.method_16838(this, this.raid)) {
						raid2.method_16516(raid2.getGroupsSpawned(), this, null, true);
					}
				}
			} else {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity instanceof PlayerEntity || livingEntity instanceof GolemEntity) {
					this.field_6278 = 0;
				}
			}
		}

		super.updateMovement();
	}

	@Override
	protected void method_16827() {
		this.field_6278 += 2;
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!this.world.isRemote) {
			if (this.getRaid() != null) {
				if (this.method_16219()) {
					this.getRaid().method_16500(this.getWave());
				}

				this.getRaid().method_16510(this, false);
			}

			if (this.method_16219() && this.getRaid() == null && this.method_16837() == null) {
				ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
				if (!itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.illagerBanner) && damageSource.getAttacker() instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)damageSource.getAttacker();
					StatusEffectInstance statusEffectInstance = playerEntity.getPotionEffect(StatusEffects.field_16595);
					int i = 0;
					if (statusEffectInstance != null) {
						i = statusEffectInstance.getAmplifier();
						if (i < 8) {
							i++;
						}

						playerEntity.removePotionEffect(StatusEffects.field_16595);
					}

					StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.field_16595, 120000, i, false, false, true);
					playerEntity.addPotionEffect(statusEffectInstance2);
				}
			}
		}

		super.onDeath(damageSource);
	}

	@Nullable
	private Raid method_16837() {
		WorldVillageManager worldVillageManager = this.world.getVillageManager();
		Raid raid = null;
		if (worldVillageManager != null) {
			VillageProperties villageProperties = worldVillageManager.getNearestVillage(new BlockPos(this.x, this.y, this.z), 0);
			if (villageProperties != null) {
				raid = villageProperties.method_16469();
			}
		}

		return raid != null && raid.method_16832() ? raid : null;
	}

	public boolean method_16485() {
		return true;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(2, new RaiderEntity.class_3764<>(this));
		this.goalSelector.add(3, new class_3759<>(this));
	}

	@Override
	public boolean method_16472() {
		return !this.method_16482();
	}

	public void setRaid(Raid raid) {
		this.raid = raid;
	}

	public Raid getRaid() {
		return this.raid;
	}

	public boolean method_16482() {
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
			this.raid = this.world.getRaidState().getRaid(compoundTag.getInt("RaidId"));
			if (this.raid != null) {
				this.raid.method_16487(this.wave, this, false);
				if (this.method_16219()) {
					this.raid.method_16491(this.wave, this);
				}
			}
		}
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		boolean bl = this.method_16482() && this.getRaid().method_16496(this.getWave()) != null;
		if (this.method_16482() && !bl && ItemStack.areEqual(itemStack, Raid.illagerBanner)) {
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
			this.method_16217(true);
		} else {
			super.method_5949(itemEntity);
		}
	}

	@Override
	public boolean isPersistent() {
		return this.getRaid() != null || super.isPersistent();
	}

	@Override
	public boolean method_5974(double d) {
		return this.getRaid() != null || super.method_5974(d);
	}

	public int method_16836() {
		return this.field_16997;
	}

	public void method_16835(int i) {
		this.field_16997 = i;
	}

	public class class_3764<T extends RaiderEntity> extends Goal {
		private final T field_16603;

		public class_3764(T raiderEntity2) {
			this.field_16603 = raiderEntity2;
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			Raid raid = this.field_16603.getRaid();
			if (!RaiderEntity.this.method_16482()
				|| !this.field_16603.method_16485()
				|| ItemStack.areEqual(this.field_16603.getEquippedStack(EquipmentSlot.HEAD), Raid.illagerBanner)) {
				return false;
			} else if (raid.method_16496(this.field_16603.getWave()) != null && raid.method_16496(this.field_16603.getWave()).isValid()) {
				return false;
			} else {
				List<ItemEntity> list = this.field_16603
					.world
					.getEntities(ItemEntity.class, this.field_16603.getBoundingBox().expand(16.0, 8.0, 16.0), RaiderEntity.field_16600);
				if (!list.isEmpty()) {
					this.field_16603.getNavigation().method_6335((Entity)list.get(0), 1.2F);
				}

				return !list.isEmpty();
			}
		}

		@Override
		public void tick() {
			if (this.field_16603.squaredDistanceTo(this.field_16603.getNavigation().method_6355()) < 2.0) {
				List<ItemEntity> list = this.field_16603
					.world
					.getEntities(ItemEntity.class, this.field_16603.getBoundingBox().expand(4.0, 4.0, 4.0), RaiderEntity.field_16600);
				if (!list.isEmpty()) {
					this.field_16603.method_5949((ItemEntity)list.get(0));
				}
			}
		}
	}
}
