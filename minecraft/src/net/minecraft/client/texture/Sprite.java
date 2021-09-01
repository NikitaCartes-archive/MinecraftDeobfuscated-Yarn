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
	final int width;
	final int height;
	protected final NativeImage[] images;
	@Nullable
	private final Sprite.Animation animation;
	private final int x;
	private final int y;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;

	protected Sprite(SpriteAtlasTexture atlas, Sprite.Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
		this.atlas = atlas;
		this.width = info.width;
		this.height = info.height;
		this.id = info.id;
		this.x = x;
		this.y = y;
		this.uMin = (float)x / (float)atlasWidth;
		this.uMax = (float)(x + this.width) / (float)atlasWidth;
		this.vMin = (float)y / (float)atlasHeight;
		this.vMax = (float)(y + this.height) / (float)atlasHeight;
		this.animation = this.createAnimation(info, nativeImage.getWidth(), nativeImage.getHeight(), maxLevel);

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
		return this.animation != null ? this.animation.frames.size() : 1;
	}

	@Nullable
	private Sprite.Animation createAnimation(Sprite.Info info, int nativeImageWidth, int nativeImageHeight, int maxLevel) {
		AnimationResourceMetadata animationResourceMetadata = info.animationData;
		int i = nativeImageWidth / animationResourceMetadata.getWidth(info.width);
		int j = nativeImageHeight / animationResourceMetadata.getHeight(info.height);
		int k = i * j;
		List<Sprite.AnimationFrame> list = Lists.<Sprite.AnimationFrame>newArrayList();
		animationResourceMetadata.forEachFrame((index, time) -> list.add(new Sprite.AnimationFrame(index, time)));
		if (list.isEmpty()) {
			for (int l = 0; l < k; l++) {
				list.add(new Sprite.AnimationFrame(l, animationResourceMetadata.getDefaultFrameTime()));
			}
		} else {
			int l = 0;
			IntSet intSet = new IntOpenHashSet();

			for (Iterator<Sprite.AnimationFrame> iterator = list.iterator(); iterator.hasNext(); l++) {
				Sprite.AnimationFrame animationFrame = (Sprite.AnimationFrame)iterator.next();
				boolean bl = true;
				if (animationFrame.time <= 0) {
					LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", this.id, l, animationFrame.time);
					bl = false;
				}

				if (animationFrame.index < 0 || animationFrame.index >= k) {
					LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", this.id, l, animationFrame.index);
					bl = false;
				}

				if (bl) {
					intSet.add(animationFrame.index);
				} else {
					iterator.remove();
				}
			}

			int[] is = IntStream.range(0, k).filter(ix -> !intSet.contains(ix)).toArray();
			if (is.length > 0) {
				LOGGER.warn("Unused frames in sprite {}: {}", this.id, Arrays.toString(is));
			}
		}

		if (list.size() <= 1) {
			return null;
		} else {
			Sprite.Interpolation interpolation = animationResourceMetadata.shouldInterpolate() ? new Sprite.Interpolation(info, maxLevel) : null;
			return new Sprite.Animation(ImmutableList.copyOf(list), i, interpolation);
		}
	}

	void upload(int frameX, int frameY, NativeImage[] output) {
		for (int i = 0; i < this.images.length; i++) {
			output[i].upload(i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.width >> i, this.height >> i, this.images.length > 1, false);
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
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

	public float method_35804(float f) {
		float g = this.uMax - this.uMin;
		return (f - this.uMin) / g * 16.0F;
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

	public float method_35805(float f) {
		float g = this.vMax - this.vMin;
		return (f - this.vMin) / g * 16.0F;
	}

	public Identifier getId() {
		return this.id;
	}

	public SpriteAtlasTexture getAtlas() {
		return this.atlas;
	}

	public IntStream getDistinctFrameCount() {
		return this.animation != null ? this.animation.getDistinctFrameCount() : IntStream.of(1);
	}

	public void close() {
		for (NativeImage nativeImage : this.images) {
			if (nativeImage != null) {
				nativeImage.close();
			}
		}

		if (this.animation != null) {
			this.animation.close();
		}
	}

	public String toString() {
		return "TextureAtlasSprite{name='"
			+ this.id
			+ "', frameCount="
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
			+ "}";
	}

	public boolean isPixelTransparent(int frame, int x, int y) {
		int i = x;
		int j = y;
		if (this.animation != null) {
			i = x + this.animation.getFrameX(frame) * this.width;
			j = y + this.animation.getFrameY(frame) * this.height;
		}

		return (this.images[0].getColor(i, j) >> 24 & 0xFF) == 0;
	}

	public void upload() {
		if (this.animation != null) {
			this.animation.upload();
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
	public TextureTickListener getAnimation() {
		return this.animation;
	}

	public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer vertexConsumer) {
		return new SpriteTexturedVertexConsumer(vertexConsumer, this);
	}

	@Environment(EnvType.CLIENT)
	class Animation implements TextureTickListener, AutoCloseable {
		int frameIndex;
		int frameTicks;
		final List<Sprite.AnimationFrame> frames;
		private final int frameCount;
		@Nullable
		private final Sprite.Interpolation interpolation;

		Animation(List<Sprite.AnimationFrame> list, int i, @Nullable Sprite.Interpolation interpolation) {
			this.frames = list;
			this.frameCount = i;
			this.interpolation = interpolation;
		}

		int getFrameX(int frame) {
			return frame % this.frameCount;
		}

		int getFrameY(int frame) {
			return frame / this.frameCount;
		}

		private void upload(int frameIndex) {
			int i = this.getFrameX(frameIndex) * Sprite.this.width;
			int j = this.getFrameY(frameIndex) * Sprite.this.height;
			Sprite.this.upload(i, j, Sprite.this.images);
		}

		public void close() {
			if (this.interpolation != null) {
				this.interpolation.close();
			}
		}

		@Override
		public void tick() {
			this.frameTicks++;
			Sprite.AnimationFrame animationFrame = (Sprite.AnimationFrame)this.frames.get(this.frameIndex);
			if (this.frameTicks >= animationFrame.time) {
				int i = animationFrame.index;
				this.frameIndex = (this.frameIndex + 1) % this.frames.size();
				this.frameTicks = 0;
				int j = ((Sprite.AnimationFrame)this.frames.get(this.frameIndex)).index;
				if (i != j) {
					this.upload(j);
				}
			} else if (this.interpolation != null) {
				if (!RenderSystem.isOnRenderThread()) {
					RenderSystem.recordRenderCall(() -> this.interpolation.apply(this));
				} else {
					this.interpolation.apply(this);
				}
			}
		}

		public void upload() {
			this.upload(((Sprite.AnimationFrame)this.frames.get(0)).index);
		}

		public IntStream getDistinctFrameCount() {
			return this.frames.stream().mapToInt(animationFrame -> animationFrame.index).distinct();
		}
	}

	@Environment(EnvType.CLIENT)
	static class AnimationFrame {
		final int index;
		final int time;

		AnimationFrame(int i, int j) {
			this.index = i;
			this.time = j;
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class Info {
		final Identifier id;
		final int width;
		final int height;
		final AnimationResourceMetadata animationData;

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

		Interpolation(Sprite.Info info, int i) {
			this.images = new NativeImage[i + 1];

			for (int j = 0; j < this.images.length; j++) {
				int k = info.width >> j;
				int l = info.height >> j;
				if (this.images[j] == null) {
					this.images[j] = new NativeImage(k, l, false);
				}
			}
		}

		/**
		 * Linearly interpolate between the current and next frame on all mip levels
		 * based on the tick position within the current frame,
		 * and upload the results to the currently bound texture to the frame slot at position (0,0).
		 */
		void apply(Sprite.Animation animation) {
			Sprite.AnimationFrame animationFrame = (Sprite.AnimationFrame)animation.frames.get(animation.frameIndex);
			double d = 1.0 - (double)animation.frameTicks / (double)animationFrame.time;
			int i = animationFrame.index;
			int j = ((Sprite.AnimationFrame)animation.frames.get((animation.frameIndex + 1) % animation.frames.size())).index;
			if (i != j) {
				for (int k = 0; k < this.images.length; k++) {
					int l = Sprite.this.width >> k;
					int m = Sprite.this.height >> k;

					for (int n = 0; n < m; n++) {
						for (int o = 0; o < l; o++) {
							int p = this.getPixelColor(animation, i, k, o, n);
							int q = this.getPixelColor(animation, j, k, o, n);
							int r = this.lerp(d, p >> 16 & 0xFF, q >> 16 & 0xFF);
							int s = this.lerp(d, p >> 8 & 0xFF, q >> 8 & 0xFF);
							int t = this.lerp(d, p & 0xFF, q & 0xFF);
							this.images[k].setColor(o, n, p & 0xFF000000 | r << 16 | s << 8 | t);
						}
					}
				}

				Sprite.this.upload(0, 0, this.images);
			}
		}

		/**
		 * {@return the pixel color at frame {@code frameIndex} within mipmap {@code layer} at sprite relative coordinates}
		 */
		private int getPixelColor(Sprite.Animation animation, int frameIndex, int layer, int x, int y) {
			return Sprite.this.images[layer]
				.getColor(x + (animation.getFrameX(frameIndex) * Sprite.this.width >> layer), y + (animation.getFrameY(frameIndex) * Sprite.this.height >> layer));
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
}
