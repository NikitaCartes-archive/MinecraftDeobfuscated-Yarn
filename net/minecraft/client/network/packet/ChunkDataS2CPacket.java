/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

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
import net.minecraft.world.biome.BiomeArray;
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
    private BiomeArray field_20664;
    private byte[] data;
    private List<CompoundTag> blockEntities;
    private boolean isFullChunk;

    public ChunkDataS2CPacket() {
    }

    public ChunkDataS2CPacket(WorldChunk worldChunk, int i) {
        ChunkPos chunkPos = worldChunk.getPos();
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.isFullChunk = i == 65535;
        this.heightmaps = new CompoundTag();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : worldChunk.getHeightmaps()) {
            if (!entry.getKey().shouldSendToClient()) continue;
            this.heightmaps.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
        }
        if (this.isFullChunk) {
            this.field_20664 = worldChunk.getBiomeArray().copy();
        }
        this.data = new byte[this.getDataSize(worldChunk, i)];
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), worldChunk, i);
        this.blockEntities = Lists.newArrayList();
        for (Map.Entry<Object, Object> entry : worldChunk.getBlockEntities().entrySet()) {
            BlockPos blockPos = (BlockPos)entry.getKey();
            BlockEntity blockEntity = (BlockEntity)entry.getValue();
            int j = blockPos.getY() >> 4;
            if (!this.isFullChunk() && (i & 1 << j) == 0) continue;
            CompoundTag compoundTag = blockEntity.toInitialChunkDataTag();
            this.blockEntities.add(compoundTag);
        }
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        int i;
        this.chunkX = packetByteBuf.readInt();
        this.chunkZ = packetByteBuf.readInt();
        this.isFullChunk = packetByteBuf.readBoolean();
        this.verticalStripBitmask = packetByteBuf.readVarInt();
        this.heightmaps = packetByteBuf.readCompoundTag();
        if (this.isFullChunk) {
            this.field_20664 = new BiomeArray(packetByteBuf);
        }
        if ((i = packetByteBuf.readVarInt()) > 0x200000) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.data = new byte[i];
        packetByteBuf.readBytes(this.data);
        int j = packetByteBuf.readVarInt();
        this.blockEntities = Lists.newArrayList();
        for (int k = 0; k < j; ++k) {
            this.blockEntities.add(packetByteBuf.readCompoundTag());
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeInt(this.chunkX);
        packetByteBuf.writeInt(this.chunkZ);
        packetByteBuf.writeBoolean(this.isFullChunk);
        packetByteBuf.writeVarInt(this.verticalStripBitmask);
        packetByteBuf.writeCompoundTag(this.heightmaps);
        if (this.field_20664 != null) {
            this.field_20664.toPacket(packetByteBuf);
        }
        packetByteBuf.writeVarInt(this.data.length);
        packetByteBuf.writeBytes(this.data);
        packetByteBuf.writeVarInt(this.blockEntities.size());
        for (CompoundTag compoundTag : this.blockEntities) {
            packetByteBuf.writeCompoundTag(compoundTag);
        }
    }

    public void method_11528(ClientPlayPacketListener clientPlayPacketListener) {
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

    public int writeData(PacketByteBuf packetByteBuf, WorldChunk worldChunk, int i) {
        int j = 0;
        ChunkSection[] chunkSections = worldChunk.getSectionArray();
        int l = chunkSections.length;
        for (int k = 0; k < l; ++k) {
            ChunkSection chunkSection = chunkSections[k];
            if (chunkSection == WorldChunk.EMPTY_SECTION || this.isFullChunk() && chunkSection.isEmpty() || (i & 1 << k) == 0) continue;
            j |= 1 << k;
            chunkSection.toPacket(packetByteBuf);
        }
        return j;
    }

    protected int getDataSize(WorldChunk worldChunk, int i) {
        int j = 0;
        ChunkSection[] chunkSections = worldChunk.getSectionArray();
        int l = chunkSections.length;
        for (int k = 0; k < l; ++k) {
            ChunkSection chunkSection = chunkSections[k];
            if (chunkSection == WorldChunk.EMPTY_SECTION || this.isFullChunk() && chunkSection.isEmpty() || (i & 1 << k) == 0) continue;
            j += chunkSection.getPacketSize();
        }
        return j;
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
    public BiomeArray method_22422() {
        return this.field_20664 == null ? null : this.field_20664.copy();
    }
}

