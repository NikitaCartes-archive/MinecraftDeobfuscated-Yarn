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

public class class_2861 implements AutoCloseable {
	private static final byte[] field_13031 = new byte[4096];
	private final RandomAccessFile field_13025;
	private final int[] field_13029 = new int[1024];
	private final int[] field_13026 = new int[1024];
	private final List<Boolean> field_13030;

	public class_2861(File file) throws IOException {
		this.field_13025 = new RandomAccessFile(file, "rw");
		if (this.field_13025.length() < 4096L) {
			this.field_13025.write(field_13031);
			this.field_13025.write(field_13031);
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
	}

	@Nullable
	public synchronized DataInputStream method_12421(class_1923 arg) throws IOException {
		int i = this.method_12419(arg);
		if (i == 0) {
			return null;
		} else {
			int j = i >> 8;
			int k = i & 0xFF;
			if (j + k > this.field_13030.size()) {
				return null;
			} else {
				this.field_13025.seek((long)(j * 4096));
				int l = this.field_13025.readInt();
				if (l > 4096 * k) {
					return null;
				} else if (l <= 0) {
					return null;
				} else {
					byte b = this.field_13025.readByte();
					if (b == 1) {
						byte[] bs = new byte[l - 1];
						this.field_13025.read(bs);
						return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bs))));
					} else if (b == 2) {
						byte[] bs = new byte[l - 1];
						this.field_13025.read(bs);
						return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bs))));
					} else {
						return null;
					}
				}
			}
		}
	}

	public boolean method_12420(class_1923 arg) {
		int i = this.method_12419(arg);
		if (i == 0) {
			return false;
		} else {
			int j = i >> 8;
			int k = i & 0xFF;
			if (j + k > this.field_13030.size()) {
				return false;
			} else {
				try {
					this.field_13025.seek((long)(j * 4096));
					int l = this.field_13025.readInt();
					return l > 4096 * k ? false : l > 0;
				} catch (IOException var6) {
					return false;
				}
			}
		}
	}

	public DataOutputStream method_12425(class_1923 arg) {
		return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new class_2861.class_2862(arg))));
	}

	protected synchronized void method_12428(class_1923 arg, byte[] bs, int i) throws IOException {
		int j = this.method_12419(arg);
		int k = j >> 8;
		int l = j & 0xFF;
		int m = (i + 5) / 4096 + 1;
		if (m >= 256) {
			throw new RuntimeException(String.format("Too big to save, %d > 1048576", i));
		} else {
			if (k != 0 && l == m) {
				this.method_12424(k, bs, i);
			} else {
				for (int n = 0; n < l; n++) {
					this.field_13030.set(k + n, true);
				}

				int n = this.field_13030.indexOf(true);
				int o = 0;
				if (n != -1) {
					for (int p = n; p < this.field_13030.size(); p++) {
						if (o != 0) {
							if ((Boolean)this.field_13030.get(p)) {
								o++;
							} else {
								o = 0;
							}
						} else if ((Boolean)this.field_13030.get(p)) {
							n = p;
							o = 1;
						}

						if (o >= m) {
							break;
						}
					}
				}

				if (o >= m) {
					k = n;
					this.method_12422(arg, n << 8 | m);

					for (int p = 0; p < m; p++) {
						this.field_13030.set(k + p, false);
					}

					this.method_12424(k, bs, i);
				} else {
					this.field_13025.seek(this.field_13025.length());
					k = this.field_13030.size();

					for (int p = 0; p < m; p++) {
						this.field_13025.write(field_13031);
						this.field_13030.add(false);
					}

					this.method_12424(k, bs, i);
					this.method_12422(arg, k << 8 | m);
				}
			}

			this.method_12427(arg, (int)(class_156.method_659() / 1000L));
		}
	}

	private void method_12424(int i, byte[] bs, int j) throws IOException {
		this.field_13025.seek((long)(i * 4096));
		this.field_13025.writeInt(j + 1);
		this.field_13025.writeByte(2);
		this.field_13025.write(bs, 0, j);
	}

	private int method_12419(class_1923 arg) {
		return this.field_13029[this.method_17909(arg)];
	}

	public boolean method_12423(class_1923 arg) {
		return this.method_12419(arg) != 0;
	}

	private void method_12422(class_1923 arg, int i) throws IOException {
		int j = this.method_17909(arg);
		this.field_13029[j] = i;
		this.field_13025.seek((long)(j * 4));
		this.field_13025.writeInt(i);
	}

	private int method_17909(class_1923 arg) {
		return arg.method_17887() + arg.method_17888() * 32;
	}

	private void method_12427(class_1923 arg, int i) throws IOException {
		int j = this.method_17909(arg);
		this.field_13026[j] = i;
		this.field_13025.seek((long)(4096 + j * 4));
		this.field_13025.writeInt(i);
	}

	public void close() throws IOException {
		this.field_13025.close();
	}

	class class_2862 extends ByteArrayOutputStream {
		private final class_1923 field_17656;

		public class_2862(class_1923 arg2) {
			super(8096);
			this.field_17656 = arg2;
		}

		public void close() throws IOException {
			class_2861.this.method_12428(this.field_17656, this.buf, this.count);
		}
	}
}
