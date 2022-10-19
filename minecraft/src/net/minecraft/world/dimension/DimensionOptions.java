package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record DimensionOptions(RegistryEntry<DimensionType> dimensionTypeEntry, ChunkGenerator chunkGenerator) {
	public static final Codec<DimensionOptions> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					DimensionType.REGISTRY_CODEC.fieldOf("type").forGetter(DimensionOptions::dimensionTypeEntry),
					ChunkGenerator.CODEC.fieldOf("generator").forGetter(DimensionOptions::chunkGenerator)
				)
				.apply(instance, instance.stable(DimensionOptions::new))
	);
	public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("overworld"));
	public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("the_nether"));
	public static final RegistryKey<DimensionOptions> END = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("the_end"));
}
