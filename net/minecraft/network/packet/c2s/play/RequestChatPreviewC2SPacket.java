/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public record RequestChatPreviewC2SPacket(int queryId, String query) implements Packet<ServerPlayPacketListener>
{
    public RequestChatPreviewC2SPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readString(256));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.queryId);
        buf.writeString(this.query, 256);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRequestChatPreview(this);
    }
}

