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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ByteBuffer ZERO = ByteBuffer.allocateDirect(1);
	private final FileChannel channel;
	private final ChunkStreamVersion outputChunkStreamVersion;
	private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
	private final IntBuffer sectorData;
	private final IntBuffer saveTimes;
	private final SectorMap sectors = new SectorMap();

	public RegionFile(File file) throws IOException {
		this(file.toPath(), ChunkStreamVersion.DEFLATE);
	}

	public RegionFile(Path path, ChunkStreamVersion chunkStreamVersion) throws IOException {
		this.outputChunkStreamVersion = chunkStreamVersion;
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
					if (n > byteBuffer.remaining()) {
						LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", chunkPos, n, byteBuffer.remaining());
						return null;
					} else if (n < 0) {
						LOGGER.error("Declared size {} of chunk {} is negative", m, chunkPos);
						return null;
					} else {
						ChunkStreamVersion chunkStreamVersion = ChunkStreamVersion.get(b);
						if (chunkStreamVersion == null) {
							LOGGER.error("Chunk {} has invalid chunk stream version {}", chunkPos, b);
							return null;
						} else {
							return new DataInputStream(new BufferedInputStream(chunkStreamVersion.wrap(getInputStream(byteBuffer, n))));
						}
					}
				}
			}
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
					int m = byteBuffer.get();
					if (!ChunkStreamVersion.exists(m)) {
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

	public DataOutputStream getChunkOutputStream(ChunkPos chunkPos) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(this.outputChunkStreamVersion.wrap(new RegionFile.ChunkBuffer(chunkPos))));
	}

	protected synchronized void writeChunk(ChunkPos chunkPos, ByteBuffer byteBuffer) throws IOException {
		int i = getIndex(chunkPos);
		int j = this.sectorData.get(i);
		int k = getOffset(j);
		int l = getSize(j);
		int m = getSectorCount(byteBuffer.remaining());
		if (m >= 256) {
			throw new RuntimeException(String.format("Too big to save, %d > 1048576", byteBuffer.remaining()));
		} else {
			int n = this.sectors.allocate(m);
			this.channel.write(byteBuffer, (long)(n * 4096));
			int o = (int)(SystemUtil.getEpochTimeMs() / 1000L);
			this.sectorData.put(i, this.packSectorData(n, m));
			this.saveTimes.put(i, o);
			this.writeHeader();
			if (k != 0) {
				this.sectors.free(k, l);
			}
		}
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
}
