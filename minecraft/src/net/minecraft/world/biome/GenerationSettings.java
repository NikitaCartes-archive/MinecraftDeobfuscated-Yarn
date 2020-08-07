package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerationSettings {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final GenerationSettings INSTANCE = new GenerationSettings(
		() -> ConfiguredSurfaceBuilders.field_26333, ImmutableMap.of(), ImmutableList.of(), ImmutableList.of()
	);
	public static final MapCodec<GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					ConfiguredSurfaceBuilder.field_25015.fieldOf("surface_builder").forGetter(generationSettings -> generationSettings.surfaceBuilder),
					Codec.simpleMap(
							GenerationStep.Carver.field_24770,
							ConfiguredCarver.field_24828.listOf().promotePartial(Util.method_29188("Carver: ", LOGGER::error)),
							StringIdentifiable.method_28142(GenerationStep.Carver.values())
						)
						.fieldOf("carvers")
						.forGetter(generationSettings -> generationSettings.carvers),
					ConfiguredFeature.CODEC
						.listOf()
						.promotePartial(Util.method_29188("Feature: ", LOGGER::error))
						.listOf()
						.fieldOf("features")
						.forGetter(generationSettings -> generationSettings.features),
					ConfiguredStructureFeature.REGISTRY_CODEC
						.listOf()
						.promotePartial(Util.method_29188("Structure start: ", LOGGER::error))
						.fieldOf("starts")
						.forGetter(generationSettings -> generationSettings.structureFeatures)
				)
				.apply(instance, GenerationSettings::new)
	);
	private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
	private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
	private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
	private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures;
	private final List<ConfiguredFeature<?, ?>> flowerFeatures;

	private GenerationSettings(
		Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder,
		Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers,
		List<List<Supplier<ConfiguredFeature<?, ?>>>> features,
		List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures
	) {
		this.surfaceBuilder = surfaceBuilder;
		this.carvers = carvers;
		this.features = features;
		this.structureFeatures = structureFeatures;
		this.flowerFeatures = (List<ConfiguredFeature<?, ?>>)features.stream()
			.flatMap(Collection::stream)
			.map(Supplier::get)
			.flatMap(ConfiguredFeature::method_30648)
			.filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER)
			.collect(ImmutableList.toImmutableList());
	}

	public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carverStep) {
		return (List<Supplier<ConfiguredCarver<?>>>)this.carvers.getOrDefault(carverStep, ImmutableList.of());
	}

	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return this.structureFeatures.stream().anyMatch(supplier -> ((ConfiguredStructureFeature)supplier.get()).feature == structureFeature);
	}

	public Collection<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureFeatures() {
		return this.structureFeatures;
	}

	public ConfiguredStructureFeature<?, ?> method_30978(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		return DataFixUtils.orElse(
			this.structureFeatures
				.stream()
				.map(Supplier::get)
				.filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature)
				.findAny(),
			configuredStructureFeature
		);
	}

	public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
		return this.flowerFeatures;
	}

	/**
	 * Returns the lists of features configured for each {@link net.minecraft.world.gen.GenerationStep.Feature feature generation step}, up to the highest step that has a configured feature.
	 * Entries are guaranteed to not be null, but may be empty lists if an earlier step has no features, but a later step does.
	 */
	public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
		return this.features;
	}

	public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
		return this.surfaceBuilder;
	}

	public SurfaceConfig getSurfaceConfig() {
		return ((ConfiguredSurfaceBuilder)this.surfaceBuilder.get()).getConfig();
	}

	public static class Builder {
		private Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = Optional.empty();
		private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers = Maps.<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>>newLinkedHashMap();
		private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = Lists.<List<Supplier<ConfiguredFeature<?, ?>>>>newArrayList();
		private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures = Lists.<Supplier<ConfiguredStructureFeature<?, ?>>>newArrayList();

		public GenerationSettings.Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
			return this.surfaceBuilder(() -> surfaceBuilder);
		}

		public GenerationSettings.Builder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilderSupplier) {
			this.surfaceBuilder = Optional.of(surfaceBuilderSupplier);
			return this;
		}

		public GenerationSettings.Builder feature(GenerationStep.Feature featureStep, ConfiguredFeature<?, ?> feature) {
			return this.feature(featureStep.ordinal(), () -> feature);
		}

		public GenerationSettings.Builder feature(int stepIndex, Supplier<ConfiguredFeature<?, ?>> featureSupplier) {
			this.addFeatureStep(stepIndex);
			((List)this.features.get(stepIndex)).add(featureSupplier);
			return this;
		}

		public <C extends CarverConfig> GenerationSettings.Builder carver(GenerationStep.Carver carverStep, ConfiguredCarver<C> carver) {
			((List)this.carvers.computeIfAbsent(carverStep, carverx -> Lists.newArrayList())).add((Supplier)() -> carver);
			return this;
		}

		public GenerationSettings.Builder structureFeature(ConfiguredStructureFeature<?, ?> structureFeature) {
			this.structureFeatures.add((Supplier)() -> structureFeature);
			return this;
		}

		private void addFeatureStep(int stepIndex) {
			while (this.features.size() <= stepIndex) {
				this.features.add(Lists.newArrayList());
			}
		}

		public GenerationSettings build() {
			return new GenerationSettings(
				(Supplier)this.surfaceBuilder.orElseThrow(() -> new IllegalStateException("Missing surface builder")),
				(Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue()))),
				(List)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()),
				ImmutableList.copyOf(this.structureFeatures)
			);
		}
	}
}
