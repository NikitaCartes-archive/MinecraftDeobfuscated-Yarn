package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class StrayEntity extends AbstractSkeletonEntity {
	public StrayEntity(EntityType<? extends StrayEntity> entityType, World world) {
		super(entityType, world);
	}

	public static boolean method_20686(EntityType<StrayEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return method_20680(entityType, iWorld, spawnType, blockPos, random) && (spawnType == SpawnType.field_16469 || iWorld.isSkyVisible(blockPos));
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
	SoundEvent getStepSound() {
		return SoundEvents.field_14540;
	}

	@Override
	protected ProjectileEntity createArrowProjectile(ItemStack itemStack, float f) {
		ProjectileEntity projectileEntity = super.createArrowProjectile(itemStack, f);
		if (projectileEntity instanceof ArrowEntity) {
			((ArrowEntity)projectileEntity).addEffect(new StatusEffectInstance(StatusEffects.field_5909, 600));
		}

		return projectileEntity;
	}
}
