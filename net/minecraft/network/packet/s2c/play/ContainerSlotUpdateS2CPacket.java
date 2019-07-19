/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ContainerSlotUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public ContainerSlotUpdateS2CPacket() {
    }

    public ContainerSlotUpdateS2CPacket(int i, int j, ItemStack itemStack) {
        this.syncId = i;
        this.slot = j;
        this.stack = itemStack.copy();
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onContainerSlotUpdate(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.syncId = packetByteBuf.readByte();
        this.slot = packetByteBuf.readShort();
        this.stack = packetByteBuf.readItemStack();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeByte(this.syncId);
        packetByteBuf.writeShort(this.slot);
        packetByteBuf.writeItemStack(this.stack);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.stack;
    }
}

