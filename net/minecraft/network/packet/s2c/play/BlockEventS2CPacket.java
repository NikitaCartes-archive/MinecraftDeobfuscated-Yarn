/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final BlockPos pos;
    private final int type;
    private final int data;
    private final Block block;

    public BlockEventS2CPacket(BlockPos pos, Block block, int type, int data) {
        this.pos = pos;
        this.block = block;
        this.type = type;
        this.data = data;
    }

    public BlockEventS2CPacket(PacketByteBuf packetByteBuf) {
        this.pos = packetByteBuf.readBlockPos();
        this.type = packetByteBuf.readUnsignedByte();
        this.data = packetByteBuf.readUnsignedByte();
        this.block = Registry.BLOCK.get(packetByteBuf.readVarInt());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.type);
        buf.writeByte(this.data);
        buf.writeVarInt(Registry.BLOCK.getRawId(this.block));
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBlockEvent(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public int getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public int getData() {
        return this.data;
    }

    @Environment(value=EnvType.CLIENT)
    public Block getBlock() {
        return this.block;
    }
}

