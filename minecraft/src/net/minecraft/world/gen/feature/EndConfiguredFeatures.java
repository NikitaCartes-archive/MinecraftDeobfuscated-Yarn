package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryEntry;

public class EndConfiguredFeatures {
	public static final RegistryEntry<ConfiguredFeature<EndSpikeFeatureConfig, ?>> END_SPIKE = ConfiguredFeatures.register(
		"end_spike", Feature.END_SPIKE, new EndSpikeFeatureConfig(false, ImmutableList.of(), null)
	);
	public static final RegistryEntry<ConfiguredFeature<EndGatewayFeatureConfig, ?>> END_GATEWAY_RETURN = ConfiguredFeatures.register(
		"end_gateway_return", Feature.END_GATEWAY, EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true)
	);
	public static final RegistryEntry<ConfiguredFeature<EndGatewayFeatureConfig, ?>> END_GATEWAY_DELAYED = ConfiguredFeatures.register(
		"end_gateway_delayed", Feature.END_GATEWAY, EndGatewayFeatureConfig.createConfig()
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> CHORUS_PLANT = ConfiguredFeatures.method_40364(
		"chorus_plant", Feature.CHORUS_PLANT
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> END_ISLAND = ConfiguredFeatures.method_40364("end_island", Feature.END_ISLAND);
}
