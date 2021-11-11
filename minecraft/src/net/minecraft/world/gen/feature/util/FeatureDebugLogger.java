package net.minecraft.world.gen.feature.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FeatureDebugLogger {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final LoadingCache<ServerWorld, FeatureDebugLogger.Features> FEATURES = CacheBuilder.newBuilder()
		.weakKeys()
		.expireAfterAccess(5L, TimeUnit.MINUTES)
		.build(new CacheLoader<ServerWorld, FeatureDebugLogger.Features>() {
			public FeatureDebugLogger.Features load(ServerWorld serverWorld) {
				return new FeatureDebugLogger.Features(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
			}
		});

	public static void incrementTotalChunksCount(ServerWorld world) {
		try {
			((FeatureDebugLogger.Features)FEATURES.get(world)).chunksWithFeatures().increment();
		} catch (Exception var2) {
			LOGGER.error(var2);
		}
	}

	public static void incrementFeatureCount(ServerWorld world, ConfiguredFeature<?, ?> configuredFeature, Optional<PlacedFeature> placedFeature) {
		try {
			((FeatureDebugLogger.Features)FEATURES.get(world))
				.featureData()
				.computeInt(new FeatureDebugLogger.FeatureData(configuredFeature, placedFeature), (featureData, count) -> count == null ? 1 : count + 1);
		} catch (Exception var4) {
			LOGGER.error(var4);
		}
	}

	public static void clear() {
		FEATURES.invalidateAll();
		LOGGER.debug("Cleared feature counts");
	}

	public static void dump() {
		LOGGER.debug("Logging feature counts:");
		FEATURES.asMap()
			.forEach(
				(world, features) -> {
					String string = world.getRegistryKey().getValue().toString();
					boolean bl = world.getServer().isRunning();
					Registry<PlacedFeature> registry = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
					String string2 = (bl ? "running" : "dead") + " " + string;
					Integer integer = features.chunksWithFeatures().getValue();
					LOGGER.debug(string2 + " total_chunks: " + integer);
					features.featureData()
						.forEach(
							(featureData, count) -> LOGGER.debug(
									string2
										+ " "
										+ String.format("%10d ", count)
										+ String.format("%10f ", (double)count.intValue() / (double)integer.intValue())
										+ featureData.topFeature().flatMap(registry::getKey).map(RegistryKey::getValue)
										+ " "
										+ featureData.feature().getFeature()
										+ " "
										+ featureData.feature()
								)
						);
				}
			);
	}

	static final class FeatureData extends Record {
		private final ConfiguredFeature<?, ?> feature;
		private final Optional<PlacedFeature> topFeature;

		FeatureData(ConfiguredFeature<?, ?> configuredFeature, Optional<PlacedFeature> optional) {
			this.feature = configuredFeature;
			this.topFeature = optional;
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",FeatureDebugLogger.FeatureData,"feature;topFeature",FeatureDebugLogger.FeatureData::feature,FeatureDebugLogger.FeatureData::topFeature>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",FeatureDebugLogger.FeatureData,"feature;topFeature",FeatureDebugLogger.FeatureData::feature,FeatureDebugLogger.FeatureData::topFeature>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",FeatureDebugLogger.FeatureData,"feature;topFeature",FeatureDebugLogger.FeatureData::feature,FeatureDebugLogger.FeatureData::topFeature>(
				this, object
			);
		}

		public ConfiguredFeature<?, ?> feature() {
			return this.feature;
		}

		public Optional<PlacedFeature> topFeature() {
			return this.topFeature;
		}
	}

	static final class Features extends Record {
		private final Object2IntMap<FeatureDebugLogger.FeatureData> featureData;
		private final MutableInt chunksWithFeatures;

		Features(Object2IntMap<FeatureDebugLogger.FeatureData> object2IntMap, MutableInt mutableInt) {
			this.featureData = object2IntMap;
			this.chunksWithFeatures = mutableInt;
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",FeatureDebugLogger.Features,"featureData;chunksWithFeatures",FeatureDebugLogger.Features::featureData,FeatureDebugLogger.Features::chunksWithFeatures>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",FeatureDebugLogger.Features,"featureData;chunksWithFeatures",FeatureDebugLogger.Features::featureData,FeatureDebugLogger.Features::chunksWithFeatures>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",FeatureDebugLogger.Features,"featureData;chunksWithFeatures",FeatureDebugLogger.Features::featureData,FeatureDebugLogger.Features::chunksWithFeatures>(
				this, object
			);
		}

		public Object2IntMap<FeatureDebugLogger.FeatureData> featureData() {
			return this.featureData;
		}

		public MutableInt chunksWithFeatures() {
			return this.chunksWithFeatures;
		}
	}
}
