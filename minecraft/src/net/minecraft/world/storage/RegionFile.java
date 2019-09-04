package net.minecraft.world.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ByteBuffer ZERO = ByteBuffer.allocateDirect(1);
	private final FileChannel channel;
	private final Path field_20657;
	private final ChunkStreamVersion outputChunkStreamVersion;
	private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
	private final IntBuffer sectorData;
	private final IntBuffer saveTimes;
	private final SectorMap sectors = new SectorMap();

	public RegionFile(File file, File file2) throws IOException {
		this(file.toPath(), file2.toPath(), ChunkStreamVersion.DEFLATE);
	}

	public RegionFile(Path path, Path path2, ChunkStreamVersion chunkStreamVersion) throws IOException {
		this.outputChunkStreamVersion = chunkStreamVersion;
		if (!Files.isDirectory(path2, new LinkOption[0])) {
			throw new IllegalArgumentException("Expected directory, got " + path2.toAbsolutePath());
		} else {
			this.field_20657 = path2;
			this.sectorData = this.header.asIntBuffer();
			this.sectorData.limit(1024);
			this.header.position(4096);
			this.saveTimes = this.header.asIntBuffer();
			this.channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
			this.sectors.allocate(0, 2);
			this.header.position(0);
			int i = this.channel.read(this.header, 0L);
			if (i != -1) {
				if (i != 8192) {
					LOGGER.warn("Region file {} has truncated header: {}", path, i);
				}

				for (int j = 0; j < 1024; j++) {
					int k = this.sectorData.get(j);
					if (k != 0) {
						int l = getOffset(k);
						int m = getSize(k);
						this.sectors.allocate(l, m);
					}
				}
			}
		}
	}

	private Path method_22413(ChunkPos chunkPos) {
		String string = "c." + chunkPos.x + "." + chunkPos.z + ".mcc";
		return this.field_20657.resolve(string);
	}

	@Nullable
	public synchronized DataInputStream getChunkInputStream(ChunkPos chunkPos) throws IOException {
		int i = this.getSectorData(chunkPos);
		if (i == 0) {
			return null;
		} else {
			int j = getOffset(i);
			int k = getSize(i);
			int l = k * 4096;
			ByteBuffer byteBuffer = ByteBuffer.allocate(l);
			this.channel.read(byteBuffer, (long)(j * 4096));
			byteBuffer.flip();
			if (byteBuffer.remaining() < 5) {
				LOGGER.error("Chunk {} header is truncated: expected {} but read {}", chunkPos, l, byteBuffer.remaining());
				return null;
			} else {
				int m = byteBuffer.getInt();
				byte b = byteBuffer.get();
				if (m == 0) {
					LOGGER.warn("Chunk {} is allocated, but stream is missing", chunkPos);
					return null;
				} else {
					int n = m - 1;
					if (method_22407(b)) {
						if (n != 0) {
							LOGGER.warn("Chunk has both internal and external streams");
						}

						return this.method_22408(chunkPos, method_22412(b));
					} else if (n > byteBuffer.remaining()) {
						LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", chunkPos, n, byteBuffer.remaining());
						return null;
					} else if (n < 0) {
						LOGGER.error("Declared size {} of chunk {} is negative", m, chunkPos);
						return null;
					} else {
						return this.method_22409(chunkPos, b, getInputStream(byteBuffer, n));
					}
				}
			}
		}
	}

	private static boolean method_22407(byte b) {
		return (b & 128) != 0;
	}

	private static byte method_22412(byte b) {
		return (byte)(b & -129);
	}

	@Nullable
	private DataInputStream method_22409(ChunkPos chunkPos, byte b, InputStream inputStream) throws IOException {
		ChunkStreamVersion chunkStreamVersion = ChunkStreamVersion.get(b);
		if (chunkStreamVersion == null) {
			LOGGER.error("Chunk {} has invalid chunk stream version {}", chunkPos, b);
			return null;
		} else {
			return new DataInputStream(new BufferedInputStream(chunkStreamVersion.wrap(inputStream)));
		}
	}

	@Nullable
	private DataInputStream method_22408(ChunkPos chunkPos, byte b) throws IOException {
		Path path = this.method_22413(chunkPos);
		if (!Files.isRegularFile(path, new LinkOption[0])) {
			LOGGER.error("External chunk path {} is not file", path);
			return null;
		} else {
			return this.method_22409(chunkPos, b, Files.newInputStream(path));
		}
	}

	private static ByteArrayInputStream getInputStream(ByteBuffer byteBuffer, int i) {
		return new ByteArrayInputStream(byteBuffer.array(), byteBuffer.position(), i);
	}

	private int packSectorData(int i, int j) {
		return i << 8 | j;
	}

	private static int getSize(int i) {
		return i & 0xFF;
	}

	private static int getOffset(int i) {
		return i >> 8;
	}

	private static int getSectorCount(int i) {
		return (i + 4096 - 1) / 4096;
	}

	public boolean isChunkValid(ChunkPos chunkPos) {
		int i = this.getSectorData(chunkPos);
		if (i == 0) {
			return false;
		} else {
			int j = getOffset(i);
			int k = getSize(i);
			ByteBuffer byteBuffer = ByteBuffer.allocate(5);

			try {
				this.channel.read(byteBuffer, (long)(j * 4096));
				byteBuffer.flip();
				if (byteBuffer.remaining() != 5) {
					return false;
				} else {
					int l = byteBuffer.getInt();
					byte b = byteBuffer.get();
					if (method_22407(b)) {
						if (!ChunkStreamVersion.exists(method_22412(b))) {
							return false;
						}

						if (!Files.isRegularFile(this.method_22413(chunkPos), new LinkOption[0])) {
							return false;
						}
					} else {
						if (!ChunkStreamVersion.exists(b)) {
							return false;
						}

						if (l == 0) {
							return false;
						}

						int m = l - 1;
						if (m < 0 || m > 4096 * k) {
							return false;
						}
					}

					return true;
				}
			} catch (IOException var9) {
				return false;
			}
		}
	}

	public DataOutputStream getChunkOutputStream(ChunkPos chunkPos) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(this.outputChunkStreamVersion.wrap(new RegionFile.ChunkBuffer(chunkPos))));
	}

	protected synchronized void writeChunk(ChunkPos chunkPos, ByteBuffer byteBuffer) throws IOException {
		int i = getIndex(chunkPos);
		int j = this.sectorData.get(i);
		int k = getOffset(j);
		int l = getSize(j);
		int m = byteBuffer.remaining();
		int n = getSectorCount(m);
		int o;
		RegionFile.class_4549 lv;
		if (n >= 256) {
			Path path = this.method_22413(chunkPos);
			LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", chunkPos, m, path);
			n = 1;
			o = this.sectors.allocate(n);
			lv = this.method_22410(path, byteBuffer);
			ByteBuffer byteBuffer2 = this.method_22406();
			this.channel.write(byteBuffer2, (long)(o * 4096));
		} else {
			o = this.sectors.allocate(n);
			lv = () -> Files.deleteIfExists(this.method_22413(chunkPos));
			this.channel.write(byteBuffer, (long)(o * 4096));
		}

		int p = (int)(SystemUtil.getEpochTimeMs() / 1000L);
		this.sectorData.put(i, this.packSectorData(o, n));
		this.saveTimes.put(i, p);
		this.writeHeader();
		lv.run();
		if (k != 0) {
			this.sectors.free(k, l);
		}
	}

	private ByteBuffer method_22406() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(5);
		byteBuffer.putInt(1);
		byteBuffer.put((byte)(this.outputChunkStreamVersion.getId() | 128));
		byteBuffer.flip();
		return byteBuffer;
	}

	private RegionFile.class_4549 method_22410(Path path, ByteBuffer byteBuffer) throws IOException {
		Path path2 = Files.createTempFile(this.field_20657, "tmp", null);
		FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		Throwable var5 = null;

		try {
			byteBuffer.position(5);
			fileChannel.write(byteBuffer);
		} catch (Throwable var14) {
			var5 = var14;
			throw var14;
		} finally {
			if (fileChannel != null) {
				if (var5 != null) {
					try {
						fileChannel.close();
					} catch (Throwable var13) {
						var5.addSuppressed(var13);
					}
				} else {
					fileChannel.close();
				}
			}
		}

		return () -> Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
	}

	private void writeHeader() throws IOException {
		this.header.position(0);
		this.channel.write(this.header, 0L);
	}

	private int getSectorData(ChunkPos chunkPos) {
		return this.sectorData.get(getIndex(chunkPos));
	}

	public boolean hasChunk(ChunkPos chunkPos) {
		return this.getSectorData(chunkPos) != 0;
	}

	private static int getIndex(ChunkPos chunkPos) {
		return chunkPos.getRegionRelativeX() + chunkPos.getRegionRelativeZ() * 32;
	}

	public void close() throws IOException {
		try {
			this.fillLastSector();
		} finally {
			try {
				this.writeHeader();
			} finally {
				this.channel.close();
			}
		}
	}

	private void fillLastSector() throws IOException {
		int i = (int)this.channel.size();
		int j = getSectorCount(i) * 4096;
		if (i != j) {
			ByteBuffer byteBuffer = ZERO.duplicate();
			byteBuffer.position(0);
			this.channel.write(byteBuffer, (long)(j - 1));
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
			super.write(RegionFile.this.outputChunkStreamVersion.getId());
			this.pos = chunkPos;
		}

		public void close() throws IOException {
			ByteBuffer byteBuffer = ByteBuffer.wrap(this.buf, 0, this.count);
			byteBuffer.putInt(0, this.count - 5 + 1);
			RegionFile.this.writeChunk(this.pos, byteBuffer);
		}
	}

	interface class_4549 {
		void run() throws IOException;
	}
}
