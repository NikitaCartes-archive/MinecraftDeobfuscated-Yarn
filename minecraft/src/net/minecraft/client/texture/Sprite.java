package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Sprite implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final SpriteAtlasTexture atlas;
	private final Identifier id;
	private final int width;
	private final int height;
	protected final NativeImage[] images;
	@Nullable
	private final Sprite.class_5790 field_28468;
	private final int x;
	private final int y;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;

	protected Sprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
		this.atlas = spriteAtlasTexture;
		this.width = info.width;
		this.height = info.height;
		this.id = info.id;
		this.x = x;
		this.y = y;
		this.uMin = (float)x / (float)atlasWidth;
		this.uMax = (float)(x + this.width) / (float)atlasWidth;
		this.vMin = (float)y / (float)atlasHeight;
		this.vMax = (float)(y + this.height) / (float)atlasHeight;
		this.field_28468 = this.method_33437(info, nativeImage.getWidth(), nativeImage.getHeight(), maxLevel);

		try {
			try {
				this.images = MipmapHelper.getMipmapLevelsImages(nativeImage, maxLevel);
			} catch (Throwable var12) {
				CrashReport crashReport = CrashReport.create(var12, "Generating mipmaps for frame");
				CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
				crashReportSection.add("First frame", (CrashCallable<String>)(() -> {
					StringBuilder stringBuilder = new StringBuilder();
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(nativeImage.getWidth()).append("x").append(nativeImage.getHeight());
					return stringBuilder.toString();
				}));
				throw new CrashException(crashReport);
			}
		} catch (Throwable var13) {
			CrashReport crashReport = CrashReport.create(var13, "Applying mipmap");
			CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
			crashReportSection.add("Sprite name", this.id::toString);
			crashReportSection.add("Sprite size", (CrashCallable<String>)(() -> this.width + " x " + this.height));
			crashReportSection.add("Sprite frames", (CrashCallable<String>)(() -> this.getFrameCount() + " frames"));
			crashReportSection.add("Mipmap levels", maxLevel);
			throw new CrashException(crashReport);
		}
	}

	private int getFrameCount() {
		return this.field_28468 != null ? this.field_28468.field_28472.size() : 1;
	}

	@Nullable
	private Sprite.class_5790 method_33437(Sprite.Info info, int i, int j, int k) {
		AnimationResourceMetadata animationResourceMetadata = info.animationData;
		int l = i / animationResourceMetadata.getWidth(info.width);
		int m = j / animationResourceMetadata.getHeight(info.height);
		int n = l * m;
		List<Sprite.class_5791> list = Lists.<Sprite.class_5791>newArrayList();
		animationResourceMetadata.method_33460((ix, jx) -> list.add(new Sprite.class_5791(ix, jx)));
		if (list.isEmpty()) {
			for (int o = 0; o < n; o++) {
				list.add(new Sprite.class_5791(o, animationResourceMetadata.getDefaultFrameTime()));
			}
		} else {
			int o = 0;
			IntSet intSet = new IntOpenHashSet();

			for (Iterator<Sprite.class_5791> iterator = list.iterator(); iterator.hasNext(); o++) {
				Sprite.class_5791 lv = (Sprite.class_5791)iterator.next();
				boolean bl = true;
				if (lv.field_28476 <= 0) {
					LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", this.id, o, lv.field_28476);
					bl = false;
				}

				if (lv.field_28475 < 0 || lv.field_28475 >= n) {
					LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", this.id, o, lv.field_28475);
					bl = false;
				}

				if (bl) {
					intSet.add(lv.field_28475);
				} else {
					iterator.remove();
				}
			}

			int[] is = IntStream.range(0, n).filter(ix -> !intSet.contains(ix)).toArray();
			if (is.length > 0) {
				LOGGER.warn("Unused frames in sprite {}: {}", this.id, Arrays.toString(is));
			}
		}

		if (list.size() <= 1) {
			return null;
		} else {
			Sprite.Interpolation interpolation = animationResourceMetadata.shouldInterpolate() ? new Sprite.Interpolation(info, k) : null;
			return new Sprite.class_5790(ImmutableList.copyOf(list), l, interpolation);
		}
	}

	private void upload(int frameX, int frameY, NativeImage[] output) {
		for (int i = 0; i < this.images.length; i++) {
			output[i].upload(i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.width >> i, this.height >> i, this.images.length > 1, false);
		}
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

	public SpriteAtlasTexture getAtlas() {
		return this.atlas;
	}

	public IntStream method_33442() {
		return this.field_28468 != null ? this.field_28468.method_33450() : IntStream.of(1);
	}

	public void close() {
		for (NativeImage nativeImage : this.images) {
			if (nativeImage != null) {
				nativeImage.close();
			}
		}

		if (this.field_28468 != null) {
			this.field_28468.close();
		}
	}

	public String toString() {
		return "TextureAtlasSprite{name='"
			+ this.id
			+ '\''
			+ ", frameCount="
			+ this.getFrameCount()
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

	public boolean isPixelTransparent(int frame, int x, int y) {
		int i = x;
		int j = y;
		if (this.field_28468 != null) {
			i = x + this.field_28468.method_33446(frame) * this.width;
			j = y + this.field_28468.method_33451(frame) * this.height;
		}

		return (this.images[0].getPixelColor(i, j) >> 24 & 0xFF) == 0;
	}

	public void upload() {
		if (this.field_28468 != null) {
			this.field_28468.method_33445();
		} else {
			this.upload(0, 0, this.images);
		}
	}

	private float getFrameDeltaFactor() {
		float f = (float)this.width / (this.uMax - this.uMin);
		float g = (float)this.height / (this.vMax - this.vMin);
		return Math.max(g, f);
	}

	public float getAnimationFrameDelta() {
		return 4.0F / this.getFrameDeltaFactor();
	}

	@Nullable
	public TextureTickListener method_33443() {
		return this.field_28468;
	}

	public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer vertexConsumer) {
		return new SpriteTexturedVertexConsumer(vertexConsumer, this);
	}

	@Environment(EnvType.CLIENT)
	public static final class Info {
		private final Identifier id;
		private final int width;
		private final int height;
		private final AnimationResourceMetadata animationData;

		public Info(Identifier id, int width, int height, AnimationResourceMetadata animationData) {
			this.id = id;
			this.width = width;
			this.height = height;
			this.animationData = animationData;
		}

		public Identifier getId() {
			return this.id;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}
	}

	@Environment(EnvType.CLIENT)
	final class Interpolation implements AutoCloseable {
		private final NativeImage[] images;

		private Interpolation(Sprite.Info info, int mipmap) {
			this.images = new NativeImage[mipmap + 1];

			for (int i = 0; i < this.images.length; i++) {
				int j = info.width >> i;
				int k = info.height >> i;
				if (this.images[i] == null) {
					this.images[i] = new NativeImage(j, k, false);
				}
			}
		}

		/**
		 * Linearly interpolate between the current and next frame on all mip levels
		 * based on the tick position within the current frame,
		 * and upload the results to the currently bound texture to the frame slot at position (0,0).
		 */
		private void apply(Sprite.class_5790 arg) {
			Sprite.class_5791 lv = (Sprite.class_5791)arg.field_28472.get(arg.field_28470);
			double d = 1.0 - (double)arg.field_28471 / (double)lv.field_28476;
			int i = lv.field_28475;
			int j = ((Sprite.class_5791)arg.field_28472.get((arg.field_28470 + 1) % arg.field_28472.size())).field_28475;
			if (i != j) {
				for (int k = 0; k < this.images.length; k++) {
					int l = Sprite.this.width >> k;
					int m = Sprite.this.height >> k;

					for (int n = 0; n < m; n++) {
						for (int o = 0; o < l; o++) {
							int p = this.getPixelColor(arg, i, k, o, n);
							int q = this.getPixelColor(arg, j, k, o, n);
							int r = this.lerp(d, p >> 16 & 0xFF, q >> 16 & 0xFF);
							int s = this.lerp(d, p >> 8 & 0xFF, q >> 8 & 0xFF);
							int t = this.lerp(d, p & 0xFF, q & 0xFF);
							this.images[k].setPixelColor(o, n, p & 0xFF000000 | r << 16 | s << 8 | t);
						}
					}
				}

				Sprite.this.upload(0, 0, this.images);
			}
		}

		/**
		 * Returns the pixel color at frame {@code frameIndex} within mipmap {@code layer} at sprite relative coordinates.
		 */
		private int getPixelColor(Sprite.class_5790 arg, int i, int j, int k, int l) {
			return Sprite.this.images[j].getPixelColor(k + (arg.method_33446(i) * Sprite.this.width >> j), l + (arg.method_33451(i) * Sprite.this.height >> j));
		}

		/**
		 * Purely mathematical single-value linear interpolation.
		 * {@code lerp(0, a, b) == b}, {@code lerp(1, a, b) == a}.
		 */
		private int lerp(double delta, int to, int from) {
			return (int)(delta * (double)to + (1.0 - delta) * (double)from);
		}

		public void close() {
			for (NativeImage nativeImage : this.images) {
				if (nativeImage != null) {
					nativeImage.close();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_5790 implements TextureTickListener, AutoCloseable {
		private int field_28470;
		private int field_28471;
		private final List<Sprite.class_5791> field_28472;
		private final int field_28473;
		@Nullable
		private final Sprite.Interpolation field_28474;

		private class_5790(List<Sprite.class_5791> list, int i, @Nullable Sprite.Interpolation interpolation) {
			this.field_28472 = list;
			this.field_28473 = i;
			this.field_28474 = interpolation;
		}

		private int method_33446(int i) {
			return i % this.field_28473;
		}

		private int method_33451(int i) {
			return i / this.field_28473;
		}

		private void method_33455(int i) {
			int j = this.method_33446(i) * Sprite.this.width;
			int k = this.method_33451(i) * Sprite.this.height;
			Sprite.this.upload(j, k, Sprite.this.images);
		}

		public void close() {
			if (this.field_28474 != null) {
				this.field_28474.close();
			}
		}

		@Override
		public void tick() {
			this.field_28471++;
			Sprite.class_5791 lv = (Sprite.class_5791)this.field_28472.get(this.field_28470);
			if (this.field_28471 >= lv.field_28476) {
				int i = lv.field_28475;
				this.field_28470 = (this.field_28470 + 1) % this.field_28472.size();
				this.field_28471 = 0;
				int j = ((Sprite.class_5791)this.field_28472.get(this.field_28470)).field_28475;
				if (i != j) {
					this.method_33455(j);
				}
			} else if (this.field_28474 != null) {
				if (!RenderSystem.isOnRenderThread()) {
					RenderSystem.recordRenderCall(() -> this.field_28474.apply(this));
				} else {
					this.field_28474.apply(this);
				}
			}
		}

		public void method_33445() {
			this.method_33455(((Sprite.class_5791)this.field_28472.get(0)).field_28475);
		}

		public IntStream method_33450() {
			return this.field_28472.stream().mapToInt(arg -> arg.field_28475).distinct();
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_5791 {
		private final int field_28475;
		private final int field_28476;

		private class_5791(int i, int j) {
			this.field_28475 = i;
			this.field_28476 = j;
		}
	}
}
