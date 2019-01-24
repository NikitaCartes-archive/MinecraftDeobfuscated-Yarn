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
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDeltaUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private ChunkPos chunkPos;
	private ChunkDeltaUpdateClientPacket.ChunkDeltaRecord[] records;

	public ChunkDeltaUpdateClientPacket() {
	}

	public ChunkDeltaUpdateClientPacket(int i, short[] ss, WorldChunk worldChunk) {
		this.chunkPos = worldChunk.getPos();
		this.records = new ChunkDeltaUpdateClientPacket.ChunkDeltaRecord[i];

		for (int j = 0; j < this.records.length; j++) {
			this.records[j] = new ChunkDeltaUpdateClientPacket.ChunkDeltaRecord(ss[j], worldChunk);
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chunkPos = new ChunkPos(packetByteBuf.readInt(), packetByteBuf.readInt());
		this.records = new ChunkDeltaUpdateClientPacket.ChunkDeltaRecord[packetByteBuf.readVarInt()];

		for (int i = 0; i < this.records.length; i++) {
			this.records[i] = new ChunkDeltaUpdateClientPacket.ChunkDeltaRecord(packetByteBuf.readShort(), Block.STATE_IDS.getInt(packetByteBuf.readVarInt()));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.chunkPos.x);
		packetByteBuf.writeInt(this.chunkPos.z);
		packetByteBuf.writeVarInt(this.records.length);

		for (ChunkDeltaUpdateClientPacket.ChunkDeltaRecord chunkDeltaRecord : this.records) {
			packetByteBuf.writeShort(chunkDeltaRecord.getPosShort());
			packetByteBuf.writeVarInt(Block.getRawIdFromState(chunkDeltaRecord.getState()));
		}
	}

	public void method_11392(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public ChunkDeltaUpdateClientPacket.ChunkDeltaRecord[] getRecords() {
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
			return new BlockPos(ChunkDeltaUpdateClientPacket.this.chunkPos.toBlockPos(this.pos >> 12 & 15, this.pos & 255, this.pos >> 8 & 15));
		}

		public short getPosShort() {
			return this.pos;
		}

		public BlockState getState() {
			return this.state;
		}
	}
}
