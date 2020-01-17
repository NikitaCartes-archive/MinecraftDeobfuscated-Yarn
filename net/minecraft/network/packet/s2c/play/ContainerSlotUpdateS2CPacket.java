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

    public ContainerSlotUpdateS2CPacket(int syncId, int slot, ItemStack stack) {
        this.syncId = syncId;
        this.slot = slot;
        this.stack = stack.copy();
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onContainerSlotUpdate(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.syncId = buf.readByte();
        this.slot = buf.readShort();
        this.stack = buf.readItemStack();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeShort(this.slot);
        buf.writeItemStack(this.stack);
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

