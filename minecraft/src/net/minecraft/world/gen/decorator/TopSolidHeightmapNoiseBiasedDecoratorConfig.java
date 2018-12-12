package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.Heightmap;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig implements DecoratorConfig {
	public final int noiseToCountRatio;
	public final double noiseFactor;
	public final double noiseOffset;
	public final Heightmap.Type heightmap;

	public TopSolidHeightmapNoiseBiasedDecoratorConfig(int i, double d, double e, Heightmap.Type type) {
		this.noiseToCountRatio = i;
		this.noiseFactor = d;
		this.noiseOffset = e;
		this.heightmap = type;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("noise_to_count_ratio"),
					dynamicOps.createInt(this.noiseToCountRatio),
					dynamicOps.createString("noise_factor"),
					dynamicOps.createDouble(this.noiseFactor),
					dynamicOps.createString("noise_offset"),
					dynamicOps.createDouble(this.noiseOffset),
					dynamicOps.createString("heightmap"),
					dynamicOps.createString(this.heightmap.getName())
				)
			)
		);
	}

	public static TopSolidHeightmapNoiseBiasedDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.getInt("noise_to_count_ratio", 10);
		double d = dynamic.getDouble("noise_factor", 80.0);
		double e = dynamic.getDouble("noise_offset", 0.0);
		Heightmap.Type type = Heightmap.Type.byName(dynamic.getString("heightmap", "OCEAN_FLOOR_WG"));
		return new TopSolidHeightmapNoiseBiasedDecoratorConfig(i, d, e, type);
	}
}
