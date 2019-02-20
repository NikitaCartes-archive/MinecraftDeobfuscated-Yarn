package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4071;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadHandler;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SplashScreen extends class_4071 {
	private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
	private final MinecraftClient field_18217;
	private final ResourceReloadHandler field_17767;
	private final Runnable field_18218;
	private final boolean field_18219;
	private float field_17770;
	private long field_17771 = -1L;
	private long field_18220 = -1L;

	public SplashScreen(MinecraftClient minecraftClient, ResourceReloadHandler resourceReloadHandler, Runnable runnable, boolean bl) {
		this.field_18217 = minecraftClient;
		this.field_17767 = resourceReloadHandler;
		this.field_18218 = runnable;
		this.field_18219 = bl;
	}

	@Override
	public void method_18326(int i, int j, float f) {
		this.field_18217.getTextureManager().registerTexture(LOGO, new SplashScreen.class_4070());
		int k = this.field_18217.window.getScaledWidth();
		int l = this.field_18217.window.getScaledHeight();
		long m = SystemUtil.getMeasuringTimeMs();
		if (this.field_18219 && (this.field_17767.method_18786() || this.field_18217.currentScreen != null) && this.field_18220 == -1L) {
			this.field_18220 = m;
		}

		float g = this.field_17771 > -1L ? (float)(m - this.field_17771) / 1000.0F : -1.0F;
		float h = this.field_18220 > -1L ? (float)(m - this.field_18220) / 500.0F : -1.0F;
		float o;
		if (g >= 1.0F) {
			if (this.field_18217.currentScreen != null) {
				this.field_18217.currentScreen.method_18326(0, 0, f);
			}

			int n = MathHelper.ceil((1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F)) * 255.0F);
			drawRect(0, 0, k, l, 16777215 | n << 24);
			o = 1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F);
		} else if (this.field_18219) {
			if (this.field_18217.currentScreen != null && h < 1.0F) {
				this.field_18217.currentScreen.method_18326(i, j, f);
			}

			int n = MathHelper.ceil(MathHelper.clamp((double)h, 0.15, 1.0) * 255.0);
			drawRect(0, 0, k, l, 16777215 | n << 24);
			o = MathHelper.clamp(h, 0.0F, 1.0F);
		} else {
			drawRect(0, 0, k, l, -1);
			o = 1.0F;
		}

		int n = (this.field_18217.window.getScaledWidth() - 256) / 2;
		int p = (this.field_18217.window.getScaledHeight() - 256) / 2;
		this.field_18217.getTextureManager().bindTexture(LOGO);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, o);
		this.drawTexturedRect(n, p, 0, 0, 256, 256);
		float q = this.field_17767.getProgress();
		this.field_17770 = this.field_17770 * 0.95F + q * 0.050000012F;
		if (g < 1.0F) {
			this.renderProgressBar(k / 2 - 150, l / 4 * 3, k / 2 + 150, l / 4 * 3 + 10, this.field_17770, 1.0F - MathHelper.clamp(g, 0.0F, 1.0F));
		}

		if (g >= 2.0F) {
			this.field_18217.method_18502(null);
		}

		if (this.field_17771 == -1L && this.field_17767.method_18787() && (!this.field_18219 || h >= 2.0F)) {
			this.field_17771 = SystemUtil.getMeasuringTimeMs();
			this.field_18218.run();
			if (this.field_18217.currentScreen != null) {
				this.field_18217.currentScreen.initialize(this.field_18217, this.field_18217.window.getScaledWidth(), this.field_18217.window.getScaledHeight());
			}
		}
	}

	private void renderProgressBar(int i, int j, int k, int l, float f, float g) {
		int m = MathHelper.ceil((float)(k - i - 2) * f);
		drawRect(
			i - 1, j - 1, k + 1, l + 1, 0xFF000000 | Math.round((1.0F - g) * 255.0F) << 16 | Math.round((1.0F - g) * 255.0F) << 8 | Math.round((1.0F - g) * 255.0F)
		);
		drawRect(i, j, k, l, -1);
		drawRect(
			i + 1,
			j + 1,
			i + m,
			l - 1,
			0xFF000000
				| (int)MathHelper.lerp(1.0F - g, 226.0F, 255.0F) << 16
				| (int)MathHelper.lerp(1.0F - g, 40.0F, 255.0F) << 8
				| (int)MathHelper.lerp(1.0F - g, 55.0F, 255.0F)
		);
	}

	@Override
	public boolean method_18640() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	static class class_4070 extends ResourceTexture {
		public class_4070() {
			super(SplashScreen.LOGO);
		}

		@Override
		protected ResourceTexture.class_4006 method_18153(ResourceManager resourceManager) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackDownloader().getPack();

			try {
				InputStream inputStream = defaultResourcePack.open(ResourceType.ASSETS, SplashScreen.LOGO);
				Throwable var5 = null;

				ResourceTexture.class_4006 var6;
				try {
					var6 = new ResourceTexture.class_4006(null, NativeImage.fromInputStream(inputStream));
				} catch (Throwable var16) {
					var5 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var5 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var5.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var6;
			} catch (IOException var18) {
				return new ResourceTexture.class_4006(var18);
			}
		}
	}
}
