/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CloseContainerS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;

    public CloseContainerS2CPacket() {
    }

    public CloseContainerS2CPacket(int syncId) {
        this.syncId = syncId;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCloseContainer(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.syncId = buf.readUnsignedByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
    }
}

