package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.Heightmap;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig implements DecoratorConfig {
	public final int noiseToCountRatio;
	public final double noiseFactor;
	public final double noiseOffset;
	public final Heightmap.Type heightmap;

	public TopSolidHeightmapNoiseBiasedDecoratorConfig(int noiseToCountRatio, double noiseFactor, double noiseOffset, Heightmap.Type heightmap) {
		this.noiseToCountRatio = noiseToCountRatio;
		this.noiseFactor = noiseFactor;
		this.noiseOffset = noiseOffset;
		this.heightmap = heightmap;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("noise_to_count_ratio"),
					ops.createInt(this.noiseToCountRatio),
					ops.createString("noise_factor"),
					ops.createDouble(this.noiseFactor),
					ops.createString("noise_offset"),
					ops.createDouble(this.noiseOffset),
					ops.createString("heightmap"),
					ops.createString(this.heightmap.getName())
				)
			)
		);
	}

	public static TopSolidHeightmapNoiseBiasedDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("noise_to_count_ratio").asInt(10);
		double d = dynamic.get("noise_factor").asDouble(80.0);
		double e = dynamic.get("noise_offset").asDouble(0.0);
		Heightmap.Type type = Heightmap.Type.byName(dynamic.get("heightmap").asString("OCEAN_FLOOR_WG"));
		return new TopSolidHeightmapNoiseBiasedDecoratorConfig(i, d, e, type);
	}
}
