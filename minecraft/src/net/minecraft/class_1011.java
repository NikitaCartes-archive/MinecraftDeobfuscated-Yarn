package net.minecraft;

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
public final class class_1011 implements AutoCloseable {
	private static final Set<StandardOpenOption> field_4992 = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	private final class_1011.class_1012 field_4986;
	private final int field_4991;
	private final int field_4989;
	private final boolean field_4990;
	private long field_4988;
	private final int field_4987;

	public class_1011(int i, int j, boolean bl) {
		this(class_1011.class_1012.field_4997, i, j, bl);
	}

	public class_1011(class_1011.class_1012 arg, int i, int j, boolean bl) {
		this.field_4986 = arg;
		this.field_4991 = i;
		this.field_4989 = j;
		this.field_4987 = i * j * arg.method_4335();
		this.field_4990 = false;
		if (bl) {
			this.field_4988 = MemoryUtil.nmemCalloc(1L, (long)this.field_4987);
		} else {
			this.field_4988 = MemoryUtil.nmemAlloc((long)this.field_4987);
		}
	}

	private class_1011(class_1011.class_1012 arg, int i, int j, boolean bl, long l) {
		this.field_4986 = arg;
		this.field_4991 = i;
		this.field_4989 = j;
		this.field_4990 = bl;
		this.field_4988 = l;
		this.field_4987 = i * j * arg.method_4335();
	}

	public String toString() {
		return "NativeImage[" + this.field_4986 + " " + this.field_4991 + "x" + this.field_4989 + "@" + this.field_4988 + (this.field_4990 ? "S" : "N") + "]";
	}

	public static class_1011 method_4309(InputStream inputStream) throws IOException {
		return method_4310(class_1011.class_1012.field_4997, inputStream);
	}

	public static class_1011 method_4310(@Nullable class_1011.class_1012 arg, InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = null;

		class_1011 var3;
		try {
			byteBuffer = TextureUtil.readResource(inputStream);
			byteBuffer.rewind();
			var3 = method_4303(arg, byteBuffer);
		} finally {
			MemoryUtil.memFree(byteBuffer);
			IOUtils.closeQuietly(inputStream);
		}

		return var3;
	}

	public static class_1011 method_4324(ByteBuffer byteBuffer) throws IOException {
		return method_4303(class_1011.class_1012.field_4997, byteBuffer);
	}

	public static class_1011 method_4303(@Nullable class_1011.class_1012 arg, ByteBuffer byteBuffer) throws IOException {
		if (arg != null && !arg.method_4338()) {
			throw new UnsupportedOperationException("Don't know how to read format " + arg);
		} else if (MemoryUtil.memAddress(byteBuffer) == 0L) {
			throw new IllegalArgumentException("Invalid buffer");
		} else {
			class_1011 var8;
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, arg == null ? 0 : arg.field_4994);
				if (byteBuffer2 == null) {
					throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
				}

				var8 = new class_1011(
					arg == null ? class_1011.class_1012.method_4336(intBuffer3.get(0)) : arg, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2)
				);
			}

			return var8;
		}
	}

	private static void method_4313(boolean bl) {
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

	private void method_4320() {
		if (this.field_4988 == 0L) {
			throw new IllegalStateException("Image is not allocated.");
		}
	}

	public void close() {
		if (this.field_4988 != 0L) {
			if (this.field_4990) {
				STBImage.nstbi_image_free(this.field_4988);
			} else {
				MemoryUtil.nmemFree(this.field_4988);
			}
		}

		this.field_4988 = 0L;
	}

	public int method_4307() {
		return this.field_4991;
	}

	public int method_4323() {
		return this.field_4989;
	}

	public class_1011.class_1012 method_4318() {
		return this.field_4986;
	}

	public int method_4315(int i, int j) {
		if (this.field_4986 != class_1011.class_1012.field_4997) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.field_4986));
		} else if (i <= this.field_4991 && j <= this.field_4989) {
			this.method_4320();
			return MemoryUtil.memIntBuffer(this.field_4988, this.field_4987).get(i + j * this.field_4991);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.field_4991, this.field_4989));
		}
	}

	public void method_4305(int i, int j, int k) {
		if (this.field_4986 != class_1011.class_1012.field_4997) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.field_4986));
		} else if (i <= this.field_4991 && j <= this.field_4989) {
			this.method_4320();
			MemoryUtil.memIntBuffer(this.field_4988, this.field_4987).put(i + j * this.field_4991, k);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.field_4991, this.field_4989));
		}
	}

	public byte method_4311(int i, int j) {
		if (!this.field_4986.method_4337()) {
			throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.field_4986));
		} else if (i <= this.field_4991 && j <= this.field_4989) {
			return MemoryUtil.memByteBuffer(this.field_4988, this.field_4987)
				.get((i + j * this.field_4991) * this.field_4986.method_4335() + this.field_4986.method_4330() / 8);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.field_4991, this.field_4989));
		}
	}

	public void method_4328(int i, int j, int k) {
		if (this.field_4986 != class_1011.class_1012.field_4997) {
			throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
		} else {
			int l = this.method_4315(i, j);
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
			this.method_4305(i, j, x << 24 | y << 16 | z << 8 | aa << 0);
		}
	}

	@Deprecated
	public int[] method_4322() {
		if (this.field_4986 != class_1011.class_1012.field_4997) {
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else {
			this.method_4320();
			int[] is = new int[this.method_4307() * this.method_4323()];

			for (int i = 0; i < this.method_4323(); i++) {
				for (int j = 0; j < this.method_4307(); j++) {
					int k = this.method_4315(j, i);
					int l = k >> 24 & 0xFF;
					int m = k >> 16 & 0xFF;
					int n = k >> 8 & 0xFF;
					int o = k >> 0 & 0xFF;
					int p = l << 24 | o << 16 | n << 8 | m;
					is[j + i * this.method_4307()] = p;
				}
			}

			return is;
		}
	}

	public void method_4301(int i, int j, int k, boolean bl) {
		this.method_4312(i, j, k, 0, 0, this.field_4991, this.field_4989, bl);
	}

	public void method_4312(int i, int j, int k, int l, int m, int n, int o, boolean bl) {
		this.method_4321(i, j, k, l, m, n, o, false, false, bl);
	}

	public void method_4321(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3) {
		this.method_4320();
		method_4308(bl, bl3);
		method_4313(bl2);
		if (n == this.method_4307()) {
			GlStateManager.pixelStore(3314, 0);
		} else {
			GlStateManager.pixelStore(3314, this.method_4307());
		}

		GlStateManager.pixelStore(3316, l);
		GlStateManager.pixelStore(3315, m);
		this.field_4986.method_4340();
		GlStateManager.texSubImage2D(3553, i, j, k, n, o, this.field_4986.method_4333(), 5121, this.field_4988);
	}

	public void method_4327(int i, boolean bl) {
		this.method_4320();
		this.field_4986.method_4339();
		GlStateManager.getTexImage(3553, i, this.field_4986.method_4333(), 5121, this.field_4988);
		if (bl && this.field_4986.method_4329()) {
			for (int j = 0; j < this.method_4323(); j++) {
				for (int k = 0; k < this.method_4307(); k++) {
					this.method_4305(k, j, this.method_4315(k, j) | 255 << this.field_4986.method_4332());
				}
			}
		}
	}

	public void method_4306(boolean bl) {
		this.method_4320();
		this.field_4986.method_4339();
		if (bl) {
			GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
		}

		GlStateManager.readPixels(0, 0, this.field_4991, this.field_4989, this.field_4986.method_4333(), 5121, this.field_4988);
		if (bl) {
			GlStateManager.pixelTransfer(3357, 0.0F);
		}
	}

	public void method_15877(String string) throws IOException {
		this.method_4314(FileSystems.getDefault().getPath(string));
	}

	public void method_4325(File file) throws IOException {
		this.method_4314(file.toPath());
	}

	public void method_4316(STBTTFontinfo sTBTTFontinfo, int i, int j, int k, float f, float g, float h, float l, int m, int n) {
		if (m < 0 || m + j > this.method_4307() || n < 0 || n + k > this.method_4323()) {
			throw new IllegalArgumentException(
				String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", m, n, j, k, this.method_4307(), this.method_4323())
			);
		} else if (this.field_4986.method_4335() != 1) {
			throw new IllegalArgumentException("Can only write fonts into 1-component images.");
		} else {
			STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(
				sTBTTFontinfo.address(), this.field_4988 + (long)m + (long)(n * this.method_4307()), j, k, this.method_4307(), f, g, h, l, i
			);
		}
	}

	public void method_4314(Path path) throws IOException {
		if (!this.field_4986.method_4338()) {
			throw new UnsupportedOperationException("Don't know how to write format " + this.field_4986);
		} else {
			this.method_4320();
			WritableByteChannel writableByteChannel = Files.newByteChannel(path, field_4992);
			Throwable var3 = null;

			try {
				class_1011.class_1014 lv = new class_1011.class_1014(writableByteChannel);

				try {
					if (!STBImageWrite.stbi_write_png_to_func(
						lv, 0L, this.method_4307(), this.method_4323(), this.field_4986.method_4335(), MemoryUtil.memByteBuffer(this.field_4988, this.field_4987), 0
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

	public void method_4317(class_1011 arg) {
		if (arg.method_4318() != this.field_4986) {
			throw new UnsupportedOperationException("Image formats don't match.");
		} else {
			int i = this.field_4986.method_4335();
			this.method_4320();
			arg.method_4320();
			if (this.field_4991 == arg.field_4991) {
				MemoryUtil.memCopy(arg.field_4988, this.field_4988, (long)Math.min(this.field_4987, arg.field_4987));
			} else {
				int j = Math.min(this.method_4307(), arg.method_4307());
				int k = Math.min(this.method_4323(), arg.method_4323());

				for (int l = 0; l < k; l++) {
					int m = l * arg.method_4307() * i;
					int n = l * this.method_4307() * i;
					MemoryUtil.memCopy(arg.field_4988 + (long)m, this.field_4988 + (long)n, (long)j);
				}
			}
		}
	}

	public void method_4326(int i, int j, int k, int l, int m) {
		for (int n = j; n < j + l; n++) {
			for (int o = i; o < i + k; o++) {
				this.method_4305(o, n, m);
			}
		}
	}

	public void method_4304(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
		for (int o = 0; o < n; o++) {
			for (int p = 0; p < m; p++) {
				int q = bl ? m - 1 - p : p;
				int r = bl2 ? n - 1 - o : o;
				int s = this.method_4315(i + p, j + o);
				this.method_4305(i + k + q, j + l + r, s);
			}
		}
	}

	public void method_4319() {
		this.method_4320();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			int i = this.field_4986.method_4335();
			int j = this.method_4307() * i;
			long l = memoryStack.nmalloc(j);

			for (int k = 0; k < this.method_4323() / 2; k++) {
				int m = k * this.method_4307() * i;
				int n = (this.method_4323() - 1 - k) * this.method_4307() * i;
				MemoryUtil.memCopy(this.field_4988 + (long)m, l, (long)j);
				MemoryUtil.memCopy(this.field_4988 + (long)n, this.field_4988 + (long)m, (long)j);
				MemoryUtil.memCopy(l, this.field_4988 + (long)n, (long)j);
			}
		}
	}

	public void method_4300(int i, int j, int k, int l, class_1011 arg) {
		this.method_4320();
		if (arg.method_4318() != this.field_4986) {
			throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
		} else {
			int m = this.field_4986.method_4335();
			STBImageResize.nstbir_resize_uint8(
				this.field_4988 + (long)((i + j * this.method_4307()) * m), k, l, this.method_4307() * m, arg.field_4988, arg.method_4307(), arg.method_4323(), 0, m
			);
		}
	}

	public void method_4302() {
		class_301.method_1407(this.field_4988);
	}

	public static class_1011 method_15990(String string) throws IOException {
		class_1011 var6;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.UTF8(string, false);
			ByteBuffer byteBuffer2 = Base64.getDecoder().decode(byteBuffer);
			ByteBuffer byteBuffer3 = memoryStack.malloc(byteBuffer2.remaining());
			byteBuffer3.put(byteBuffer2);
			byteBuffer3.rewind();
			var6 = method_4324(byteBuffer3);
		}

		return var6;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1012 {
		field_4997(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
		field_5001(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
		field_5002(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
		field_4998(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

		private final int field_4994;
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

		private class_1012(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o, int p, boolean bl6) {
			this.field_4994 = j;
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

		public int method_4335() {
			return this.field_4994;
		}

		public void method_4339() {
			GlStateManager.pixelStore(3333, this.method_4335());
		}

		public void method_4340() {
			GlStateManager.pixelStore(3317, this.method_4335());
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

		private static class_1011.class_1012 method_4336(int i) {
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
