/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ChunkLoadDistanceS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int distance;

    public ChunkLoadDistanceS2CPacket(int distance) {
        this.distance = distance;
    }

    public ChunkLoadDistanceS2CPacket(PacketByteBuf buf) {
        this.distance = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.distance);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onChunkLoadDistance(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getDistance() {
        return this.distance;
    }
}

