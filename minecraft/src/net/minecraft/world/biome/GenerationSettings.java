package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.slf4j.Logger;

public class GenerationSettings {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final GenerationSettings INSTANCE = new GenerationSettings(ImmutableMap.of(), ImmutableList.of());
	public static final MapCodec<GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.simpleMap(
							GenerationStep.Carver.CODEC,
							ConfiguredCarver.LIST_CODEC
								.promotePartial(Util.addPrefix("Carver: ", LOGGER::error))
								.flatXmap(Codecs.createPresentValuesChecker(), Codecs.createPresentValuesChecker()),
							StringIdentifiable.toKeyable(GenerationStep.Carver.values())
						)
						.fieldOf("carvers")
						.forGetter(generationSettings -> generationSettings.carvers),
					PlacedFeature.LIST_CODEC
						.promotePartial(Util.addPrefix("Feature: ", LOGGER::error))
						.flatXmap(Codecs.createPresentValuesChecker(), Codecs.createPresentValuesChecker())
						.listOf()
						.fieldOf("features")
						.forGetter(generationSettings -> generationSettings.features)
				)
				.apply(instance, GenerationSettings::new)
	);
	private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
	private final List<List<Supplier<PlacedFeature>>> features;
	private final List<ConfiguredFeature<?, ?>> flowerFeatures;
	private final Set<PlacedFeature> allowedFeatures;

	GenerationSettings(Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers, List<List<Supplier<PlacedFeature>>> features) {
		this.carvers = carvers;
		this.features = features;
		this.flowerFeatures = (List<ConfiguredFeature<?, ?>>)features.stream()
			.flatMap(Collection::stream)
			.map(Supplier::get)
			.flatMap(PlacedFeature::getDecoratedFeatures)
			.filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER)
			.collect(ImmutableList.toImmutableList());
		this.allowedFeatures = (Set<PlacedFeature>)features.stream().flatMap(Collection::stream).map(Supplier::get).collect(Collectors.toSet());
	}

	public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carverStep) {
		return (List<Supplier<ConfiguredCarver<?>>>)this.carvers.getOrDefault(carverStep, ImmutableList.of());
	}

	public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
		return this.flowerFeatures;
	}

	/**
	 * Returns the lists of features configured for each {@link net.minecraft.world.gen.GenerationStep.Feature feature generation step}, up to the highest step that has a configured feature.
	 * Entries are guaranteed to not be null, but may be empty lists if an earlier step has no features, but a later step does.
	 */
	public List<List<Supplier<PlacedFeature>>> getFeatures() {
		return this.features;
	}

	public boolean isFeatureAllowed(PlacedFeature feature) {
		return this.allowedFeatures.contains(feature);
	}

	public static class Builder {
		private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers = Maps.<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>>newLinkedHashMap();
		private final List<List<Supplier<PlacedFeature>>> features = Lists.<List<Supplier<PlacedFeature>>>newArrayList();

		public GenerationSettings.Builder feature(GenerationStep.Feature featureStep, PlacedFeature feature) {
			return this.feature(featureStep.ordinal(), () -> feature);
		}

		public GenerationSettings.Builder feature(int stepIndex, Supplier<PlacedFeature> featureSupplier) {
			this.addFeatureStep(stepIndex);
			((List)this.features.get(stepIndex)).add(featureSupplier);
			return this;
		}

		public <C extends CarverConfig> GenerationSettings.Builder carver(GenerationStep.Carver carverStep, ConfiguredCarver<C> carver) {
			((List)this.carvers.computeIfAbsent(carverStep, carverx -> Lists.newArrayList())).add((Supplier)() -> carver);
			return this;
		}

		private void addFeatureStep(int stepIndex) {
			while (this.features.size() <= stepIndex) {
				this.features.add(Lists.newArrayList());
			}
		}

		public GenerationSettings build() {
			return new GenerationSettings(
				(Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>>)this.carvers
					.entrySet()
					.stream()
					.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue()))),
				(List<List<Supplier<PlacedFeature>>>)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList())
			);
		}
	}
}
