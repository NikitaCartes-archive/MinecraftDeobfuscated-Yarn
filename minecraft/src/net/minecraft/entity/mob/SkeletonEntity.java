package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity extends AbstractSkeletonEntity {
	public SkeletonEntity(World world) {
		super(EntityType.SKELETON, world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15200;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15069;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14877;
	}

	@Override
	SoundEvent method_6998() {
		return SoundEvents.field_14548;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				this.method_5706(Items.field_8398);
			}
		}
	}

	@Override
	protected ProjectileEntity method_6996(float f) {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HAND_OFF);
		if (itemStack.getItem() == Items.field_8236) {
			SpectralArrowEntity spectralArrowEntity = new SpectralArrowEntity(this.world, this);
			spectralArrowEntity.method_7435(this, f);
			return spectralArrowEntity;
		} else {
			ProjectileEntity projectileEntity = super.method_6996(f);
			if (itemStack.getItem() == Items.field_8087 && projectileEntity instanceof ArrowEntity) {
				((ArrowEntity)projectileEntity).initFromStack(itemStack);
			}

			return projectileEntity;
		}
	}
}
