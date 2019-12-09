/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network.packet;

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
    public PickFromInventoryC2SPacket(int slot) {
        this.slot = slot;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.slot = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.slot);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPickFromInventory(this);
    }

    public int getSlot() {
        return this.slot;
    }
}

