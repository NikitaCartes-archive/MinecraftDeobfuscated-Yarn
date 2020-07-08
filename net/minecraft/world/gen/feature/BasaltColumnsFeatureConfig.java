/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_5428;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
    public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)class_5428.method_30316(0, 2, 1).fieldOf("reach")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_25841), ((MapCodec)class_5428.method_30316(1, 5, 5).fieldOf("height")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_25842)).apply((Applicative<BasaltColumnsFeatureConfig, ?>)instance, BasaltColumnsFeatureConfig::new));
    private final class_5428 field_25841;
    private final class_5428 field_25842;

    public BasaltColumnsFeatureConfig(class_5428 arg, class_5428 arg2) {
        this.field_25841 = arg;
        this.field_25842 = arg2;
    }

    public class_5428 method_30391() {
        return this.field_25841;
    }

    public class_5428 method_30394() {
        return this.field_25842;
    }
}

