/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

public class LoginQueryResponseC2SPacket
implements Packet<ServerLoginPacketListener> {
    private static final int MAX_PAYLOAD_SIZE = 0x100000;
    private final int queryId;
    @Nullable
    private final PacketByteBuf response;

    public LoginQueryResponseC2SPacket(int queryId, @Nullable PacketByteBuf response) {
        this.queryId = queryId;
        this.response = response;
    }

    public LoginQueryResponseC2SPacket(PacketByteBuf buf) {
        this.queryId = buf.readVarInt();
        this.response = (PacketByteBuf)buf.readNullable(buf2 -> {
            int i = buf2.readableBytes();
            if (i < 0 || i > 0x100000) {
                throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
            }
            return new PacketByteBuf(buf2.readBytes(i));
        });
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.queryId);
        buf.writeNullable(this.response, (buf2, response) -> buf2.writeBytes(response.slice()));
    }

    @Override
    public void apply(ServerLoginPacketListener serverLoginPacketListener) {
        serverLoginPacketListener.onQueryResponse(this);
    }

    public int getQueryId() {
        return this.queryId;
    }

    @Nullable
    public PacketByteBuf getResponse() {
        return this.response;
    }
}

