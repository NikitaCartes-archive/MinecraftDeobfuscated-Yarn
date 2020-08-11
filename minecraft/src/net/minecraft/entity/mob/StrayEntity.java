package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class StrayEntity extends AbstractSkeletonEntity {
	public StrayEntity(EntityType<? extends StrayEntity> entityType, World world) {
		super(entityType, world);
	}

	public static boolean canSpawn(EntityType<StrayEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return canSpawnInDark(type, world, spawnReason, pos, random) && (spawnReason == SpawnReason.SPAWNER || world.isSkyVisible(pos));
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
	protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
		PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier);
		if (persistentProjectileEntity instanceof ArrowEntity) {
			((ArrowEntity)persistentProjectileEntity).addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600));
		}

		return persistentProjectileEntity;
	}
}
