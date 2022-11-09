package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeatureEntry {
	public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(config -> config.feature),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(config -> config.chance)
				)
				.apply(instance, RandomFeatureEntry::new)
	);
	public final RegistryEntry<PlacedFeature> feature;
	public final float chance;

	public RandomFeatureEntry(RegistryEntry<PlacedFeature> feature, float chance) {
		this.feature = feature;
		this.chance = chance;
	}

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
		return this.feature.value().generateUnregistered(world, chunkGenerator, random, pos);
	}
}
