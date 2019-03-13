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
	protected SoundEvent method_5994() {
		super.method_5994();
		return SoundEvents.field_15094;
	}

	@Override
	protected SoundEvent method_6002() {
		super.method_6002();
		return SoundEvents.field_14827;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		super.method_6011(damageSource);
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
		EntityType<? extends HorseBaseEntity> entityType = passiveEntity instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
		HorseBaseEntity horseBaseEntity = entityType.method_5883(this.field_6002);
		this.method_6743(passiveEntity, horseBaseEntity);
		return horseBaseEntity;
	}
}
