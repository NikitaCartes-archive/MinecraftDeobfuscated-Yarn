/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class UpdateSelectedSlotS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int selectedSlot;

    public UpdateSelectedSlotS2CPacket(int slot) {
        this.selectedSlot = slot;
    }

    public UpdateSelectedSlotS2CPacket(PacketByteBuf packetByteBuf) {
        this.selectedSlot = packetByteBuf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.selectedSlot);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onHeldItemChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlot() {
        return this.selectedSlot;
    }
}

