package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public abstract class GolemEntity extends PathAwareEntity {
	protected GolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}
}
