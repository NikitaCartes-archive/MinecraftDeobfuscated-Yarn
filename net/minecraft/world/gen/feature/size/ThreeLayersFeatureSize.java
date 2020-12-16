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

public class ThreeLayersFeatureSize
extends FeatureSize {
    public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(0, 80).fieldOf("limit")).orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.limit), ((MapCodec)Codec.intRange(0, 80).fieldOf("upper_limit")).orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperLimit), ((MapCodec)Codec.intRange(0, 16).fieldOf("lower_size")).orElse(0).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.lowerSize), ((MapCodec)Codec.intRange(0, 16).fieldOf("middle_size")).orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.middleSize), ((MapCodec)Codec.intRange(0, 16).fieldOf("upper_size")).orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperSize), ThreeLayersFeatureSize.createCodec()).apply((Applicative<ThreeLayersFeatureSize, ?>)instance, ThreeLayersFeatureSize::new));
    private final int limit;
    private final int upperLimit;
    private final int lowerSize;
    private final int middleSize;
    private final int upperSize;

    public ThreeLayersFeatureSize(int limit, int upperLimit, int lowerSize, int middleSize, int upperSize, OptionalInt minClippedHeight) {
        super(minClippedHeight);
        this.limit = limit;
        this.upperLimit = upperLimit;
        this.lowerSize = lowerSize;
        this.middleSize = middleSize;
        this.upperSize = upperSize;
    }

    @Override
    protected FeatureSizeType<?> getType() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int getRadius(int height, int y) {
        if (y < this.limit) {
            return this.lowerSize;
        }
        if (y >= height - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }
}

