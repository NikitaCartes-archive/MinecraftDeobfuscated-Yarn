/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PickFromInventoryC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int slot;

    public PickFromInventoryC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PickFromInventoryC2SPacket(int i) {
        this.slot = i;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.slot = packetByteBuf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.slot);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPickFromInventory(this);
    }

    public int getSlot() {
        return this.slot;
    }
}

