/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.class_5428;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class CountConfig
implements DecoratorConfig,
FeatureConfig {
    public static final Codec<CountConfig> CODEC = ((MapCodec)class_5428.method_30316(-10, 128, 128).fieldOf("count")).xmap(CountConfig::new, CountConfig::method_30396).codec();
    private final class_5428 count;

    public CountConfig(int count) {
        this.count = class_5428.method_30314(count);
    }

    public CountConfig(class_5428 arg) {
        this.count = arg;
    }

    public class_5428 method_30396() {
        return this.count;
    }
}

