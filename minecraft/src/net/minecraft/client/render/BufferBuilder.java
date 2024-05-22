package net.minecraft.client.render;

import java.nio.ByteOrder;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9799;
import net.minecraft.class_9801;
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
	private static final boolean field_52070 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
	private final class_9799 field_52071;
	private long field_52072 = -1L;
	private int vertexCount;
	private final VertexFormat format;
	private final VertexFormat.DrawMode field_52073;
	/**
	 * Whether this builder is aware of the vertex format and can skip checks
	 * for the current target element while building a vertex in {@link
	 * #vertex(float, float, float, float, float, float, float, float, float, int, int, float, float, float)}.
	 */
	private final boolean canSkipElementChecks;
	private final boolean hasOverlay;
	private final int field_52074;
	private final int field_52075;
	private final int[] field_52076;
	private int field_52077;
	private boolean building = true;

	public BufferBuilder(class_9799 arg, VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
		if (!vertexFormat.method_60836(VertexFormatElement.field_52107)) {
			throw new IllegalArgumentException("Cannot build mesh with no position element");
		} else {
			this.field_52071 = arg;
			this.field_52073 = drawMode;
			this.format = vertexFormat;
			this.field_52074 = vertexFormat.getVertexSizeByte();
			this.field_52075 = vertexFormat.method_60839() & ~VertexFormatElement.field_52107.method_60843();
			this.field_52076 = vertexFormat.method_60838();
			boolean bl = vertexFormat == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
			boolean bl2 = vertexFormat == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
			this.canSkipElementChecks = bl || bl2;
			this.hasOverlay = bl;
		}
	}

	@Nullable
	public class_9801 method_60794() {
		this.method_60802();
		this.method_60806();
		class_9801 lv = this.method_60804();
		this.building = false;
		this.field_52072 = -1L;
		return lv;
	}

	public class_9801 method_60800() {
		class_9801 lv = this.method_60794();
		if (lv == null) {
			throw new IllegalStateException("BufferBuilder was empty");
		} else {
			return lv;
		}
	}

	private void method_60802() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		}
	}

	@Nullable
	private class_9801 method_60804() {
		if (this.vertexCount == 0) {
			return null;
		} else {
			class_9799.class_9800 lv = this.field_52071.method_60807();
			if (lv == null) {
				return null;
			} else {
				int i = this.field_52073.getIndexCount(this.vertexCount);
				VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(this.vertexCount);
				return new class_9801(lv, new class_9801.DrawParameters(this.format, this.vertexCount, i, this.field_52073, indexType));
			}
		}
	}

	private long method_60805() {
		this.method_60802();
		this.method_60806();
		this.vertexCount++;
		long l = this.field_52071.method_60808(this.field_52074);
		this.field_52072 = l;
		return l;
	}

	private long method_60798(VertexFormatElement vertexFormatElement) {
		int i = this.field_52077;
		int j = i & ~vertexFormatElement.method_60843();
		if (j == i) {
			return -1L;
		} else {
			this.field_52077 = j;
			long l = this.field_52072;
			if (l == -1L) {
				throw new IllegalArgumentException("Not currently building vertex");
			} else {
				return l + (long)this.field_52076[vertexFormatElement.id()];
			}
		}
	}

	private void method_60806() {
		if (this.vertexCount != 0) {
			if (this.field_52077 != 0) {
				String string = (String)VertexFormatElement.method_60848(this.field_52077).map(this.format::method_60837).collect(Collectors.joining(", "));
				throw new IllegalStateException("Missing elements in vertex: " + string);
			} else {
				if (this.field_52073 == VertexFormat.DrawMode.LINES || this.field_52073 == VertexFormat.DrawMode.LINE_STRIP) {
					long l = this.field_52071.method_60808(this.field_52074);
					MemoryUtil.memCopy(l - (long)this.field_52074, l, (long)this.field_52074);
					this.vertexCount++;
				}
			}
		}
	}

	private static void method_60797(long l, int i) {
		int j = ColorHelper.Abgr.method_60675(i);
		MemoryUtil.memPutInt(l, field_52070 ? j : Integer.reverseBytes(j));
	}

	private static void method_60801(long l, int i) {
		if (field_52070) {
			MemoryUtil.memPutInt(l, i);
		} else {
			MemoryUtil.memPutShort(l, (short)(i & 65535));
			MemoryUtil.memPutShort(l + 2L, (short)(i >> 16 & 65535));
		}
	}

	@Override
	public VertexConsumer vertex(float f, float g, float h) {
		long l = this.method_60805() + (long)this.field_52076[VertexFormatElement.field_52107.id()];
		this.field_52077 = this.field_52075;
		MemoryUtil.memPutFloat(l, f);
		MemoryUtil.memPutFloat(l + 4L, g);
		MemoryUtil.memPutFloat(l + 8L, h);
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		long l = this.method_60798(VertexFormatElement.field_52108);
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
		long l = this.method_60798(VertexFormatElement.field_52108);
		if (l != -1L) {
			method_60797(l, argb);
		}

		return this;
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		long l = this.method_60798(VertexFormatElement.field_52109);
		if (l != -1L) {
			MemoryUtil.memPutFloat(l, u);
			MemoryUtil.memPutFloat(l + 4L, v);
		}

		return this;
	}

	@Override
	public VertexConsumer method_60796(int i, int j) {
		return this.method_60799((short)i, (short)j, VertexFormatElement.field_52111);
	}

	@Override
	public VertexConsumer overlay(int uv) {
		long l = this.method_60798(VertexFormatElement.field_52111);
		if (l != -1L) {
			method_60801(l, uv);
		}

		return this;
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return this.method_60799((short)u, (short)v, VertexFormatElement.field_52112);
	}

	@Override
	public VertexConsumer method_60803(int i) {
		long l = this.method_60798(VertexFormatElement.field_52112);
		if (l != -1L) {
			method_60801(l, i);
		}

		return this;
	}

	private VertexConsumer method_60799(short s, short t, VertexFormatElement vertexFormatElement) {
		long l = this.method_60798(vertexFormatElement);
		if (l != -1L) {
			MemoryUtil.memPutShort(l, s);
			MemoryUtil.memPutShort(l + 2L, t);
		}

		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		long l = this.method_60798(VertexFormatElement.field_52113);
		if (l != -1L) {
			MemoryUtil.memPutByte(l, method_60795(x));
			MemoryUtil.memPutByte(l + 1L, method_60795(y));
			MemoryUtil.memPutByte(l + 2L, method_60795(z));
		}

		return this;
	}

	private static byte method_60795(float f) {
		return (byte)((int)(MathHelper.clamp(f, -1.0F, 1.0F) * 127.0F) & 0xFF);
	}

	@Override
	public void vertex(float x, float y, float z, int i, float green, float blue, int j, int k, float v, float f, float g) {
		if (this.canSkipElementChecks) {
			long l = this.method_60805();
			MemoryUtil.memPutFloat(l + 0L, x);
			MemoryUtil.memPutFloat(l + 4L, y);
			MemoryUtil.memPutFloat(l + 8L, z);
			method_60797(l + 12L, i);
			MemoryUtil.memPutFloat(l + 16L, green);
			MemoryUtil.memPutFloat(l + 20L, blue);
			long m;
			if (this.hasOverlay) {
				method_60801(l + 24L, j);
				m = l + 28L;
			} else {
				m = l + 24L;
			}

			method_60801(m + 0L, k);
			MemoryUtil.memPutByte(m + 4L, method_60795(v));
			MemoryUtil.memPutByte(m + 5L, method_60795(f));
			MemoryUtil.memPutByte(m + 6L, method_60795(g));
		} else {
			VertexConsumer.super.vertex(x, y, z, i, green, blue, j, k, v, f, g);
		}
	}
}
