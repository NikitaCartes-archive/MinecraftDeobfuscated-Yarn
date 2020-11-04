/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public class TwoLayersFeatureSize
extends FeatureSize {
    public static final Codec<TwoLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(0, 81).fieldOf("limit")).orElse(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.limit), ((MapCodec)Codec.intRange(0, 16).fieldOf("lower_size")).orElse(0).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.lowerSize), ((MapCodec)Codec.intRange(0, 16).fieldOf("upper_size")).orElse(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.upperSize), TwoLayersFeatureSize.createCodecBuilder()).apply((Applicative<TwoLayersFeatureSize, ?>)instance, TwoLayersFeatureSize::new));
    private final int limit;
    private final int lowerSize;
    private final int upperSize;

    public TwoLayersFeatureSize(int limit, int lowerSize, int upperSize) {
        this(limit, lowerSize, upperSize, OptionalInt.empty());
    }

    public TwoLayersFeatureSize(int limit, int lowerSize, int upperSize, OptionalInt minClippedHeight) {
        super(minClippedHeight);
        this.limit = limit;
        this.lowerSize = lowerSize;
        this.upperSize = upperSize;
    }

    @Override
    protected FeatureSizeType<?> getType() {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int method_27378(int i, int j) {
        return j < this.limit ? this.lowerSize : this.upperSize;
    }
}

