/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.logging;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.Util;
import net.minecraft.util.logging.LogReader;
import org.jetbrains.annotations.Nullable;

public class LogWriter<T>
implements Closeable {
    private static final Gson GSON = new Gson();
    private final Codec<T> codec;
    final FileChannel channel;
    private final AtomicInteger refCount = new AtomicInteger(1);

    public LogWriter(Codec<T> codec, FileChannel channel) {
        this.codec = codec;
        this.channel = channel;
    }

    public static <T> LogWriter<T> create(Codec<T> codec, Path path) throws IOException {
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        return new LogWriter<T>(codec, fileChannel);
    }

    public void write(T object) throws IOException, JsonIOException {
        JsonElement jsonElement = Util.getResult(this.codec.encodeStart(JsonOps.INSTANCE, (JsonElement)object), IOException::new);
        this.channel.position(this.channel.size());
        Writer writer = Channels.newWriter((WritableByteChannel)this.channel, StandardCharsets.UTF_8);
        GSON.toJson(jsonElement, (Appendable)writer);
        writer.write(10);
        writer.flush();
    }

    public LogReader<T> getReader() throws IOException {
        if (this.refCount.get() <= 0) {
            throw new IOException("Event log has already been closed");
        }
        this.refCount.incrementAndGet();
        final LogReader<T> logReader = LogReader.create(this.codec, Channels.newReader((ReadableByteChannel)this.channel, StandardCharsets.UTF_8));
        return new LogReader<T>(){
            private volatile long pos;

            @Override
            @Nullable
            public T read() throws IOException {
                try {
                    LogWriter.this.channel.position(this.pos);
                    Object t = logReader.read();
                    return t;
                } finally {
                    this.pos = LogWriter.this.channel.position();
                }
            }

            @Override
            public void close() throws IOException {
                LogWriter.this.closeIfNotReferenced();
            }
        };
    }

    @Override
    public void close() throws IOException {
        this.closeIfNotReferenced();
    }

    void closeIfNotReferenced() throws IOException {
        if (this.refCount.decrementAndGet() <= 0) {
            this.channel.close();
        }
    }
}

