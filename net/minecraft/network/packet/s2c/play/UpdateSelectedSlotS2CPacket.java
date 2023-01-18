/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class UpdateSelectedSlotS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int selectedSlot;

    public UpdateSelectedSlotS2CPacket(int slot) {
        this.selectedSlot = slot;
    }

    public UpdateSelectedSlotS2CPacket(PacketByteBuf buf) {
        this.selectedSlot = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.selectedSlot);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onUpdateSelectedSlot(this);
    }

    public int getSlot() {
        return this.selectedSlot;
    }
}

