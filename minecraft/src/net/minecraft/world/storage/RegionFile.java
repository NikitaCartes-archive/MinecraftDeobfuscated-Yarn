package net.minecraft.world.storage;

import com.google.common.annotations.VisibleForTesting;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_31418 = 4096;
	@VisibleForTesting
	protected static final int field_31417 = 1024;
	private static final int field_31419 = 5;
	private static final int field_31420 = 0;
	private static final ByteBuffer ZERO = ByteBuffer.allocateDirect(1);
	private static final String field_31421 = ".mcc";
	private static final int field_31422 = 128;
	private static final int field_31423 = 256;
	private static final int field_31424 = 0;
	private final FileChannel channel;
	private final Path directory;
	final ChunkStreamVersion outputChunkStreamVersion;
	private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
	private final IntBuffer sectorData;
	private final IntBuffer saveTimes;
	@VisibleForTesting
	protected final SectorMap sectors = new SectorMap();

	public RegionFile(File file, File directory, boolean dsync) throws IOException {
		this(file.toPath(), directory.toPath(), ChunkStreamVersion.DEFLATE, dsync);
	}

	public RegionFile(Path file, Path directory, ChunkStreamVersion outputChunkStreamVersion, boolean dsync) throws IOException {
		this.outputChunkStreamVersion = outputChunkStreamVersion;
		if (!Files.isDirectory(directory, new LinkOption[0])) {
			throw new IllegalArgumentException("Expected directory, got " + directory.toAbsolutePath());
		} else {
			this.directory = directory;
			this.sectorData = this.header.asIntBuffer();
			this.sectorData.limit(1024);
			this.header.position(4096);
			this.saveTimes = this.header.asIntBuffer();
			if (dsync) {
				this.channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC);
			} else {
				this.channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
			}

			this.sectors.allocate(0, 2);
			this.header.position(0);
			int i = this.channel.read(this.header, 0L);
			if (i != -1) {
				if (i != 8192) {
					LOGGER.warn("Region file {} has truncated header: {}", file, i);
				}

				long l = Files.size(file);

				for (int j = 0; j < 1024; j++) {
					int k = this.sectorData.get(j);
					if (k != 0) {
						int m = getOffset(k);
						int n = getSize(k);
						if (m < 2) {
							LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", file, j, m);
							this.sectorData.put(j, 0);
						} else if (n == 0) {
							LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", file, j);
							this.sectorData.put(j, 0);
						} else if ((long)m * 4096L > l) {
							LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", file, j, m);
							this.sectorData.put(j, 0);
						} else {
							this.sectors.allocate(m, n);
						}
					}
				}
			}
		}
	}

	private Path getExternalChunkPath(ChunkPos chunkPos) {
		String string = "c." + chunkPos.x + "." + chunkPos.z + ".mcc";
		return this.directory.resolve(string);
	}

	@Nullable
	public synchronized DataInputStream getChunkInputStream(ChunkPos pos) throws IOException {
		int i = this.getSectorData(pos);
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
				LOGGER.error("Chunk {} header is truncated: expected {} but read {}", pos, l, byteBuffer.remaining());
				return null;
			} else {
				int m = byteBuffer.getInt();
				byte b = byteBuffer.get();
				if (m == 0) {
					LOGGER.warn("Chunk {} is allocated, but stream is missing", pos);
					return null;
				} else {
					int n = m - 1;
					if (hasChunkStreamVersionId(b)) {
						if (n != 0) {
							LOGGER.warn("Chunk has both internal and external streams");
						}

						return this.method_22408(pos, getChunkStreamVersionId(b));
					} else if (n > byteBuffer.remaining()) {
						LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", pos, n, byteBuffer.remaining());
						return null;
					} else if (n < 0) {
						LOGGER.error("Declared size {} of chunk {} is negative", m, pos);
						return null;
					} else {
						return this.method_22409(pos, b, getInputStream(byteBuffer, n));
					}
				}
			}
		}
	}

	private static int method_31739() {
		return (int)(Util.getEpochTimeMs() / 1000L);
	}

	private static boolean hasChunkStreamVersionId(byte b) {
		return (b & 128) != 0;
	}

	private static byte getChunkStreamVersionId(byte b) {
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
		Path path = this.getExternalChunkPath(chunkPos);
		if (!Files.isRegularFile(path, new LinkOption[0])) {
			LOGGER.error("External chunk path {} is not file", path);
			return null;
		} else {
			return this.method_22409(chunkPos, b, Files.newInputStream(path));
		}
	}

	private static ByteArrayInputStream getInputStream(ByteBuffer buffer, int length) {
		return new ByteArrayInputStream(buffer.array(), buffer.position(), length);
	}

	private int packSectorData(int offset, int size) {
		return offset << 8 | size;
	}

	private static int getSize(int sectorData) {
		return sectorData & 0xFF;
	}

	private static int getOffset(int sectorData) {
		return sectorData >> 8 & 16777215;
	}

	private static int getSectorCount(int byteCount) {
		return (byteCount + 4096 - 1) / 4096;
	}

	public boolean isChunkValid(ChunkPos pos) {
		int i = this.getSectorData(pos);
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
					if (hasChunkStreamVersionId(b)) {
						if (!ChunkStreamVersion.exists(getChunkStreamVersionId(b))) {
							return false;
						}

						if (!Files.isRegularFile(this.getExternalChunkPath(pos), new LinkOption[0])) {
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

	public DataOutputStream getChunkOutputStream(ChunkPos pos) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(this.outputChunkStreamVersion.wrap(new RegionFile.ChunkBuffer(pos))));
	}

	public void sync() throws IOException {
		this.channel.force(true);
	}

	public void method_31740(ChunkPos chunkPos) throws IOException {
		int i = getIndex(chunkPos);
		int j = this.sectorData.get(i);
		if (j != 0) {
			this.sectorData.put(i, 0);
			this.saveTimes.put(i, method_31739());
			this.writeHeader();
			Files.deleteIfExists(this.getExternalChunkPath(chunkPos));
			this.sectors.free(getOffset(j), getSize(j));
		}
	}

	protected synchronized void writeChunk(ChunkPos pos, ByteBuffer byteBuffer) throws IOException {
		int i = getIndex(pos);
		int j = this.sectorData.get(i);
		int k = getOffset(j);
		int l = getSize(j);
		int m = byteBuffer.remaining();
		int n = getSectorCount(m);
		int o;
		RegionFile.OutputAction outputAction;
		if (n >= 256) {
			Path path = this.getExternalChunkPath(pos);
			LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", pos, m, path);
			n = 1;
			o = this.sectors.allocate(n);
			outputAction = this.writeSafely(path, byteBuffer);
			ByteBuffer byteBuffer2 = this.method_22406();
			this.channel.write(byteBuffer2, (long)(o * 4096));
		} else {
			o = this.sectors.allocate(n);
			outputAction = () -> Files.deleteIfExists(this.getExternalChunkPath(pos));
			this.channel.write(byteBuffer, (long)(o * 4096));
		}

		this.sectorData.put(i, this.packSectorData(o, n));
		this.saveTimes.put(i, method_31739());
		this.writeHeader();
		outputAction.run();
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

	private RegionFile.OutputAction writeSafely(Path path, ByteBuffer byteBuffer) throws IOException {
		Path path2 = Files.createTempFile(this.directory, "tmp", null);
		FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

		try {
			byteBuffer.position(5);
			fileChannel.write(byteBuffer);
		} catch (Throwable var8) {
			if (fileChannel != null) {
				try {
					fileChannel.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}
			}

			throw var8;
		}

		if (fileChannel != null) {
			fileChannel.close();
		}

		return () -> Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
	}

	private void writeHeader() throws IOException {
		this.header.position(0);
		this.channel.write(this.header, 0L);
	}

	private int getSectorData(ChunkPos pos) {
		return this.sectorData.get(getIndex(pos));
	}

	public boolean hasChunk(ChunkPos pos) {
		return this.getSectorData(pos) != 0;
	}

	private static int getIndex(ChunkPos pos) {
		return pos.getRegionRelativeX() + pos.getRegionRelativeZ() * 32;
	}

	public void close() throws IOException {
		try {
			this.fillLastSector();
		} finally {
			try {
				this.channel.force(true);
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

		public ChunkBuffer(ChunkPos pos) {
			super(8096);
			super.write(0);
			super.write(0);
			super.write(0);
			super.write(0);
			super.write(RegionFile.this.outputChunkStreamVersion.getId());
			this.pos = pos;
		}

		public void close() throws IOException {
			ByteBuffer byteBuffer = ByteBuffer.wrap(this.buf, 0, this.count);
			byteBuffer.putInt(0, this.count - 5 + 1);
			RegionFile.this.writeChunk(this.pos, byteBuffer);
		}
	}

	interface OutputAction {
		void run() throws IOException;
	}
}
