package net.minecraft.client.network.packet;

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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chunkPos = new ChunkPos(packetByteBuf.readInt(), packetByteBuf.readInt());
		this.records = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[packetByteBuf.readVarInt()];

		for (int i = 0; i < this.records.length; i++) {
			this.records[i] = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord(packetByteBuf.readShort(), Block.STATE_IDS.get(packetByteBuf.readVarInt()));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.chunkPos.x);
		packetByteBuf.writeInt(this.chunkPos.z);
		packetByteBuf.writeVarInt(this.records.length);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : this.records) {
			packetByteBuf.writeShort(chunkDeltaRecord.getPosShort());
			packetByteBuf.writeVarInt(Block.getRawIdFromState(chunkDeltaRecord.getState()));
		}
	}

	public void method_11392(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] getRecords() {
		return this.records;
	}

	public class ChunkDeltaRecord {
		private final short pos;
		private final BlockState state;

		public ChunkDeltaRecord(short s, BlockState blockState) {
			this.pos = s;
			this.state = blockState;
		}

		public ChunkDeltaRecord(short s, WorldChunk worldChunk) {
			this.pos = s;
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
