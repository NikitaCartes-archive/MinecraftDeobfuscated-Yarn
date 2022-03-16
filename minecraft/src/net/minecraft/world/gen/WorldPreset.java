package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;

public class WorldPreset {
	public static final Codec<WorldPreset> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.unboundedMap(RegistryKey.createCodec(Registry.DIMENSION_KEY), DimensionOptions.CODEC).fieldOf("dimensions").forGetter(preset -> preset.dimensions)
				)
				.apply(instance, WorldPreset::new)
	);
	public static final Codec<RegistryEntry<WorldPreset>> ENTRY_CODEC = RegistryElementCodec.of(Registry.WORLD_PRESET_WORLDGEN, CODEC);
	private final Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions;

	public WorldPreset(Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions) {
		this.dimensions = dimensions;
	}

	private Registry<DimensionOptions> createDimensionOptionsRegistry() {
		MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
		DimensionOptions.streamRegistry(this.dimensions.keySet().stream()).forEach(registryKey -> {
			DimensionOptions dimensionOptions = (DimensionOptions)this.dimensions.get(registryKey);
			if (dimensionOptions != null) {
				mutableRegistry.add(registryKey, dimensionOptions, Lifecycle.stable());
			}
		});
		return mutableRegistry.freeze();
	}

	public GeneratorOptions createGeneratorOptions(long seed, boolean generateStructures, boolean bonusChest) {
		return new GeneratorOptions(seed, generateStructures, bonusChest, this.createDimensionOptionsRegistry());
	}

	public GeneratorOptions createGeneratorOptions(GeneratorOptions generatorOptions) {
		return this.createGeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest());
	}

	public Optional<DimensionOptions> getOverworld() {
		return Optional.ofNullable((DimensionOptions)this.dimensions.get(DimensionOptions.OVERWORLD));
	}

	public DimensionOptions getOverworldOrElseThrow() {
		return (DimensionOptions)this.getOverworld().orElseThrow(() -> new IllegalStateException("Can't find overworld in this preset"));
	}
}
