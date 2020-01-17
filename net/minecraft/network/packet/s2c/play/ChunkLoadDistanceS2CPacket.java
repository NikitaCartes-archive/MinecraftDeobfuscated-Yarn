/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkLoadDistanceS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int distance;

    public ChunkLoadDistanceS2CPacket() {
    }

    public ChunkLoadDistanceS2CPacket(int distance) {
        this.distance = distance;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.distance = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
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

