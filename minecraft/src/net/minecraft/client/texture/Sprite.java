package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4723;
import net.minecraft.class_4725;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

@Environment(EnvType.CLIENT)
public class Sprite implements AutoCloseable {
	private final SpriteAtlasTexture field_21750;
	private final Sprite.class_4727 field_21751;
	private final AnimationResourceMetadata animationMetadata;
	protected final NativeImage[] images;
	private final int[] frameXs;
	private final int[] frameYs;
	@Nullable
	private final Sprite.class_4728 field_21752;
	private final int x;
	private final int y;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private int frameIndex;
	private int frameTicks;

	protected Sprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.class_4727 arg, int i, int j, int k, int l, int m, NativeImage nativeImage) {
		this.field_21750 = spriteAtlasTexture;
		AnimationResourceMetadata animationResourceMetadata = arg.field_21756;
		int n = arg.field_21754;
		int o = arg.field_21755;
		this.x = l;
		this.y = m;
		this.uMin = (float)l / (float)j;
		this.uMax = (float)(l + n) / (float)j;
		this.vMin = (float)m / (float)k;
		this.vMax = (float)(m + o) / (float)k;
		int p = nativeImage.getWidth() / animationResourceMetadata.getWidth(n);
		int q = nativeImage.getHeight() / animationResourceMetadata.getHeight(o);
		if (animationResourceMetadata.getFrameCount() > 0) {
			int r = (Integer)animationResourceMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
			this.frameXs = new int[r];
			this.frameYs = new int[r];
			Arrays.fill(this.frameXs, -1);
			Arrays.fill(this.frameYs, -1);

			for (int s : animationResourceMetadata.getFrameIndexSet()) {
				if (s >= p * q) {
					throw new RuntimeException("invalid frameindex " + s);
				}

				int t = s / p;
				int u = s % p;
				this.frameXs[s] = u;
				this.frameYs[s] = t;
			}
		} else {
			List<AnimationFrameResourceMetadata> list = Lists.<AnimationFrameResourceMetadata>newArrayList();
			int v = p * q;
			this.frameXs = new int[v];
			this.frameYs = new int[v];

			for (int s = 0; s < q; s++) {
				for (int t = 0; t < p; t++) {
					int u = s * p + t;
					this.frameXs[u] = t;
					this.frameYs[u] = s;
					list.add(new AnimationFrameResourceMetadata(u, -1));
				}
			}

			animationResourceMetadata = new AnimationResourceMetadata(
				list, n, o, animationResourceMetadata.getDefaultFrameTime(), animationResourceMetadata.shouldInterpolate()
			);
		}

		this.field_21751 = new Sprite.class_4727(arg.field_21753, n, o, animationResourceMetadata);
		this.animationMetadata = animationResourceMetadata;

		try {
			try {
				this.images = class_4725.method_24102(nativeImage, i);
			} catch (Throwable var19) {
				CrashReport crashReport = CrashReport.create(var19, "Generating mipmaps for frame");
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
		} catch (Throwable var20) {
			CrashReport crashReport = CrashReport.create(var20, "Applying mipmap");
			CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
			crashReportSection.add("Sprite name", (CrashCallable<String>)(() -> this.getId().toString()));
			crashReportSection.add("Sprite size", (CrashCallable<String>)(() -> this.getWidth() + " x " + this.getHeight()));
			crashReportSection.add("Sprite frames", (CrashCallable<String>)(() -> this.getFrameCount() + " frames"));
			crashReportSection.add("Mipmap levels", i);
			throw new CrashException(crashReport);
		}

		if (animationResourceMetadata.shouldInterpolate()) {
			this.field_21752 = new Sprite.class_4728(arg, i);
		} else {
			this.field_21752 = null;
		}
	}

	private void upload(int frame) {
		int i = this.frameXs[frame] * this.field_21751.field_21754;
		int j = this.frameYs[frame] * this.field_21751.field_21755;
		this.upload(i, j, this.images);
	}

	private void upload(int frameX, int frameY, NativeImage[] output) {
		for (int i = 0; i < this.images.length; i++) {
			output[i]
				.upload(
					i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.field_21751.field_21754 >> i, this.field_21751.field_21755 >> i, this.images.length > 1, false
				);
		}
	}

	public int getWidth() {
		return this.field_21751.field_21754;
	}

	public int getHeight() {
		return this.field_21751.field_21755;
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
		return this.field_21751.field_21753;
	}

	public SpriteAtlasTexture method_24119() {
		return this.field_21750;
	}

	public int getFrameCount() {
		return this.frameXs.length;
	}

	public void destroy() {
		for (NativeImage nativeImage : this.images) {
			if (nativeImage != null) {
				nativeImage.close();
			}
		}

		if (this.field_21752 != null) {
			this.field_21752.close();
		}
	}

	public String toString() {
		int i = this.frameXs.length;
		return "TextureAtlasSprite{name='"
			+ this.field_21751.field_21753
			+ '\''
			+ ", frameCount="
			+ i
			+ ", x="
			+ this.x
			+ ", y="
			+ this.y
			+ ", height="
			+ this.field_21751.field_21755
			+ ", width="
			+ this.field_21751.field_21754
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
		return (
				this.images[0].getPixelRgba(x + this.frameXs[frame] * this.field_21751.field_21754, y + this.frameYs[frame] * this.field_21751.field_21755) >> 24 & 0xFF
			)
			== 0;
	}

	public void upload() {
		this.upload(0);
	}

	private float getFrameDeltaFactor() {
		float f = (float)this.field_21751.field_21754 / (this.uMax - this.uMin);
		float g = (float)this.field_21751.field_21755 / (this.vMax - this.vMin);
		return Math.max(g, f);
	}

	public float getAnimationFrameDelta() {
		return 4.0F / this.getFrameDeltaFactor();
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
		} else if (this.field_21752 != null) {
			if (!RenderSystem.isOnRenderThread()) {
				RenderSystem.recordRenderCall(() -> arg.method_24128());
			} else {
				this.field_21752.method_24128();
			}
		}
	}

	public boolean isAnimated() {
		return this.animationMetadata.getFrameCount() > 1;
	}

	public VertexConsumer method_24108(VertexConsumer vertexConsumer) {
		return new class_4723(vertexConsumer, this);
	}

	@Environment(EnvType.CLIENT)
	public static final class class_4727 {
		private final Identifier field_21753;
		private final int field_21754;
		private final int field_21755;
		private final AnimationResourceMetadata field_21756;

		public class_4727(Identifier identifier, int i, int j, AnimationResourceMetadata animationResourceMetadata) {
			this.field_21753 = identifier;
			this.field_21754 = i;
			this.field_21755 = j;
			this.field_21756 = animationResourceMetadata;
		}

		public Identifier method_24121() {
			return this.field_21753;
		}

		public int method_24123() {
			return this.field_21754;
		}

		public int method_24125() {
			return this.field_21755;
		}
	}

	@Environment(EnvType.CLIENT)
	final class class_4728 implements AutoCloseable {
		private final NativeImage[] field_21758;

		private class_4728(Sprite.class_4727 arg, int i) {
			this.field_21758 = new NativeImage[i + 1];

			for (int j = 0; j < this.field_21758.length; j++) {
				int k = arg.field_21754 >> j;
				int l = arg.field_21755 >> j;
				if (this.field_21758[j] == null) {
					this.field_21758[j] = new NativeImage(k, l, false);
				}
			}
		}

		private void method_24128() {
			double d = 1.0 - (double)Sprite.this.frameTicks / (double)Sprite.this.animationMetadata.getFrameTime(Sprite.this.frameIndex);
			int i = Sprite.this.animationMetadata.getFrameIndex(Sprite.this.frameIndex);
			int j = Sprite.this.animationMetadata.getFrameCount() == 0 ? Sprite.this.getFrameCount() : Sprite.this.animationMetadata.getFrameCount();
			int k = Sprite.this.animationMetadata.getFrameIndex((Sprite.this.frameIndex + 1) % j);
			if (i != k && k >= 0 && k < Sprite.this.getFrameCount()) {
				for (int l = 0; l < this.field_21758.length; l++) {
					int m = Sprite.this.field_21751.field_21754 >> l;
					int n = Sprite.this.field_21751.field_21755 >> l;

					for (int o = 0; o < n; o++) {
						for (int p = 0; p < m; p++) {
							int q = this.method_24130(i, l, p, o);
							int r = this.method_24130(k, l, p, o);
							int s = this.method_24129(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
							int t = this.method_24129(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
							int u = this.method_24129(d, q & 0xFF, r & 0xFF);
							this.field_21758[l].setPixelRgba(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
						}
					}
				}

				Sprite.this.upload(0, 0, this.field_21758);
			}
		}

		private int method_24130(int i, int j, int k, int l) {
			return Sprite.this.images[j]
				.getPixelRgba(
					k + (Sprite.this.frameXs[i] * Sprite.this.field_21751.field_21754 >> j), l + (Sprite.this.frameYs[i] * Sprite.this.field_21751.field_21755 >> j)
				);
		}

		private int method_24129(double d, int i, int j) {
			return (int)(d * (double)i + (1.0 - d) * (double)j);
		}

		public void close() {
			for (NativeImage nativeImage : this.field_21758) {
				if (nativeImage != null) {
					nativeImage.close();
				}
			}
		}
	}
}
