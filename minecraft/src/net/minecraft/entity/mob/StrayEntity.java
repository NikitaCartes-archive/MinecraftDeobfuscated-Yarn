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

	public static boolean canSpawn(EntityType<StrayEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return canSpawnInDark(type, world, spawnType, pos, random) && (spawnType == SpawnType.SPAWNER || world.isSkyVisible(pos));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_STRAY_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_STRAY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_STRAY_DEATH;
	}

	@Override
	SoundEvent getStepSound() {
		return SoundEvents.ENTITY_STRAY_STEP;
	}

	@Override
	protected ProjectileEntity createArrowProjectile(ItemStack arrow, float f) {
		ProjectileEntity projectileEntity = super.createArrowProjectile(arrow, f);
		if (projectileEntity instanceof ArrowEntity) {
			((ArrowEntity)projectileEntity).addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600));
		}

		return projectileEntity;
	}
}
