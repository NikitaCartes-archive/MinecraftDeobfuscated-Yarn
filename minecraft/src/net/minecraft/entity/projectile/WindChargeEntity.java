package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class WindChargeEntity extends AbstractWindChargeEntity {
	private static final WindChargeEntity.WindChargeExplosionBehavior EXPLOSION_BEHAVIOR = new WindChargeEntity.WindChargeExplosionBehavior();
	private static final float BASE_EXPLOSION_POWER = 1.0F;

	public WindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
		super(entityType, world);
	}

	public WindChargeEntity(PlayerEntity player, World world, double x, double y, double z) {
		super(EntityType.WIND_CHARGE, world, player, x, y, z);
	}

	public WindChargeEntity(World world, double x, double y, double z, double directionX, double directionY, double directionZ) {
		super(EntityType.WIND_CHARGE, x, y, z, directionX, directionY, directionZ, world);
	}

	@Override
	protected void createExplosion() {
		this.getWorld()
			.createExplosion(
				this,
				null,
				EXPLOSION_BEHAVIOR,
				this.getX(),
				this.getY(),
				this.getZ(),
				1.0F + 0.3F * this.random.nextFloat(),
				false,
				World.ExplosionSourceType.BLOW,
				ParticleTypes.GUST_EMITTER_SMALL,
				ParticleTypes.GUST_EMITTER_LARGE,
				SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
			);
	}

	public static final class WindChargeExplosionBehavior extends AbstractWindChargeEntity.WindChargeExplosionBehavior {
		@Override
		public float getKnockbackModifier() {
			return 1.1F;
		}
	}
}
