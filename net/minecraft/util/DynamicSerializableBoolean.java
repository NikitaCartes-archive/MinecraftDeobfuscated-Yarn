/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.DynamicSerializable;

public final class DynamicSerializableBoolean
implements DynamicSerializable {
    private final boolean value;

    private DynamicSerializableBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return ops.createBoolean(this.value);
    }

    public static DynamicSerializableBoolean of(Dynamic<?> dynamic) {
        return new DynamicSerializableBoolean(dynamic.asBoolean(false));
    }

    public static DynamicSerializableBoolean of(boolean value) {
        return new DynamicSerializableBoolean(value);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        DynamicSerializableBoolean dynamicSerializableBoolean = (DynamicSerializableBoolean)object;
        return this.value == dynamicSerializableBoolean.value;
    }

    public int hashCode() {
        return Boolean.hashCode(this.value);
    }

    public String toString() {
        return Boolean.toString(this.value);
    }
}

