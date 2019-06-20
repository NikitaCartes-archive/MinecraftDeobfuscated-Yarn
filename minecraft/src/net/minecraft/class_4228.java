package net.minecraft;

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
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class class_4228 implements class_4234 {
	private long field_18907;
	private final AudioFormat field_18908;
	private final InputStream field_18909;
	private ByteBuffer field_18910 = MemoryUtil.memAlloc(8192);

	public class_4228(InputStream inputStream) throws IOException {
		this.field_18909 = inputStream;
		this.field_18910.limit(0);

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);

			while (this.field_18907 == 0L) {
				if (!this.method_19677()) {
					throw new IOException("Failed to find Ogg header");
				}

				int i = this.field_18910.position();
				this.field_18910.position(0);
				this.field_18907 = STBVorbis.stb_vorbis_open_pushdata(this.field_18910, intBuffer, intBuffer2, null);
				this.field_18910.position(i);
				int j = intBuffer2.get(0);
				if (j == 1) {
					this.method_19678();
				} else if (j != 0) {
					throw new IOException("Failed to read Ogg file " + j);
				}
			}

			this.field_18910.position(this.field_18910.position() + intBuffer.get(0));
			STBVorbisInfo sTBVorbisInfo = STBVorbisInfo.mallocStack(memoryStack);
			STBVorbis.stb_vorbis_get_info(this.field_18907, sTBVorbisInfo);
			this.field_18908 = new AudioFormat((float)sTBVorbisInfo.sample_rate(), 16, sTBVorbisInfo.channels(), true, false);
		}
	}

	private boolean method_19677() throws IOException {
		int i = this.field_18910.limit();
		int j = this.field_18910.capacity() - i;
		if (j == 0) {
			return true;
		} else {
			byte[] bs = new byte[j];
			int k = this.field_18909.read(bs);
			if (k == -1) {
				return false;
			} else {
				int l = this.field_18910.position();
				this.field_18910.limit(i + k);
				this.field_18910.position(i);
				this.field_18910.put(bs, 0, k);
				this.field_18910.position(l);
				return true;
			}
		}
	}

	private void method_19678() {
		boolean bl = this.field_18910.position() == 0;
		boolean bl2 = this.field_18910.position() == this.field_18910.limit();
		if (bl2 && !bl) {
			this.field_18910.position(0);
			this.field_18910.limit(0);
		} else {
			ByteBuffer byteBuffer = MemoryUtil.memAlloc(bl ? 2 * this.field_18910.capacity() : this.field_18910.capacity());
			byteBuffer.put(this.field_18910);
			MemoryUtil.memFree(this.field_18910);
			byteBuffer.flip();
			this.field_18910 = byteBuffer;
		}
	}

	private boolean method_19674(class_4228.class_4229 arg) throws IOException {
		if (this.field_18907 == 0L) {
			return false;
		} else {
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);

				while (true) {
					int i = STBVorbis.stb_vorbis_decode_frame_pushdata(this.field_18907, this.field_18910, intBuffer, pointerBuffer, intBuffer2);
					this.field_18910.position(this.field_18910.position() + i);
					int j = STBVorbis.stb_vorbis_get_error(this.field_18907);
					if (j == 1) {
						this.method_19678();
						if (!this.method_19677()) {
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
								this.method_19675(pointerBuffer2.getFloatBuffer(0, k), arg);
								return true;
							}

							if (l == 2) {
								this.method_19676(pointerBuffer2.getFloatBuffer(0, k), pointerBuffer2.getFloatBuffer(1, k), arg);
								return true;
							}

							throw new IllegalStateException("Invalid number of channels: " + l);
						}
					}
				}
			}
		}
	}

	private void method_19675(FloatBuffer floatBuffer, class_4228.class_4229 arg) {
		while (floatBuffer.hasRemaining()) {
			arg.method_19680(floatBuffer.get());
		}
	}

	private void method_19676(FloatBuffer floatBuffer, FloatBuffer floatBuffer2, class_4228.class_4229 arg) {
		while (floatBuffer.hasRemaining() && floatBuffer2.hasRemaining()) {
			arg.method_19680(floatBuffer.get());
			arg.method_19680(floatBuffer2.get());
		}
	}

	public void close() throws IOException {
		if (this.field_18907 != 0L) {
			STBVorbis.stb_vorbis_close(this.field_18907);
			this.field_18907 = 0L;
		}

		MemoryUtil.memFree(this.field_18910);
		this.field_18909.close();
	}

	@Override
	public AudioFormat method_19719() {
		return this.field_18908;
	}

	@Nullable
	@Override
	public ByteBuffer method_19720(int i) throws IOException {
		class_4228.class_4229 lv = new class_4228.class_4229(i + 8192);

		while (this.method_19674(lv) && lv.field_18913 < i) {
		}

		return lv.method_19679();
	}

	@Override
	public ByteBuffer method_19721() throws IOException {
		class_4228.class_4229 lv = new class_4228.class_4229(16384);

		while (this.method_19674(lv)) {
		}

		return lv.method_19679();
	}

	@Environment(EnvType.CLIENT)
	static class class_4229 {
		private final List<ByteBuffer> field_18911 = Lists.<ByteBuffer>newArrayList();
		private final int field_18912;
		private int field_18913;
		private ByteBuffer field_18914;

		public class_4229(int i) {
			this.field_18912 = i + 1 & -2;
			this.method_19682();
		}

		private void method_19682() {
			this.field_18914 = BufferUtils.createByteBuffer(this.field_18912);
		}

		public void method_19680(float f) {
			if (this.field_18914.remaining() == 0) {
				this.field_18914.flip();
				this.field_18911.add(this.field_18914);
				this.method_19682();
			}

			int i = class_3532.method_15340((int)(f * 32767.5F - 0.5F), -32768, 32767);
			this.field_18914.putShort((short)i);
			this.field_18913 += 2;
		}

		public ByteBuffer method_19679() {
			this.field_18914.flip();
			if (this.field_18911.isEmpty()) {
				return this.field_18914;
			} else {
				ByteBuffer byteBuffer = BufferUtils.createByteBuffer(this.field_18913);
				this.field_18911.forEach(byteBuffer::put);
				byteBuffer.put(this.field_18914);
				byteBuffer.flip();
				return byteBuffer;
			}
		}
	}
}
