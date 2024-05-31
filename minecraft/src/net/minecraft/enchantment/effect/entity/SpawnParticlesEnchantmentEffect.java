package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;

public record SpawnParticlesEnchantmentEffect(
	ParticleEffect particle,
	SpawnParticlesEnchantmentEffect.PositionSource horizontalPosition,
	SpawnParticlesEnchantmentEffect.PositionSource verticalPosition,
	SpawnParticlesEnchantmentEffect.VelocitySource horizontalVelocity,
	SpawnParticlesEnchantmentEffect.VelocitySource verticalVelocity,
	FloatProvider speed
) implements EnchantmentEntityEffect {
	public static final MapCodec<SpawnParticlesEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					ParticleTypes.TYPE_CODEC.fieldOf("particle").forGetter(SpawnParticlesEnchantmentEffect::particle),
					SpawnParticlesEnchantmentEffect.PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEnchantmentEffect::horizontalPosition),
					SpawnParticlesEnchantmentEffect.PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEnchantmentEffect::verticalPosition),
					SpawnParticlesEnchantmentEffect.VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEnchantmentEffect::horizontalVelocity),
					SpawnParticlesEnchantmentEffect.VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEnchantmentEffect::verticalVelocity),
					FloatProvider.VALUE_CODEC.optionalFieldOf("speed", ConstantFloatProvider.ZERO).forGetter(SpawnParticlesEnchantmentEffect::speed)
				)
				.apply(instance, SpawnParticlesEnchantmentEffect::new)
	);

	public static SpawnParticlesEnchantmentEffect.PositionSource entityPosition(float offset) {
		return new SpawnParticlesEnchantmentEffect.PositionSource(SpawnParticlesEnchantmentEffect.PositionSourceType.ENTITY_POSITION, offset, 1.0F);
	}

	public static SpawnParticlesEnchantmentEffect.PositionSource withinBoundingBox() {
		return new SpawnParticlesEnchantmentEffect.PositionSource(SpawnParticlesEnchantmentEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F);
	}

	public static SpawnParticlesEnchantmentEffect.VelocitySource scaledVelocity(float movementScale) {
		return new SpawnParticlesEnchantmentEffect.VelocitySource(movementScale, ConstantFloatProvider.ZERO);
	}

	public static SpawnParticlesEnchantmentEffect.VelocitySource fixedVelocity(FloatProvider base) {
		return new SpawnParticlesEnchantmentEffect.VelocitySource(0.0F, base);
	}

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		Random random = user.getRandom();
		Vec3d vec3d = user.getMovement();
		float f = user.getWidth();
		float g = user.getHeight();
		world.spawnParticles(
			this.particle,
			this.horizontalPosition.getPosition(pos.getX(), pos.getX(), f, random),
			this.verticalPosition.getPosition(pos.getY(), pos.getY() + (double)(g / 2.0F), g, random),
			this.horizontalPosition.getPosition(pos.getZ(), pos.getZ(), f, random),
			0,
			this.horizontalVelocity.getVelocity(vec3d.getX(), random),
			this.verticalVelocity.getVelocity(vec3d.getY(), random),
			this.horizontalVelocity.getVelocity(vec3d.getZ(), random),
			(double)this.speed.get(random)
		);
	}

	@Override
	public MapCodec<SpawnParticlesEnchantmentEffect> getCodec() {
		return CODEC;
	}

	public static record PositionSource(SpawnParticlesEnchantmentEffect.PositionSourceType type, float offset, float scale) {
		public static final MapCodec<SpawnParticlesEnchantmentEffect.PositionSource> CODEC = RecordCodecBuilder.<SpawnParticlesEnchantmentEffect.PositionSource>mapCodec(
				instance -> instance.group(
							SpawnParticlesEnchantmentEffect.PositionSourceType.CODEC.fieldOf("type").forGetter(SpawnParticlesEnchantmentEffect.PositionSource::type),
							Codec.FLOAT.optionalFieldOf("offset", Float.valueOf(0.0F)).forGetter(SpawnParticlesEnchantmentEffect.PositionSource::offset),
							Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1.0F).forGetter(SpawnParticlesEnchantmentEffect.PositionSource::scale)
						)
						.apply(instance, SpawnParticlesEnchantmentEffect.PositionSource::new)
			)
			.validate(
				source -> source.type() == SpawnParticlesEnchantmentEffect.PositionSourceType.ENTITY_POSITION && source.scale() != 1.0F
						? DataResult.error(() -> "Cannot scale an entity position coordinate source")
						: DataResult.success(source)
			);

		public double getPosition(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random) {
			return this.type.getCoordinate(entityPosition, boundingBoxCenter, boundingBoxSize * this.scale, random) + (double)this.offset;
		}
	}

	public static enum PositionSourceType implements StringIdentifiable {
		ENTITY_POSITION("entity_position", (entityPosition, boundingBoxCenter, boundingBoxSize, random) -> entityPosition),
		BOUNDING_BOX(
			"in_bounding_box", (entityPosition, boundingBoxCenter, boundingBoxSize, random) -> boundingBoxCenter + (random.nextDouble() - 0.5) * (double)boundingBoxSize
		);

		public static final Codec<SpawnParticlesEnchantmentEffect.PositionSourceType> CODEC = StringIdentifiable.createCodec(
			SpawnParticlesEnchantmentEffect.PositionSourceType::values
		);
		private final String id;
		private final SpawnParticlesEnchantmentEffect.PositionSourceType.CoordinateSource coordinateSource;

		private PositionSourceType(final String id, final SpawnParticlesEnchantmentEffect.PositionSourceType.CoordinateSource coordinateSource) {
			this.id = id;
			this.coordinateSource = coordinateSource;
		}

		public double getCoordinate(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random) {
			return this.coordinateSource.getCoordinate(entityPosition, boundingBoxCenter, boundingBoxSize, random);
		}

		@Override
		public String asString() {
			return this.id;
		}

		@FunctionalInterface
		interface CoordinateSource {
			double getCoordinate(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random);
		}
	}

	public static record VelocitySource(float movementScale, FloatProvider base) {
		public static final MapCodec<SpawnParticlesEnchantmentEffect.VelocitySource> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.FLOAT.optionalFieldOf("movement_scale", Float.valueOf(0.0F)).forGetter(SpawnParticlesEnchantmentEffect.VelocitySource::movementScale),
						FloatProvider.VALUE_CODEC.optionalFieldOf("base", ConstantFloatProvider.ZERO).forGetter(SpawnParticlesEnchantmentEffect.VelocitySource::base)
					)
					.apply(instance, SpawnParticlesEnchantmentEffect.VelocitySource::new)
		);

		public double getVelocity(double entityVelocity, Random random) {
			return entityVelocity * (double)this.movementScale + (double)this.base.get(random);
		}
	}
}
