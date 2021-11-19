package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeatureEntry {
	public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PlacedFeature.REGISTRY_CODEC
						.fieldOf("feature")
						.flatXmap(Codecs.createPresentValueChecker(), Codecs.createPresentValueChecker())
						.forGetter(randomFeatureEntry -> randomFeatureEntry.feature),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(randomFeatureEntry -> randomFeatureEntry.chance)
				)
				.apply(instance, RandomFeatureEntry::new)
	);
	public final Supplier<PlacedFeature> feature;
	public final float chance;

	public RandomFeatureEntry(PlacedFeature feature, float chance) {
		this(() -> feature, chance);
	}

	private RandomFeatureEntry(Supplier<PlacedFeature> feature, float chance) {
		this.feature = feature;
		this.chance = chance;
	}

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
		return ((PlacedFeature)this.feature.get()).generateUnregistered(world, chunkGenerator, random, pos);
	}
}
