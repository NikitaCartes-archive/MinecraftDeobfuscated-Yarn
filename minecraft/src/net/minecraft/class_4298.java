package net.minecraft;

import net.minecraft.world.chunk.ChunkNibbleArray;

public class class_4298 extends ChunkNibbleArray {
	public static final int field_31707 = 128;

	public class_4298() {
		super(128);
	}

	public class_4298(ChunkNibbleArray chunkNibbleArray, int i) {
		super(128);
		System.arraycopy(chunkNibbleArray.asByteArray(), i * 128, this.bytes, 0, 128);
	}

	@Override
	protected int getIndex(int x, int y, int z) {
		return z << 4 | x;
	}

	@Override
	public byte[] asByteArray() {
		byte[] bs = new byte[2048];

		for (int i = 0; i < 16; i++) {
			System.arraycopy(this.bytes, 0, bs, i * 128, 128);
		}

		return bs;
	}
}
