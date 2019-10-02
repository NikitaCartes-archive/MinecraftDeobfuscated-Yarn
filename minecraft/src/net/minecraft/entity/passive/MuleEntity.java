package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class MuleEntity extends AbstractDonkeyEntity {
	public MuleEntity(EntityType<? extends MuleEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return SoundEvents.ENTITY_MULE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_MULE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return SoundEvents.ENTITY_MULE_HURT;
	}

	@Override
	protected void playAddChestSound() {
		this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.MULE.create(this.world);
	}
}
