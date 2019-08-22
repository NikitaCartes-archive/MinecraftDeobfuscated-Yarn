package net.minecraft.world.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.class_4485;
import net.minecraft.class_4486;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
	private static final Logger field_20434 = LogManager.getLogger();
	private static final ByteBuffer field_20435 = ByteBuffer.allocateDirect(1);
	private final FileChannel field_20436;
	private final class_4486 field_20437;
	private final ByteBuffer field_20438 = ByteBuffer.allocateDirect(8192);
	private final IntBuffer field_20439;
	private final IntBuffer field_20440;
	private final class_4485 field_20441 = new class_4485();

	public RegionFile(File file) throws IOException {
		this(file.toPath(), class_4486.field_20443);
	}

	public RegionFile(Path path, class_4486 arg) throws IOException {
		this.field_20437 = arg;
		this.field_20439 = this.field_20438.asIntBuffer();
		this.field_20439.limit(1024);
		this.field_20438.position(4096);
		this.field_20440 = this.field_20438.asIntBuffer();
		this.field_20436 = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
		this.field_20441.method_21868(0, 2);
		this.field_20438.position(0);
		int i = this.field_20436.read(this.field_20438, 0L);
		if (i != -1) {
			if (i != 8192) {
				field_20434.warn("Region file {} has truncated header: {}", path, i);
			}

			for (int j = 0; j < 1024; j++) {
				int k = this.field_20439.get(j);
				if (k != 0) {
					int l = method_21878(k);
					int m = method_21871(k);
					this.field_20441.method_21868(l, m);
				}
			}
		}
	}

	@Nullable
	public synchronized DataInputStream method_21873(ChunkPos chunkPos) throws IOException {
		int i = this.getOffset(chunkPos);
		if (i == 0) {
			return null;
		} else {
			int j = method_21878(i);
			int k = method_21871(i);
			int l = k * 4096;
			ByteBuffer byteBuffer = ByteBuffer.allocate(l);
			this.field_20436.read(byteBuffer, (long)(j * 4096));
			byteBuffer.flip();
			if (byteBuffer.remaining() < 5) {
				field_20434.error("Chunk {} header is truncated: expected {} but read {}", chunkPos, l, byteBuffer.remaining());
				return null;
			} else {
				int m = byteBuffer.getInt();
				byte b = byteBuffer.get();
				if (m == 0) {
					field_20434.warn("Chunk {} is allocated, but stream is missing", chunkPos);
					return null;
				} else {
					int n = m - 1;
					if (n > byteBuffer.remaining()) {
						field_20434.error("Chunk {} stream is truncated: expected {} but read {}", chunkPos, n, byteBuffer.remaining());
						return null;
					} else if (n < 0) {
						field_20434.error("Declared size {} of chunk {} is negative", m, chunkPos);
						return null;
					} else {
						class_4486 lv = class_4486.method_21883(b);
						if (lv == null) {
							field_20434.error("Chunk {} has invalid chunk stream version {}", chunkPos, b);
							return null;
						} else {
							return new DataInputStream(new BufferedInputStream(lv.method_21885(method_21876(byteBuffer, n))));
						}
					}
				}
			}
		}
	}

	private static ByteArrayInputStream method_21876(ByteBuffer byteBuffer, int i) {
		return new ByteArrayInputStream(byteBuffer.array(), byteBuffer.position(), i);
	}

	private int method_21872(int i, int j) {
		return i << 8 | j;
	}

	private static int method_21871(int i) {
		return i & 0xFF;
	}

	private static int method_21878(int i) {
		return i >> 8;
	}

	private static int method_21880(int i) {
		return (i + 4096 - 1) / 4096;
	}

	public boolean method_21879(ChunkPos chunkPos) {
		int i = this.getOffset(chunkPos);
		if (i == 0) {
			return false;
		} else {
			int j = method_21878(i);
			int k = method_21871(i);
			ByteBuffer byteBuffer = ByteBuffer.allocate(5);

			try {
				this.field_20436.read(byteBuffer, (long)(j * 4096));
				byteBuffer.flip();
				if (byteBuffer.remaining() != 5) {
					return false;
				} else {
					int l = byteBuffer.getInt();
					int m = byteBuffer.get();
					if (!class_4486.method_21887(m)) {
						return false;
					} else if (l == 0) {
						return false;
					} else {
						int n = l - 1;
						return n >= 0 && n <= 4096 * k;
					}
				}
			} catch (IOException var9) {
				return false;
			}
		}
	}

	public DataOutputStream method_21881(ChunkPos chunkPos) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(this.field_20437.method_21886(new RegionFile.ChunkBuffer(chunkPos))));
	}

	protected synchronized void method_21874(ChunkPos chunkPos, ByteBuffer byteBuffer) throws IOException {
		int i = getPackedRegionRelativePosition(chunkPos);
		int j = this.field_20439.get(i);
		int k = method_21878(j);
		int l = method_21871(j);
		int m = method_21880(byteBuffer.remaining());
		if (m >= 256) {
			throw new RuntimeException(String.format("Too big to save, %d > 1048576", byteBuffer.remaining()));
		} else {
			int n = this.field_20441.method_21867(m);
			this.field_20436.write(byteBuffer, (long)(n * 4096));
			int o = (int)(SystemUtil.getEpochTimeMs() / 1000L);
			this.field_20439.put(i, this.method_21872(n, m));
			this.field_20440.put(i, o);
			this.method_21870();
			if (k != 0) {
				this.field_20441.method_21869(k, l);
			}
		}
	}

	private void method_21870() throws IOException {
		this.field_20438.position(0);
		this.field_20436.write(this.field_20438, 0L);
	}

	private int getOffset(ChunkPos chunkPos) {
		return this.field_20439.get(getPackedRegionRelativePosition(chunkPos));
	}

	public boolean hasChunk(ChunkPos chunkPos) {
		return this.getOffset(chunkPos) != 0;
	}

	private static int getPackedRegionRelativePosition(ChunkPos chunkPos) {
		return chunkPos.getRegionRelativeX() + chunkPos.getRegionRelativeZ() * 32;
	}

	public void close() throws IOException {
		try {
			this.method_21877();
		} finally {
			try {
				this.method_21870();
			} finally {
				this.field_20436.close();
			}
		}
	}

	private void method_21877() throws IOException {
		int i = (int)this.field_20436.size();
		int j = method_21880(i) * 4096;
		if (i != j) {
			ByteBuffer byteBuffer = field_20435.duplicate();
			byteBuffer.position(0);
			this.field_20436.write(byteBuffer, (long)(j - 1));
		}
	}

	class ChunkBuffer extends ByteArrayOutputStream {
		private final ChunkPos pos;

		public ChunkBuffer(ChunkPos chunkPos) {
			super(8096);
			super.write(0);
			super.write(0);
			super.write(0);
			super.write(0);
			super.write(RegionFile.this.field_20437.method_21882());
			this.pos = chunkPos;
		}

		public void close() throws IOException {
			ByteBuffer byteBuffer = ByteBuffer.wrap(this.buf, 0, this.count);
			byteBuffer.putInt(0, this.count - 5 + 1);
			RegionFile.this.method_21874(this.pos, byteBuffer);
		}
	}
}
