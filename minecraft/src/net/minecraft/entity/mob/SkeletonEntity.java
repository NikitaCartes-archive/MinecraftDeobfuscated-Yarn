package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity extends AbstractSkeletonEntity {
	public SkeletonEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15200;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15069;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14877;
	}

	@Override
	SoundEvent method_6998() {
		return SoundEvents.field_14548;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.method_5529();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				this.method_5706(Items.SKELETON_SKULL);
			}
		}
	}
}
