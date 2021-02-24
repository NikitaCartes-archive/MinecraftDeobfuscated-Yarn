/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener> {
    public void write(PacketByteBuf var1);

    public void apply(T var1);

    /**
     * Returns whether a throwable in writing of this packet allows the
     * connection to simply skip the packet's sending than disconnecting.
     */
    default public boolean isWritingErrorSkippable() {
        return false;
    }
}

