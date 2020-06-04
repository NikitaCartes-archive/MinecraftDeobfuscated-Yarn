/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.class_5379;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionTracker;

public class class_5384<T>
extends class_5379<T> {
    private final DimensionTracker field_25514;

    public static <T> class_5384<T> method_29771(DynamicOps<T> dynamicOps, DimensionTracker dimensionTracker) {
        return new class_5384<T>(dynamicOps, dimensionTracker);
    }

    private class_5384(DynamicOps<T> dynamicOps, DimensionTracker dimensionTracker) {
        super(dynamicOps);
        this.field_25514 = dimensionTracker;
    }

    protected <E> DataResult<T> method_29772(E object, T object2, RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
        Optional<RegistryKey<E>> optional2;
        Optional<MutableRegistry<E>> optional = this.field_25514.method_29726(registryKey);
        if (optional.isPresent() && (optional2 = optional.get().getKey(object)).isPresent()) {
            return Identifier.field_25139.encode(optional2.get().getValue(), this.field_25503, object2);
        }
        return codec.encode(object, this.field_25503, object2);
    }
}

