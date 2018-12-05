package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_301;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public final class NativeImage implements AutoCloseable {
	private static final Set<StandardOpenOption> field_4992 = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	private final NativeImage.Format imageFormat;
	private final int width;
	private final int height;
	private final boolean field_4990;
	private long pointer;
	private final int sizeBytes;

	public NativeImage(int i, int j, boolean bl) {
		this(NativeImage.Format.field_4997, i, j, bl);
	}

	public NativeImage(NativeImage.Format format, int i, int j, boolean bl) {
		this.imageFormat = format;
		this.width = i;
		this.height = j;
		this.sizeBytes = i * j * format.getBytesPerPixel();
		this.field_4990 = false;
		if (bl) {
			this.pointer = MemoryUtil.nmemCalloc(1L, (long)this.sizeBytes);
		} else {
			this.pointer = MemoryUtil.nmemAlloc((long)this.sizeBytes);
		}
	}

	private NativeImage(NativeImage.Format format, int i, int j, boolean bl, long l) {
		this.imageFormat = format;
		this.width = i;
		this.height = j;
		this.field_4990 = bl;
		this.pointer = l;
		this.sizeBytes = i * j * format.getBytesPerPixel();
	}

	public String toString() {
		return "NativeImage[" + this.imageFormat + " " + this.width + "x" + this.height + "@" + this.pointer + (this.field_4990 ? "S" : "N") + "]";
	}

	public static NativeImage fromInputStream(InputStream inputStream) throws IOException {
		return fromInputStream(NativeImage.Format.field_4997, inputStream);
	}

	public static NativeImage fromInputStream(@Nullable NativeImage.Format format, InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = null;

		NativeImage var3;
		try {
			byteBuffer = TextureUtil.readResource(inputStream);
			byteBuffer.rewind();
			var3 = fromByteBuffer(format, byteBuffer);
		} finally {
			MemoryUtil.memFree(byteBuffer);
			IOUtils.closeQuietly(inputStream);
		}

		return var3;
	}

	public static NativeImage fromByteBuffer(ByteBuffer byteBuffer) throws IOException {
		return fromByteBuffer(NativeImage.Format.field_4997, byteBuffer);
	}

	public static NativeImage fromByteBuffer(@Nullable NativeImage.Format format, ByteBuffer byteBuffer) throws IOException {
		if (format != null && !format.method_4338()) {
			throw new UnsupportedOperationException("Don't know how to read format " + format);
		} else if (MemoryUtil.memAddress(byteBuffer) == 0L) {
			throw new IllegalArgumentException("Invalid buffer");
		} else {
			NativeImage var8;
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, format == null ? 0 : format.bytesPerPixel);
				if (byteBuffer2 == null) {
					throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
				}

				var8 = new NativeImage(
					format == null ? NativeImage.Format.method_4336(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2)
				);
			}

			return var8;
		}
	}

	private static void setTextureClamp(boolean bl) {
		if (bl) {
			GlStateManager.texParameter(3553, 10242, 10496);
			GlStateManager.texParameter(3553, 10243, 10496);
		} else {
			GlStateManager.texParameter(3553, 10242, 10497);
			GlStateManager.texParameter(3553, 10243, 10497);
		}
	}

	private static void method_4308(boolean bl, boolean bl2) {
		if (bl) {
			GlStateManager.texParameter(3553, 10241, bl2 ? 9987 : 9729);
			GlStateManager.texParameter(3553, 10240, 9729);
		} else {
			GlStateManager.texParameter(3553, 10241, bl2 ? 9986 : 9728);
			GlStateManager.texParameter(3553, 10240, 9728);
		}
	}

	private void checkAllocated() {
		if (this.pointer == 0L) {
			throw new IllegalStateException("Image is not allocated.");
		}
	}

	public void close() {
		if (this.pointer != 0L) {
			if (this.field_4990) {
				STBImage.nstbi_image_free(this.pointer);
			} else {
				MemoryUtil.nmemFree(this.pointer);
			}
		}

		this.pointer = 0L;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public NativeImage.Format getFormat() {
		return this.imageFormat;
	}

	public int getPixelRGBA(int i, int j) {
		if (this.imageFormat != NativeImage.Format.field_4997) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.imageFormat));
		} else if (i <= this.width && j <= this.height) {
			this.checkAllocated();
			return MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).get(i + j * this.width);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
		}
	}

	public void setPixelRGBA(int i, int j, int k) {
		if (this.imageFormat != NativeImage.Format.field_4997) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.imageFormat));
		} else if (i <= this.width && j <= this.height) {
			this.checkAllocated();
			MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).put(i + j * this.width, k);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
		}
	}

	public byte method_4311(int i, int j) {
		if (!this.imageFormat.method_4337()) {
			throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.imageFormat));
		} else if (i <= this.width && j <= this.height) {
			return MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes)
				.get((i + j * this.width) * this.imageFormat.getBytesPerPixel() + this.imageFormat.method_4330() / 8);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
		}
	}

	public void blendPixel(int i, int j, int k) {
		if (this.imageFormat != NativeImage.Format.field_4997) {
			throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
		} else {
			int l = this.getPixelRGBA(i, j);
			float f = (float)(k >> 24 & 0xFF) / 255.0F;
			float g = (float)(k >> 16 & 0xFF) / 255.0F;
			float h = (float)(k >> 8 & 0xFF) / 255.0F;
			float m = (float)(k >> 0 & 0xFF) / 255.0F;
			float n = (float)(l >> 24 & 0xFF) / 255.0F;
			float o = (float)(l >> 16 & 0xFF) / 255.0F;
			float p = (float)(l >> 8 & 0xFF) / 255.0F;
			float q = (float)(l >> 0 & 0xFF) / 255.0F;
			float s = 1.0F - f;
			float t = f * f + n * s;
			float u = g * f + o * s;
			float v = h * f + p * s;
			float w = m * f + q * s;
			if (t > 1.0F) {
				t = 1.0F;
			}

			if (u > 1.0F) {
				u = 1.0F;
			}

			if (v > 1.0F) {
				v = 1.0F;
			}

			if (w > 1.0F) {
				w = 1.0F;
			}

			int x = (int)(t * 255.0F);
			int y = (int)(u * 255.0F);
			int z = (int)(v * 255.0F);
			int aa = (int)(w * 255.0F);
			this.setPixelRGBA(i, j, x << 24 | y << 16 | z << 8 | aa << 0);
		}
	}

	@Deprecated
	public int[] makePixelArray() {
		if (this.imageFormat != NativeImage.Format.field_4997) {
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else {
			this.checkAllocated();
			int[] is = new int[this.getWidth() * this.getHeight()];

			for (int i = 0; i < this.getHeight(); i++) {
				for (int j = 0; j < this.getWidth(); j++) {
					int k = this.getPixelRGBA(j, i);
					int l = k >> 24 & 0xFF;
					int m = k >> 16 & 0xFF;
					int n = k >> 8 & 0xFF;
					int o = k >> 0 & 0xFF;
					int p = l << 24 | o << 16 | n << 8 | m;
					is[j + i * this.getWidth()] = p;
				}
			}

			return is;
		}
	}

	public void upload(int i, int j, int k, boolean bl) {
		this.upload(i, j, k, 0, 0, this.width, this.height, bl);
	}

	public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl) {
		this.upload(i, j, k, l, m, n, o, false, false, bl);
	}

	public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3) {
		this.checkAllocated();
		method_4308(bl, bl3);
		setTextureClamp(bl2);
		if (n == this.getWidth()) {
			GlStateManager.pixelStore(3314, 0);
		} else {
			GlStateManager.pixelStore(3314, this.getWidth());
		}

		GlStateManager.pixelStore(3316, l);
		GlStateManager.pixelStore(3315, m);
		this.imageFormat.method_4340();
		GlStateManager.texSubImage2D(3553, i, j, k, n, o, this.imageFormat.method_4333(), 5121, this.pointer);
	}

	public void method_4327(int i, boolean bl) {
		this.checkAllocated();
		this.imageFormat.method_4339();
		GlStateManager.getTexImage(3553, i, this.imageFormat.method_4333(), 5121, this.pointer);
		if (bl && this.imageFormat.method_4329()) {
			for (int j = 0; j < this.getHeight(); j++) {
				for (int k = 0; k < this.getWidth(); k++) {
					this.setPixelRGBA(k, j, this.getPixelRGBA(k, j) | 255 << this.imageFormat.method_4332());
				}
			}
		}
	}

	public void method_4306(boolean bl) {
		this.checkAllocated();
		this.imageFormat.method_4339();
		if (bl) {
			GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
		}

		GlStateManager.readPixels(0, 0, this.width, this.height, this.imageFormat.method_4333(), 5121, this.pointer);
		if (bl) {
			GlStateManager.pixelTransfer(3357, 0.0F);
		}
	}

	public void writeFile(String string) throws IOException {
		this.writeFile(FileSystems.getDefault().getPath(string));
	}

	public void writeFile(File file) throws IOException {
		this.writeFile(file.toPath());
	}

	public void method_4316(STBTTFontinfo sTBTTFontinfo, int i, int j, int k, float f, float g, float h, float l, int m, int n) {
		if (m < 0 || m + j > this.getWidth() || n < 0 || n + k > this.getHeight()) {
			throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", m, n, j, k, this.getWidth(), this.getHeight()));
		} else if (this.imageFormat.getBytesPerPixel() != 1) {
			throw new IllegalArgumentException("Can only write fonts into 1-component images.");
		} else {
			STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(
				sTBTTFontinfo.address(), this.pointer + (long)m + (long)(n * this.getWidth()), j, k, this.getWidth(), f, g, h, l, i
			);
		}
	}

	public void writeFile(Path path) throws IOException {
		if (!this.imageFormat.method_4338()) {
			throw new UnsupportedOperationException("Don't know how to write format " + this.imageFormat);
		} else {
			this.checkAllocated();
			WritableByteChannel writableByteChannel = Files.newByteChannel(path, field_4992);
			Throwable var3 = null;

			try {
				NativeImage.class_1014 lv = new NativeImage.class_1014(writableByteChannel);

				try {
					if (!STBImageWrite.stbi_write_png_to_func(
						lv, 0L, this.getWidth(), this.getHeight(), this.imageFormat.getBytesPerPixel(), MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes), 0
					)) {
						throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
					}
				} finally {
					lv.free();
				}

				lv.method_4342();
			} catch (Throwable var19) {
				var3 = var19;
				throw var19;
			} finally {
				if (writableByteChannel != null) {
					if (var3 != null) {
						try {
							writableByteChannel.close();
						} catch (Throwable var17) {
							var3.addSuppressed(var17);
						}
					} else {
						writableByteChannel.close();
					}
				}
			}
		}
	}

	public void method_4317(NativeImage nativeImage) {
		if (nativeImage.getFormat() != this.imageFormat) {
			throw new UnsupportedOperationException("Image formats don't match.");
		} else {
			int i = this.imageFormat.getBytesPerPixel();
			this.checkAllocated();
			nativeImage.checkAllocated();
			if (this.width == nativeImage.width) {
				MemoryUtil.memCopy(nativeImage.pointer, this.pointer, (long)Math.min(this.sizeBytes, nativeImage.sizeBytes));
			} else {
				int j = Math.min(this.getWidth(), nativeImage.getWidth());
				int k = Math.min(this.getHeight(), nativeImage.getHeight());

				for (int l = 0; l < k; l++) {
					int m = l * nativeImage.getWidth() * i;
					int n = l * this.getWidth() * i;
					MemoryUtil.memCopy(nativeImage.pointer + (long)m, this.pointer + (long)n, (long)j);
				}
			}
		}
	}

	public void fillRGBA(int i, int j, int k, int l, int m) {
		for (int n = j; n < j + l; n++) {
			for (int o = i; o < i + k; o++) {
				this.setPixelRGBA(o, n, m);
			}
		}
	}

	public void method_4304(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
		for (int o = 0; o < n; o++) {
			for (int p = 0; p < m; p++) {
				int q = bl ? m - 1 - p : p;
				int r = bl2 ? n - 1 - o : o;
				int s = this.getPixelRGBA(i + p, j + o);
				this.setPixelRGBA(i + k + q, j + l + r, s);
			}
		}
	}

	public void method_4319() {
		this.checkAllocated();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			int i = this.imageFormat.getBytesPerPixel();
			int j = this.getWidth() * i;
			long l = memoryStack.nmalloc(j);

			for (int k = 0; k < this.getHeight() / 2; k++) {
				int m = k * this.getWidth() * i;
				int n = (this.getHeight() - 1 - k) * this.getWidth() * i;
				MemoryUtil.memCopy(this.pointer + (long)m, l, (long)j);
				MemoryUtil.memCopy(this.pointer + (long)n, this.pointer + (long)m, (long)j);
				MemoryUtil.memCopy(l, this.pointer + (long)n, (long)j);
			}
		}
	}

	public void resizeSubRectTo(int i, int j, int k, int l, NativeImage nativeImage) {
		this.checkAllocated();
		if (nativeImage.getFormat() != this.imageFormat) {
			throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
		} else {
			int m = this.imageFormat.getBytesPerPixel();
			STBImageResize.nstbir_resize_uint8(
				this.pointer + (long)((i + j * this.getWidth()) * m), k, l, this.getWidth() * m, nativeImage.pointer, nativeImage.getWidth(), nativeImage.getHeight(), 0, m
			);
		}
	}

	public void method_4302() {
		class_301.method_1407(this.pointer);
	}

	public static NativeImage fromBase64(String string) throws IOException {
		NativeImage var6;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.UTF8(string, false);
			ByteBuffer byteBuffer2 = Base64.getDecoder().decode(byteBuffer);
			ByteBuffer byteBuffer3 = memoryStack.malloc(byteBuffer2.remaining());
			byteBuffer3.put(byteBuffer2);
			byteBuffer3.rewind();
			var6 = fromByteBuffer(byteBuffer3);
		}

		return var6;
	}

	@Environment(EnvType.CLIENT)
	public static enum Format {
		field_4997(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
		field_5001(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
		field_5002(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
		field_4998(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

		private final int bytesPerPixel;
		private final int field_4993;
		private final boolean field_5005;
		private final boolean field_5004;
		private final boolean field_5003;
		private final boolean field_5000;
		private final boolean field_4999;
		private final int field_5010;
		private final int field_5009;
		private final int field_5008;
		private final int field_5007;
		private final int field_5006;
		private final boolean field_4996;

		private Format(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o, int p, boolean bl6) {
			this.bytesPerPixel = j;
			this.field_4993 = k;
			this.field_5005 = bl;
			this.field_5004 = bl2;
			this.field_5003 = bl3;
			this.field_5000 = bl4;
			this.field_4999 = bl5;
			this.field_5010 = l;
			this.field_5009 = m;
			this.field_5008 = n;
			this.field_5007 = o;
			this.field_5006 = p;
			this.field_4996 = bl6;
		}

		public int getBytesPerPixel() {
			return this.bytesPerPixel;
		}

		public void method_4339() {
			GlStateManager.pixelStore(3333, this.getBytesPerPixel());
		}

		public void method_4340() {
			GlStateManager.pixelStore(3317, this.getBytesPerPixel());
		}

		public int method_4333() {
			return this.field_4993;
		}

		public boolean method_4329() {
			return this.field_4999;
		}

		public int method_4332() {
			return this.field_5006;
		}

		public boolean method_4337() {
			return this.field_5000 || this.field_4999;
		}

		public int method_4330() {
			return this.field_5000 ? this.field_5007 : this.field_5006;
		}

		public boolean method_4338() {
			return this.field_4996;
		}

		private static NativeImage.Format method_4336(int i) {
			switch (i) {
				case 1:
					return field_4998;
				case 2:
					return field_5002;
				case 3:
					return field_5001;
				case 4:
				default:
					return field_4997;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1013 {
		field_5012(6408),
		field_5011(6407),
		field_5013(6410),
		field_5017(6409),
		field_5016(32841);

		private final int field_5015;

		private class_1013(int j) {
			this.field_5015 = j;
		}

		public int method_4341() {
			return this.field_5015;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1014 extends STBIWriteCallback {
		private final WritableByteChannel field_5018;
		private IOException field_5019;

		private class_1014(WritableByteChannel writableByteChannel) {
			this.field_5018 = writableByteChannel;
		}

		@Override
		public void invoke(long l, long m, int i) {
			ByteBuffer byteBuffer = getData(m, i);

			try {
				this.field_5018.write(byteBuffer);
			} catch (IOException var8) {
				this.field_5019 = var8;
			}
		}

		public void method_4342() throws IOException {
			if (this.field_5019 != null) {
				throw this.field_5019;
			}
		}
	}
}
