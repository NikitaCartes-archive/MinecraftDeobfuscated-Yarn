package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class BoggedEntity extends AbstractSkeletonEntity {
	private static final int HARD_ATTACK_INTERVAL = 50;
	private static final int REGULAR_ATTACK_INTERVAL = 70;

	public static DefaultAttributeContainer.Builder createBoggedAttributes() {
		return AbstractSkeletonEntity.createAbstractSkeletonAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0);
	}

	public BoggedEntity(EntityType<? extends BoggedEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_BOGGED_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BOGGED_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BOGGED_DEATH;
	}

	@Override
	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_BOGGED_STEP;
	}

	@Override
	protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
		PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier);
		if (persistentProjectileEntity instanceof ArrowEntity arrowEntity) {
			arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.POISON, 100));
		}

		return persistentProjectileEntity;
	}

	@Override
	protected int getHardAttackInterval() {
		return 50;
	}

	@Override
	protected int getRegularAttackInterval() {
		return 70;
	}
}
