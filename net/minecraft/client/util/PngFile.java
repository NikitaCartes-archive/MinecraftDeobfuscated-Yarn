/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class PngFile {
    public final int width;
    public final int height;

    public PngFile(String string, InputStream inputStream) throws IOException {
        try (MemoryStack memoryStack = MemoryStack.stackPush();
             Reader reader = PngFile.createReader(inputStream);
             STBIReadCallback sTBIReadCallback = STBIReadCallback.create(reader::read);
             STBISkipCallback sTBISkipCallback = STBISkipCallback.create(reader::skip);
             STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(reader::eof);){
            STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack(memoryStack);
            sTBIIOCallbacks.read(sTBIReadCallback);
            sTBIIOCallbacks.skip(sTBISkipCallback);
            sTBIIOCallbacks.eof(sTBIEOFCallback);
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            if (!STBImage.stbi_info_from_callbacks(sTBIIOCallbacks, 0L, intBuffer, intBuffer2, intBuffer3)) {
                throw new IOException("Could not read info from the PNG file " + string + " " + STBImage.stbi_failure_reason());
            }
            this.width = intBuffer.get(0);
            this.height = intBuffer2.get(0);
        }
    }

    private static Reader createReader(InputStream inputStream) {
        if (inputStream instanceof FileInputStream) {
            return new SeekableChannelReader(((FileInputStream)inputStream).getChannel());
        }
        return new ChannelReader(Channels.newChannel(inputStream));
    }

    @Environment(value=EnvType.CLIENT)
    static class ChannelReader
    extends Reader {
        private final ReadableByteChannel channel;
        private long buffer = MemoryUtil.nmemAlloc(128L);
        private int bufferSize = 128;
        private int bufferPosition;
        private int readPosition;

        private ChannelReader(ReadableByteChannel readableByteChannel) {
            this.channel = readableByteChannel;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void readToBuffer(int i) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(this.buffer, this.bufferSize);
            if (i + this.readPosition > this.bufferSize) {
                this.bufferSize = i + this.readPosition;
                byteBuffer = MemoryUtil.memRealloc(byteBuffer, this.bufferSize);
                this.buffer = MemoryUtil.memAddress(byteBuffer);
            }
            byteBuffer.position(this.bufferPosition);
            while (i + this.readPosition > this.bufferPosition) {
                try {
                    int j = this.channel.read(byteBuffer);
                    if (j != -1) continue;
                    break;
                } finally {
                    this.bufferPosition = byteBuffer.position();
                }
            }
        }

        @Override
        public int read(long l, int i) throws IOException {
            this.readToBuffer(i);
            if (i + this.readPosition > this.bufferPosition) {
                i = this.bufferPosition - this.readPosition;
            }
            MemoryUtil.memCopy(this.buffer + (long)this.readPosition, l, i);
            this.readPosition += i;
            return i;
        }

        @Override
        public void skip(int i) throws IOException {
            if (i > 0) {
                this.readToBuffer(i);
                if (i + this.readPosition > this.bufferPosition) {
                    throw new EOFException("Can't skip past the EOF.");
                }
            }
            if (this.readPosition + i < 0) {
                throw new IOException("Can't seek before the beginning: " + (this.readPosition + i));
            }
            this.readPosition += i;
        }

        @Override
        public void close() throws IOException {
            MemoryUtil.nmemFree(this.buffer);
            this.channel.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class SeekableChannelReader
    extends Reader {
        private final SeekableByteChannel channel;

        private SeekableChannelReader(SeekableByteChannel seekableByteChannel) {
            this.channel = seekableByteChannel;
        }

        @Override
        public int read(long l, int i) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(l, i);
            return this.channel.read(byteBuffer);
        }

        @Override
        public void skip(int i) throws IOException {
            this.channel.position(this.channel.position() + (long)i);
        }

        @Override
        public int eof(long l) {
            return super.eof(l) != 0 && this.channel.isOpen() ? 1 : 0;
        }

        @Override
        public void close() throws IOException {
            this.channel.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class Reader
    implements AutoCloseable {
        protected boolean errored;

        private Reader() {
        }

        int read(long l, long m, int i) {
            try {
                return this.read(m, i);
            } catch (IOException iOException) {
                this.errored = true;
                return 0;
            }
        }

        void skip(long l, int i) {
            try {
                this.skip(i);
            } catch (IOException iOException) {
                this.errored = true;
            }
        }

        int eof(long l) {
            return this.errored ? 1 : 0;
        }

        protected abstract int read(long var1, int var3) throws IOException;

        protected abstract void skip(int var1) throws IOException;

        @Override
        public abstract void close() throws IOException;
    }
}

