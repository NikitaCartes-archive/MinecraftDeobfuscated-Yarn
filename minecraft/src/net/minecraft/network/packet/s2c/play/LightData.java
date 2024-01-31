package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightData {
	private static final PacketCodec<ByteBuf, byte[]> CODEC = PacketCodecs.byteArray(2048);
	private final BitSet initedSky;
	private final BitSet initedBlock;
	private final BitSet uninitedSky;
	private final BitSet uninitedBlock;
	private final List<byte[]> skyNibbles;
	private final List<byte[]> blockNibbles;

	public LightData(ChunkPos pos, LightingProvider lightProvider, @Nullable BitSet skyBits, @Nullable BitSet blockBits) {
		this.initedSky = new BitSet();
		this.initedBlock = new BitSet();
		this.uninitedSky = new BitSet();
		this.uninitedBlock = new BitSet();
		this.skyNibbles = Lists.<byte[]>newArrayList();
		this.blockNibbles = Lists.<byte[]>newArrayList();

		for (int i = 0; i < lightProvider.getHeight(); i++) {
			if (skyBits == null || skyBits.get(i)) {
				this.putChunk(pos, lightProvider, LightType.SKY, i, this.initedSky, this.uninitedSky, this.skyNibbles);
			}

			if (blockBits == null || blockBits.get(i)) {
				this.putChunk(pos, lightProvider, LightType.BLOCK, i, this.initedBlock, this.uninitedBlock, this.blockNibbles);
			}
		}
	}

	public LightData(PacketByteBuf buf, int x, int y) {
		this.initedSky = buf.readBitSet();
		this.initedBlock = buf.readBitSet();
		this.uninitedSky = buf.readBitSet();
		this.uninitedBlock = buf.readBitSet();
		this.skyNibbles = buf.readList(CODEC);
		this.blockNibbles = buf.readList(CODEC);
	}

	public void write(PacketByteBuf buf) {
		buf.writeBitSet(this.initedSky);
		buf.writeBitSet(this.initedBlock);
		buf.writeBitSet(this.uninitedSky);
		buf.writeBitSet(this.uninitedBlock);
		buf.writeCollection(this.skyNibbles, CODEC);
		buf.writeCollection(this.blockNibbles, CODEC);
	}

	private void putChunk(ChunkPos pos, LightingProvider lightProvider, LightType type, int y, BitSet initialized, BitSet uninitialized, List<byte[]> nibbles) {
		ChunkNibbleArray chunkNibbleArray = lightProvider.get(type).getLightSection(ChunkSectionPos.from(pos, lightProvider.getBottomY() + y));
		if (chunkNibbleArray != null) {
			if (chunkNibbleArray.isUninitialized()) {
				uninitialized.set(y);
			} else {
				initialized.set(y);
				nibbles.add(chunkNibbleArray.copy().asByteArray());
			}
		}
	}

	public BitSet getInitedSky() {
		return this.initedSky;
	}

	public BitSet getUninitedSky() {
		return this.uninitedSky;
	}

	public List<byte[]> getSkyNibbles() {
		return this.skyNibbles;
	}

	public BitSet getInitedBlock() {
		return this.initedBlock;
	}

	public BitSet getUninitedBlock() {
		return this.uninitedBlock;
	}

	public List<byte[]> getBlockNibbles() {
		return this.blockNibbles;
	}
}
