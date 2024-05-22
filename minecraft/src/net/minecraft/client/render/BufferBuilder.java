package net.minecraft.client.render;

import java.nio.ByteOrder;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.system.MemoryUtil;

/**
 * Builds a buffer of primitives and optionally sorts them by the distance
 * from the camera.
 * 
 * <p>This builder can sort quad primitives. It sorts them by the distance
 * between the camera position and the center of the quad. Sorting is
 * required when drawing translucent objects because they have to be drawn
 * in back-to-front order. See
 * <a href="https://www.khronos.org/opengl/wiki/Transparency_Sorting">
 * Transparency Sorting - OpenGL Wiki</a>.
 * 
 * <p>For {@link VertexFormat.DrawMode#LINES LINES} and {@link
 * VertexFormat.DrawMode#LINE_STRIP LINE_STRIP} draw modes, this builder
 * duplicates every vertex in a line to produce a quad with zero area. See
 * {@link GameRenderer#getRenderTypeLinesProgram}.
 */
@Environment(EnvType.CLIENT)
public class BufferBuilder implements VertexConsumer {
	private static final long field_52068 = -1L;
	private static final long field_52069 = -1L;
	private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
	private final BufferAllocator allocator;
	private long vertexPointer = -1L;
	private int vertexCount;
	private final VertexFormat format;
	private final VertexFormat.DrawMode drawMode;
	/**
	 * Whether this builder is aware of the vertex format and can skip checks
	 * for the current target element while building a vertex in {@link
	 * #vertex(float, float, float, float, float, float, float, float, float, int, int, float, float, float)}.
	 */
	private final boolean canSkipElementChecks;
	private final boolean hasOverlay;
	private final int vertexSizeByte;
	private final int requiredMask;
	private final int[] offsetsByElementId;
	private int currentMask;
	private boolean building = true;

	public BufferBuilder(BufferAllocator allocator, VertexFormat.DrawMode drawMode, VertexFormat format) {
		if (!format.has(VertexFormatElement.POSITION)) {
			throw new IllegalArgumentException("Cannot build mesh with no position element");
		} else {
			this.allocator = allocator;
			this.drawMode = drawMode;
			this.format = format;
			this.vertexSizeByte = format.getVertexSizeByte();
			this.requiredMask = format.getRequiredMask() & ~VertexFormatElement.POSITION.getBit();
			this.offsetsByElementId = format.getOffsetsByElementId();
			boolean bl = format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
			boolean bl2 = format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
			this.canSkipElementChecks = bl || bl2;
			this.hasOverlay = bl;
		}
	}

	@Nullable
	public BuiltBuffer endNullable() {
		this.ensureBuilding();
		this.endVertex();
		BuiltBuffer builtBuffer = this.build();
		this.building = false;
		this.vertexPointer = -1L;
		return builtBuffer;
	}

	public BuiltBuffer end() {
		BuiltBuffer builtBuffer = this.endNullable();
		if (builtBuffer == null) {
			throw new IllegalStateException("BufferBuilder was empty");
		} else {
			return builtBuffer;
		}
	}

	private void ensureBuilding() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		}
	}

	@Nullable
	private BuiltBuffer build() {
		if (this.vertexCount == 0) {
			return null;
		} else {
			BufferAllocator.CloseableBuffer closeableBuffer = this.allocator.getAllocated();
			if (closeableBuffer == null) {
				return null;
			} else {
				int i = this.drawMode.getIndexCount(this.vertexCount);
				VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(this.vertexCount);
				return new BuiltBuffer(closeableBuffer, new BuiltBuffer.DrawParameters(this.format, this.vertexCount, i, this.drawMode, indexType));
			}
		}
	}

	private long beginVertex() {
		this.ensureBuilding();
		this.endVertex();
		this.vertexCount++;
		long l = this.allocator.allocate(this.vertexSizeByte);
		this.vertexPointer = l;
		return l;
	}

	private long beginElement(VertexFormatElement element) {
		int i = this.currentMask;
		int j = i & ~element.getBit();
		if (j == i) {
			return -1L;
		} else {
			this.currentMask = j;
			long l = this.vertexPointer;
			if (l == -1L) {
				throw new IllegalArgumentException("Not currently building vertex");
			} else {
				return l + (long)this.offsetsByElementId[element.id()];
			}
		}
	}

	private void endVertex() {
		if (this.vertexCount != 0) {
			if (this.currentMask != 0) {
				String string = (String)VertexFormatElement.streamFromMask(this.currentMask).map(this.format::getName).collect(Collectors.joining(", "));
				throw new IllegalStateException("Missing elements in vertex: " + string);
			} else {
				if (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP) {
					long l = this.allocator.allocate(this.vertexSizeByte);
					MemoryUtil.memCopy(l - (long)this.vertexSizeByte, l, (long)this.vertexSizeByte);
					this.vertexCount++;
				}
			}
		}
	}

	private static void putColor(long pointer, int argb) {
		int i = ColorHelper.Abgr.toAbgr(argb);
		MemoryUtil.memPutInt(pointer, LITTLE_ENDIAN ? i : Integer.reverseBytes(i));
	}

	private static void putInt(long pointer, int i) {
		if (LITTLE_ENDIAN) {
			MemoryUtil.memPutInt(pointer, i);
		} else {
			MemoryUtil.memPutShort(pointer, (short)(i & 65535));
			MemoryUtil.memPutShort(pointer + 2L, (short)(i >> 16 & 65535));
		}
	}

	@Override
	public VertexConsumer vertex(float x, float y, float z) {
		long l = this.beginVertex() + (long)this.offsetsByElementId[VertexFormatElement.POSITION.id()];
		this.currentMask = this.requiredMask;
		MemoryUtil.memPutFloat(l, x);
		MemoryUtil.memPutFloat(l + 4L, y);
		MemoryUtil.memPutFloat(l + 8L, z);
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		long l = this.beginElement(VertexFormatElement.COLOR);
		if (l != -1L) {
			MemoryUtil.memPutByte(l, (byte)red);
			MemoryUtil.memPutByte(l + 1L, (byte)green);
			MemoryUtil.memPutByte(l + 2L, (byte)blue);
			MemoryUtil.memPutByte(l + 3L, (byte)alpha);
		}

		return this;
	}

	@Override
	public VertexConsumer color(int argb) {
		long l = this.beginElement(VertexFormatElement.COLOR);
		if (l != -1L) {
			putColor(l, argb);
		}

		return this;
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		long l = this.beginElement(VertexFormatElement.UV_0);
		if (l != -1L) {
			MemoryUtil.memPutFloat(l, u);
			MemoryUtil.memPutFloat(l + 4L, v);
		}

		return this;
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		return this.putUv((short)u, (short)v, VertexFormatElement.UV_1);
	}

	@Override
	public VertexConsumer overlay(int uv) {
		long l = this.beginElement(VertexFormatElement.UV_1);
		if (l != -1L) {
			putInt(l, uv);
		}

		return this;
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return this.putUv((short)u, (short)v, VertexFormatElement.UV_2);
	}

	@Override
	public VertexConsumer light(int uv) {
		long l = this.beginElement(VertexFormatElement.UV_2);
		if (l != -1L) {
			putInt(l, uv);
		}

		return this;
	}

	private VertexConsumer putUv(short u, short v, VertexFormatElement element) {
		long l = this.beginElement(element);
		if (l != -1L) {
			MemoryUtil.memPutShort(l, u);
			MemoryUtil.memPutShort(l + 2L, v);
		}

		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		long l = this.beginElement(VertexFormatElement.NORMAL);
		if (l != -1L) {
			MemoryUtil.memPutByte(l, floatToByte(x));
			MemoryUtil.memPutByte(l + 1L, floatToByte(y));
			MemoryUtil.memPutByte(l + 2L, floatToByte(z));
		}

		return this;
	}

	private static byte floatToByte(float f) {
		return (byte)((int)(MathHelper.clamp(f, -1.0F, 1.0F) * 127.0F) & 0xFF);
	}

	@Override
	public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
		if (this.canSkipElementChecks) {
			long l = this.beginVertex();
			MemoryUtil.memPutFloat(l + 0L, x);
			MemoryUtil.memPutFloat(l + 4L, y);
			MemoryUtil.memPutFloat(l + 8L, z);
			putColor(l + 12L, color);
			MemoryUtil.memPutFloat(l + 16L, u);
			MemoryUtil.memPutFloat(l + 20L, v);
			long m;
			if (this.hasOverlay) {
				putInt(l + 24L, overlay);
				m = l + 28L;
			} else {
				m = l + 24L;
			}

			putInt(m + 0L, light);
			MemoryUtil.memPutByte(m + 4L, floatToByte(normalX));
			MemoryUtil.memPutByte(m + 5L, floatToByte(normalY));
			MemoryUtil.memPutByte(m + 6L, floatToByte(normalZ));
		} else {
			VertexConsumer.super.vertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ);
		}
	}
}
