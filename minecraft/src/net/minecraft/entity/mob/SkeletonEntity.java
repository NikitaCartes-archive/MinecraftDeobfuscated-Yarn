package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class SkeletonEntity extends AbstractSkeletonEntity {
	private static final int TOTAL_CONVERSION_TIME = 300;
	private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(SkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final String STRAY_CONVERSION_TIME_KEY = "StrayConversionTime";
	private int inPowderSnowTime;
	private int conversionTime;

	public SkeletonEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(CONVERTING, false);
	}

	/**
	 * Returns whether this skeleton is currently converting to a stray.
	 */
	public boolean isConverting() {
		return this.getDataTracker().get(CONVERTING);
	}

	public void setConverting(boolean converting) {
		this.dataTracker.set(CONVERTING, converting);
	}

	@Override
	public boolean isShaking() {
		return this.isConverting();
	}

	@Override
	public void tick() {
		if (!this.getWorld().isClient && this.isAlive() && !this.isAiDisabled()) {
			if (this.inPowderSnow) {
				if (this.isConverting()) {
					this.conversionTime--;
					if (this.conversionTime < 0) {
						this.convertToStray();
					}
				} else {
					this.inPowderSnowTime++;
					if (this.inPowderSnowTime >= 140) {
						this.setConversionTime(300);
					}
				}
			} else {
				this.inPowderSnowTime = -1;
				this.setConverting(false);
			}
		}

		super.tick();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("StrayConversionTime", this.isConverting() ? this.conversionTime : -1);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("StrayConversionTime", NbtElement.NUMBER_TYPE) && nbt.getInt("StrayConversionTime") > -1) {
			this.setConversionTime(nbt.getInt("StrayConversionTime"));
		}
	}

	private void setConversionTime(int time) {
		this.conversionTime = time;
		this.setConverting(true);
	}

	/**
	 * Converts this skeleton to a stray and plays a sound if it is not silent.
	 */
	protected void convertToStray() {
		this.convertTo(EntityType.STRAY, true);
		if (!this.isSilent()) {
			this.getWorld().syncWorldEvent(null, WorldEvents.SKELETON_CONVERTS_TO_STRAY, this.getBlockPos(), 0);
		}
	}

	@Override
	public boolean canFreeze() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SKELETON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SKELETON_DEATH;
	}

	@Override
	SoundEvent getStepSound() {
		return SoundEvents.ENTITY_SKELETON_STEP;
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		if (source.getAttacker() instanceof CreeperEntity creeperEntity && creeperEntity.shouldDropHead()) {
			creeperEntity.onHeadDropped();
			this.dropItem(Items.SKELETON_SKULL);
		}
	}
}
