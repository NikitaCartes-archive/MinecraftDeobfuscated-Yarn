package net.minecraft.entity.passive;

import javax.annotation.Nullable;
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
	protected SoundEvent getAngrySound() {
		super.getAngrySound();
		return SoundEvents.ENTITY_MULE_ANGRY;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_MULE_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent method_28368() {
		return SoundEvents.ENTITY_MULE_EAT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
		return SoundEvents.ENTITY_MULE_HURT;
	}

	@Override
	protected void playAddChestSound() {
		this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return EntityType.MULE.create(this.world);
	}
}
