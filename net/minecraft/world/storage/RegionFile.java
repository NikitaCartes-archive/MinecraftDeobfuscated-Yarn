/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.ChunkStreamVersion;
import net.minecraft.world.storage.SectorMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class RegionFile
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_31418 = 4096;
    @VisibleForTesting
    protected static final int SECTOR_DATA_LIMIT = 1024;
    private static final int field_31419 = 5;
    private static final int field_31420 = 0;
    private static final ByteBuffer ZERO = ByteBuffer.allocateDirect(1);
    private static final String FILE_EXTENSION = ".mcc";
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

    public RegionFile(Path file, Path directory, boolean dsync) throws IOException {
        this(file, directory, ChunkStreamVersion.DEFLATE, dsync);
    }

    public RegionFile(Path file, Path directory, ChunkStreamVersion outputChunkStreamVersion, boolean dsync) throws IOException {
        this.outputChunkStreamVersion = outputChunkStreamVersion;
        if (!Files.isDirectory(directory, new LinkOption[0])) {
            throw new IllegalArgumentException("Expected directory, got " + directory.toAbsolutePath());
        }
        this.directory = directory;
        this.sectorData = this.header.asIntBuffer();
        this.sectorData.limit(1024);
        this.header.position(4096);
        this.saveTimes = this.header.asIntBuffer();
        this.channel = dsync ? FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC) : FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        this.sectors.allocate(0, 2);
        this.header.position(0);
        int i = this.channel.read(this.header, 0L);
        if (i != -1) {
            if (i != 8192) {
                LOGGER.warn("Region file {} has truncated header: {}", (Object)file, (Object)i);
            }
            long l = Files.size(file);
            for (int j = 0; j < 1024; ++j) {
                int k = this.sectorData.get(j);
                if (k == 0) continue;
                int m = RegionFile.getOffset(k);
                int n = RegionFile.getSize(k);
                if (m < 2) {
                    LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", file, j, m);
                    this.sectorData.put(j, 0);
                    continue;
                }
                if (n == 0) {
                    LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", (Object)file, (Object)j);
                    this.sectorData.put(j, 0);
                    continue;
                }
                if ((long)m * 4096L > l) {
                    LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", file, j, m);
                    this.sectorData.put(j, 0);
                    continue;
                }
                this.sectors.allocate(m, n);
            }
        }
    }

    private Path getExternalChunkPath(ChunkPos chunkPos) {
        String string = "c." + chunkPos.x + "." + chunkPos.z + FILE_EXTENSION;
        return this.directory.resolve(string);
    }

    @Nullable
    public synchronized DataInputStream getChunkInputStream(ChunkPos pos) throws IOException {
        int i = this.getSectorData(pos);
        if (i == 0) {
            return null;
        }
        int j = RegionFile.getOffset(i);
        int k = RegionFile.getSize(i);
        int l = k * 4096;
        ByteBuffer byteBuffer = ByteBuffer.allocate(l);
        this.channel.read(byteBuffer, j * 4096);
        byteBuffer.flip();
        if (byteBuffer.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", pos, l, byteBuffer.remaining());
            return null;
        }
        int m = byteBuffer.getInt();
        byte b = byteBuffer.get();
        if (m == 0) {
            LOGGER.warn("Chunk {} is allocated, but stream is missing", (Object)pos);
            return null;
        }
        int n = m - 1;
        if (RegionFile.hasChunkStreamVersionId(b)) {
            if (n != 0) {
                LOGGER.warn("Chunk has both internal and external streams");
            }
            return this.getInputStream(pos, RegionFile.getChunkStreamVersionId(b));
        }
        if (n > byteBuffer.remaining()) {
            LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", pos, n, byteBuffer.remaining());
            return null;
        }
        if (n < 0) {
            LOGGER.error("Declared size {} of chunk {} is negative", (Object)m, (Object)pos);
            return null;
        }
        return this.decompress(pos, b, RegionFile.getInputStream(byteBuffer, n));
    }

    private static int getEpochTimeSeconds() {
        return (int)(Util.getEpochTimeMs() / 1000L);
    }

    private static boolean hasChunkStreamVersionId(byte flags) {
        return (flags & 0x80) != 0;
    }

    private static byte getChunkStreamVersionId(byte flags) {
        return (byte)(flags & 0xFFFFFF7F);
    }

    @Nullable
    private DataInputStream decompress(ChunkPos pos, byte flags, InputStream stream) throws IOException {
        ChunkStreamVersion chunkStreamVersion = ChunkStreamVersion.get(flags);
        if (chunkStreamVersion == null) {
            LOGGER.error("Chunk {} has invalid chunk stream version {}", (Object)pos, (Object)flags);
            return null;
        }
        return new DataInputStream(chunkStreamVersion.wrap(stream));
    }

    @Nullable
    private DataInputStream getInputStream(ChunkPos pos, byte flags) throws IOException {
        Path path = this.getExternalChunkPath(pos);
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            LOGGER.error("External chunk path {} is not file", (Object)path);
            return null;
        }
        return this.decompress(pos, flags, Files.newInputStream(path, new OpenOption[0]));
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
        return sectorData >> 8 & 0xFFFFFF;
    }

    private static int getSectorCount(int byteCount) {
        return (byteCount + 4096 - 1) / 4096;
    }

    public boolean isChunkValid(ChunkPos pos) {
        int i = this.getSectorData(pos);
        if (i == 0) {
            return false;
        }
        int j = RegionFile.getOffset(i);
        int k = RegionFile.getSize(i);
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        try {
            this.channel.read(byteBuffer, j * 4096);
            byteBuffer.flip();
            if (byteBuffer.remaining() != 5) {
                return false;
            }
            int l = byteBuffer.getInt();
            byte b = byteBuffer.get();
            if (RegionFile.hasChunkStreamVersionId(b)) {
                if (!ChunkStreamVersion.exists(RegionFile.getChunkStreamVersionId(b))) {
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
        } catch (IOException iOException) {
            return false;
        }
        return true;
    }

    public DataOutputStream getChunkOutputStream(ChunkPos pos) throws IOException {
        return new DataOutputStream(this.outputChunkStreamVersion.wrap(new ChunkBuffer(pos)));
    }

    public void sync() throws IOException {
        this.channel.force(true);
    }

    public void delete(ChunkPos pos) throws IOException {
        int i = RegionFile.getIndex(pos);
        int j = this.sectorData.get(i);
        if (j == 0) {
            return;
        }
        this.sectorData.put(i, 0);
        this.saveTimes.put(i, RegionFile.getEpochTimeSeconds());
        this.writeHeader();
        Files.deleteIfExists(this.getExternalChunkPath(pos));
        this.sectors.free(RegionFile.getOffset(j), RegionFile.getSize(j));
    }

    protected synchronized void writeChunk(ChunkPos pos, ByteBuffer buf) throws IOException {
        OutputAction outputAction;
        int o;
        int i = RegionFile.getIndex(pos);
        int j = this.sectorData.get(i);
        int k = RegionFile.getOffset(j);
        int l = RegionFile.getSize(j);
        int m = buf.remaining();
        int n = RegionFile.getSectorCount(m);
        if (n >= 256) {
            Path path = this.getExternalChunkPath(pos);
            LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", pos, m, path);
            n = 1;
            o = this.sectors.allocate(n);
            outputAction = this.writeSafely(path, buf);
            ByteBuffer byteBuffer = this.getHeaderBuf();
            this.channel.write(byteBuffer, o * 4096);
        } else {
            o = this.sectors.allocate(n);
            outputAction = () -> Files.deleteIfExists(this.getExternalChunkPath(pos));
            this.channel.write(buf, o * 4096);
        }
        this.sectorData.put(i, this.packSectorData(o, n));
        this.saveTimes.put(i, RegionFile.getEpochTimeSeconds());
        this.writeHeader();
        outputAction.run();
        if (k != 0) {
            this.sectors.free(k, l);
        }
    }

    private ByteBuffer getHeaderBuf() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        byteBuffer.putInt(1);
        byteBuffer.put((byte)(this.outputChunkStreamVersion.getId() | 0x80));
        byteBuffer.flip();
        return byteBuffer;
    }

    private OutputAction writeSafely(Path path, ByteBuffer buf) throws IOException {
        Path path2 = Files.createTempFile(this.directory, "tmp", null, new FileAttribute[0]);
        try (FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE);){
            buf.position(5);
            fileChannel.write(buf);
        }
        return () -> Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeHeader() throws IOException {
        this.header.position(0);
        this.channel.write(this.header, 0L);
    }

    private int getSectorData(ChunkPos pos) {
        return this.sectorData.get(RegionFile.getIndex(pos));
    }

    public boolean hasChunk(ChunkPos pos) {
        return this.getSectorData(pos) != 0;
    }

    private static int getIndex(ChunkPos pos) {
        return pos.getRegionRelativeX() + pos.getRegionRelativeZ() * 32;
    }

    @Override
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
        int j;
        int i = (int)this.channel.size();
        if (i != (j = RegionFile.getSectorCount(i) * 4096)) {
            ByteBuffer byteBuffer = ZERO.duplicate();
            byteBuffer.position(0);
            this.channel.write(byteBuffer, j - 1);
        }
    }

    class ChunkBuffer
    extends ByteArrayOutputStream {
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

        @Override
        public void close() throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.wrap(this.buf, 0, this.count);
            byteBuffer.putInt(0, this.count - 5 + 1);
            RegionFile.this.writeChunk(this.pos, byteBuffer);
        }
    }

    static interface OutputAction {
        public void run() throws IOException;
    }
}

