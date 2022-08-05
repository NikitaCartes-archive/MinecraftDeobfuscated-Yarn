package net.minecraft.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * A buffered input stream that uses a fixed-size buffer array.
 */
public class FixedBufferInputStream extends InputStream {
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private final InputStream stream;
	private final byte[] buf;
	private int end;
	private int start;

	public FixedBufferInputStream(InputStream stream) {
		this(stream, 8192);
	}

	public FixedBufferInputStream(InputStream stream, int size) {
		this.stream = stream;
		this.buf = new byte[size];
	}

	public int read() throws IOException {
		if (this.start >= this.end) {
			this.fill();
			if (this.start >= this.end) {
				return -1;
			}
		}

		return Byte.toUnsignedInt(this.buf[this.start++]);
	}

	public int read(byte[] buf, int offset, int length) throws IOException {
		int i = this.getAvailableBuffer();
		if (i <= 0) {
			if (length >= this.buf.length) {
				return this.stream.read(buf, offset, length);
			}

			this.fill();
			i = this.getAvailableBuffer();
			if (i <= 0) {
				return -1;
			}
		}

		if (length > i) {
			length = i;
		}

		System.arraycopy(this.buf, this.start, buf, offset, length);
		this.start += length;
		return length;
	}

	public long skip(long n) throws IOException {
		if (n <= 0L) {
			return 0L;
		} else {
			long l = (long)this.getAvailableBuffer();
			if (l <= 0L) {
				return this.stream.skip(n);
			} else {
				if (n > l) {
					n = l;
				}

				this.start = (int)((long)this.start + n);
				return n;
			}
		}
	}

	public int available() throws IOException {
		return this.getAvailableBuffer() + this.stream.available();
	}

	public void close() throws IOException {
		this.stream.close();
	}

	private int getAvailableBuffer() {
		return this.end - this.start;
	}

	private void fill() throws IOException {
		this.end = 0;
		this.start = 0;
		int i = this.stream.read(this.buf, 0, this.buf.length);
		if (i > 0) {
			this.end = i;
		}
	}
}
