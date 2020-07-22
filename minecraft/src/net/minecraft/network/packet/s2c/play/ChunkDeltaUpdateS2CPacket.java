package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private ChunkSectionPos sectionPos;
	/**
	 * The packed local positions {@see ChunkSectionPos#getPackedLocalPos} for
	 * each entry in {@see #blockState}.
	 */
	private short[] packedLocalPos;
	private BlockState[] blockState;

	public ChunkDeltaUpdateS2CPacket() {
	}

	/**
	 * @param sectionPos the position of the given chunk section that will be sent to the client
	 * @param updatedLocalPosSet the set of packed local positions within the given chunk section that should be included in the packet
	 */
	public ChunkDeltaUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet updatedLocalPosSet, ChunkSection section) {
		this.sectionPos = sectionPos;
		this.allocateBuffers(updatedLocalPosSet.size());
		int i = 0;

		for (short s : updatedLocalPosSet) {
			this.packedLocalPos[i] = s;
			this.blockState[i] = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
			i++;
		}
	}

	private void allocateBuffers(int posCount) {
		this.packedLocalPos = new short[posCount];
		this.blockState = new BlockState[posCount];
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sectionPos = ChunkSectionPos.from(buf.readLong());
		int i = buf.readVarInt();
		this.allocateBuffers(i);

		for (int j = 0; j < this.packedLocalPos.length; j++) {
			long l = buf.readVarLong();
			this.packedLocalPos[j] = (short)((int)(l & 4095L));
			this.blockState[j] = Block.STATE_IDS.get((int)(l >>> 12));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.sectionPos.asLong());
		buf.writeVarInt(this.packedLocalPos.length);

		for (int i = 0; i < this.packedLocalPos.length; i++) {
			buf.writeVarLong((long)(Block.getRawIdFromState(this.blockState[i]) << 12 | this.packedLocalPos[i]));
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	/**
	 * Calls the given consumer for each pair of block position and block state contained in this packet.
	 */
	public void visitUpdates(BiConsumer<BlockPos, BlockState> biConsumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < this.packedLocalPos.length; i++) {
			short s = this.packedLocalPos[i];
			mutable.set(this.sectionPos.unpackBlockX(s), this.sectionPos.unpackBlockY(s), this.sectionPos.unpackBlockZ(s));
			biConsumer.accept(mutable, this.blockState[i]);
		}
	}
}
