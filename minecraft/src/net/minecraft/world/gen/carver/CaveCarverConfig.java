package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class CaveCarverConfig extends CarverConfig {
	public static final Codec<CaveCarverConfig> CAVE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CarverConfig.CONFIG_CODEC.forGetter(config -> config),
					FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_multiplier").forGetter(config -> config.horizontalRadiusMultiplier),
					FloatProvider.VALUE_CODEC.fieldOf("vertical_radius_multiplier").forGetter(config -> config.verticalRadiusMultiplier),
					FloatProvider.createValidatedCodec(-1.0F, 1.0F).fieldOf("floor_level").forGetter(config -> config.floorLevel)
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
		CarverDebugConfig debugConfig,
		RegistryEntryList<Block> replaceable,
		FloatProvider horizontalRadiusMultiplier,
		FloatProvider verticalRadiusMultiplier,
		FloatProvider floorLevel
	) {
		super(probability, y, yScale, lavaLevel, debugConfig, replaceable);
		this.horizontalRadiusMultiplier = horizontalRadiusMultiplier;
		this.verticalRadiusMultiplier = verticalRadiusMultiplier;
		this.floorLevel = floorLevel;
	}

	public CaveCarverConfig(
		float probability,
		HeightProvider y,
		FloatProvider yScale,
		YOffset lavaLevel,
		RegistryEntryList<Block> replaceable,
		FloatProvider horizontalRadiusMultiplier,
		FloatProvider verticalRadiusMultiplier,
		FloatProvider floorLevel
	) {
		this(probability, y, yScale, lavaLevel, CarverDebugConfig.DEFAULT, replaceable, horizontalRadiusMultiplier, verticalRadiusMultiplier, floorLevel);
	}

	public CaveCarverConfig(CarverConfig config, FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier, FloatProvider floorLevel) {
		this(
			config.probability,
			config.y,
			config.yScale,
			config.lavaLevel,
			config.debugConfig,
			config.replaceable,
			horizontalRadiusMultiplier,
			verticalRadiusMultiplier,
			floorLevel
		);
	}
}
