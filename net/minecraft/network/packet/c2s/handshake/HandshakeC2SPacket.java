/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.handshake;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.util.PacketByteBuf;

public class HandshakeC2SPacket
implements Packet<ServerHandshakePacketListener> {
    private int protocolVersion;
    private String address;
    private int port;
    private NetworkState intendedState;

    public HandshakeC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public HandshakeC2SPacket(String string, int i, NetworkState networkState) {
        this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
        this.address = string;
        this.port = i;
        this.intendedState = networkState;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.protocolVersion = packetByteBuf.readVarInt();
        this.address = packetByteBuf.readString(255);
        this.port = packetByteBuf.readUnsignedShort();
        this.intendedState = NetworkState.byId(packetByteBuf.readVarInt());
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.protocolVersion);
        packetByteBuf.writeString(this.address);
        packetByteBuf.writeShort(this.port);
        packetByteBuf.writeVarInt(this.intendedState.getId());
    }

    @Override
    public void apply(ServerHandshakePacketListener serverHandshakePacketListener) {
        serverHandshakePacketListener.onHandshake(this);
    }

    public NetworkState getIntendedState() {
        return this.intendedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}

