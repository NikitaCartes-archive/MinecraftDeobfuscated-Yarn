/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.PacketByteBuf;

public interface TrackedDataHandler<T> {
    public void write(PacketByteBuf var1, T var2);

    public T read(PacketByteBuf var1);

    default public TrackedData<T> create(int i) {
        return new TrackedData(i, this);
    }

    public T copy(T var1);
}

