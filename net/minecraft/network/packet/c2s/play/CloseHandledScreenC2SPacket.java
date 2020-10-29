/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class CloseHandledScreenC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int syncId;

    public CloseHandledScreenC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public CloseHandledScreenC2SPacket(int syncId) {
        this.syncId = syncId;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onCloseHandledScreen(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.syncId = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
    }
}

