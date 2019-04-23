/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;

public class TrackedData<T> {
    private final int id;
    private final TrackedDataHandler<T> dataType;

    public TrackedData(int i, TrackedDataHandler<T> trackedDataHandler) {
        this.id = i;
        this.dataType = trackedDataHandler;
    }

    public int getId() {
        return this.id;
    }

    public TrackedDataHandler<T> getType() {
        return this.dataType;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TrackedData trackedData = (TrackedData)object;
        return this.id == trackedData.id;
    }

    public int hashCode() {
        return this.id;
    }
}

