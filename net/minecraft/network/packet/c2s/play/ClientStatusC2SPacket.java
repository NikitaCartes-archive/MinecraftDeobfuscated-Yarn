/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ClientStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final Mode mode;

    public ClientStatusC2SPacket(Mode mode) {
        this.mode = mode;
    }

    public ClientStatusC2SPacket(PacketByteBuf buf) {
        this.mode = buf.readEnumConstant(Mode.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.mode);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onClientStatus(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public static enum Mode {
        PERFORM_RESPAWN,
        REQUEST_STATS;

    }
}

