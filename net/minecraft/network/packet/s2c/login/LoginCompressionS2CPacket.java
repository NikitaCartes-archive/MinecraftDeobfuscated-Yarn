/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.login;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginCompressionS2CPacket
implements Packet<ClientLoginPacketListener> {
    private int compressionThreshold;

    public LoginCompressionS2CPacket() {
    }

    public LoginCompressionS2CPacket(int i) {
        this.compressionThreshold = i;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.compressionThreshold = packetByteBuf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.compressionThreshold);
    }

    @Override
    public void apply(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.onCompression(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}

