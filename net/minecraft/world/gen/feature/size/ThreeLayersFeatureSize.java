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
    public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("limit")).withDefault(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.limit), ((MapCodec)Codec.INT.fieldOf("upper_limit")).withDefault(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperLimit), ((MapCodec)Codec.INT.fieldOf("lower_size")).withDefault(0).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.lowerSize), ((MapCodec)Codec.INT.fieldOf("middle_size")).withDefault(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.middleSize), ((MapCodec)Codec.INT.fieldOf("upper_size")).withDefault(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperSize), ThreeLayersFeatureSize.createCodecBuilder()).apply((Applicative<ThreeLayersFeatureSize, ?>)instance, ThreeLayersFeatureSize::new));
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
    public int method_27378(int i, int j) {
        if (j < this.limit) {
            return this.lowerSize;
        }
        if (j >= i - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }
}

