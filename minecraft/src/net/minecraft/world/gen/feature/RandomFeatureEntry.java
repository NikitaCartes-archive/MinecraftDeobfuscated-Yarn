package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.FeatureConfig;

public class RandomFeatureEntry<FC extends FeatureConfig> {
	public final Feature<FC> feature;
	public final FC config;
	public final Float chance;

	public RandomFeatureEntry(Feature<FC> feature, FC featureConfig, Float float_) {
		this.feature = feature;
		this.config = featureConfig;
		this.chance = float_;
	}

	public RandomFeatureEntry(Feature<FC> feature, Dynamic<?> dynamic, float f) {
		this(feature, feature.deserialize(dynamic), Float.valueOf(f));
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.FEATURE.getId(this.feature).toString()),
					dynamicOps.createString("config"),
					this.config.serialize(dynamicOps).getValue(),
					dynamicOps.createString("chance"),
					dynamicOps.createFloat(this.chance)
				)
			)
		);
	}

	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos) {
		return this.feature.generate(iWorld, chunkGenerator, random, blockPos, this.config);
	}

	public static <T> RandomFeatureEntry<?> deserialize(Dynamic<T> dynamic) {
		Feature<? extends FeatureConfig> feature = (Feature<? extends FeatureConfig>)Registry.FEATURE.get(new Identifier(dynamic.getString("name", "")));
		return new RandomFeatureEntry<>(feature, (Dynamic<?>)dynamic.get("config").orElseGet(dynamic::emptyMap), dynamic.getFloat("chance", 0.0F));
	}
}
