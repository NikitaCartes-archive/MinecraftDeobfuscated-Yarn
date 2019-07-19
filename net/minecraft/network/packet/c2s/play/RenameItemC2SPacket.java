/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class RenameItemC2SPacket
implements Packet<ServerPlayPacketListener> {
    private String itemName;

    public RenameItemC2SPacket() {
    }

    public RenameItemC2SPacket(String string) {
        this.itemName = string;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.itemName = packetByteBuf.readString(Short.MAX_VALUE);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(this.itemName);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRenameItem(this);
    }

    public String getItemName() {
        return this.itemName;
    }
}

