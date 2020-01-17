/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class ChunkDataS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int chunkX;
    private int chunkZ;
    private int verticalStripBitmask;
    private CompoundTag heightmaps;
    @Nullable
    private BiomeArray biomeArray;
    private byte[] data;
    private List<CompoundTag> blockEntities;
    private boolean isFullChunk;

    public ChunkDataS2CPacket() {
    }

    public ChunkDataS2CPacket(WorldChunk chunk, int includedSectionsMask) {
        ChunkPos chunkPos = chunk.getPos();
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.isFullChunk = includedSectionsMask == 65535;
        this.heightmaps = new CompoundTag();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
            if (!entry.getKey().shouldSendToClient()) continue;
            this.heightmaps.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
        }
        if (this.isFullChunk) {
            this.biomeArray = chunk.getBiomeArray().copy();
        }
        this.data = new byte[this.getDataSize(chunk, includedSectionsMask)];
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk, includedSectionsMask);
        this.blockEntities = Lists.newArrayList();
        for (Map.Entry<Object, Object> entry : chunk.getBlockEntities().entrySet()) {
            BlockPos blockPos = (BlockPos)entry.getKey();
            BlockEntity blockEntity = (BlockEntity)entry.getValue();
            int i = blockPos.getY() >> 4;
            if (!this.isFullChunk() && (includedSectionsMask & 1 << i) == 0) continue;
            CompoundTag compoundTag = blockEntity.toInitialChunkDataTag();
            this.blockEntities.add(compoundTag);
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        int i;
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.isFullChunk = buf.readBoolean();
        this.verticalStripBitmask = buf.readVarInt();
        this.heightmaps = buf.readCompoundTag();
        if (this.isFullChunk) {
            this.biomeArray = new BiomeArray(buf);
        }
        if ((i = buf.readVarInt()) > 0x200000) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.data = new byte[i];
        buf.readBytes(this.data);
        int j = buf.readVarInt();
        this.blockEntities = Lists.newArrayList();
        for (int k = 0; k < j; ++k) {
            this.blockEntities.add(buf.readCompoundTag());
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        buf.writeBoolean(this.isFullChunk);
        buf.writeVarInt(this.verticalStripBitmask);
        buf.writeCompoundTag(this.heightmaps);
        if (this.biomeArray != null) {
            this.biomeArray.toPacket(buf);
        }
        buf.writeVarInt(this.data.length);
        buf.writeBytes(this.data);
        buf.writeVarInt(this.blockEntities.size());
        for (CompoundTag compoundTag : this.blockEntities) {
            buf.writeCompoundTag(compoundTag);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onChunkData(this);
    }

    @Environment(value=EnvType.CLIENT)
    public PacketByteBuf getReadBuffer() {
        return new PacketByteBuf(Unpooled.wrappedBuffer(this.data));
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(this.data);
        byteBuf.writerIndex(0);
        return byteBuf;
    }

    public int writeData(PacketByteBuf packetByteBuf, WorldChunk chunk, int includedSectionsMask) {
        int i = 0;
        ChunkSection[] chunkSections = chunk.getSectionArray();
        int k = chunkSections.length;
        for (int j = 0; j < k; ++j) {
            ChunkSection chunkSection = chunkSections[j];
            if (chunkSection == WorldChunk.EMPTY_SECTION || this.isFullChunk() && chunkSection.isEmpty() || (includedSectionsMask & 1 << j) == 0) continue;
            i |= 1 << j;
            chunkSection.toPacket(packetByteBuf);
        }
        return i;
    }

    protected int getDataSize(WorldChunk chunk, int includedSectionsMark) {
        int i = 0;
        ChunkSection[] chunkSections = chunk.getSectionArray();
        int k = chunkSections.length;
        for (int j = 0; j < k; ++j) {
            ChunkSection chunkSection = chunkSections[j];
            if (chunkSection == WorldChunk.EMPTY_SECTION || this.isFullChunk() && chunkSection.isEmpty() || (includedSectionsMark & 1 << j) == 0) continue;
            i += chunkSection.getPacketSize();
        }
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public int getX() {
        return this.chunkX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getZ() {
        return this.chunkZ;
    }

    @Environment(value=EnvType.CLIENT)
    public int getVerticalStripBitmask() {
        return this.verticalStripBitmask;
    }

    public boolean isFullChunk() {
        return this.isFullChunk;
    }

    @Environment(value=EnvType.CLIENT)
    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    @Environment(value=EnvType.CLIENT)
    public List<CompoundTag> getBlockEntityTagList() {
        return this.blockEntities;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public BiomeArray getBiomeArray() {
        return this.biomeArray == null ? null : this.biomeArray.copy();
    }
}

