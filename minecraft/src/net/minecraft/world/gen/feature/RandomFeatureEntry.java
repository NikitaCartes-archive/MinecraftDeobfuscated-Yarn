package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeatureEntry {
	public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.CODEC.fieldOf("feature").forGetter(randomFeatureEntry -> randomFeatureEntry.feature),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(randomFeatureEntry -> randomFeatureEntry.chance)
				)
				.apply(instance, RandomFeatureEntry::new)
	);
	public final Supplier<ConfiguredFeature<?, ?>> feature;
	public final float chance;

	public RandomFeatureEntry(ConfiguredFeature<?, ?> feature, float chance) {
		this(() -> feature, chance);
	}

	private RandomFeatureEntry(Supplier<ConfiguredFeature<?, ?>> supplier, float f) {
		this.feature = supplier;
		this.chance = f;
	}

	public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
		return ((ConfiguredFeature)this.feature.get()).generate(serverWorldAccess, chunkGenerator, random, blockPos);
	}
}
