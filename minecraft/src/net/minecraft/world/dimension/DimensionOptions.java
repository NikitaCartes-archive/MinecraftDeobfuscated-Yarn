package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
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
	private static final Set<RegistryKey<DimensionOptions>> BASE_DIMENSIONS = Sets.<RegistryKey<DimensionOptions>>newLinkedHashSet(
		ImmutableList.of(OVERWORLD, NETHER, END)
	);
	private final Supplier<DimensionType> dimensionTypeSupplier;
	private final ChunkGenerator chunkGenerator;

	public DimensionOptions(Supplier<DimensionType> typeSupplier, ChunkGenerator chunkGenerator) {
		this.dimensionTypeSupplier = typeSupplier;
		this.chunkGenerator = chunkGenerator;
	}

	public Supplier<DimensionType> getDimensionTypeSupplier() {
		return this.dimensionTypeSupplier;
	}

	public DimensionType getDimensionType() {
		return (DimensionType)this.dimensionTypeSupplier.get();
	}

	public ChunkGenerator getChunkGenerator() {
		return this.chunkGenerator;
	}

	public static SimpleRegistry<DimensionOptions> method_29569(SimpleRegistry<DimensionOptions> simpleRegistry) {
		SimpleRegistry<DimensionOptions> simpleRegistry2 = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental());

		for (RegistryKey<DimensionOptions> registryKey : BASE_DIMENSIONS) {
			DimensionOptions dimensionOptions = simpleRegistry.get(registryKey);
			if (dimensionOptions != null) {
				simpleRegistry2.add(registryKey, dimensionOptions, simpleRegistry.getEntryLifecycle(dimensionOptions));
			}
		}

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
			RegistryKey<DimensionOptions> registryKey2 = (RegistryKey<DimensionOptions>)entry.getKey();
			if (!BASE_DIMENSIONS.contains(registryKey2)) {
				simpleRegistry2.add(registryKey2, (DimensionOptions)entry.getValue(), simpleRegistry.getEntryLifecycle((DimensionOptions)entry.getValue()));
			}
		}

		return simpleRegistry2;
	}

	public static boolean hasDefaultSettings(long seed, SimpleRegistry<DimensionOptions> options) {
		List<Entry<RegistryKey<DimensionOptions>, DimensionOptions>> list = Lists.<Entry<RegistryKey<DimensionOptions>, DimensionOptions>>newArrayList(
			options.getEntries()
		);
		if (list.size() != BASE_DIMENSIONS.size()) {
			return false;
		} else {
			Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry = (Entry<RegistryKey<DimensionOptions>, DimensionOptions>)list.get(0);
			Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry2 = (Entry<RegistryKey<DimensionOptions>, DimensionOptions>)list.get(1);
			Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry3 = (Entry<RegistryKey<DimensionOptions>, DimensionOptions>)list.get(2);
			if (entry.getKey() != OVERWORLD || entry2.getKey() != NETHER || entry3.getKey() != END) {
				return false;
			} else if (!((DimensionOptions)entry.getValue()).getDimensionType().equals(DimensionType.OVERWORLD)
				&& ((DimensionOptions)entry.getValue()).getDimensionType() != DimensionType.OVERWORLD_CAVES) {
				return false;
			} else if (!((DimensionOptions)entry2.getValue()).getDimensionType().equals(DimensionType.THE_NETHER)) {
				return false;
			} else if (!((DimensionOptions)entry3.getValue()).getDimensionType().equals(DimensionType.THE_END)) {
				return false;
			} else if (((DimensionOptions)entry2.getValue()).getChunkGenerator() instanceof NoiseChunkGenerator
				&& ((DimensionOptions)entry3.getValue()).getChunkGenerator() instanceof NoiseChunkGenerator) {
				NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)((DimensionOptions)entry2.getValue()).getChunkGenerator();
				NoiseChunkGenerator noiseChunkGenerator2 = (NoiseChunkGenerator)((DimensionOptions)entry3.getValue()).getChunkGenerator();
				if (!noiseChunkGenerator.matchesSettings(seed, ChunkGeneratorSettings.NETHER)) {
					return false;
				} else if (!noiseChunkGenerator2.matchesSettings(seed, ChunkGeneratorSettings.END)) {
					return false;
				} else if (!(noiseChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
					return false;
				} else {
					MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)noiseChunkGenerator.getBiomeSource();
					if (!multiNoiseBiomeSource.matchesInstance(seed)) {
						return false;
					} else if (!(noiseChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
						return false;
					} else {
						TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)noiseChunkGenerator2.getBiomeSource();
						return theEndBiomeSource.matches(seed);
					}
				}
			} else {
				return false;
			}
		}
	}
}
