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

public class ContainerPropertyUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int propertyId;
    private int value;

    public ContainerPropertyUpdateS2CPacket() {
    }

    public ContainerPropertyUpdateS2CPacket(int i, int j, int k) {
        this.syncId = i;
        this.propertyId = j;
        this.value = k;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onContainerPropertyUpdate(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.syncId = packetByteBuf.readUnsignedByte();
        this.propertyId = packetByteBuf.readShort();
        this.value = packetByteBuf.readShort();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeByte(this.syncId);
        packetByteBuf.writeShort(this.propertyId);
        packetByteBuf.writeShort(this.value);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getPropertyId() {
        return this.propertyId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getValue() {
        return this.value;
    }
}

