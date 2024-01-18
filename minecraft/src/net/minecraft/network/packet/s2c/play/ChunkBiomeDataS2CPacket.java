package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public record ChunkBiomeDataS2CPacket(List<ChunkBiomeDataS2CPacket.Serialized> chunkBiomeData) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChunkBiomeDataS2CPacket> CODEC = Packet.createCodec(
		ChunkBiomeDataS2CPacket::write, ChunkBiomeDataS2CPacket::new
	);
	private static final int MAX_SIZE = 2097152;

	private ChunkBiomeDataS2CPacket(PacketByteBuf buf) {
		this(buf.readList(ChunkBiomeDataS2CPacket.Serialized::new));
	}

	public static ChunkBiomeDataS2CPacket create(List<WorldChunk> chunks) {
		return new ChunkBiomeDataS2CPacket(chunks.stream().map(ChunkBiomeDataS2CPacket.Serialized::new).toList());
	}

	private void write(PacketByteBuf buf) {
		buf.writeCollection(this.chunkBiomeData, (bufx, data) -> data.write(bufx));
	}

	@Override
	public PacketType<ChunkBiomeDataS2CPacket> getPacketId() {
		return PlayPackets.CHUNKS_BIOMES;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkBiomeData(this);
	}

	public static record Serialized(ChunkPos pos, byte[] buffer) {
		public Serialized(WorldChunk chunk) {
			this(chunk.getPos(), new byte[getTotalPacketSize(chunk)]);
			write(new PacketByteBuf(this.toWritingBuf()), chunk);
		}

		public Serialized(PacketByteBuf buf) {
			this(buf.readChunkPos(), buf.readByteArray(2097152));
		}

		private static int getTotalPacketSize(WorldChunk chunk) {
			int i = 0;

			for (ChunkSection chunkSection : chunk.getSectionArray()) {
				i += chunkSection.getBiomeContainer().getPacketSize();
			}

			return i;
		}

		public PacketByteBuf toReadingBuf() {
			return new PacketByteBuf(Unpooled.wrappedBuffer(this.buffer));
		}

		private ByteBuf toWritingBuf() {
			ByteBuf byteBuf = Unpooled.wrappedBuffer(this.buffer);
			byteBuf.writerIndex(0);
			return byteBuf;
		}

		public static void write(PacketByteBuf buf, WorldChunk chunk) {
			for (ChunkSection chunkSection : chunk.getSectionArray()) {
				chunkSection.getBiomeContainer().writePacket(buf);
			}
		}

		public void write(PacketByteBuf buf) {
			buf.writeChunkPos(this.pos);
			buf.writeByteArray(this.buffer);
		}
	}
}
