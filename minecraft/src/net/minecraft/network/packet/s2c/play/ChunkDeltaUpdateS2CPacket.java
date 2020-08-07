package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
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

public class ChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private ChunkSectionPos sectionPos;
	/**
	 * The packed local positions {@see ChunkSectionPos#getPackedLocalPos} for each entry in {@see #blockStates}.
	 */
	private short[] positions;
	private BlockState[] blockStates;
	private boolean field_26749;

	public ChunkDeltaUpdateS2CPacket() {
	}

	/**
	 * @param sectionPos the position of the given chunk section that will be sent to the client
	 */
	public ChunkDeltaUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet shortSet, ChunkSection section, boolean bl) {
		this.sectionPos = sectionPos;
		this.field_26749 = bl;
		this.allocateBuffers(shortSet.size());
		int i = 0;

		for (short s : shortSet) {
			this.positions[i] = s;
			this.blockStates[i] = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
			i++;
		}
	}

	private void allocateBuffers(int positionCount) {
		this.positions = new short[positionCount];
		this.blockStates = new BlockState[positionCount];
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sectionPos = ChunkSectionPos.from(buf.readLong());
		this.field_26749 = buf.readBoolean();
		int i = buf.readVarInt();
		this.allocateBuffers(i);

		for (int j = 0; j < this.positions.length; j++) {
			long l = buf.readVarLong();
			this.positions[j] = (short)((int)(l & 4095L));
			this.blockStates[j] = Block.STATE_IDS.get((int)(l >>> 12));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.sectionPos.asLong());
		buf.writeBoolean(this.field_26749);
		buf.writeVarInt(this.positions.length);

		for (int i = 0; i < this.positions.length; i++) {
			buf.writeVarLong((long)(Block.getRawIdFromState(this.blockStates[i]) << 12 | this.positions[i]));
		}
	}

	public void method_11392(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	/**
	 * Calls the given consumer for each pair of block position and block state contained in this packet.
	 */
	public void visitUpdates(BiConsumer<BlockPos, BlockState> biConsumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < this.positions.length; i++) {
			short s = this.positions[i];
			mutable.set(this.sectionPos.unpackBlockX(s), this.sectionPos.unpackBlockY(s), this.sectionPos.unpackBlockZ(s));
			biConsumer.accept(mutable, this.blockStates[i]);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_31179() {
		return this.field_26749;
	}
}
