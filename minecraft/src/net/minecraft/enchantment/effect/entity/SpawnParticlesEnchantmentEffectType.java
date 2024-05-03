package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
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

public record SpawnParticlesEnchantmentEffectType(
	ParticleEffect particle,
	SpawnParticlesEnchantmentEffectType.PositionSource horizontalPosition,
	SpawnParticlesEnchantmentEffectType.PositionSource verticalPosition,
	SpawnParticlesEnchantmentEffectType.VelocitySource horizontalVelocity,
	SpawnParticlesEnchantmentEffectType.VelocitySource verticalVelocity,
	FloatProvider speed
) implements EnchantmentEntityEffectType {
	public static final MapCodec<SpawnParticlesEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					ParticleTypes.TYPE_CODEC.fieldOf("particle").forGetter(SpawnParticlesEnchantmentEffectType::particle),
					SpawnParticlesEnchantmentEffectType.PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEnchantmentEffectType::horizontalPosition),
					SpawnParticlesEnchantmentEffectType.PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEnchantmentEffectType::verticalPosition),
					SpawnParticlesEnchantmentEffectType.VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEnchantmentEffectType::horizontalVelocity),
					SpawnParticlesEnchantmentEffectType.VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEnchantmentEffectType::verticalVelocity),
					FloatProvider.VALUE_CODEC.optionalFieldOf("speed", ConstantFloatProvider.ZERO).forGetter(SpawnParticlesEnchantmentEffectType::speed)
				)
				.apply(instance, SpawnParticlesEnchantmentEffectType::new)
	);

	public static SpawnParticlesEnchantmentEffectType.PositionSource entityPosition(float offset) {
		return new SpawnParticlesEnchantmentEffectType.PositionSource(SpawnParticlesEnchantmentEffectType.PositionSourceType.ENTITY_POSITION, offset, 1.0F);
	}

	public static SpawnParticlesEnchantmentEffectType.PositionSource withinBoundingBox() {
		return new SpawnParticlesEnchantmentEffectType.PositionSource(SpawnParticlesEnchantmentEffectType.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F);
	}

	public static SpawnParticlesEnchantmentEffectType.VelocitySource scaledVelocity(float movementScale) {
		return new SpawnParticlesEnchantmentEffectType.VelocitySource(movementScale, ConstantFloatProvider.ZERO);
	}

	public static SpawnParticlesEnchantmentEffectType.VelocitySource fixedVelocity(FloatProvider base) {
		return new SpawnParticlesEnchantmentEffectType.VelocitySource(0.0F, base);
	}

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		Random random = user.getRandom();
		Vec3d vec3d = user.getVelocity();
		float f = user.getWidth();
		float g = user.getHeight();
		world.spawnParticles(
			this.particle,
			this.horizontalPosition.getPosition(pos.getX(), f, random),
			this.verticalPosition.getPosition(pos.getY(), g, random),
			this.horizontalPosition.getPosition(pos.getZ(), f, random),
			0,
			this.horizontalVelocity.getVelocity(vec3d.getX(), random),
			this.verticalVelocity.getVelocity(vec3d.getY(), random),
			this.horizontalVelocity.getVelocity(vec3d.getZ(), random),
			(double)this.speed.get(random)
		);
	}

	@Override
	public MapCodec<SpawnParticlesEnchantmentEffectType> getCodec() {
		return CODEC;
	}

	public static record PositionSource(SpawnParticlesEnchantmentEffectType.PositionSourceType type, float offset, float scale) {
		public static final MapCodec<SpawnParticlesEnchantmentEffectType.PositionSource> CODEC = RecordCodecBuilder.<SpawnParticlesEnchantmentEffectType.PositionSource>mapCodec(
				instance -> instance.group(
							SpawnParticlesEnchantmentEffectType.PositionSourceType.CODEC.fieldOf("type").forGetter(SpawnParticlesEnchantmentEffectType.PositionSource::type),
							Codec.FLOAT.optionalFieldOf("offset", Float.valueOf(0.0F)).forGetter(SpawnParticlesEnchantmentEffectType.PositionSource::offset),
							Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1.0F).forGetter(SpawnParticlesEnchantmentEffectType.PositionSource::scale)
						)
						.apply(instance, SpawnParticlesEnchantmentEffectType.PositionSource::new)
			)
			.validate(
				positionSource -> positionSource.type() == SpawnParticlesEnchantmentEffectType.PositionSourceType.ENTITY_POSITION && positionSource.scale() != 1.0F
						? DataResult.error(() -> "Cannot scale an entity position coordinate source")
						: DataResult.success(positionSource)
			);

		public double getPosition(double entityPosition, float boundingBoxSize, Random random) {
			return this.type.getCoordinate(entityPosition, boundingBoxSize * this.scale, random) + (double)this.offset;
		}
	}

	public static enum PositionSourceType implements StringIdentifiable {
		ENTITY_POSITION("entity_position", (entityPosition, boundingBoxSize, random) -> entityPosition),
		BOUNDING_BOX("in_bounding_box", (entityPosition, boundingBoxSize, random) -> entityPosition + (random.nextDouble() - 0.5) * (double)boundingBoxSize);

		public static final Codec<SpawnParticlesEnchantmentEffectType.PositionSourceType> CODEC = StringIdentifiable.createCodec(
			SpawnParticlesEnchantmentEffectType.PositionSourceType::values
		);
		private final String id;
		private final SpawnParticlesEnchantmentEffectType.PositionSourceType.CoordinateSource coordinateSource;

		private PositionSourceType(final String id, final SpawnParticlesEnchantmentEffectType.PositionSourceType.CoordinateSource coordinateSource) {
			this.id = id;
			this.coordinateSource = coordinateSource;
		}

		public double getCoordinate(double entityPosition, float boundingBoxSize, Random random) {
			return this.coordinateSource.getCoordinate(entityPosition, boundingBoxSize, random);
		}

		@Override
		public String asString() {
			return this.id;
		}

		@FunctionalInterface
		interface CoordinateSource {
			double getCoordinate(double entityPosition, float boundingBoxSize, Random random);
		}
	}

	public static record VelocitySource(float movementScale, FloatProvider base) {
		public static final MapCodec<SpawnParticlesEnchantmentEffectType.VelocitySource> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.FLOAT.optionalFieldOf("movement_scale", Float.valueOf(0.0F)).forGetter(SpawnParticlesEnchantmentEffectType.VelocitySource::movementScale),
						FloatProvider.VALUE_CODEC.optionalFieldOf("base", ConstantFloatProvider.ZERO).forGetter(SpawnParticlesEnchantmentEffectType.VelocitySource::base)
					)
					.apply(instance, SpawnParticlesEnchantmentEffectType.VelocitySource::new)
		);

		public double getVelocity(double entityVelocity, Random random) {
			return entityVelocity * (double)this.movementScale + (double)this.base.get(random);
		}
	}
}
