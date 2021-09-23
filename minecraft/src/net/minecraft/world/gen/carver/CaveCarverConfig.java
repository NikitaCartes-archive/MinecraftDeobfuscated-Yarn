package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class CaveCarverConfig extends CarverConfig {
	public static final Codec<CaveCarverConfig> CAVE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CarverConfig.CONFIG_CODEC.forGetter(caveCarverConfig -> caveCarverConfig),
					FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_multiplier").forGetter(caveCarverConfig -> caveCarverConfig.horizontalRadiusMultiplier),
					FloatProvider.VALUE_CODEC.fieldOf("vertical_radius_multiplier").forGetter(caveCarverConfig -> caveCarverConfig.verticalRadiusMultiplier),
					FloatProvider.createValidatedCodec(-1.0F, 1.0F).fieldOf("floor_level").forGetter(caveCarverConfig -> caveCarverConfig.floorLevel)
				)
				.apply(instance, CaveCarverConfig::new)
	);
	public final FloatProvider horizontalRadiusMultiplier;
	public final FloatProvider verticalRadiusMultiplier;
	final FloatProvider floorLevel;

	public CaveCarverConfig(
		float probability,
		HeightProvider y,
		FloatProvider yScale,
		YOffset lavaLevel,
		CarverDebugConfig carverDebugConfig,
		FloatProvider floatProvider,
		FloatProvider floatProvider2,
		FloatProvider floatProvider3
	) {
		super(probability, y, yScale, lavaLevel, carverDebugConfig);
		this.horizontalRadiusMultiplier = floatProvider;
		this.verticalRadiusMultiplier = floatProvider2;
		this.floorLevel = floatProvider3;
	}

	public CaveCarverConfig(
		float probability,
		HeightProvider y,
		FloatProvider yScale,
		YOffset lavaLevel,
		boolean aquifers,
		FloatProvider horizontalRadiusMultiplier,
		FloatProvider verticalRadiusMultiplier,
		FloatProvider floorLevel
	) {
		this(probability, y, yScale, lavaLevel, CarverDebugConfig.DEFAULT, horizontalRadiusMultiplier, verticalRadiusMultiplier, floorLevel);
	}

	public CaveCarverConfig(CarverConfig config, FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier, FloatProvider floorLevel) {
		this(config.probability, config.y, config.yScale, config.lavaLevel, config.debugConfig, horizontalRadiusMultiplier, verticalRadiusMultiplier, floorLevel);
	}
}
