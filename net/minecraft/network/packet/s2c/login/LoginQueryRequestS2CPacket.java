/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.login;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.Identifier;

public class LoginQueryRequestS2CPacket
implements Packet<ClientLoginPacketListener> {
    private final int queryId;
    private final Identifier channel;
    private final PacketByteBuf payload;

    public LoginQueryRequestS2CPacket(PacketByteBuf buf) {
        this.queryId = buf.readVarInt();
        this.channel = buf.readIdentifier();
        int i = buf.readableBytes();
        if (i < 0 || i > 0x100000) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        this.payload = new PacketByteBuf(buf.readBytes(i));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.queryId);
        buf.writeIdentifier(this.channel);
        buf.writeBytes(this.payload.copy());
    }

    @Override
    public void apply(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.onQueryRequest(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getQueryId() {
        return this.queryId;
    }
}

