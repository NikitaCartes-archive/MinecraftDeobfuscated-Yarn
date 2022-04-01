package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public final class DimensionOptions {
	public static final Codec<DimensionOptions> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					DimensionType.REGISTRY_CODEC.fieldOf("type").forGetter(DimensionOptions::getDimensionTypeSupplier),
					ChunkGenerator.CODEC.fieldOf("generator").forGetter(DimensionOptions::getChunkGenerator)
				)
				.apply(instance, instance.stable(DimensionOptions::new))
	);
	public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("overworld"));
	public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("the_nether"));
	public static final RegistryKey<DimensionOptions> END = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("the_end"));
	private static final Set<RegistryKey<DimensionOptions>> BASE_DIMENSIONS = ImmutableSet.of(OVERWORLD, NETHER, END);
	private final RegistryEntry<DimensionType> dimensionTypeSupplier;
	private final ChunkGenerator chunkGenerator;

	public DimensionOptions(RegistryEntry<DimensionType> registryEntry, ChunkGenerator chunkGenerator) {
		this.dimensionTypeSupplier = registryEntry;
		this.chunkGenerator = chunkGenerator;
	}

	public RegistryEntry<DimensionType> getDimensionTypeSupplier() {
		return this.dimensionTypeSupplier;
	}

	public ChunkGenerator getChunkGenerator() {
		return this.chunkGenerator;
	}

	public static Registry<DimensionOptions> method_29569(Registry<DimensionOptions> registry) {
		MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);

		for (RegistryKey<DimensionOptions> registryKey : BASE_DIMENSIONS) {
			DimensionOptions dimensionOptions = registry.get(registryKey);
			if (dimensionOptions != null) {
				mutableRegistry.add(registryKey, dimensionOptions, registry.getEntryLifecycle(dimensionOptions));
			}
		}

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : registry.getEntrySet()) {
			RegistryKey<DimensionOptions> registryKey2 = (RegistryKey<DimensionOptions>)entry.getKey();
			if (!BASE_DIMENSIONS.contains(registryKey2)) {
				mutableRegistry.add(registryKey2, (DimensionOptions)entry.getValue(), registry.getEntryLifecycle((DimensionOptions)entry.getValue()));
			}
		}

		return mutableRegistry;
	}

	public static boolean hasDefaultSettings(long seed, Registry<DimensionOptions> registry) {
		if (registry.size() != BASE_DIMENSIONS.size()) {
			return false;
		} else {
			Optional<DimensionOptions> optional = registry.getOrEmpty(OVERWORLD);
			Optional<DimensionOptions> optional2 = registry.getOrEmpty(NETHER);
			Optional<DimensionOptions> optional3 = registry.getOrEmpty(END);
			if (!optional.isEmpty() && !optional2.isEmpty() && !optional3.isEmpty()) {
				if (!((DimensionOptions)optional.get()).getDimensionTypeSupplier().matchesKey(DimensionType.OVERWORLD_REGISTRY_KEY)
					&& !((DimensionOptions)optional.get()).getDimensionTypeSupplier().matchesKey(DimensionType.OVERWORLD_CAVES_REGISTRY_KEY)) {
					return false;
				} else if (!((DimensionOptions)optional2.get()).getDimensionTypeSupplier().matchesKey(DimensionType.THE_NETHER_REGISTRY_KEY)) {
					return false;
				} else if (!((DimensionOptions)optional3.get()).getDimensionTypeSupplier().matchesKey(DimensionType.THE_END_REGISTRY_KEY)) {
					return false;
				} else if (((DimensionOptions)optional2.get()).getChunkGenerator() instanceof NoiseChunkGenerator
					&& ((DimensionOptions)optional3.get()).getChunkGenerator() instanceof NoiseChunkGenerator) {
					NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)((DimensionOptions)optional2.get()).getChunkGenerator();
					NoiseChunkGenerator noiseChunkGenerator2 = (NoiseChunkGenerator)((DimensionOptions)optional3.get()).getChunkGenerator();
					if (!noiseChunkGenerator.matchesSettings(seed, ChunkGeneratorSettings.NETHER)) {
						return false;
					} else if (!noiseChunkGenerator2.matchesSettings(seed, ChunkGeneratorSettings.END)) {
						return false;
					} else if (!(noiseChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
						return false;
					} else {
						MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)noiseChunkGenerator.getBiomeSource();
						if (!multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSource.Preset.NETHER)) {
							return false;
						} else {
							BiomeSource biomeSource = ((DimensionOptions)optional.get()).getChunkGenerator().getBiomeSource();
							if (biomeSource instanceof MultiNoiseBiomeSource && !((MultiNoiseBiomeSource)biomeSource).matchesInstance(MultiNoiseBiomeSource.Preset.OVERWORLD)) {
								return false;
							} else if (!(noiseChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
								return false;
							} else {
								TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)noiseChunkGenerator2.getBiomeSource();
								return theEndBiomeSource.matches(seed);
							}
						}
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
}
