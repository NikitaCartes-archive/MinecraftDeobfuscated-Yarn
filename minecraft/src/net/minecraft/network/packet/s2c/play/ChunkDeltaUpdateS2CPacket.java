package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private ChunkPos chunkPos;
	private ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] records;

	public ChunkDeltaUpdateS2CPacket() {
	}

	public ChunkDeltaUpdateS2CPacket(int i, short[] ss, WorldChunk worldChunk) {
		this.chunkPos = worldChunk.getPos();
		this.records = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[i];

		for (int j = 0; j < this.records.length; j++) {
			this.records[j] = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord(ss[j], worldChunk);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkPos = new ChunkPos(buf.readInt(), buf.readInt());
		this.records = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[buf.readVarInt()];

		for (int i = 0; i < this.records.length; i++) {
			this.records[i] = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord(buf.readShort(), Block.STATE_IDS.get(buf.readVarInt()));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.chunkPos.x);
		buf.writeInt(this.chunkPos.z);
		buf.writeVarInt(this.records.length);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : this.records) {
			buf.writeShort(chunkDeltaRecord.getPosShort());
			buf.writeVarInt(Block.getRawIdFromState(chunkDeltaRecord.getState()));
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] getRecords() {
		return this.records;
	}

	public class ChunkDeltaRecord {
		private final short pos;
		private final BlockState state;

		public ChunkDeltaRecord(short pos, BlockState state) {
			this.pos = pos;
			this.state = state;
		}

		public ChunkDeltaRecord(short pos, WorldChunk worldChunk) {
			this.pos = pos;
			this.state = worldChunk.getBlockState(this.getBlockPos());
		}

		public BlockPos getBlockPos() {
			return new BlockPos(ChunkDeltaUpdateS2CPacket.this.chunkPos.toBlockPos(this.pos >> 12 & 15, this.pos & 255, this.pos >> 8 & 15));
		}

		public short getPosShort() {
			return this.pos;
		}

		public BlockState getState() {
			return this.state;
		}
	}
}
