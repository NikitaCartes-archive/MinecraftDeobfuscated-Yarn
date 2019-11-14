package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

@Environment(EnvType.CLIENT)
public class Sprite {
	private final Identifier id;
	protected final int width;
	protected final int height;
	protected NativeImage[] images;
	@Nullable
	protected int[] frameXs;
	@Nullable
	protected int[] frameYs;
	protected NativeImage[] interpolatedImages;
	private AnimationResourceMetadata animationMetadata;
	protected int x;
	protected int y;
	private float uMin;
	private float uMax;
	private float vMin;
	private float vMax;
	protected int frameIndex;
	protected int frameTicks;
	private static final float[] SRGB_LINEAR_MAP = Util.create(new float[256], fs -> {
		for (int i = 0; i < fs.length; i++) {
			fs[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
		}
	});

	protected Sprite(Identifier id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	protected Sprite(Identifier id, PngFile sourceFile, @Nullable AnimationResourceMetadata animationData) {
		this.id = id;
		if (animationData != null) {
			Pair<Integer, Integer> pair = getDimensions(animationData.getWidth(), animationData.getHeight(), sourceFile.width, sourceFile.height);
			this.width = pair.getFirst();
			this.height = pair.getSecond();
			if (!isDivisibleBy(sourceFile.width, this.width) || !isDivisibleBy(sourceFile.height, this.height)) {
				throw new IllegalArgumentException(
					String.format("Image size %s,%s is not multiply of frame size %s,%s", this.width, this.height, sourceFile.width, sourceFile.height)
				);
			}
		} else {
			this.width = sourceFile.width;
			this.height = sourceFile.height;
		}

		this.animationMetadata = animationData;
	}

	private static Pair<Integer, Integer> getDimensions(int frameWidth, int frameHeight, int imageWidth, int imageHeight) {
		if (frameWidth != -1) {
			return frameHeight != -1 ? Pair.of(frameWidth, frameHeight) : Pair.of(frameWidth, imageHeight);
		} else if (frameHeight != -1) {
			return Pair.of(imageWidth, frameHeight);
		} else {
			int i = Math.min(imageWidth, imageHeight);
			return Pair.of(i, i);
		}
	}

	private static boolean isDivisibleBy(int number, int divisor) {
		return number / divisor * divisor == number;
	}

	private void generateMipmapsInternal(int mipLevels) {
		NativeImage[] nativeImages = new NativeImage[mipLevels + 1];
		nativeImages[0] = this.images[0];
		if (mipLevels > 0) {
			boolean bl = false;

			label71:
			for (int i = 0; i < this.images[0].getWidth(); i++) {
				for (int j = 0; j < this.images[0].getHeight(); j++) {
					if (this.images[0].getPixelRgba(i, j) >> 24 == 0) {
						bl = true;
						break label71;
					}
				}
			}

			for (int i = 1; i <= mipLevels; i++) {
				if (this.images.length > i && this.images[i] != null) {
					nativeImages[i] = this.images[i];
				} else {
					NativeImage nativeImage = nativeImages[i - 1];
					NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
					int k = nativeImage2.getWidth();
					int l = nativeImage2.getHeight();

					for (int m = 0; m < k; m++) {
						for (int n = 0; n < l; n++) {
							nativeImage2.setPixelRgba(
								m,
								n,
								blendPixels(
									nativeImage.getPixelRgba(m * 2 + 0, n * 2 + 0),
									nativeImage.getPixelRgba(m * 2 + 1, n * 2 + 0),
									nativeImage.getPixelRgba(m * 2 + 0, n * 2 + 1),
									nativeImage.getPixelRgba(m * 2 + 1, n * 2 + 1),
									bl
								)
							);
						}
					}

					nativeImages[i] = nativeImage2;
				}
			}

			for (int ix = mipLevels + 1; ix < this.images.length; ix++) {
				if (this.images[ix] != null) {
					this.images[ix].close();
				}
			}
		}

		this.images = nativeImages;
	}

	private static int blendPixels(int colorTopLeft, int colorTopRight, int colorBottomLeft, int colorBottomRight, boolean hasTransparency) {
		if (hasTransparency) {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float i = 0.0F;
			if (colorTopLeft >> 24 != 0) {
				f += srgbToLinear(colorTopLeft >> 24);
				g += srgbToLinear(colorTopLeft >> 16);
				h += srgbToLinear(colorTopLeft >> 8);
				i += srgbToLinear(colorTopLeft >> 0);
			}

			if (colorTopRight >> 24 != 0) {
				f += srgbToLinear(colorTopRight >> 24);
				g += srgbToLinear(colorTopRight >> 16);
				h += srgbToLinear(colorTopRight >> 8);
				i += srgbToLinear(colorTopRight >> 0);
			}

			if (colorBottomLeft >> 24 != 0) {
				f += srgbToLinear(colorBottomLeft >> 24);
				g += srgbToLinear(colorBottomLeft >> 16);
				h += srgbToLinear(colorBottomLeft >> 8);
				i += srgbToLinear(colorBottomLeft >> 0);
			}

			if (colorBottomRight >> 24 != 0) {
				f += srgbToLinear(colorBottomRight >> 24);
				g += srgbToLinear(colorBottomRight >> 16);
				h += srgbToLinear(colorBottomRight >> 8);
				i += srgbToLinear(colorBottomRight >> 0);
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			i /= 4.0F;
			int j = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int k = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int l = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int m = (int)(Math.pow((double)i, 0.45454545454545453) * 255.0);
			if (j < 96) {
				j = 0;
			}

			return j << 24 | k << 16 | l << 8 | m;
		} else {
			int n = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 24);
			int o = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 16);
			int p = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 8);
			int q = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 0);
			return n << 24 | o << 16 | p << 8 | q;
		}
	}

	private static int blendPixelsComponent(int colorTopLeft, int colorTopRight, int colorBottomLeft, int colorBottomRight, int componentShift) {
		float f = srgbToLinear(colorTopLeft >> componentShift);
		float g = srgbToLinear(colorTopRight >> componentShift);
		float h = srgbToLinear(colorBottomLeft >> componentShift);
		float i = srgbToLinear(colorBottomRight >> componentShift);
		float j = (float)((double)((float)Math.pow((double)(f + g + h + i) * 0.25, 0.45454545454545453)));
		return (int)((double)j * 255.0);
	}

	private static float srgbToLinear(int rgbComponentValue) {
		return SRGB_LINEAR_MAP[rgbComponentValue & 0xFF];
	}

	private void upload(int frame) {
		int i = 0;
		int j = 0;
		if (this.frameXs != null) {
			i = this.frameXs[frame] * this.width;
			j = this.frameYs[frame] * this.height;
		}

		this.upload(i, j, this.images);
	}

	private void upload(int frameX, int frameY, NativeImage[] output) {
		for (int i = 0; i < this.images.length; i++) {
			output[i].upload(i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.width >> i, this.height >> i, this.images.length > 1, false);
		}
	}

	public void init(int width, int height, int x, int y) {
		this.x = x;
		this.y = y;
		this.uMin = (float)x / (float)width;
		this.uMax = (float)(x + this.width) / (float)width;
		this.vMin = (float)y / (float)height;
		this.vMax = (float)(y + this.height) / (float)height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public float getMinU() {
		return this.uMin;
	}

	public float getMaxU() {
		return this.uMax;
	}

	public float getFrameU(double frame) {
		float f = this.uMax - this.uMin;
		return this.uMin + f * (float)frame / 16.0F;
	}

	public float getMinV() {
		return this.vMin;
	}

	public float getMaxV() {
		return this.vMax;
	}

	public float getFrameV(double frame) {
		float f = this.vMax - this.vMin;
		return this.vMin + f * (float)frame / 16.0F;
	}

	public Identifier getId() {
		return this.id;
	}

	public void tickAnimation() {
		this.frameTicks++;
		if (this.frameTicks >= this.animationMetadata.getFrameTime(this.frameIndex)) {
			int i = this.animationMetadata.getFrameIndex(this.frameIndex);
			int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
			this.frameIndex = (this.frameIndex + 1) % j;
			this.frameTicks = 0;
			int k = this.animationMetadata.getFrameIndex(this.frameIndex);
			if (i != k && k >= 0 && k < this.getFrameCount()) {
				this.upload(k);
			}
		} else if (this.animationMetadata.shouldInterpolate()) {
			if (!RenderSystem.isOnRenderThread()) {
				RenderSystem.recordRenderCall(this::interpolateFrames);
			} else {
				this.interpolateFrames();
			}
		}
	}

	private void interpolateFrames() {
		double d = 1.0 - (double)this.frameTicks / (double)this.animationMetadata.getFrameTime(this.frameIndex);
		int i = this.animationMetadata.getFrameIndex(this.frameIndex);
		int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
		int k = this.animationMetadata.getFrameIndex((this.frameIndex + 1) % j);
		if (i != k && k >= 0 && k < this.getFrameCount()) {
			if (this.interpolatedImages == null || this.interpolatedImages.length != this.images.length) {
				if (this.interpolatedImages != null) {
					for (NativeImage nativeImage : this.interpolatedImages) {
						if (nativeImage != null) {
							nativeImage.close();
						}
					}
				}

				this.interpolatedImages = new NativeImage[this.images.length];
			}

			for (int l = 0; l < this.images.length; l++) {
				int m = this.width >> l;
				int n = this.height >> l;
				if (this.interpolatedImages[l] == null) {
					this.interpolatedImages[l] = new NativeImage(m, n, false);
				}

				for (int o = 0; o < n; o++) {
					for (int p = 0; p < m; p++) {
						int q = this.getFramePixel(i, l, p, o);
						int r = this.getFramePixel(k, l, p, o);
						int s = this.lerp(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
						int t = this.lerp(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
						int u = this.lerp(d, q & 0xFF, r & 0xFF);
						this.interpolatedImages[l].setPixelRgba(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
					}
				}
			}

			this.upload(0, 0, this.interpolatedImages);
		}
	}

	private int lerp(double multiplier, int first, int second) {
		return (int)(multiplier * (double)first + (1.0 - multiplier) * (double)second);
	}

	public int getFrameCount() {
		return this.frameXs == null ? 0 : this.frameXs.length;
	}

	public void load(Resource resource, int mipLevel) throws IOException {
		NativeImage nativeImage = NativeImage.read(resource.getInputStream());
		this.images = new NativeImage[mipLevel];
		this.images[0] = nativeImage;
		int i;
		if (this.animationMetadata != null && this.animationMetadata.getWidth() != -1) {
			i = nativeImage.getWidth() / this.animationMetadata.getWidth();
		} else {
			i = nativeImage.getWidth() / this.width;
		}

		int j;
		if (this.animationMetadata != null && this.animationMetadata.getHeight() != -1) {
			j = nativeImage.getHeight() / this.animationMetadata.getHeight();
		} else {
			j = nativeImage.getHeight() / this.height;
		}

		if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
			int k = (Integer)this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
			this.frameXs = new int[k];
			this.frameYs = new int[k];
			Arrays.fill(this.frameXs, -1);
			Arrays.fill(this.frameYs, -1);

			for (int l : this.animationMetadata.getFrameIndexSet()) {
				if (l >= i * j) {
					throw new RuntimeException("invalid frameindex " + l);
				}

				int m = l / i;
				int n = l % i;
				this.frameXs[l] = n;
				this.frameYs[l] = m;
			}
		} else {
			List<AnimationFrameResourceMetadata> list = Lists.<AnimationFrameResourceMetadata>newArrayList();
			int o = i * j;
			this.frameXs = new int[o];
			this.frameYs = new int[o];

			for (int l = 0; l < j; l++) {
				for (int m = 0; m < i; m++) {
					int n = l * i + m;
					this.frameXs[n] = m;
					this.frameYs[n] = l;
					list.add(new AnimationFrameResourceMetadata(n, -1));
				}
			}

			int l = 1;
			boolean bl = false;
			if (this.animationMetadata != null) {
				l = this.animationMetadata.getDefaultFrameTime();
				bl = this.animationMetadata.shouldInterpolate();
			}

			this.animationMetadata = new AnimationResourceMetadata(list, this.width, this.height, l, bl);
		}
	}

	public void generateMipmaps(int mipLevel) {
		try {
			this.generateMipmapsInternal(mipLevel);
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Generating mipmaps for frame");
			CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
			crashReportSection.add("Frame sizes", (CrashCallable<String>)(() -> {
				StringBuilder stringBuilder = new StringBuilder();

				for (NativeImage nativeImage : this.images) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(nativeImage == null ? "null" : nativeImage.getWidth() + "x" + nativeImage.getHeight());
				}

				return stringBuilder.toString();
			}));
			throw new CrashException(crashReport);
		}
	}

	public void destroy() {
		if (this.images != null) {
			for (NativeImage nativeImage : this.images) {
				if (nativeImage != null) {
					nativeImage.close();
				}
			}
		}

		this.images = null;
		if (this.interpolatedImages != null) {
			for (NativeImage nativeImagex : this.interpolatedImages) {
				if (nativeImagex != null) {
					nativeImagex.close();
				}
			}
		}

		this.interpolatedImages = null;
	}

	public boolean isAnimated() {
		return this.animationMetadata != null && this.animationMetadata.getFrameCount() > 1;
	}

	public String toString() {
		int i = this.frameXs == null ? 0 : this.frameXs.length;
		return "TextureAtlasSprite{name='"
			+ this.id
			+ '\''
			+ ", frameCount="
			+ i
			+ ", x="
			+ this.x
			+ ", y="
			+ this.y
			+ ", height="
			+ this.height
			+ ", width="
			+ this.width
			+ ", u0="
			+ this.uMin
			+ ", u1="
			+ this.uMax
			+ ", v0="
			+ this.vMin
			+ ", v1="
			+ this.vMax
			+ '}';
	}

	private int getFramePixel(int frame, int image, int x, int y) {
		return this.images[image].getPixelRgba(x + (this.frameXs[frame] * this.width >> image), y + (this.frameYs[frame] * this.height >> image));
	}

	public boolean isPixelTransparent(int frame, int x, int y) {
		return (this.images[0].getPixelRgba(x + this.frameXs[frame] * this.width, y + this.frameYs[frame] * this.height) >> 24 & 0xFF) == 0;
	}

	public void upload() {
		this.upload(0);
	}

	private float getFrameDeltaFactor() {
		float f = (float)this.width / (this.uMax - this.uMin);
		float g = (float)this.height / (this.vMax - this.vMin);
		return Math.max(g, f);
	}

	public float getAnimationFrameDelta() {
		return 4.0F / this.getFrameDeltaFactor();
	}
}
