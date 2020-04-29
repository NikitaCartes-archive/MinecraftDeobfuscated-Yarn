/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature.size;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public abstract class FeatureSize {
    protected final FeatureSizeType<?> type;
    private final OptionalInt minClippedHeight;

    public FeatureSize(FeatureSizeType<?> type, OptionalInt minClippedHeight) {
        this.type = type;
        this.minClippedHeight = minClippedHeight;
    }

    public abstract int method_27378(int var1, int var2);

    public OptionalInt getMinClippedHeight() {
        return this.minClippedHeight;
    }

    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.FEATURE_SIZE_TYPE.getId(this.type).toString()));
        this.minClippedHeight.ifPresent(i -> builder.put(ops.createString("min_clipped_height"), ops.createInt(i)));
        return new Dynamic<T>(ops, ops.createMap(builder.build())).getValue();
    }
}

