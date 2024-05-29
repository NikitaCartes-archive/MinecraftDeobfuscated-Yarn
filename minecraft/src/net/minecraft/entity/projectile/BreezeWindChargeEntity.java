package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BreezeWindChargeEntity extends AbstractWindChargeEntity {
	private static final float EXPLOSION_POWER = 3.0F;

	public BreezeWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
		super(entityType, world);
	}

	public BreezeWindChargeEntity(BreezeEntity breeze, World world) {
		super(EntityType.BREEZE_WIND_CHARGE, world, breeze, breeze.getX(), breeze.getChargeY(), breeze.getZ());
	}

	@Override
	protected void createExplosion(Vec3d pos) {
		this.getWorld()
			.createExplosion(
				this,
				null,
				EXPLOSION_BEHAVIOR,
				pos.getX(),
				pos.getY(),
				pos.getZ(),
				3.0F,
				false,
				World.ExplosionSourceType.TRIGGER,
				ParticleTypes.GUST_EMITTER_SMALL,
				ParticleTypes.GUST_EMITTER_LARGE,
				SoundEvents.ENTITY_BREEZE_WIND_BURST
			);
	}
}
