/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDataS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int chunkX;
    private final int chunkZ;
    private final BitSet verticalStripBitmask;
    private final CompoundTag heightmaps;
    private final int[] biomeArray;
    private final byte[] data;
    private final List<CompoundTag> blockEntities;

    public ChunkDataS2CPacket(WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.heightmaps = new CompoundTag();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
            if (!entry.getKey().shouldSendToClient()) continue;
            this.heightmaps.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
        }
        this.biomeArray = chunk.getBiomeArray().toIntArray();
        this.data = new byte[this.getDataSize(chunk)];
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk);
        this.blockEntities = Lists.newArrayList();
        for (Map.Entry<Object, Object> entry : chunk.getBlockEntities().entrySet()) {
            BlockEntity blockEntity = (BlockEntity)entry.getValue();
            CompoundTag compoundTag = blockEntity.toInitialChunkDataNbt();
            this.blockEntities.add(compoundTag);
        }
    }

    public ChunkDataS2CPacket(PacketByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.verticalStripBitmask = buf.readBitSet();
        this.heightmaps = buf.readCompoundTag();
        this.biomeArray = buf.readIntArray(BiomeArray.DEFAULT_LENGTH);
        int i = buf.readVarInt();
        if (i > 0x200000) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.data = new byte[i];
        buf.readBytes(this.data);
        this.blockEntities = buf.readList(PacketByteBuf::readCompoundTag);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        buf.writeBitSet(this.verticalStripBitmask);
        buf.writeCompoundTag(this.heightmaps);
        buf.writeIntArray(this.biomeArray);
        buf.writeVarInt(this.data.length);
        buf.writeBytes(this.data);
        buf.writeCollection(this.blockEntities, PacketByteBuf::writeCompoundTag);
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

    public BitSet writeData(PacketByteBuf buf, WorldChunk chunk) {
        BitSet bitSet = new BitSet();
        ChunkSection[] chunkSections = chunk.getSectionArray();
        int j = chunkSections.length;
        for (int i = 0; i < j; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            if (chunkSection == WorldChunk.EMPTY_SECTION || chunkSection.isEmpty()) continue;
            bitSet.set(i);
            chunkSection.toPacket(buf);
        }
        return bitSet;
    }

    protected int getDataSize(WorldChunk chunk) {
        int i = 0;
        for (ChunkSection chunkSection : chunk.getSectionArray()) {
            if (chunkSection == WorldChunk.EMPTY_SECTION || chunkSection.isEmpty()) continue;
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
    public BitSet getVerticalStripBitmask() {
        return this.verticalStripBitmask;
    }

    @Environment(value=EnvType.CLIENT)
    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    @Environment(value=EnvType.CLIENT)
    public List<CompoundTag> getBlockEntityTagList() {
        return this.blockEntities;
    }

    @Environment(value=EnvType.CLIENT)
    public int[] getBiomeArray() {
        return this.biomeArray;
    }
}

