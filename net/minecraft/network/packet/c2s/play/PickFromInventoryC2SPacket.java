/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PickFromInventoryC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final int slot;

    @Environment(value=EnvType.CLIENT)
    public PickFromInventoryC2SPacket(int slot) {
        this.slot = slot;
    }

    public PickFromInventoryC2SPacket(PacketByteBuf buf) {
        this.slot = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
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

