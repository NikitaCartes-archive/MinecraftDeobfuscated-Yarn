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
import net.minecraft.client.util.Untracker;
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
	private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(
		StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
	);
	private final NativeImage.Format format;
	private final int width;
	private final int height;
	private final boolean isStbImage;
	private long pointer;
	private final int sizeBytes;

	public NativeImage(int width, int height, boolean useStb) {
		this(NativeImage.Format.RGBA, width, height, useStb);
	}

	public NativeImage(NativeImage.Format format, int width, int height, boolean useStb) {
		this.format = format;
		this.width = width;
		this.height = height;
		this.sizeBytes = width * height * format.getChannelCount();
		this.isStbImage = false;
		if (useStb) {
			this.pointer = MemoryUtil.nmemCalloc(1L, (long)this.sizeBytes);
		} else {
			this.pointer = MemoryUtil.nmemAlloc((long)this.sizeBytes);
		}
	}

	private NativeImage(NativeImage.Format format, int width, int height, boolean useStb, long pointer) {
		this.format = format;
		this.width = width;
		this.height = height;
		this.isStbImage = useStb;
		this.pointer = pointer;
		this.sizeBytes = width * height * format.getChannelCount();
	}

	public String toString() {
		return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
	}

	public static NativeImage read(InputStream inputStream) throws IOException {
		return read(NativeImage.Format.RGBA, inputStream);
	}

	public static NativeImage read(@Nullable NativeImage.Format format, InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = null;

		NativeImage var3;
		try {
			byteBuffer = TextureUtil.readResource(inputStream);
			byteBuffer.rewind();
			var3 = read(format, byteBuffer);
		} finally {
			MemoryUtil.memFree(byteBuffer);
			IOUtils.closeQuietly(inputStream);
		}

		return var3;
	}

	public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
		return read(NativeImage.Format.RGBA, byteBuffer);
	}

	public static NativeImage read(@Nullable NativeImage.Format format, ByteBuffer byteBuffer) throws IOException {
		if (format != null && !format.isWriteable()) {
			throw new UnsupportedOperationException("Don't know how to read format " + format);
		} else if (MemoryUtil.memAddress(byteBuffer) == 0L) {
			throw new IllegalArgumentException("Invalid buffer");
		} else {
			NativeImage var8;
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, format == null ? 0 : format.channelCount);
				if (byteBuffer2 == null) {
					throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
				}

				var8 = new NativeImage(
					format == null ? NativeImage.Format.getFormat(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2)
				);
			}

			return var8;
		}
	}

	private static void setTextureClamp(boolean clamp) {
		if (clamp) {
			GlStateManager.texParameter(3553, 10242, 10496);
			GlStateManager.texParameter(3553, 10243, 10496);
		} else {
			GlStateManager.texParameter(3553, 10242, 10497);
			GlStateManager.texParameter(3553, 10243, 10497);
		}
	}

	private static void setTextureFilter(boolean blur, boolean mipmap) {
		if (blur) {
			GlStateManager.texParameter(3553, 10241, mipmap ? 9987 : 9729);
			GlStateManager.texParameter(3553, 10240, 9729);
		} else {
			GlStateManager.texParameter(3553, 10241, mipmap ? 9986 : 9728);
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
			if (this.isStbImage) {
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
		return this.format;
	}

	public int getPixelRgba(int x, int y) {
		if (this.format != NativeImage.Format.RGBA) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
		} else if (x <= this.width && y <= this.height) {
			this.checkAllocated();
			return MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).get(x + y * this.width);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		}
	}

	public void setPixelRgba(int x, int y, int color) {
		if (this.format != NativeImage.Format.RGBA) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
		} else if (x <= this.width && y <= this.height) {
			this.checkAllocated();
			MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).put(x + y * this.width, color);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		}
	}

	public byte getPixelOpacity(int x, int y) {
		if (!this.format.hasOpacityChannel()) {
			throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
		} else if (x <= this.width && y <= this.height) {
			return MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes).get((x + y * this.width) * this.format.getChannelCount() + this.format.getOpacityOffset() / 8);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		}
	}

	public void blendPixel(int x, int y, int radius) {
		if (this.format != NativeImage.Format.RGBA) {
			throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
		} else {
			int i = this.getPixelRgba(x, y);
			float f = (float)(radius >> 24 & 0xFF) / 255.0F;
			float g = (float)(radius >> 16 & 0xFF) / 255.0F;
			float h = (float)(radius >> 8 & 0xFF) / 255.0F;
			float j = (float)(radius >> 0 & 0xFF) / 255.0F;
			float k = (float)(i >> 24 & 0xFF) / 255.0F;
			float l = (float)(i >> 16 & 0xFF) / 255.0F;
			float m = (float)(i >> 8 & 0xFF) / 255.0F;
			float n = (float)(i >> 0 & 0xFF) / 255.0F;
			float p = 1.0F - f;
			float q = f * f + k * p;
			float r = g * f + l * p;
			float s = h * f + m * p;
			float t = j * f + n * p;
			if (q > 1.0F) {
				q = 1.0F;
			}

			if (r > 1.0F) {
				r = 1.0F;
			}

			if (s > 1.0F) {
				s = 1.0F;
			}

			if (t > 1.0F) {
				t = 1.0F;
			}

			int u = (int)(q * 255.0F);
			int v = (int)(r * 255.0F);
			int w = (int)(s * 255.0F);
			int z = (int)(t * 255.0F);
			this.setPixelRgba(x, y, u << 24 | v << 16 | w << 8 | z << 0);
		}
	}

	@Deprecated
	public int[] makePixelArray() {
		if (this.format != NativeImage.Format.RGBA) {
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else {
			this.checkAllocated();
			int[] is = new int[this.getWidth() * this.getHeight()];

			for (int i = 0; i < this.getHeight(); i++) {
				for (int j = 0; j < this.getWidth(); j++) {
					int k = this.getPixelRgba(j, i);
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

	public void upload(int level, int offsetX, int offsetY, boolean mipmap) {
		this.upload(level, offsetX, offsetY, 0, 0, this.width, this.height, mipmap);
	}

	public void upload(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean mipmap) {
		this.upload(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, width, height, false, false, mipmap);
	}

	public void upload(
		int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean blur, boolean clamp, boolean mipmap
	) {
		this.checkAllocated();
		setTextureFilter(blur, mipmap);
		setTextureClamp(clamp);
		if (width == this.getWidth()) {
			GlStateManager.pixelStore(3314, 0);
		} else {
			GlStateManager.pixelStore(3314, this.getWidth());
		}

		GlStateManager.pixelStore(3316, unpackSkipPixels);
		GlStateManager.pixelStore(3315, unpackSkipRows);
		this.format.setUnpackAlignment();
		GlStateManager.texSubImage2D(3553, level, xOffset, yOffset, width, height, this.format.getPixelDataFormat(), 5121, this.pointer);
	}

	public void loadFromTextureImage(int level, boolean removeAlpha) {
		this.checkAllocated();
		this.format.setPackAlignment();
		GlStateManager.getTexImage(3553, level, this.format.getPixelDataFormat(), 5121, this.pointer);
		if (removeAlpha && this.format.hasAlphaChannel()) {
			for (int i = 0; i < this.getHeight(); i++) {
				for (int j = 0; j < this.getWidth(); j++) {
					this.setPixelRgba(j, i, this.getPixelRgba(j, i) | 255 << this.format.getAlphaChannelOffset());
				}
			}
		}
	}

	public void loadFromMemory(boolean removeAlpha) {
		this.checkAllocated();
		this.format.setPackAlignment();
		if (removeAlpha) {
			GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
		}

		GlStateManager.readPixels(0, 0, this.width, this.height, this.format.getPixelDataFormat(), 5121, this.pointer);
		if (removeAlpha) {
			GlStateManager.pixelTransfer(3357, 0.0F);
		}
	}

	public void writeFile(String fileName) throws IOException {
		this.writeFile(FileSystems.getDefault().getPath(fileName));
	}

	public void writeFile(File file) throws IOException {
		this.writeFile(file.toPath());
	}

	public void makeGlyphBitmapSubpixel(
		STBTTFontinfo fontInfo, int glyphIndex, int width, int height, float scaleX, float scaleY, float shiftX, float shiftY, int startX, int startY
	) {
		if (startX < 0 || startX + width > this.getWidth() || startY < 0 || startY + height > this.getHeight()) {
			throw new IllegalArgumentException(
				String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", startX, startY, width, height, this.getWidth(), this.getHeight())
			);
		} else if (this.format.getChannelCount() != 1) {
			throw new IllegalArgumentException("Can only write fonts into 1-component images.");
		} else {
			STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(
				fontInfo.address(),
				this.pointer + (long)startX + (long)(startY * this.getWidth()),
				width,
				height,
				this.getWidth(),
				scaleX,
				scaleY,
				shiftX,
				shiftY,
				glyphIndex
			);
		}
	}

	public void writeFile(Path path) throws IOException {
		if (!this.format.isWriteable()) {
			throw new UnsupportedOperationException("Don't know how to write format " + this.format);
		} else {
			this.checkAllocated();
			WritableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS);
			Throwable var3 = null;

			try {
				NativeImage.WriteCallback writeCallback = new NativeImage.WriteCallback(writableByteChannel);

				try {
					if (!STBImageWrite.stbi_write_png_to_func(
						writeCallback, 0L, this.getWidth(), this.getHeight(), this.format.getChannelCount(), MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes), 0
					)) {
						throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
					}
				} finally {
					writeCallback.free();
				}

				writeCallback.throwStoredException();
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

	public void copyFrom(NativeImage image) {
		if (image.getFormat() != this.format) {
			throw new UnsupportedOperationException("Image formats don't match.");
		} else {
			int i = this.format.getChannelCount();
			this.checkAllocated();
			image.checkAllocated();
			if (this.width == image.width) {
				MemoryUtil.memCopy(image.pointer, this.pointer, (long)Math.min(this.sizeBytes, image.sizeBytes));
			} else {
				int j = Math.min(this.getWidth(), image.getWidth());
				int k = Math.min(this.getHeight(), image.getHeight());

				for (int l = 0; l < k; l++) {
					int m = l * image.getWidth() * i;
					int n = l * this.getWidth() * i;
					MemoryUtil.memCopy(image.pointer + (long)m, this.pointer + (long)n, (long)j);
				}
			}
		}
	}

	public void fillRect(int x, int y, int width, int height, int color) {
		for (int i = y; i < y + height; i++) {
			for (int j = x; j < x + width; j++) {
				this.setPixelRgba(j, i, color);
			}
		}
	}

	public void copyRect(int x, int y, int translateX, int translateY, int width, int height, boolean flipX, boolean flipY) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int k = flipX ? width - 1 - j : j;
				int l = flipY ? height - 1 - i : i;
				int m = this.getPixelRgba(x + j, y + i);
				this.setPixelRgba(x + translateX + k, y + translateY + l, m);
			}
		}
	}

	public void method_4319() {
		this.checkAllocated();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			int i = this.format.getChannelCount();
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

	public void resizeSubRectTo(int x, int y, int width, int height, NativeImage targetImage) {
		this.checkAllocated();
		if (targetImage.getFormat() != this.format) {
			throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
		} else {
			int i = this.format.getChannelCount();
			STBImageResize.nstbir_resize_uint8(
				this.pointer + (long)((x + y * this.getWidth()) * i),
				width,
				height,
				this.getWidth() * i,
				targetImage.pointer,
				targetImage.getWidth(),
				targetImage.getHeight(),
				0,
				i
			);
		}
	}

	public void untrack() {
		Untracker.untrack(this.pointer);
	}

	public static NativeImage read(String dataUri) throws IOException {
		NativeImage var6;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.UTF8(dataUri.replaceAll("\n", ""), false);
			ByteBuffer byteBuffer2 = Base64.getDecoder().decode(byteBuffer);
			ByteBuffer byteBuffer3 = memoryStack.malloc(byteBuffer2.remaining());
			byteBuffer3.put(byteBuffer2);
			byteBuffer3.rewind();
			var6 = read(byteBuffer3);
		}

		return var6;
	}

	@Environment(EnvType.CLIENT)
	public static enum Format {
		RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
		RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
		LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
		LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

		private final int channelCount;
		private final int pixelDataFormat;
		private final boolean hasRed;
		private final boolean hasGreen;
		private final boolean hasBlue;
		private final boolean hasLuminance;
		private final boolean hasAlpha;
		private final int redOffset;
		private final int greenOffset;
		private final int blueOffset;
		private final int luminanceChannelOffset;
		private final int alphaChannelOffset;
		private final boolean writeable;

		private Format(
			int channels,
			int glFormat,
			boolean hasRed,
			boolean hasGreen,
			boolean hasBlue,
			boolean hasLuminance,
			boolean hasAlpha,
			int redOffset,
			int greenOffset,
			int blueOffset,
			int luminanceOffset,
			int alphaOffset,
			boolean writeable
		) {
			this.channelCount = channels;
			this.pixelDataFormat = glFormat;
			this.hasRed = hasRed;
			this.hasGreen = hasGreen;
			this.hasBlue = hasBlue;
			this.hasLuminance = hasLuminance;
			this.hasAlpha = hasAlpha;
			this.redOffset = redOffset;
			this.greenOffset = greenOffset;
			this.blueOffset = blueOffset;
			this.luminanceChannelOffset = luminanceOffset;
			this.alphaChannelOffset = alphaOffset;
			this.writeable = writeable;
		}

		public int getChannelCount() {
			return this.channelCount;
		}

		public void setPackAlignment() {
			GlStateManager.pixelStore(3333, this.getChannelCount());
		}

		public void setUnpackAlignment() {
			GlStateManager.pixelStore(3317, this.getChannelCount());
		}

		public int getPixelDataFormat() {
			return this.pixelDataFormat;
		}

		public boolean hasAlphaChannel() {
			return this.hasAlpha;
		}

		public int getAlphaChannelOffset() {
			return this.alphaChannelOffset;
		}

		public boolean hasOpacityChannel() {
			return this.hasLuminance || this.hasAlpha;
		}

		public int getOpacityOffset() {
			return this.hasLuminance ? this.luminanceChannelOffset : this.alphaChannelOffset;
		}

		public boolean isWriteable() {
			return this.writeable;
		}

		private static NativeImage.Format getFormat(int glFormat) {
			switch (glFormat) {
				case 1:
					return LUMINANCE;
				case 2:
					return LUMINANCE_ALPHA;
				case 3:
					return RGB;
				case 4:
				default:
					return RGBA;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum GLFormat {
		RGBA(6408),
		RGB(6407),
		LUMINANCE_ALPHA(6410),
		LUMINANCE(6409),
		INTENSITY(32841);

		private final int glConstant;

		private GLFormat(int glConstant) {
			this.glConstant = glConstant;
		}

		public int getGlConstant() {
			return this.glConstant;
		}
	}

	@Environment(EnvType.CLIENT)
	static class WriteCallback extends STBIWriteCallback {
		private final WritableByteChannel channel;
		private IOException exception;

		private WriteCallback(WritableByteChannel channel) {
			this.channel = channel;
		}

		@Override
		public void invoke(long context, long data, int size) {
			ByteBuffer byteBuffer = getData(data, size);

			try {
				this.channel.write(byteBuffer);
			} catch (IOException var8) {
				this.exception = var8;
			}
		}

		public void throwStoredException() throws IOException {
			if (this.exception != null) {
				throw this.exception;
			}
		}
	}
}
