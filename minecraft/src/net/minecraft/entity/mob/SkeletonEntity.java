package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity extends AbstractSkeletonEntity {
	private static final TrackedData<Boolean> field_28642 = DataTracker.registerData(SkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_28643;
	private int field_28644;

	public SkeletonEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(field_28642, false);
	}

	public boolean method_33590() {
		return this.getDataTracker().get(field_28642);
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.isAlive() && !this.isAiDisabled()) {
			if (this.method_33590()) {
				this.field_28644--;
				if (this.field_28644 < 0) {
					this.method_33591();
				}
			} else if (this.inPowderSnow) {
				this.field_28643++;
				if (this.field_28643 >= 600) {
					this.method_33589(300);
				}
			} else {
				this.field_28643 = -1;
			}
		}

		super.tick();
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("StrayConversionTime", this.method_33590() ? this.field_28644 : -1);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("StrayConversionTime", 99) && tag.getInt("StrayConversionTime") > -1) {
			this.method_33589(tag.getInt("StrayConversionTime"));
		}
	}

	private void method_33589(int i) {
		this.field_28644 = i;
		this.dataTracker.set(field_28642, true);
	}

	protected void method_33591() {
		this.method_29243(EntityType.STRAY, true);
		if (!this.isSilent()) {
			this.world.syncWorldEvent(null, 1048, this.getBlockPos(), 0);
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
		Entity entity = source.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				this.dropItem(Items.SKELETON_SKULL);
			}
		}
	}
}
