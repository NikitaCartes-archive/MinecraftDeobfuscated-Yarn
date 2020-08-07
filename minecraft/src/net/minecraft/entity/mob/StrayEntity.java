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

	public static boolean canSpawn(EntityType<StrayEntity> type, ServerWorldAccess serverWorldAccess, SpawnReason spawnReason, BlockPos pos, Random random) {
		return canSpawnInDark(type, serverWorldAccess, spawnReason, pos, random) && (spawnReason == SpawnReason.field_16469 || serverWorldAccess.isSkyVisible(pos));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15041;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
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
	protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
		PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier);
		if (persistentProjectileEntity instanceof ArrowEntity) {
			((ArrowEntity)persistentProjectileEntity).addEffect(new StatusEffectInstance(StatusEffects.field_5909, 600));
		}

		return persistentProjectileEntity;
	}
}
