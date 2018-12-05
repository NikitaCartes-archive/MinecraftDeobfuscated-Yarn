package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.SystemUtil;

public class RegionFile {
	private static final byte[] EMPTY_SECTOR = new byte[4096];
	private final File fileName;
	private RandomAccessFile file;
	private final int[] offsets = new int[1024];
	private final int[] chunkTimestamps = new int[1024];
	private List<Boolean> sectorFree;
	private int sizeDelta;
	private long lastModified;

	public RegionFile(File file) {
		this.fileName = file;
		this.sizeDelta = 0;

		try {
			if (file.exists()) {
				this.lastModified = file.lastModified();
			}

			this.file = new RandomAccessFile(file, "rw");
			if (this.file.length() < 4096L) {
				this.file.write(EMPTY_SECTOR);
				this.file.write(EMPTY_SECTOR);
				this.sizeDelta += 8192;
			}

			if ((this.file.length() & 4095L) != 0L) {
				for (int i = 0; (long)i < (this.file.length() & 4095L); i++) {
					this.file.write(0);
				}
			}

			int i = (int)this.file.length() / 4096;
			this.sectorFree = Lists.<Boolean>newArrayListWithCapacity(i);

			for (int j = 0; j < i; j++) {
				this.sectorFree.add(true);
			}

			this.sectorFree.set(0, false);
			this.sectorFree.set(1, false);
			this.file.seek(0L);

			for (int j = 0; j < 1024; j++) {
				int k = this.file.readInt();
				this.offsets[j] = k;
				if (k != 0 && (k >> 8) + (k & 0xFF) <= this.sectorFree.size()) {
					for (int l = 0; l < (k & 0xFF); l++) {
						this.sectorFree.set((k >> 8) + l, false);
					}
				}
			}

			for (int jx = 0; jx < 1024; jx++) {
				int k = this.file.readInt();
				this.chunkTimestamps[jx] = k;
			}
		} catch (IOException var6) {
			var6.printStackTrace();
		}
	}

	@Nullable
	public synchronized DataInputStream getChunkDataInputStream(int i, int j) {
		if (this.outOfBounds(i, j)) {
			return null;
		} else {
			try {
				int k = this.getOffset(i, j);
				if (k == 0) {
					return null;
				} else {
					int l = k >> 8;
					int m = k & 0xFF;
					if (l + m > this.sectorFree.size()) {
						return null;
					} else {
						this.file.seek((long)(l * 4096));
						int n = this.file.readInt();
						if (n > 4096 * m) {
							return null;
						} else if (n <= 0) {
							return null;
						} else {
							byte b = this.file.readByte();
							if (b == 1) {
								byte[] bs = new byte[n - 1];
								this.file.read(bs);
								return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bs))));
							} else if (b == 2) {
								byte[] bs = new byte[n - 1];
								this.file.read(bs);
								return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bs))));
							} else {
								return null;
							}
						}
					}
				}
			} catch (IOException var9) {
				return null;
			}
		}
	}

	public boolean method_12420(int i, int j) {
		if (this.outOfBounds(i, j)) {
			return false;
		} else {
			int k = this.getOffset(i, j);
			if (k == 0) {
				return false;
			} else {
				int l = k >> 8;
				int m = k & 0xFF;
				if (l + m > this.sectorFree.size()) {
					return false;
				} else {
					try {
						this.file.seek((long)(l * 4096));
						int n = this.file.readInt();
						return n > 4096 * m ? false : n > 0;
					} catch (IOException var7) {
						return false;
					}
				}
			}
		}
	}

	@Nullable
	public DataOutputStream getChunkDataOutputStream(int i, int j) {
		return this.outOfBounds(i, j) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(i, j))));
	}

	protected synchronized void write(int i, int j, byte[] bs, int k) {
		try {
			int l = this.getOffset(i, j);
			int m = l >> 8;
			int n = l & 0xFF;
			int o = (k + 5) / 4096 + 1;
			if (o >= 256) {
				return;
			}

			if (m != 0 && n == o) {
				this.write(m, bs, k);
			} else {
				for (int p = 0; p < n; p++) {
					this.sectorFree.set(m + p, true);
				}

				int p = this.sectorFree.indexOf(true);
				int q = 0;
				if (p != -1) {
					for (int r = p; r < this.sectorFree.size(); r++) {
						if (q != 0) {
							if ((Boolean)this.sectorFree.get(r)) {
								q++;
							} else {
								q = 0;
							}
						} else if ((Boolean)this.sectorFree.get(r)) {
							p = r;
							q = 1;
						}

						if (q >= o) {
							break;
						}
					}
				}

				if (q >= o) {
					m = p;
					this.setOffset(i, j, p << 8 | o);

					for (int r = 0; r < o; r++) {
						this.sectorFree.set(m + r, false);
					}

					this.write(m, bs, k);
				} else {
					this.file.seek(this.file.length());
					m = this.sectorFree.size();

					for (int r = 0; r < o; r++) {
						this.file.write(EMPTY_SECTOR);
						this.sectorFree.add(false);
					}

					this.sizeDelta += 4096 * o;
					this.write(m, bs, k);
					this.setOffset(i, j, m << 8 | o);
				}
			}

			this.setTimestamp(i, j, (int)(SystemUtil.getEpochTimeMili() / 1000L));
		} catch (IOException var12) {
			var12.printStackTrace();
		}
	}

	private void write(int i, byte[] bs, int j) throws IOException {
		this.file.seek((long)(i * 4096));
		this.file.writeInt(j + 1);
		this.file.writeByte(2);
		this.file.write(bs, 0, j);
	}

	private boolean outOfBounds(int i, int j) {
		return i < 0 || i >= 32 || j < 0 || j >= 32;
	}

	private int getOffset(int i, int j) {
		return this.offsets[i + j * 32];
	}

	public boolean hasChunk(int i, int j) {
		return this.getOffset(i, j) != 0;
	}

	private void setOffset(int i, int j, int k) throws IOException {
		this.offsets[i + j * 32] = k;
		this.file.seek((long)((i + j * 32) * 4));
		this.file.writeInt(k);
	}

	private void setTimestamp(int i, int j, int k) throws IOException {
		this.chunkTimestamps[i + j * 32] = k;
		this.file.seek((long)(4096 + (i + j * 32) * 4));
		this.file.writeInt(k);
	}

	public void close() throws IOException {
		if (this.file != null) {
			this.file.close();
		}
	}

	class ChunkBuffer extends ByteArrayOutputStream {
		private final int x;
		private final int z;

		public ChunkBuffer(int i, int j) {
			super(8096);
			this.x = i;
			this.z = j;
		}

		public void close() {
			RegionFile.this.write(this.x, this.z, this.buf, this.count);
		}
	}
}
