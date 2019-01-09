package net.minecraft;

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

public class class_2861 {
	private static final byte[] field_13031 = new byte[4096];
	private final File field_13032;
	private RandomAccessFile field_13025;
	private final int[] field_13029 = new int[1024];
	private final int[] field_13026 = new int[1024];
	private List<Boolean> field_13030;
	private int field_13027;
	private long field_13028;

	public class_2861(File file) {
		this.field_13032 = file;
		this.field_13027 = 0;

		try {
			if (file.exists()) {
				this.field_13028 = file.lastModified();
			}

			this.field_13025 = new RandomAccessFile(file, "rw");
			if (this.field_13025.length() < 4096L) {
				this.field_13025.write(field_13031);
				this.field_13025.write(field_13031);
				this.field_13027 += 8192;
			}

			if ((this.field_13025.length() & 4095L) != 0L) {
				for (int i = 0; (long)i < (this.field_13025.length() & 4095L); i++) {
					this.field_13025.write(0);
				}
			}

			int i = (int)this.field_13025.length() / 4096;
			this.field_13030 = Lists.<Boolean>newArrayListWithCapacity(i);

			for (int j = 0; j < i; j++) {
				this.field_13030.add(true);
			}

			this.field_13030.set(0, false);
			this.field_13030.set(1, false);
			this.field_13025.seek(0L);

			for (int j = 0; j < 1024; j++) {
				int k = this.field_13025.readInt();
				this.field_13029[j] = k;
				if (k != 0 && (k >> 8) + (k & 0xFF) <= this.field_13030.size()) {
					for (int l = 0; l < (k & 0xFF); l++) {
						this.field_13030.set((k >> 8) + l, false);
					}
				}
			}

			for (int jx = 0; jx < 1024; jx++) {
				int k = this.field_13025.readInt();
				this.field_13026[jx] = k;
			}
		} catch (IOException var6) {
			var6.printStackTrace();
		}
	}

	@Nullable
	public synchronized DataInputStream method_12421(int i, int j) {
		if (this.method_12426(i, j)) {
			return null;
		} else {
			try {
				int k = this.method_12419(i, j);
				if (k == 0) {
					return null;
				} else {
					int l = k >> 8;
					int m = k & 0xFF;
					if (l + m > this.field_13030.size()) {
						return null;
					} else {
						this.field_13025.seek((long)(l * 4096));
						int n = this.field_13025.readInt();
						if (n > 4096 * m) {
							return null;
						} else if (n <= 0) {
							return null;
						} else {
							byte b = this.field_13025.readByte();
							if (b == 1) {
								byte[] bs = new byte[n - 1];
								this.field_13025.read(bs);
								return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bs))));
							} else if (b == 2) {
								byte[] bs = new byte[n - 1];
								this.field_13025.read(bs);
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
		if (this.method_12426(i, j)) {
			return false;
		} else {
			int k = this.method_12419(i, j);
			if (k == 0) {
				return false;
			} else {
				int l = k >> 8;
				int m = k & 0xFF;
				if (l + m > this.field_13030.size()) {
					return false;
				} else {
					try {
						this.field_13025.seek((long)(l * 4096));
						int n = this.field_13025.readInt();
						return n > 4096 * m ? false : n > 0;
					} catch (IOException var7) {
						return false;
					}
				}
			}
		}
	}

	@Nullable
	public DataOutputStream method_12425(int i, int j) {
		return this.method_12426(i, j) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new class_2861.class_2862(i, j))));
	}

	protected synchronized void method_12428(int i, int j, byte[] bs, int k) {
		try {
			int l = this.method_12419(i, j);
			int m = l >> 8;
			int n = l & 0xFF;
			int o = (k + 5) / 4096 + 1;
			if (o >= 256) {
				return;
			}

			if (m != 0 && n == o) {
				this.method_12424(m, bs, k);
			} else {
				for (int p = 0; p < n; p++) {
					this.field_13030.set(m + p, true);
				}

				int p = this.field_13030.indexOf(true);
				int q = 0;
				if (p != -1) {
					for (int r = p; r < this.field_13030.size(); r++) {
						if (q != 0) {
							if ((Boolean)this.field_13030.get(r)) {
								q++;
							} else {
								q = 0;
							}
						} else if ((Boolean)this.field_13030.get(r)) {
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
					this.method_12422(i, j, p << 8 | o);

					for (int r = 0; r < o; r++) {
						this.field_13030.set(m + r, false);
					}

					this.method_12424(m, bs, k);
				} else {
					this.field_13025.seek(this.field_13025.length());
					m = this.field_13030.size();

					for (int r = 0; r < o; r++) {
						this.field_13025.write(field_13031);
						this.field_13030.add(false);
					}

					this.field_13027 += 4096 * o;
					this.method_12424(m, bs, k);
					this.method_12422(i, j, m << 8 | o);
				}
			}

			this.method_12427(i, j, (int)(class_156.method_659() / 1000L));
		} catch (IOException var12) {
			var12.printStackTrace();
		}
	}

	private void method_12424(int i, byte[] bs, int j) throws IOException {
		this.field_13025.seek((long)(i * 4096));
		this.field_13025.writeInt(j + 1);
		this.field_13025.writeByte(2);
		this.field_13025.write(bs, 0, j);
	}

	private boolean method_12426(int i, int j) {
		return i < 0 || i >= 32 || j < 0 || j >= 32;
	}

	private int method_12419(int i, int j) {
		return this.field_13029[i + j * 32];
	}

	public boolean method_12423(int i, int j) {
		return this.method_12419(i, j) != 0;
	}

	private void method_12422(int i, int j, int k) throws IOException {
		this.field_13029[i + j * 32] = k;
		this.field_13025.seek((long)((i + j * 32) * 4));
		this.field_13025.writeInt(k);
	}

	private void method_12427(int i, int j, int k) throws IOException {
		this.field_13026[i + j * 32] = k;
		this.field_13025.seek((long)(4096 + (i + j * 32) * 4));
		this.field_13025.writeInt(k);
	}

	public void method_12429() throws IOException {
		if (this.field_13025 != null) {
			this.field_13025.close();
		}
	}

	class class_2862 extends ByteArrayOutputStream {
		private final int field_13034;
		private final int field_13033;

		public class_2862(int i, int j) {
			super(8096);
			this.field_13034 = i;
			this.field_13033 = j;
		}

		public void close() {
			class_2861.this.method_12428(this.field_13034, this.field_13033, this.buf, this.count);
		}
	}
}
