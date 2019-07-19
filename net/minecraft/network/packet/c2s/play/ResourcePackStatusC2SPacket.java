/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ResourcePackStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Status status;

    public ResourcePackStatusC2SPacket() {
    }

    public ResourcePackStatusC2SPacket(Status status) {
        this.status = status;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.status = packetByteBuf.readEnumConstant(Status.class);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.status);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onResourcePackStatus(this);
    }

    public static enum Status {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;

    }
}

