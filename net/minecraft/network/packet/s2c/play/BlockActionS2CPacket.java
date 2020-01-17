/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockActionS2CPacket
implements Packet<ClientPlayPacketListener> {
    private BlockPos pos;
    private int type;
    private int data;
    private Block block;

    public BlockActionS2CPacket() {
    }

    public BlockActionS2CPacket(BlockPos pos, Block block, int type, int data) {
        this.pos = pos;
        this.block = block;
        this.type = type;
        this.data = data;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.type = buf.readUnsignedByte();
        this.data = buf.readUnsignedByte();
        this.block = Registry.BLOCK.get(buf.readVarInt());
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.type);
        buf.writeByte(this.data);
        buf.writeVarInt(Registry.BLOCK.getRawId(this.block));
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBlockAction(this);
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

