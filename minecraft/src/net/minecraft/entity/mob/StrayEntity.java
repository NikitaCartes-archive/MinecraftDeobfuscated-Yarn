package net.minecraft.entity.mob;

import net.minecraft.class_3730;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class StrayEntity extends AbstractSkeletonEntity {
	public StrayEntity(World world) {
		super(EntityType.STRAY, world);
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		return super.method_5979(iWorld, arg) && (arg == class_3730.field_16469 || iWorld.getSkyLightLevel(new BlockPos(this)));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15041;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14805;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14771;
	}

	@Override
	SoundEvent method_6998() {
		return SoundEvents.field_14540;
	}

	@Override
	protected ProjectileEntity method_6996(float f) {
		ProjectileEntity projectileEntity = super.method_6996(f);
		if (projectileEntity instanceof ArrowEntity) {
			((ArrowEntity)projectileEntity).addEffect(new StatusEffectInstance(StatusEffects.field_5909, 600));
		}

		return projectileEntity;
	}
}
