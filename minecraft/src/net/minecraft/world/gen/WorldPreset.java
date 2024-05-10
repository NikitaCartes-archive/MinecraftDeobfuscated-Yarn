package net.minecraft.world.gen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;

public class WorldPreset {
	public static final Codec<WorldPreset> CODEC = RecordCodecBuilder.<WorldPreset>create(
			instance -> instance.group(
						Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.DIMENSION), DimensionOptions.CODEC).fieldOf("dimensions").forGetter(preset -> preset.dimensions)
					)
					.apply(instance, WorldPreset::new)
		)
		.validate(WorldPreset::validate);
	public static final Codec<RegistryEntry<WorldPreset>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.WORLD_PRESET, CODEC);
	private final Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions;

	public WorldPreset(Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions) {
		this.dimensions = dimensions;
	}

	private ImmutableMap<RegistryKey<DimensionOptions>, DimensionOptions> collectDimensions() {
		Builder<RegistryKey<DimensionOptions>, DimensionOptions> builder = ImmutableMap.builder();
		DimensionOptionsRegistryHolder.streamAll(this.dimensions.keySet().stream()).forEach(dimensionKey -> {
			DimensionOptions dimensionOptions = (DimensionOptions)this.dimensions.get(dimensionKey);
			if (dimensionOptions != null) {
				builder.put(dimensionKey, dimensionOptions);
			}
		});
		return builder.build();
	}

	public DimensionOptionsRegistryHolder createDimensionsRegistryHolder() {
		return new DimensionOptionsRegistryHolder(this.collectDimensions());
	}

	public Optional<DimensionOptions> getOverworld() {
		return Optional.ofNullable((DimensionOptions)this.dimensions.get(DimensionOptions.OVERWORLD));
	}

	private static DataResult<WorldPreset> validate(WorldPreset preset) {
		return preset.getOverworld().isEmpty() ? DataResult.error(() -> "Missing overworld dimension") : DataResult.success(preset, Lifecycle.stable());
	}
}
