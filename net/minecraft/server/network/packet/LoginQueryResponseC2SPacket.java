/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class LoginQueryResponseC2SPacket
implements Packet<ServerLoginPacketListener> {
    private int queryId;
    private PacketByteBuf response;

    public LoginQueryResponseC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public LoginQueryResponseC2SPacket(int i, @Nullable PacketByteBuf packetByteBuf) {
        this.queryId = i;
        this.response = packetByteBuf;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.queryId = packetByteBuf.readVarInt();
        if (packetByteBuf.readBoolean()) {
            int i = packetByteBuf.readableBytes();
            if (i < 0 || i > 0x100000) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.response = new PacketByteBuf(packetByteBuf.readBytes(i));
        } else {
            this.response = null;
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.queryId);
        if (this.response != null) {
            packetByteBuf.writeBoolean(true);
            packetByteBuf.writeBytes(this.response.copy());
        } else {
            packetByteBuf.writeBoolean(false);
        }
    }

    public void method_12645(ServerLoginPacketListener serverLoginPacketListener) {
        serverLoginPacketListener.onQueryResponse(this);
    }
}

