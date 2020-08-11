package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
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
	protected SoundEvent getAngrySound() {
		super.getAngrySound();
		return SoundEvents.ENTITY_DONKEY_ANGRY;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_DONKEY_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent getEatSound() {
		return SoundEvents.ENTITY_DONKEY_EAT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
		return SoundEvents.ENTITY_DONKEY_HURT;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return !(other instanceof DonkeyEntity) && !(other instanceof HorseEntity) ? false : this.canBreed() && ((HorseBaseEntity)other).canBreed();
		}
	}

	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		EntityType<? extends HorseBaseEntity> entityType = entity instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
		HorseBaseEntity horseBaseEntity = entityType.create(world);
		this.setChildAttributes(entity, horseBaseEntity);
		return horseBaseEntity;
	}
}
