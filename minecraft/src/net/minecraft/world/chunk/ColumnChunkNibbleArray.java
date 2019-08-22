package net.minecraft.world.chunk;

public class ColumnChunkNibbleArray extends ChunkNibbleArray {
	public ColumnChunkNibbleArray() {
		super(128);
	}

	public ColumnChunkNibbleArray(ChunkNibbleArray chunkNibbleArray, int i) {
		super(128);
		System.arraycopy(chunkNibbleArray.asByteArray(), i * 128, this.byteArray, 0, 128);
	}

	@Override
	protected int getIndex(int i, int j, int k) {
		return k << 4 | i;
	}

	@Override
	public byte[] asByteArray() {
		byte[] bs = new byte[2048];

		for (int i = 0; i < 16; i++) {
			System.arraycopy(this.byteArray, 0, bs, i * 128, 128);
		}

		return bs;
	}
}
