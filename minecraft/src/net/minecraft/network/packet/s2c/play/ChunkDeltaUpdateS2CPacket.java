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
	private ChunkSectionPos field_26345;
	private short[] field_26346;
	private BlockState[] field_26347;

	public ChunkDeltaUpdateS2CPacket() {
	}

	public ChunkDeltaUpdateS2CPacket(ChunkSectionPos chunkSectionPos, ShortSet shortSet, ChunkSection chunkSection) {
		this.field_26345 = chunkSectionPos;
		this.method_30620(shortSet.size());
		int i = 0;

		for (short s : shortSet) {
			this.field_26346[i] = s;
			this.field_26347[i] = chunkSection.getBlockState(ChunkSectionPos.method_30551(s), ChunkSectionPos.method_30552(s), ChunkSectionPos.method_30553(s));
			i++;
		}
	}

	private void method_30620(int i) {
		this.field_26346 = new short[i];
		this.field_26347 = new BlockState[i];
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_26345 = ChunkSectionPos.from(buf.readLong());
		int i = buf.readVarInt();
		this.method_30620(i);

		for (int j = 0; j < this.field_26346.length; j++) {
			long l = buf.readVarLong();
			this.field_26346[j] = (short)((int)(l & 4095L));
			this.field_26347[j] = Block.STATE_IDS.get((int)(l >>> 12));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.field_26345.asLong());
		buf.writeVarInt(this.field_26346.length);

		for (int i = 0; i < this.field_26346.length; i++) {
			buf.writeVarLong((long)(Block.getRawIdFromState(this.field_26347[i]) << 12 | this.field_26346[i]));
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	public void method_30621(BiConsumer<BlockPos, BlockState> biConsumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < this.field_26346.length; i++) {
			short s = this.field_26346[i];
			mutable.set(this.field_26345.method_30554(s), this.field_26345.method_30555(s), this.field_26345.method_30556(s));
			biConsumer.accept(mutable, this.field_26347[i]);
		}
	}
}
