/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ConfirmGuiActionS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private short actionId;
    private boolean accepted;

    public ConfirmGuiActionS2CPacket() {
    }

    public ConfirmGuiActionS2CPacket(int i, short s, boolean bl) {
        this.id = i;
        this.actionId = s;
        this.accepted = bl;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGuiActionConfirm(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.id = packetByteBuf.readUnsignedByte();
        this.actionId = packetByteBuf.readShort();
        this.accepted = packetByteBuf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeByte(this.id);
        packetByteBuf.writeShort(this.actionId);
        packetByteBuf.writeBoolean(this.accepted);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public short getActionId() {
        return this.actionId;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean wasAccepted() {
        return this.accepted;
    }
}

