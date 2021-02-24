/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final BlockPos pos;
    private final int blockEntityType;
    private final CompoundTag tag;

    public BlockEntityUpdateS2CPacket(BlockPos pos, int blockEntityType, CompoundTag tag) {
        this.pos = pos;
        this.blockEntityType = blockEntityType;
        this.tag = tag;
    }

    public BlockEntityUpdateS2CPacket(PacketByteBuf packetByteBuf) {
        this.pos = packetByteBuf.readBlockPos();
        this.blockEntityType = packetByteBuf.readUnsignedByte();
        this.tag = packetByteBuf.readCompoundTag();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeByte((byte)this.blockEntityType);
        buf.writeCompoundTag(this.tag);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBlockEntityUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public int getBlockEntityType() {
        return this.blockEntityType;
    }

    @Environment(value=EnvType.CLIENT)
    public CompoundTag getCompoundTag() {
        return this.tag;
    }
}

