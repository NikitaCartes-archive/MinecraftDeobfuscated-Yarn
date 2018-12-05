package net.minecraft;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.Closeable;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_380 extends AbstractTexture implements Closeable {
	private final Identifier field_2262;
	private final boolean field_2263;
	private final class_380.class_381 field_2264;

	public class_380(Identifier identifier, boolean bl) {
		this.field_2262 = identifier;
		this.field_2263 = bl;
		this.field_2264 = new class_380.class_381(0, 0, 256, 256);
		TextureUtil.prepareImage(bl ? NativeImage.class_1013.field_5012 : NativeImage.class_1013.field_5016, this.getGlId(), 256, 256);
	}

	@Override
	public void load(ResourceManager resourceManager) {
	}

	public void close() {
		this.clearGlId();
	}

	@Nullable
	public class_382 method_2022(class_383 arg) {
		if (arg.method_2033() != this.field_2263) {
			return null;
		} else {
			class_380.class_381 lv = this.field_2264.method_2024(arg);
			if (lv != null) {
				this.bindTexture();
				arg.method_2030(lv.field_2269, lv.field_2268);
				float f = 256.0F;
				float g = 256.0F;
				float h = 0.01F;
				return new class_382(
					this.field_2262,
					((float)lv.field_2269 + 0.01F) / 256.0F,
					((float)lv.field_2269 - 0.01F + (float)arg.method_2031()) / 256.0F,
					((float)lv.field_2268 + 0.01F) / 256.0F,
					((float)lv.field_2268 - 0.01F + (float)arg.method_2032()) / 256.0F,
					arg.method_2034(),
					arg.method_2027(),
					arg.method_2028(),
					arg.method_2029()
				);
			} else {
				return null;
			}
		}
	}

	public Identifier method_2023() {
		return this.field_2262;
	}

	@Environment(EnvType.CLIENT)
	static class class_381 {
		final int field_2269;
		final int field_2268;
		final int field_2267;
		final int field_2266;
		class_380.class_381 field_2270;
		class_380.class_381 field_2271;
		boolean field_2265;

		private class_381(int i, int j, int k, int l) {
			this.field_2269 = i;
			this.field_2268 = j;
			this.field_2267 = k;
			this.field_2266 = l;
		}

		@Nullable
		class_380.class_381 method_2024(class_383 arg) {
			if (this.field_2270 != null && this.field_2271 != null) {
				class_380.class_381 lv = this.field_2270.method_2024(arg);
				if (lv == null) {
					lv = this.field_2271.method_2024(arg);
				}

				return lv;
			} else if (this.field_2265) {
				return null;
			} else {
				int i = arg.method_2031();
				int j = arg.method_2032();
				if (i > this.field_2267 || j > this.field_2266) {
					return null;
				} else if (i == this.field_2267 && j == this.field_2266) {
					this.field_2265 = true;
					return this;
				} else {
					int k = this.field_2267 - i;
					int l = this.field_2266 - j;
					if (k > l) {
						this.field_2270 = new class_380.class_381(this.field_2269, this.field_2268, i, this.field_2266);
						this.field_2271 = new class_380.class_381(this.field_2269 + i + 1, this.field_2268, this.field_2267 - i - 1, this.field_2266);
					} else {
						this.field_2270 = new class_380.class_381(this.field_2269, this.field_2268, this.field_2267, j);
						this.field_2271 = new class_380.class_381(this.field_2269, this.field_2268 + j + 1, this.field_2267, this.field_2266 - j - 1);
					}

					return this.field_2270.method_2024(arg);
				}
			}
		}
	}
}
