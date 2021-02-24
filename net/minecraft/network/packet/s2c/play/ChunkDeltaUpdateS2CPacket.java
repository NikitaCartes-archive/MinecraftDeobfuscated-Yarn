/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkDeltaUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final ChunkSectionPos sectionPos;
    /**
     * The packed local positions for each entry in {@link #blockStates}.
     * 
     * @see ChunkSectionPos#packLocal(BlockPos)
     */
    private final short[] positions;
    private final BlockState[] blockStates;
    private final boolean field_26749;

    /**
     * @param sectionPos the position of the given chunk section that will be sent to the client
     */
    public ChunkDeltaUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet shortSet, ChunkSection section, boolean bl) {
        this.sectionPos = sectionPos;
        this.field_26749 = bl;
        int i = shortSet.size();
        this.positions = new short[i];
        this.blockStates = new BlockState[i];
        int j = 0;
        ShortIterator shortIterator = shortSet.iterator();
        while (shortIterator.hasNext()) {
            short s;
            this.positions[j] = s = ((Short)shortIterator.next()).shortValue();
            this.blockStates[j] = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
            ++j;
        }
    }

    public ChunkDeltaUpdateS2CPacket(PacketByteBuf packetByteBuf) {
        this.sectionPos = ChunkSectionPos.from(packetByteBuf.readLong());
        this.field_26749 = packetByteBuf.readBoolean();
        int i = packetByteBuf.readVarInt();
        this.positions = new short[i];
        this.blockStates = new BlockState[i];
        for (int j = 0; j < i; ++j) {
            long l = packetByteBuf.readVarLong();
            this.positions[j] = (short)(l & 0xFFFL);
            this.blockStates[j] = Block.STATE_IDS.get((int)(l >>> 12));
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeBoolean(this.field_26749);
        buf.writeVarInt(this.positions.length);
        for (int i = 0; i < this.positions.length; ++i) {
            buf.writeVarLong(Block.getRawIdFromState(this.blockStates[i]) << 12 | this.positions[i]);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onChunkDeltaUpdate(this);
    }

    /**
     * Calls the given consumer for each pair of block position and block state contained in this packet.
     */
    public void visitUpdates(BiConsumer<BlockPos, BlockState> biConsumer) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < this.positions.length; ++i) {
            short s = this.positions[i];
            mutable.set(this.sectionPos.unpackBlockX(s), this.sectionPos.unpackBlockY(s), this.sectionPos.unpackBlockZ(s));
            biConsumer.accept(mutable, this.blockStates[i]);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_31179() {
        return this.field_26749;
    }
}

