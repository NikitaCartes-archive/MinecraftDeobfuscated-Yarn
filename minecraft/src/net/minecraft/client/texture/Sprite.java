package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1050;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;

@Environment(EnvType.CLIENT)
public class Sprite {
	private final Identifier id;
	protected final int width;
	protected final int height;
	protected NativeImage[] images;
	@Nullable
	protected int[] field_5265;
	@Nullable
	protected int[] field_5264;
	protected NativeImage[] interpolatedImages;
	private AnimationResourceMetadata animationMetadata;
	protected boolean rotated;
	protected int x;
	protected int y;
	private float uMin;
	private float uMax;
	private float vMin;
	private float vMax;
	protected int framePos;
	protected int ticks;
	private static final int[] blendedPixelCache = new int[4];
	private static final float[] srgbLinearMap = SystemUtil.consume(new float[256], fs -> {
		for (int i = 0; i < fs.length; i++) {
			fs[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
		}
	});

	protected Sprite(Identifier identifier, int i, int j) {
		this.id = identifier;
		this.width = i;
		this.height = j;
	}

	protected Sprite(Identifier identifier, class_1050 arg, @Nullable AnimationResourceMetadata animationResourceMetadata) {
		this.id = identifier;
		if (animationResourceMetadata != null) {
			int i = Math.min(arg.width, arg.height);
			this.height = this.width = i;
		} else {
			if (arg.height != arg.width) {
				throw new RuntimeException("broken aspect ratio and not an animation");
			}

			this.width = arg.width;
			this.height = arg.height;
		}

		this.animationMetadata = animationResourceMetadata;
	}

	private void generateMipmapsInternal(int i) {
		NativeImage[] nativeImages = new NativeImage[i + 1];
		nativeImages[0] = this.images[0];
		if (i > 0) {
			boolean bl = false;

			label71:
			for (int j = 0; j < this.images[0].getWidth(); j++) {
				for (int k = 0; k < this.images[0].getHeight(); k++) {
					if (this.images[0].getPixelRGBA(j, k) >> 24 == 0) {
						bl = true;
						break label71;
					}
				}
			}

			for (int j = 1; j <= i; j++) {
				if (this.images.length > j && this.images[j] != null) {
					nativeImages[j] = this.images[j];
				} else {
					NativeImage nativeImage = nativeImages[j - 1];
					NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
					int l = nativeImage2.getWidth();
					int m = nativeImage2.getHeight();

					for (int n = 0; n < l; n++) {
						for (int o = 0; o < m; o++) {
							nativeImage2.setPixelRGBA(
								n,
								o,
								blendPixels(
									nativeImage.getPixelRGBA(n * 2 + 0, o * 2 + 0),
									nativeImage.getPixelRGBA(n * 2 + 1, o * 2 + 0),
									nativeImage.getPixelRGBA(n * 2 + 0, o * 2 + 1),
									nativeImage.getPixelRGBA(n * 2 + 1, o * 2 + 1),
									bl
								)
							);
						}
					}

					nativeImages[j] = nativeImage2;
				}
			}

			for (int jx = i + 1; jx < this.images.length; jx++) {
				if (this.images[jx] != null) {
					this.images[jx].close();
				}
			}
		}

		this.images = nativeImages;
	}

	private static int blendPixels(int i, int j, int k, int l, boolean bl) {
		if (bl) {
			blendedPixelCache[0] = i;
			blendedPixelCache[1] = j;
			blendedPixelCache[2] = k;
			blendedPixelCache[3] = l;
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float m = 0.0F;

			for (int n = 0; n < 4; n++) {
				if (blendedPixelCache[n] >> 24 != 0) {
					f += srgbToLinear(blendedPixelCache[n] >> 24);
					g += srgbToLinear(blendedPixelCache[n] >> 16);
					h += srgbToLinear(blendedPixelCache[n] >> 8);
					m += srgbToLinear(blendedPixelCache[n] >> 0);
				}
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			m /= 4.0F;
			int nx = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int o = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int p = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int q = (int)(Math.pow((double)m, 0.45454545454545453) * 255.0);
			if (nx < 96) {
				nx = 0;
			}

			return nx << 24 | o << 16 | p << 8 | q;
		} else {
			int r = blendPixelsComponent(i, j, k, l, 24);
			int s = blendPixelsComponent(i, j, k, l, 16);
			int t = blendPixelsComponent(i, j, k, l, 8);
			int u = blendPixelsComponent(i, j, k, l, 0);
			return r << 24 | s << 16 | t << 8 | u;
		}
	}

	private static int blendPixelsComponent(int i, int j, int k, int l, int m) {
		float f = srgbToLinear(i >> m);
		float g = srgbToLinear(j >> m);
		float h = srgbToLinear(k >> m);
		float n = srgbToLinear(l >> m);
		float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
		return (int)((double)o * 255.0);
	}

	private static float srgbToLinear(int i) {
		return srgbLinearMap[i & 0xFF];
	}

	private void method_4573(int i) {
		int j = 0;
		int k = 0;
		if (this.field_5265 != null) {
			j = this.field_5265[i] * this.width;
			k = this.field_5264[i] * this.height;
		}

		this.upload(j, k, this.images);
	}

	private void upload(int i, int j, NativeImage[] nativeImages) {
		for (int k = 0; k < this.images.length; k++) {
			nativeImages[k].upload(k, this.x >> k, this.y >> k, i >> k, j >> k, this.width >> k, this.height >> k, this.images.length > 1);
		}
	}

	public void init(int i, int j, int k, int l, boolean bl) {
		this.x = k;
		this.y = l;
		this.rotated = bl;
		this.uMin = (float)k / (float)i;
		this.uMax = (float)(k + this.width) / (float)i;
		this.vMin = (float)l / (float)j;
		this.vMax = (float)(l + this.height) / (float)j;
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

	public float getU(double d) {
		float f = this.uMax - this.uMin;
		return this.uMin + f * (float)d / 16.0F;
	}

	public float getXFromU(float f) {
		float g = this.uMax - this.uMin;
		return (f - this.uMin) / g * 16.0F;
	}

	public float getMinV() {
		return this.vMin;
	}

	public float getMaxV() {
		return this.vMax;
	}

	public float getV(double d) {
		float f = this.vMax - this.vMin;
		return this.vMin + f * (float)d / 16.0F;
	}

	public float getYFromV(float f) {
		float g = this.vMax - this.vMin;
		return (f - this.vMin) / g * 16.0F;
	}

	public Identifier getId() {
		return this.id;
	}

	public void tick() {
		this.ticks++;
		if (this.ticks >= this.animationMetadata.getFrameTime(this.framePos)) {
			int i = this.animationMetadata.getFrameIndex(this.framePos);
			int j = this.animationMetadata.getFrameCount() == 0 ? this.method_4592() : this.animationMetadata.getFrameCount();
			this.framePos = (this.framePos + 1) % j;
			this.ticks = 0;
			int k = this.animationMetadata.getFrameIndex(this.framePos);
			if (i != k && k >= 0 && k < this.method_4592()) {
				this.method_4573(k);
			}
		} else if (this.animationMetadata.shouldInterpolate()) {
			this.interpolateFrames();
		}
	}

	private void interpolateFrames() {
		double d = 1.0 - (double)this.ticks / (double)this.animationMetadata.getFrameTime(this.framePos);
		int i = this.animationMetadata.getFrameIndex(this.framePos);
		int j = this.animationMetadata.getFrameCount() == 0 ? this.method_4592() : this.animationMetadata.getFrameCount();
		int k = this.animationMetadata.getFrameIndex((this.framePos + 1) % j);
		if (i != k && k >= 0 && k < this.method_4592()) {
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
						int q = this.method_4589(i, l, p, o);
						int r = this.method_4589(k, l, p, o);
						int s = this.lerp(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
						int t = this.lerp(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
						int u = this.lerp(d, q & 0xFF, r & 0xFF);
						this.interpolatedImages[l].setPixelRGBA(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
					}
				}
			}

			this.upload(0, 0, this.interpolatedImages);
		}
	}

	private int lerp(double d, int i, int j) {
		return (int)(d * (double)i + (1.0 - d) * (double)j);
	}

	public int method_4592() {
		return this.field_5265 == null ? 0 : this.field_5265.length;
	}

	public void load(Resource resource, int i) throws IOException {
		NativeImage nativeImage = NativeImage.fromInputStream(resource.getInputStream());
		this.images = new NativeImage[i];
		this.images[0] = nativeImage;
		int j;
		if (this.animationMetadata != null && this.animationMetadata.getWidth() != -1) {
			j = nativeImage.getWidth() / this.animationMetadata.getWidth();
		} else {
			j = nativeImage.getWidth() / this.width;
		}

		int k;
		if (this.animationMetadata != null && this.animationMetadata.getHeight() != -1) {
			k = nativeImage.getHeight() / this.animationMetadata.getHeight();
		} else {
			k = nativeImage.getHeight() / this.height;
		}

		if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
			int l = (Integer)this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
			this.field_5265 = new int[l];
			this.field_5264 = new int[l];
			Arrays.fill(this.field_5265, -1);
			Arrays.fill(this.field_5264, -1);

			for (int m : this.animationMetadata.getFrameIndexSet()) {
				if (m >= j * k) {
					throw new RuntimeException("invalid frameindex " + m);
				}

				int n = m / j;
				int o = m % j;
				this.field_5265[m] = o;
				this.field_5264[m] = n;
			}
		} else {
			List<AnimationFrameResourceMetadata> list = Lists.<AnimationFrameResourceMetadata>newArrayList();
			int p = j * k;
			this.field_5265 = new int[p];
			this.field_5264 = new int[p];

			for (int m = 0; m < k; m++) {
				for (int n = 0; n < j; n++) {
					int o = m * j + n;
					this.field_5265[o] = n;
					this.field_5264[o] = m;
					list.add(new AnimationFrameResourceMetadata(o, -1));
				}
			}

			int m = 1;
			boolean bl = false;
			if (this.animationMetadata != null) {
				m = this.animationMetadata.getDefaultFrameTime();
				bl = this.animationMetadata.shouldInterpolate();
			}

			this.animationMetadata = new AnimationResourceMetadata(list, this.width, this.height, m, bl);
		}
	}

	public void generateMipmaps(int i) {
		try {
			this.generateMipmapsInternal(i);
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Generating mipmaps for frame");
			CrashReportSection crashReportSection = crashReport.method_562("Frame being iterated");
			crashReportSection.add("Frame sizes", (ICrashCallable<String>)(() -> {
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
		int i = this.field_5265 == null ? 0 : this.field_5265.length;
		return "TextureAtlasSprite{name='"
			+ this.id
			+ '\''
			+ ", frameCount="
			+ i
			+ ", rotated="
			+ this.rotated
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

	private int method_4589(int i, int j, int k, int l) {
		return this.images[j].getPixelRGBA(k + (this.field_5265[i] * this.width >> j), l + (this.field_5264[i] * this.height >> j));
	}

	public boolean method_4583(int i, int j, int k) {
		return (this.images[0].getPixelRGBA(j + this.field_5265[i] * this.width, k + this.field_5264[i] * this.height) >> 24 & 0xFF) == 0;
	}

	public void method_4584() {
		this.method_4573(0);
	}
}
