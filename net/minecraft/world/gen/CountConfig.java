/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class CountConfig
implements FeatureConfig {
    public static final Codec<CountConfig> CODEC = ((MapCodec)IntProvider.createValidatingCodec(0, 256).fieldOf("count")).xmap(CountConfig::new, CountConfig::getCount).codec();
    private final IntProvider count;

    public CountConfig(int count) {
        this.count = ConstantIntProvider.create(count);
    }

    public CountConfig(IntProvider distribution) {
        this.count = distribution;
    }

    public IntProvider getCount() {
        return this.count;
    }
}

