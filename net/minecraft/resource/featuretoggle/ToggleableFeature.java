/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.featuretoggle;

import java.util.Set;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface ToggleableFeature {
    public static final Set<RegistryKey<? extends Registry<? extends ToggleableFeature>>> FEATURE_ENABLED_REGISTRY_KEYS = Set.of(Registry.ITEM_KEY, Registry.BLOCK_KEY, Registry.ENTITY_TYPE_KEY);

    public FeatureSet getRequiredFeatures();

    default public boolean isEnabled(FeatureSet enabledFeatures) {
        return this.getRequiredFeatures().isSubsetOf(enabledFeatures);
    }
}

