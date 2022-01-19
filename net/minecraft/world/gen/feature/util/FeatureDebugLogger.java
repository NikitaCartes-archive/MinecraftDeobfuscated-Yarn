/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureDebugLogger {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LoadingCache<ServerWorld, Features> FEATURES = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<ServerWorld, Features>(){

        @Override
        public Features load(ServerWorld serverWorld) {
            return new Features(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
        }

        @Override
        public /* synthetic */ Object load(Object world) throws Exception {
            return this.load((ServerWorld)world);
        }
    });

    public static void incrementTotalChunksCount(ServerWorld world) {
        try {
            FEATURES.get(world).chunksWithFeatures().increment();
        } catch (Exception exception) {
            LOGGER.error("Failed to increment chunk count", exception);
        }
    }

    public static void incrementFeatureCount(ServerWorld world, ConfiguredFeature<?, ?> configuredFeature, Optional<PlacedFeature> placedFeature) {
        try {
            FEATURES.get(world).featureData().computeInt(new FeatureData(configuredFeature, placedFeature), (featureData, count) -> count == null ? 1 : count + 1);
        } catch (Exception exception) {
            LOGGER.error("Failed to increment feature count", exception);
        }
    }

    public static void clear() {
        FEATURES.invalidateAll();
        LOGGER.debug("Cleared feature counts");
    }

    public static void dump() {
        LOGGER.debug("Logging feature counts:");
        FEATURES.asMap().forEach((world, features) -> {
            String string = world.getRegistryKey().getValue().toString();
            boolean bl = world.getServer().isRunning();
            Registry<PlacedFeature> registry = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
            String string2 = (bl ? "running" : "dead") + " " + string;
            Integer integer = features.chunksWithFeatures().getValue();
            LOGGER.debug(string2 + " total_chunks: " + integer);
            features.featureData().forEach((featureData, count) -> LOGGER.debug(string2 + " " + String.format("%10d ", count) + String.format("%10f ", (double)count.intValue() / (double)integer.intValue()) + featureData.topFeature().flatMap(registry::getKey).map(RegistryKey::getValue) + " " + featureData.feature().getFeature() + " " + featureData.feature()));
        });
    }

    record Features(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
    }

    record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
    }
}

