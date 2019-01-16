package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class DonkeyEntity extends AbstractDonkeyEntity {
	public DonkeyEntity(World world) {
		super(EntityType.DONKEY, world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return SoundEvents.field_15094;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.field_14827;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return SoundEvents.field_14781;
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (animalEntity == this) {
			return false;
		} else {
			return !(animalEntity instanceof DonkeyEntity) && !(animalEntity instanceof HorseEntity)
				? false
				: this.method_6734() && ((HorseBaseEntity)animalEntity).method_6734();
		}
	}

	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		HorseBaseEntity horseBaseEntity = (HorseBaseEntity)(passiveEntity instanceof HorseEntity ? new MuleEntity(this.world) : new DonkeyEntity(this.world));
		this.method_6743(passiveEntity, horseBaseEntity);
		return horseBaseEntity;
	}
}
