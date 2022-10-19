/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.featuretoggle;

import net.minecraft.resource.featuretoggle.FeatureUniverse;

public class FeatureFlag {
    final FeatureUniverse universe;
    final long mask;

    FeatureFlag(FeatureUniverse universe, int id) {
        this.universe = universe;
        this.mask = 1L << id;
    }
}

