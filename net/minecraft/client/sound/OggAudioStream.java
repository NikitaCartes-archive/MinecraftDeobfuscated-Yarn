/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class OggAudioStream
implements AudioStream {
    private long pointer;
    private final AudioFormat format;
    private final InputStream inputStream;
    private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

    public OggAudioStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.buffer.limit(0);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            while (this.pointer == 0L) {
                if (!this.readHeader()) {
                    throw new IOException("Failed to find Ogg header");
                }
                int i = this.buffer.position();
                this.buffer.position(0);
                this.pointer = STBVorbis.stb_vorbis_open_pushdata(this.buffer, intBuffer, intBuffer2, null);
                this.buffer.position(i);
                int j = intBuffer2.get(0);
                if (j == 1) {
                    this.increaseBufferSize();
                    continue;
                }
                if (j == 0) continue;
                throw new IOException("Failed to read Ogg file " + j);
            }
            this.buffer.position(this.buffer.position() + intBuffer.get(0));
            STBVorbisInfo sTBVorbisInfo = STBVorbisInfo.mallocStack(memoryStack);
            STBVorbis.stb_vorbis_get_info(this.pointer, sTBVorbisInfo);
            this.format = new AudioFormat(sTBVorbisInfo.sample_rate(), 16, sTBVorbisInfo.channels(), true, false);
        }
    }

    private boolean readHeader() throws IOException {
        int i = this.buffer.limit();
        int j = this.buffer.capacity() - i;
        if (j == 0) {
            return true;
        }
        byte[] bs = new byte[j];
        int k = this.inputStream.read(bs);
        if (k == -1) {
            return false;
        }
        int l = this.buffer.position();
        this.buffer.limit(i + k);
        this.buffer.position(i);
        this.buffer.put(bs, 0, k);
        this.buffer.position(l);
        return true;
    }

    private void increaseBufferSize() {
        boolean bl2;
        boolean bl = this.buffer.position() == 0;
        boolean bl3 = bl2 = this.buffer.position() == this.buffer.limit();
        if (bl2 && !bl) {
            this.buffer.position(0);
            this.buffer.limit(0);
        } else {
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(bl ? 2 * this.buffer.capacity() : this.buffer.capacity());
            byteBuffer.put(this.buffer);
            MemoryUtil.memFree(this.buffer);
            byteBuffer.flip();
            this.buffer = byteBuffer;
        }
    }

    private boolean readOggFile(ChannelList channelList) throws IOException {
        if (this.pointer == 0L) {
            return false;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            block24: {
                int k;
                PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                IntBuffer intBuffer = memoryStack.mallocInt(1);
                IntBuffer intBuffer2 = memoryStack.mallocInt(1);
                while (true) {
                    int i = STBVorbis.stb_vorbis_decode_frame_pushdata(this.pointer, this.buffer, intBuffer, pointerBuffer, intBuffer2);
                    this.buffer.position(this.buffer.position() + i);
                    int j = STBVorbis.stb_vorbis_get_error(this.pointer);
                    if (j == 1) {
                        this.increaseBufferSize();
                        if (this.readHeader()) continue;
                        break block24;
                    }
                    if (j != 0) {
                        throw new IOException("Failed to read Ogg file " + j);
                    }
                    k = intBuffer2.get(0);
                    if (k != 0) break;
                }
                int l = intBuffer.get(0);
                PointerBuffer pointerBuffer2 = pointerBuffer.getPointerBuffer(l);
                if (l == 1) {
                    this.readChannels(pointerBuffer2.getFloatBuffer(0, k), channelList);
                    boolean bl = true;
                    return bl;
                }
                if (l == 2) {
                    this.readChannels(pointerBuffer2.getFloatBuffer(0, k), pointerBuffer2.getFloatBuffer(1, k), channelList);
                    boolean bl = true;
                    return bl;
                }
                throw new IllegalStateException("Invalid number of channels: " + l);
            }
            boolean bl = false;
            return bl;
        }
    }

    private void readChannels(FloatBuffer floatBuffer, ChannelList channelList) {
        while (floatBuffer.hasRemaining()) {
            channelList.addChannel(floatBuffer.get());
        }
    }

    private void readChannels(FloatBuffer floatBuffer, FloatBuffer floatBuffer2, ChannelList channelList) {
        while (floatBuffer.hasRemaining() && floatBuffer2.hasRemaining()) {
            channelList.addChannel(floatBuffer.get());
            channelList.addChannel(floatBuffer2.get());
        }
    }

    @Override
    public void close() throws IOException {
        if (this.pointer != 0L) {
            STBVorbis.stb_vorbis_close(this.pointer);
            this.pointer = 0L;
        }
        MemoryUtil.memFree(this.buffer);
        this.inputStream.close();
    }

    @Override
    public AudioFormat getFormat() {
        return this.format;
    }

    @Override
    public ByteBuffer getBuffer(int size) throws IOException {
        ChannelList channelList = new ChannelList(size + 8192);
        while (this.readOggFile(channelList) && channelList.currentBufferSize < size) {
        }
        return channelList.getBuffer();
    }

    public ByteBuffer getBuffer() throws IOException {
        ChannelList channelList = new ChannelList(16384);
        while (this.readOggFile(channelList)) {
        }
        return channelList.getBuffer();
    }

    @Environment(value=EnvType.CLIENT)
    static class ChannelList {
        private final List<ByteBuffer> buffers = Lists.newArrayList();
        private final int size;
        private int currentBufferSize;
        private ByteBuffer buffer;

        public ChannelList(int size) {
            this.size = size + 1 & 0xFFFFFFFE;
            this.init();
        }

        private void init() {
            this.buffer = BufferUtils.createByteBuffer(this.size);
        }

        public void addChannel(float f) {
            if (this.buffer.remaining() == 0) {
                this.buffer.flip();
                this.buffers.add(this.buffer);
                this.init();
            }
            int i = MathHelper.clamp((int)(f * 32767.5f - 0.5f), Short.MIN_VALUE, Short.MAX_VALUE);
            this.buffer.putShort((short)i);
            this.currentBufferSize += 2;
        }

        public ByteBuffer getBuffer() {
            this.buffer.flip();
            if (this.buffers.isEmpty()) {
                return this.buffer;
            }
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(this.currentBufferSize);
            this.buffers.forEach(byteBuffer::put);
            byteBuffer.put(this.buffer);
            byteBuffer.flip();
            return byteBuffer;
        }
    }
}

