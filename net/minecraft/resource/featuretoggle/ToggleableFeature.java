/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.featuretoggle;

import java.util.Set;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureSet;

public interface ToggleableFeature {
    public static final Set<RegistryKey<? extends Registry<? extends ToggleableFeature>>> FEATURE_ENABLED_REGISTRY_KEYS = Set.of(RegistryKeys.ITEM, RegistryKeys.BLOCK, RegistryKeys.ENTITY_TYPE, RegistryKeys.SCREEN_HANDLER);

    public FeatureSet getRequiredFeatures();

    default public boolean isEnabled(FeatureSet enabledFeatures) {
        return this.getRequiredFeatures().isSubsetOf(enabledFeatures);
    }
}

