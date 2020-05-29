package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeatureEntry<FC extends FeatureConfig> {
	public static final Codec<RandomFeatureEntry<?>> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.CODEC.fieldOf("feature").forGetter(randomFeatureEntry -> randomFeatureEntry.feature),
					Codec.FLOAT.fieldOf("chance").forGetter(randomFeatureEntry -> randomFeatureEntry.chance)
				)
				.apply(instance, RandomFeatureEntry::new)
	);
	public final ConfiguredFeature<FC, ?> feature;
	public final float chance;

	public RandomFeatureEntry(ConfiguredFeature<FC, ?> feature, float chance) {
		this.feature = feature;
		this.chance = chance;
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos
	) {
		return this.feature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
	}
}
