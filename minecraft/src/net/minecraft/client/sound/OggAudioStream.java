package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class OggAudioStream implements AudioStream {
	private long pointer;
	private final AudioFormat format;
	private final InputStream inputStream;
	private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

	public OggAudioStream(InputStream inputStream) throws IOException {
		this.inputStream = inputStream;
		this.buffer.limit(0);

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
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
				} else if (j != 0) {
					throw new IOException("Failed to read Ogg file " + j);
				}
			}

			this.buffer.position(this.buffer.position() + intBuffer.get(0));
			STBVorbisInfo sTBVorbisInfo = STBVorbisInfo.mallocStack(memoryStack);
			STBVorbis.stb_vorbis_get_info(this.pointer, sTBVorbisInfo);
			this.format = new AudioFormat((float)sTBVorbisInfo.sample_rate(), 16, sTBVorbisInfo.channels(), true, false);
		}
	}

	private boolean readHeader() throws IOException {
		int i = this.buffer.limit();
		int j = this.buffer.capacity() - i;
		if (j == 0) {
			return true;
		} else {
			byte[] bs = new byte[j];
			int k = this.inputStream.read(bs);
			if (k == -1) {
				return false;
			} else {
				int l = this.buffer.position();
				this.buffer.limit(i + k);
				this.buffer.position(i);
				this.buffer.put(bs, 0, k);
				this.buffer.position(l);
				return true;
			}
		}
	}

	private void increaseBufferSize() {
		boolean bl = this.buffer.position() == 0;
		boolean bl2 = this.buffer.position() == this.buffer.limit();
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

	private boolean readOggFile(OggAudioStream.ChannelList channelList) throws IOException {
		if (this.pointer == 0L) {
			return false;
		} else {
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);

				while (true) {
					int i = STBVorbis.stb_vorbis_decode_frame_pushdata(this.pointer, this.buffer, intBuffer, pointerBuffer, intBuffer2);
					this.buffer.position(this.buffer.position() + i);
					int j = STBVorbis.stb_vorbis_get_error(this.pointer);
					if (j == 1) {
						this.increaseBufferSize();
						if (!this.readHeader()) {
							return false;
						}
					} else {
						if (j != 0) {
							throw new IOException("Failed to read Ogg file " + j);
						}

						int k = intBuffer2.get(0);
						if (k != 0) {
							int l = intBuffer.get(0);
							PointerBuffer pointerBuffer2 = pointerBuffer.getPointerBuffer(l);
							if (l == 1) {
								this.readChannels(pointerBuffer2.getFloatBuffer(0, k), channelList);
								return true;
							}

							if (l == 2) {
								this.readChannels(pointerBuffer2.getFloatBuffer(0, k), pointerBuffer2.getFloatBuffer(1, k), channelList);
								return true;
							}

							throw new IllegalStateException("Invalid number of channels: " + l);
						}
					}
				}
			}
		}
	}

	private void readChannels(FloatBuffer floatBuffer, OggAudioStream.ChannelList channelList) {
		while (floatBuffer.hasRemaining()) {
			channelList.addChannel(floatBuffer.get());
		}
	}

	private void readChannels(FloatBuffer floatBuffer, FloatBuffer floatBuffer2, OggAudioStream.ChannelList channelList) {
		while (floatBuffer.hasRemaining() && floatBuffer2.hasRemaining()) {
			channelList.addChannel(floatBuffer.get());
			channelList.addChannel(floatBuffer2.get());
		}
	}

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

	@Nullable
	@Override
	public ByteBuffer method_19720(int i) throws IOException {
		OggAudioStream.ChannelList channelList = new OggAudioStream.ChannelList(i + 8192);

		while (this.readOggFile(channelList) && channelList.field_18913 < i) {
		}

		return channelList.getBuffer();
	}

	@Override
	public ByteBuffer getBuffer() throws IOException {
		OggAudioStream.ChannelList channelList = new OggAudioStream.ChannelList(16384);

		while (this.readOggFile(channelList)) {
		}

		return channelList.getBuffer();
	}

	@Environment(EnvType.CLIENT)
	static class ChannelList {
		private final List<ByteBuffer> buffers = Lists.<ByteBuffer>newArrayList();
		private final int size;
		private int field_18913;
		private ByteBuffer buffer;

		public ChannelList(int i) {
			this.size = i + 1 & -2;
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

			int i = MathHelper.clamp((int)(f * 32767.5F - 0.5F), -32768, 32767);
			this.buffer.putShort((short)i);
			this.field_18913 += 2;
		}

		public ByteBuffer getBuffer() {
			this.buffer.flip();
			if (this.buffers.isEmpty()) {
				return this.buffer;
			} else {
				ByteBuffer byteBuffer = BufferUtils.createByteBuffer(this.field_18913);
				this.buffers.forEach(byteBuffer::put);
				byteBuffer.put(this.buffer);
				byteBuffer.flip();
				return byteBuffer;
			}
		}
	}
}
