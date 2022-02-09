package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeatureEntry {
	public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(randomFeatureEntry -> randomFeatureEntry.feature),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(randomFeatureEntry -> randomFeatureEntry.chance)
				)
				.apply(instance, RandomFeatureEntry::new)
	);
	public final RegistryEntry<PlacedFeature> feature;
	public final float chance;

	public RandomFeatureEntry(RegistryEntry<PlacedFeature> registryEntry, float chance) {
		this.feature = registryEntry;
		this.chance = chance;
	}

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
		return this.feature.value().generateUnregistered(world, chunkGenerator, random, pos);
	}
}
