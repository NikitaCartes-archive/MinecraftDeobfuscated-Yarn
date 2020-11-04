/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ResourcePackStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Status status;

    public ResourcePackStatusC2SPacket() {
    }

    public ResourcePackStatusC2SPacket(Status status) {
        this.status = status;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.status = buf.readEnumConstant(Status.class);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.status);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onResourcePackStatus(this);
    }

    public Status getStatus() {
        return this.status;
    }

    public static enum Status {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;

    }
}

