/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.DynamicSerializable;

public final class Timestamp
implements DynamicSerializable {
    private final long time;

    private Timestamp(long l) {
        this.time = l;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return dynamicOps.createLong(this.time);
    }

    public static Timestamp of(Dynamic<?> dynamic) {
        return new Timestamp(dynamic.asNumber(0).longValue());
    }

    public static Timestamp of(long l) {
        return new Timestamp(l);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Timestamp timestamp = (Timestamp)object;
        return this.time == timestamp.time;
    }

    public int hashCode() {
        return Long.hashCode(this.time);
    }

    public String toString() {
        return Long.toString(this.time);
    }
}

