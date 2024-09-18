package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GpuBuffer implements AutoCloseable {
	private static final MemoryPool MEMORY_POOL = TracyClient.createMemoryPool("GPU Buffers");
	private final GlBufferTarget target;
	private final GlUsage usage;
	private boolean closed;
	private boolean initialized = false;
	public final int handle;
	public int size;

	public GpuBuffer(GlBufferTarget target, GlUsage usage, int size) {
		this.target = target;
		this.size = size;
		this.usage = usage;
		this.handle = GlStateManager._glGenBuffers();
	}

	public GpuBuffer(GlBufferTarget target, GlUsage usage, ByteBuffer buf) {
		this(target, usage, buf.remaining());
		this.copyFrom(buf, 0);
	}

	public void resize(int newSize) {
		if (this.closed) {
			throw new IllegalStateException("Buffer already closed");
		} else {
			if (this.initialized) {
				MEMORY_POOL.free((long)this.handle);
			}

			this.size = newSize;
			if (this.usage.writable) {
				this.initialized = false;
			} else {
				this.bind();
				GlStateManager._glBufferData(this.target.id, (long)newSize, this.usage.id);
				MEMORY_POOL.malloc((long)this.handle, newSize);
				this.initialized = true;
			}
		}
	}

	public void copyFrom(ByteBuffer buf, int offset) {
		if (this.closed) {
			throw new IllegalStateException("Buffer already closed");
		} else if (!this.usage.writable) {
			throw new IllegalStateException("Buffer is not writable");
		} else {
			int i = buf.remaining();
			if (i + offset > this.size) {
				throw new IllegalArgumentException(
					"Cannot write more data than this buffer can hold (attempting to write " + i + " bytes at offset " + offset + " to " + this.size + " size buffer)"
				);
			} else {
				this.bind();
				if (this.initialized) {
					GlStateManager._glBufferSubData(this.target.id, offset, buf);
				} else if (offset == 0 && i == this.size) {
					GlStateManager._glBufferData(this.target.id, buf, this.usage.id);
					MEMORY_POOL.malloc((long)this.handle, this.size);
					this.initialized = true;
				} else {
					GlStateManager._glBufferData(this.target.id, (long)this.size, this.usage.id);
					GlStateManager._glBufferSubData(this.target.id, offset, buf);
					MEMORY_POOL.malloc((long)this.handle, this.size);
					this.initialized = true;
				}
			}
		}
	}

	@Nullable
	public GpuBuffer.ReadResult read() {
		return this.read(0, this.size);
	}

	@Nullable
	public GpuBuffer.ReadResult read(int offset, int bytes) {
		if (this.closed) {
			throw new IllegalStateException("Buffer already closed");
		} else if (!this.usage.readable) {
			throw new IllegalStateException("Buffer is not readable");
		} else if (offset + bytes > this.size) {
			throw new IllegalArgumentException(
				"Cannot read more data than this buffer can hold (attempting to read " + bytes + " bytes at offset " + offset + " from " + this.size + " size buffer)"
			);
		} else {
			this.bind();
			ByteBuffer byteBuffer = GlStateManager._glMapBufferRange(this.target.id, offset, bytes, 1);
			return byteBuffer == null ? null : new GpuBuffer.ReadResult(this.target.id, byteBuffer);
		}
	}

	public void close() {
		if (!this.closed) {
			this.closed = true;
			GlStateManager._glDeleteBuffers(this.handle);
			if (this.initialized) {
				MEMORY_POOL.free((long)this.handle);
			}
		}
	}

	public void bind() {
		GlStateManager._glBindBuffer(this.target.id, this.handle);
	}

	@Environment(EnvType.CLIENT)
	public static class ReadResult implements AutoCloseable {
		private final int handle;
		private final ByteBuffer buf;

		protected ReadResult(int handle, ByteBuffer buf) {
			this.handle = handle;
			this.buf = buf;
		}

		public ByteBuffer getBuf() {
			return this.buf;
		}

		public void close() {
			GlStateManager._glUnmapBuffer(this.handle);
		}
	}
}
