package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;

public class WindChargeEntity extends AbstractWindChargeEntity {
	private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
		true, false, Optional.of(1.1F), Registries.BLOCK.getEntryList(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
	);
	private static final float EXPLOSION_POWER = 1.2F;

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
				1.2F,
				false,
				World.ExplosionSourceType.TRIGGER,
				ParticleTypes.GUST_EMITTER_SMALL,
				ParticleTypes.GUST_EMITTER_LARGE,
				SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
			);
	}
}
