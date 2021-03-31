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

	public PngFile(String name, InputStream in) throws IOException {
		try (
			MemoryStack memoryStack = MemoryStack.stackPush();
			PngFile.Reader reader = createReader(in);
			STBIReadCallback sTBIReadCallback = STBIReadCallback.create(reader::read);
			STBISkipCallback sTBISkipCallback = STBISkipCallback.create(reader::skip);
			STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(reader::eof);
		) {
			STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack(memoryStack);
			sTBIIOCallbacks.read(sTBIReadCallback);
			sTBIIOCallbacks.skip(sTBISkipCallback);
			sTBIIOCallbacks.eof(sTBIEOFCallback);
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			if (!STBImage.stbi_info_from_callbacks(sTBIIOCallbacks, 0L, intBuffer, intBuffer2, intBuffer3)) {
				throw new IOException("Could not read info from the PNG file " + name + " " + STBImage.stbi_failure_reason());
			}

			this.width = intBuffer.get(0);
			this.height = intBuffer2.get(0);
		}
	}

	private static PngFile.Reader createReader(InputStream is) {
		return (PngFile.Reader)(is instanceof FileInputStream
			? new PngFile.SeekableChannelReader(((FileInputStream)is).getChannel())
			: new PngFile.ChannelReader(Channels.newChannel(is)));
	}

	@Environment(EnvType.CLIENT)
	static class ChannelReader extends PngFile.Reader {
		private static final int field_32035 = 128;
		private final ReadableByteChannel channel;
		private long buffer = MemoryUtil.nmemAlloc(128L);
		private int bufferSize = 128;
		private int bufferPosition;
		private int readPosition;

		private ChannelReader(ReadableByteChannel readableByteChannel) {
			this.channel = readableByteChannel;
		}

		private void readToBuffer(int size) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(this.buffer, this.bufferSize);
			if (size + this.readPosition > this.bufferSize) {
				this.bufferSize = size + this.readPosition;
				byteBuffer = MemoryUtil.memRealloc(byteBuffer, this.bufferSize);
				this.buffer = MemoryUtil.memAddress(byteBuffer);
			}

			byteBuffer.position(this.bufferPosition);

			while (size + this.readPosition > this.bufferPosition) {
				try {
					int i = this.channel.read(byteBuffer);
					if (i == -1) {
						break;
					}
				} finally {
					this.bufferPosition = byteBuffer.position();
				}
			}
		}

		@Override
		public int read(long data, int size) throws IOException {
			this.readToBuffer(size);
			if (size + this.readPosition > this.bufferPosition) {
				size = this.bufferPosition - this.readPosition;
			}

			MemoryUtil.memCopy(this.buffer + (long)this.readPosition, data, (long)size);
			this.readPosition += size;
			return size;
		}

		@Override
		public void skip(int n) throws IOException {
			if (n > 0) {
				this.readToBuffer(n);
				if (n + this.readPosition > this.bufferPosition) {
					throw new EOFException("Can't skip past the EOF.");
				}
			}

			if (this.readPosition + n < 0) {
				throw new IOException("Can't seek before the beginning: " + (this.readPosition + n));
			} else {
				this.readPosition += n;
			}
		}

		@Override
		public void close() throws IOException {
			MemoryUtil.nmemFree(this.buffer);
			this.channel.close();
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class Reader implements AutoCloseable {
		protected boolean errored;

		private Reader() {
		}

		int read(long user, long data, int size) {
			try {
				return this.read(data, size);
			} catch (IOException var7) {
				this.errored = true;
				return 0;
			}
		}

		void skip(long user, int n) {
			try {
				this.skip(n);
			} catch (IOException var5) {
				this.errored = true;
			}
		}

		int eof(long user) {
			return this.errored ? 1 : 0;
		}

		protected abstract int read(long data, int size) throws IOException;

		protected abstract void skip(int n) throws IOException;

		public abstract void close() throws IOException;
	}

	@Environment(EnvType.CLIENT)
	static class SeekableChannelReader extends PngFile.Reader {
		private final SeekableByteChannel channel;

		private SeekableChannelReader(SeekableByteChannel channel) {
			this.channel = channel;
		}

		@Override
		public int read(long data, int size) throws IOException {
			ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(data, size);
			return this.channel.read(byteBuffer);
		}

		@Override
		public void skip(int n) throws IOException {
			this.channel.position(this.channel.position() + (long)n);
		}

		@Override
		public int eof(long user) {
			return super.eof(user) != 0 && this.channel.isOpen() ? 1 : 0;
		}

		@Override
		public void close() throws IOException {
			this.channel.close();
		}
	}
}
