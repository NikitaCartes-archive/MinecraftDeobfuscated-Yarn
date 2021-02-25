/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ResourcePackStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final Status status;

    public ResourcePackStatusC2SPacket(Status status) {
        this.status = status;
    }

    public ResourcePackStatusC2SPacket(PacketByteBuf buf) {
        this.status = buf.readEnumConstant(Status.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
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

