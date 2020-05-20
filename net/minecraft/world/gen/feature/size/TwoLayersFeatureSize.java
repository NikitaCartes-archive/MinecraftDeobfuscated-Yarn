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
    public static final Codec<TwoLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("limit")).withDefault(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.field_24155), ((MapCodec)Codec.INT.fieldOf("lower_size")).withDefault(0).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.field_24156), ((MapCodec)Codec.INT.fieldOf("upper_size")).withDefault(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.field_24157), TwoLayersFeatureSize.method_28820()).apply((Applicative<TwoLayersFeatureSize, ?>)instance, TwoLayersFeatureSize::new));
    private final int field_24155;
    private final int field_24156;
    private final int field_24157;

    public TwoLayersFeatureSize(int i, int j, int k) {
        this(i, j, k, OptionalInt.empty());
    }

    public TwoLayersFeatureSize(int i, int j, int k, OptionalInt minClippedHeight) {
        super(minClippedHeight);
        this.field_24155 = i;
        this.field_24156 = j;
        this.field_24157 = k;
    }

    @Override
    protected FeatureSizeType<?> method_28824() {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int method_27378(int i, int j) {
        return j < this.field_24155 ? this.field_24156 : this.field_24157;
    }
}

