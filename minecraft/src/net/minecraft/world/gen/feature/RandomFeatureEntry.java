package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeatureEntry<FC extends FeatureConfig> {
	public final ConfiguredFeature<FC, ?> feature;
	public final float chance;

	public RandomFeatureEntry(ConfiguredFeature<FC, ?> feature, float chance) {
		this.feature = feature;
		this.chance = chance;
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamic) {
		return new Dynamic<>(
			dynamic,
			dynamic.createMap(
				ImmutableMap.of(
					dynamic.createString("name"),
					dynamic.createString(Registry.FEATURE.getId(this.feature.feature).toString()),
					dynamic.createString("config"),
					this.feature.config.serialize(dynamic).getValue(),
					dynamic.createString("chance"),
					dynamic.createFloat(this.chance)
				)
			)
		);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos
	) {
		return this.feature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
	}

	public static <T> RandomFeatureEntry<?> deserialize(Dynamic<T> dynamic) {
		return ConfiguredFeature.deserialize(dynamic).withChance(dynamic.get("chance").asFloat(0.0F));
	}
}
