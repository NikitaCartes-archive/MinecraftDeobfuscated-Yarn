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

@Environment(EnvType.CLIENT)
public class PngFile {
	public final int width;
	public final int height;

	public PngFile(String string, InputStream inputStream) throws IOException {
		try (
			MemoryStack memoryStack = MemoryStack.stackPush();
			PngFile.class_1051 lv = method_4542(inputStream);
			STBIReadCallback sTBIReadCallback = STBIReadCallback.create(lv::method_4543);
			STBISkipCallback sTBISkipCallback = STBISkipCallback.create(lv::method_4547);
			STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(lv::method_4546);
		) {
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

	private static PngFile.class_1051 method_4542(InputStream inputStream) {
		return (PngFile.class_1051)(inputStream instanceof FileInputStream
			? new PngFile.class_1053(((FileInputStream)inputStream).getChannel())
			: new PngFile.class_1052(Channels.newChannel(inputStream)));
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_1051 implements AutoCloseable {
		protected boolean field_5228;

		private class_1051() {
		}

		int method_4543(long l, long m, int i) {
			try {
				return this.method_4544(m, i);
			} catch (IOException var7) {
				this.field_5228 = true;
				return 0;
			}
		}

		void method_4547(long l, int i) {
			try {
				this.method_4545(i);
			} catch (IOException var5) {
				this.field_5228 = true;
			}
		}

		int method_4546(long l) {
			return this.field_5228 ? 1 : 0;
		}

		protected abstract int method_4544(long l, int i) throws IOException;

		protected abstract void method_4545(int i) throws IOException;

		public abstract void close() throws IOException;
	}

	@Environment(EnvType.CLIENT)
	static class class_1052 extends PngFile.class_1051 {
		private final ReadableByteChannel field_5229;
		private long field_5233 = MemoryUtil.nmemAlloc(128L);
		private int field_5232 = 128;
		private int field_5231;
		private int field_5230;

		private class_1052(ReadableByteChannel readableByteChannel) {
			this.field_5229 = readableByteChannel;
		}

		private void method_4548(int i) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(this.field_5233, this.field_5232);
			if (i + this.field_5230 > this.field_5232) {
				this.field_5232 = i + this.field_5230;
				byteBuffer = MemoryUtil.memRealloc(byteBuffer, this.field_5232);
				this.field_5233 = MemoryUtil.memAddress(byteBuffer);
			}

			byteBuffer.position(this.field_5231);

			while (i + this.field_5230 > this.field_5231) {
				try {
					int j = this.field_5229.read(byteBuffer);
					if (j == -1) {
						break;
					}
				} finally {
					this.field_5231 = byteBuffer.position();
				}
			}
		}

		@Override
		public int method_4544(long l, int i) throws IOException {
			this.method_4548(i);
			if (i + this.field_5230 > this.field_5231) {
				i = this.field_5231 - this.field_5230;
			}

			MemoryUtil.memCopy(this.field_5233 + (long)this.field_5230, l, (long)i);
			this.field_5230 += i;
			return i;
		}

		@Override
		public void method_4545(int i) throws IOException {
			if (i > 0) {
				this.method_4548(i);
				if (i + this.field_5230 > this.field_5231) {
					throw new EOFException("Can't skip past the EOF.");
				}
			}

			if (this.field_5230 + i < 0) {
				throw new IOException("Can't seek before the beginning: " + (this.field_5230 + i));
			} else {
				this.field_5230 += i;
			}
		}

		@Override
		public void close() throws IOException {
			MemoryUtil.nmemFree(this.field_5233);
			this.field_5229.close();
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1053 extends PngFile.class_1051 {
		private final SeekableByteChannel field_5234;

		private class_1053(SeekableByteChannel seekableByteChannel) {
			this.field_5234 = seekableByteChannel;
		}

		@Override
		public int method_4544(long l, int i) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(l, i);
			return this.field_5234.read(byteBuffer);
		}

		@Override
		public void method_4545(int i) throws IOException {
			this.field_5234.position(this.field_5234.position() + (long)i);
		}

		@Override
		public int method_4546(long l) {
			return super.method_4546(l) != 0 && this.field_5234.isOpen() ? 1 : 0;
		}

		@Override
		public void close() throws IOException {
			this.field_5234.close();
		}
	}
}
