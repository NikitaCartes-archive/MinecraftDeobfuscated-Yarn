package net.minecraft.resource.featuretoggle;

import java.util.Set;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public interface ToggleableFeature {
	Set<RegistryKey<? extends Registry<? extends ToggleableFeature>>> FEATURE_ENABLED_REGISTRY_KEYS = Set.of(
		RegistryKeys.ITEM, RegistryKeys.BLOCK, RegistryKeys.ENTITY_TYPE, RegistryKeys.SCREEN_HANDLER
	);

	FeatureSet getRequiredFeatures();

	default boolean isEnabled(FeatureSet enabledFeatures) {
		return this.getRequiredFeatures().isSubsetOf(enabledFeatures);
	}
}
