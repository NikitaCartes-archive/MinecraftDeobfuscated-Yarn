package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public final class DimensionOptions {
	public static final Codec<DimensionOptions> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					DimensionType.field_24756.fieldOf("type").forGetter(DimensionOptions::getDimensionTypeSupplier),
					ChunkGenerator.field_24746.fieldOf("generator").forGetter(DimensionOptions::getChunkGenerator)
				)
				.apply(instance, instance.stable(DimensionOptions::new))
	);
	public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("overworld"));
	public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("the_nether"));
	public static final RegistryKey<DimensionOptions> END = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("the_end"));
	private static final LinkedHashSet<RegistryKey<DimensionOptions>> BASE_DIMENSIONS = Sets.newLinkedHashSet(ImmutableList.of(OVERWORLD, NETHER, END));
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
		SimpleRegistry<DimensionOptions> simpleRegistry2 = new SimpleRegistry<>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());

		for (RegistryKey<DimensionOptions> registryKey : BASE_DIMENSIONS) {
			DimensionOptions dimensionOptions = simpleRegistry.get(registryKey);
			if (dimensionOptions != null) {
				simpleRegistry2.add(registryKey, dimensionOptions);
				if (simpleRegistry.isLoaded(registryKey)) {
					simpleRegistry2.markLoaded(registryKey);
				}
			}
		}

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
			RegistryKey<DimensionOptions> registryKey2 = (RegistryKey<DimensionOptions>)entry.getKey();
			if (!BASE_DIMENSIONS.contains(registryKey2)) {
				simpleRegistry2.add(registryKey2, entry.getValue());
				if (simpleRegistry.isLoaded(registryKey2)) {
					simpleRegistry2.markLoaded(registryKey2);
				}
			}
		}

		return simpleRegistry2;
	}

	public static boolean method_29567(long seed, SimpleRegistry<DimensionOptions> simpleRegistry) {
		List<Entry<RegistryKey<DimensionOptions>, DimensionOptions>> list = Lists.<Entry<RegistryKey<DimensionOptions>, DimensionOptions>>newArrayList(
			simpleRegistry.getEntries()
		);
		if (list.size() != BASE_DIMENSIONS.size()) {
			return false;
		} else {
			Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry = (Entry<RegistryKey<DimensionOptions>, DimensionOptions>)list.get(0);
			Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry2 = (Entry<RegistryKey<DimensionOptions>, DimensionOptions>)list.get(1);
			Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry3 = (Entry<RegistryKey<DimensionOptions>, DimensionOptions>)list.get(2);
			if (entry.getKey() != OVERWORLD || entry2.getKey() != NETHER || entry3.getKey() != END) {
				return false;
			} else if (!((DimensionOptions)entry.getValue()).getDimensionType().isOverworld()
				|| !((DimensionOptions)entry2.getValue()).getDimensionType().isNether()
				|| !((DimensionOptions)entry3.getValue()).getDimensionType().isEnd()) {
				return false;
			} else if (((DimensionOptions)entry2.getValue()).getChunkGenerator() instanceof SurfaceChunkGenerator
				&& ((DimensionOptions)entry3.getValue()).getChunkGenerator() instanceof SurfaceChunkGenerator) {
				SurfaceChunkGenerator surfaceChunkGenerator = (SurfaceChunkGenerator)((DimensionOptions)entry2.getValue()).getChunkGenerator();
				SurfaceChunkGenerator surfaceChunkGenerator2 = (SurfaceChunkGenerator)((DimensionOptions)entry3.getValue()).getChunkGenerator();
				if (!surfaceChunkGenerator.method_28548(seed, ChunkGeneratorType.Preset.NETHER)) {
					return false;
				} else if (!surfaceChunkGenerator2.method_28548(seed, ChunkGeneratorType.Preset.END)) {
					return false;
				} else if (!(surfaceChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
					return false;
				} else {
					MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)surfaceChunkGenerator.getBiomeSource();
					if (!multiNoiseBiomeSource.method_28462(seed)) {
						return false;
					} else if (!(surfaceChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
						return false;
					} else {
						TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)surfaceChunkGenerator2.getBiomeSource();
						return theEndBiomeSource.method_28479(seed);
					}
				}
			} else {
				return false;
			}
		}
	}
}
