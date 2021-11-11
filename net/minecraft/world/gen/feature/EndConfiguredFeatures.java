/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EndConfiguredFeatures {
    public static final ConfiguredFeature<?, ?> END_SPIKE = ConfiguredFeatures.register("end_spike", Feature.END_SPIKE.configure(new EndSpikeFeatureConfig(false, (List<EndSpikeFeature.Spike>)ImmutableList.of(), null)));
    public static final ConfiguredFeature<?, ?> END_GATEWAY_RETURN = ConfiguredFeatures.register("end_gateway_return", Feature.END_GATEWAY.configure(EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true)));
    public static final ConfiguredFeature<?, ?> END_GATEWAY_DELAYED = ConfiguredFeatures.register("end_gateway_delayed", Feature.END_GATEWAY.configure(EndGatewayFeatureConfig.createConfig()));
    public static final ConfiguredFeature<?, ?> CHORUS_PLANT = ConfiguredFeatures.register("chorus_plant", Feature.CHORUS_PLANT.configure(FeatureConfig.DEFAULT));
    public static final ConfiguredFeature<?, ?> END_ISLAND = ConfiguredFeatures.register("end_island", Feature.END_ISLAND.configure(FeatureConfig.DEFAULT));
}

