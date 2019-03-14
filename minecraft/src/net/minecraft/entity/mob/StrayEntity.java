package net.minecraft.entity.mob;

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

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return super.canSpawn(iWorld, spawnType) && (spawnType == SpawnType.field_16469 || iWorld.isSkyVisible(new BlockPos(this)));
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
	protected ProjectileEntity method_6996(ItemStack itemStack, float f) {
		ProjectileEntity projectileEntity = super.method_6996(itemStack, f);
		if (projectileEntity instanceof ArrowEntity) {
			((ArrowEntity)projectileEntity).addEffect(new StatusEffectInstance(StatusEffects.field_5909, 600));
		}

		return projectileEntity;
	}
}
