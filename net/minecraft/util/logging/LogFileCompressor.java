/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.logging;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LogFileCompressor {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int COMPRESSION_BUFFER_SIZE = 4096;
    private static final String GZ_EXTENSION = ".gz";
    private final Path directory;
    private final String extension;

    private LogFileCompressor(Path directory, String extension) {
        this.directory = directory;
        this.extension = extension;
    }

    public static LogFileCompressor create(Path directory, String extension) throws IOException {
        Files.createDirectories(directory, new FileAttribute[0]);
        return new LogFileCompressor(directory, extension);
    }

    public LogFileIterable getAll() throws IOException {
        try (Stream<Path> stream = Files.list(this.directory);){
            LogFileIterable logFileIterable = new LogFileIterable(stream.filter(path -> Files.isRegularFile(path, new LinkOption[0])).map(this::get).filter(Objects::nonNull).toList());
            return logFileIterable;
        }
    }

    @Nullable
    private LogFile get(Path path) {
        String string = path.getFileName().toString();
        int i = string.indexOf(46);
        if (i == -1) {
            return null;
        }
        LogId logId = LogId.fromFileName(string.substring(0, i));
        if (logId != null) {
            String string2 = string.substring(i);
            if (string2.equals(this.extension)) {
                return new Uncompressed(path, logId);
            }
            if (string2.equals(this.extension + GZ_EXTENSION)) {
                return new Compressed(path, logId);
            }
        }
        return null;
    }

    static void compress(Path from, Path to) throws IOException {
        if (Files.exists(to, new LinkOption[0])) {
            throw new IOException("Compressed target file already exists: " + to);
        }
        try (FileChannel fileChannel = FileChannel.open(from, StandardOpenOption.WRITE, StandardOpenOption.READ);){
            FileLock fileLock = fileChannel.tryLock();
            if (fileLock == null) {
                throw new IOException("Raw log file is already locked, cannot compress: " + from);
            }
            LogFileCompressor.compress(fileChannel, to);
            fileChannel.truncate(0L);
        }
        Files.delete(from);
    }

    private static void compress(ReadableByteChannel source, Path outputPath) throws IOException {
        try (GZIPOutputStream outputStream = new GZIPOutputStream(Files.newOutputStream(outputPath, new OpenOption[0]));){
            byte[] bs = new byte[4096];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bs);
            while (source.read(byteBuffer) >= 0) {
                byteBuffer.flip();
                ((OutputStream)outputStream).write(bs, 0, byteBuffer.limit());
                byteBuffer.clear();
            }
        }
    }

    public Uncompressed createLogFile(LocalDate date) throws IOException {
        LogId logId;
        int i = 1;
        Set<LogId> set = this.getAll().toIdSet();
        while (set.contains(logId = new LogId(date, i++))) {
        }
        Uncompressed uncompressed = new Uncompressed(this.directory.resolve(logId.getFileName(this.extension)), logId);
        Files.createFile(uncompressed.path(), new FileAttribute[0]);
        return uncompressed;
    }

    public static class LogFileIterable
    implements Iterable<LogFile> {
        private final List<LogFile> logs;

        LogFileIterable(List<LogFile> logs) {
            this.logs = new ArrayList<LogFile>(logs);
        }

        public LogFileIterable removeExpired(LocalDate currentDate, int retentionDays) {
            this.logs.removeIf(log -> {
                LogId logId = log.id();
                LocalDate localDate2 = logId.date().plusDays(retentionDays);
                if (!currentDate.isBefore(localDate2)) {
                    try {
                        Files.delete(log.path());
                        return true;
                    } catch (IOException iOException) {
                        LOGGER.warn("Failed to delete expired event log file: {}", (Object)log.path(), (Object)iOException);
                    }
                }
                return false;
            });
            return this;
        }

        public LogFileIterable compressAll() {
            ListIterator<LogFile> listIterator = this.logs.listIterator();
            while (listIterator.hasNext()) {
                LogFile logFile = listIterator.next();
                try {
                    listIterator.set(logFile.compress());
                } catch (IOException iOException) {
                    LOGGER.warn("Failed to compress event log file: {}", (Object)logFile.path(), (Object)iOException);
                }
            }
            return this;
        }

        @Override
        public Iterator<LogFile> iterator() {
            return this.logs.iterator();
        }

        public Stream<LogFile> stream() {
            return this.logs.stream();
        }

        public Set<LogId> toIdSet() {
            return this.logs.stream().map(LogFile::id).collect(Collectors.toSet());
        }
    }

    public record LogId(LocalDate date, int index) {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

        @Nullable
        public static LogId fromFileName(String fileName) {
            int i = fileName.indexOf("-");
            if (i == -1) {
                return null;
            }
            String string = fileName.substring(0, i);
            String string2 = fileName.substring(i + 1);
            try {
                return new LogId(LocalDate.parse(string, DATE_TIME_FORMATTER), Integer.parseInt(string2));
            } catch (NumberFormatException | DateTimeParseException runtimeException) {
                return null;
            }
        }

        @Override
        public String toString() {
            return DATE_TIME_FORMATTER.format(this.date) + "-" + this.index;
        }

        public String getFileName(String extension) {
            return this + extension;
        }
    }

    public record Uncompressed(Path path, LogId id) implements LogFile
    {
        public FileChannel open() throws IOException {
            return FileChannel.open(this.path, StandardOpenOption.WRITE, StandardOpenOption.READ);
        }

        @Override
        @Nullable
        public Reader getReader() throws IOException {
            return Files.exists(this.path, new LinkOption[0]) ? Files.newBufferedReader(this.path) : null;
        }

        @Override
        public Compressed compress() throws IOException {
            Path path = this.path.resolveSibling(this.path.getFileName().toString() + LogFileCompressor.GZ_EXTENSION);
            LogFileCompressor.compress(this.path, path);
            return new Compressed(path, this.id);
        }
    }

    public record Compressed(Path path, LogId id) implements LogFile
    {
        @Override
        @Nullable
        public Reader getReader() throws IOException {
            if (!Files.exists(this.path, new LinkOption[0])) {
                return null;
            }
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(this.path, new OpenOption[0]))));
        }

        @Override
        public Compressed compress() {
            return this;
        }
    }

    public static interface LogFile {
        public Path path();

        public LogId id();

        @Nullable
        public Reader getReader() throws IOException;

        public Compressed compress() throws IOException;
    }
}

