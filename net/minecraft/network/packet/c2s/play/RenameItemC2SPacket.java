/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class RenameItemC2SPacket
implements Packet<ServerPlayPacketListener> {
    private String name;

    public RenameItemC2SPacket() {
    }

    public RenameItemC2SPacket(String name) {
        this.name = name;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.name = buf.readString(Short.MAX_VALUE);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeString(this.name);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRenameItem(this);
    }

    public String getName() {
        return this.name;
    }
}

