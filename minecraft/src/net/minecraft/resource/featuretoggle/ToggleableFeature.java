package net.minecraft.resource.featuretoggle;

import java.util.Set;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface ToggleableFeature {
	Set<RegistryKey<? extends Registry<? extends ToggleableFeature>>> FEATURE_ENABLED_REGISTRY_KEYS = Set.of(
		Registry.ITEM_KEY, Registry.BLOCK_KEY, Registry.ENTITY_TYPE_KEY
	);

	FeatureSet getRequiredFeatures();

	default boolean isEnabled(FeatureSet enabledFeatures) {
		return this.getRequiredFeatures().isSubsetOf(enabledFeatures);
	}
}
