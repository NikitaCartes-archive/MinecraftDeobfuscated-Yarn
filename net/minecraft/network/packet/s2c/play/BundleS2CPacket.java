/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;

public class BundleS2CPacket
extends BundlePacket<ClientPlayPacketListener> {
    public BundleS2CPacket(Iterable<Packet<ClientPlayPacketListener>> iterable) {
        super(iterable);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBundle(this);
    }
}

