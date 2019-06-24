package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class DonkeyEntity extends AbstractDonkeyEntity {
	public DonkeyEntity(EntityType<? extends DonkeyEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return SoundEvents.ENTITY_DONKEY_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_DONKEY_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return SoundEvents.ENTITY_DONKEY_HURT;
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (animalEntity == this) {
			return false;
		} else {
			return !(animalEntity instanceof DonkeyEntity) && !(animalEntity instanceof HorseEntity)
				? false
				: this.canBreed() && ((HorseBaseEntity)animalEntity).canBreed();
		}
	}

	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		EntityType<? extends HorseBaseEntity> entityType = passiveEntity instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
		HorseBaseEntity horseBaseEntity = entityType.create(this.world);
		this.setChildAttributes(passiveEntity, horseBaseEntity);
		return horseBaseEntity;
	}
}
