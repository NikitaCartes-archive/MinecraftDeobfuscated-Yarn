/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryEntityNbtC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int transactionId;
    private int entityId;

    public QueryEntityNbtC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public QueryEntityNbtC2SPacket(int i, int j) {
        this.transactionId = i;
        this.entityId = j;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.transactionId = packetByteBuf.readVarInt();
        this.entityId = packetByteBuf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.transactionId);
        packetByteBuf.writeVarInt(this.entityId);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onQueryEntityNbt(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public int getEntityId() {
        return this.entityId;
    }
}

