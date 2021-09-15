package net.minecraft;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class class_6606 {
	private final BitSet field_34873;
	private final BitSet field_34874;
	private final BitSet field_34875;
	private final BitSet field_34876;
	private final List<byte[]> field_34877;
	private final List<byte[]> field_34878;
	private final boolean field_34879;

	public class_6606(ChunkPos chunkPos, LightingProvider lightingProvider, @Nullable BitSet bitSet, @Nullable BitSet bitSet2, boolean bl) {
		this.field_34879 = bl;
		this.field_34873 = new BitSet();
		this.field_34874 = new BitSet();
		this.field_34875 = new BitSet();
		this.field_34876 = new BitSet();
		this.field_34877 = Lists.<byte[]>newArrayList();
		this.field_34878 = Lists.<byte[]>newArrayList();

		for (int i = 0; i < lightingProvider.getHeight(); i++) {
			if (bitSet == null || bitSet.get(i)) {
				this.method_38602(chunkPos, lightingProvider, LightType.SKY, i, this.field_34873, this.field_34875, this.field_34877);
			}

			if (bitSet2 == null || bitSet2.get(i)) {
				this.method_38602(chunkPos, lightingProvider, LightType.BLOCK, i, this.field_34874, this.field_34876, this.field_34878);
			}
		}
	}

	public class_6606(PacketByteBuf packetByteBuf, int i, int j) {
		this.field_34879 = packetByteBuf.readBoolean();
		this.field_34873 = packetByteBuf.readBitSet();
		this.field_34874 = packetByteBuf.readBitSet();
		this.field_34875 = packetByteBuf.readBitSet();
		this.field_34876 = packetByteBuf.readBitSet();
		this.field_34877 = packetByteBuf.readList(packetByteBufx -> packetByteBufx.readByteArray(2048));
		this.field_34878 = packetByteBuf.readList(packetByteBufx -> packetByteBufx.readByteArray(2048));
	}

	public void method_38603(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeBoolean(this.field_34879);
		packetByteBuf.writeBitSet(this.field_34873);
		packetByteBuf.writeBitSet(this.field_34874);
		packetByteBuf.writeBitSet(this.field_34875);
		packetByteBuf.writeBitSet(this.field_34876);
		packetByteBuf.writeCollection(this.field_34877, PacketByteBuf::writeByteArray);
		packetByteBuf.writeCollection(this.field_34878, PacketByteBuf::writeByteArray);
	}

	private void method_38602(ChunkPos chunkPos, LightingProvider lightingProvider, LightType lightType, int i, BitSet bitSet, BitSet bitSet2, List<byte[]> list) {
		ChunkNibbleArray chunkNibbleArray = lightingProvider.get(lightType).getLightSection(ChunkSectionPos.from(chunkPos, lightingProvider.getBottomY() + i));
		if (chunkNibbleArray != null) {
			if (chunkNibbleArray.isUninitialized()) {
				bitSet2.set(i);
			} else {
				bitSet.set(i);
				list.add((byte[])chunkNibbleArray.asByteArray().clone());
			}
		}
	}

	public BitSet method_38601() {
		return this.field_34873;
	}

	public BitSet method_38604() {
		return this.field_34875;
	}

	public List<byte[]> method_38606() {
		return this.field_34877;
	}

	public BitSet method_38608() {
		return this.field_34874;
	}

	public BitSet method_38609() {
		return this.field_34876;
	}

	public List<byte[]> method_38610() {
		return this.field_34878;
	}

	public boolean method_38611() {
		return this.field_34879;
	}
}
