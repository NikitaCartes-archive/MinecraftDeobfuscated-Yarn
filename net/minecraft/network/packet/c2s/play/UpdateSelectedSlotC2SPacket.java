/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdateSelectedSlotC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final int selectedSlot;

    @Environment(value=EnvType.CLIENT)
    public UpdateSelectedSlotC2SPacket(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public UpdateSelectedSlotC2SPacket(PacketByteBuf buf) {
        this.selectedSlot = buf.readShort();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeShort(this.selectedSlot);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onUpdateSelectedSlot(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}

