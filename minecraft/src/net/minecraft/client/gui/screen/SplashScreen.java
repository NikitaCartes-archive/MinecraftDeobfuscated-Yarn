package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SplashScreen extends Overlay {
	private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
	private final MinecraftClient client;
	private final ResourceReloadMonitor reloadMonitor;
	private final Runnable field_18218;
	private final boolean field_18219;
	private float field_17770;
	private long field_17771 = -1L;
	private long field_18220 = -1L;

	public SplashScreen(MinecraftClient minecraftClient, ResourceReloadMonitor resourceReloadMonitor, Runnable runnable, boolean bl) {
		this.client = minecraftClient;
		this.reloadMonitor = resourceReloadMonitor;
		this.field_18218 = runnable;
		this.field_18219 = bl;
	}

	public static void init(MinecraftClient minecraftClient) {
		minecraftClient.getTextureManager().registerTexture(LOGO, new SplashScreen.LogoTexture());
	}

	@Override
	public void render(int i, int j, float f) {
		int k = this.client.getWindow().getScaledWidth();
		int l = this.client.getWindow().getScaledHeight();
		long m = SystemUtil.getMeasuringTimeMs();
		if (this.field_18219 && (this.reloadMonitor.isLoadStageComplete() || this.client.currentScreen != null) && this.field_18220 == -1L) {
			this.field_18220 = m;
		}

		float g = this.field_17771 > -1L ? (float)(m - this.field_17771) / 1000.0F : -1.0F;
		float h = this.field_18220 > -1L ? (float)(m - this.field_18220) / 500.0F : -1.0F;
		float o;
		if (g >= 1.0F) {
			if (this.client.currentScreen != null) {
				this.client.currentScreen.render(0, 0, f);
			}

			int n = MathHelper.ceil((1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F)) * 255.0F);
			fill(0, 0, k, l, 16777215 | n << 24);
			o = 1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F);
		} else if (this.field_18219) {
			if (this.client.currentScreen != null && h < 1.0F) {
				this.client.currentScreen.render(i, j, f);
			}

			int n = MathHelper.ceil(MathHelper.clamp((double)h, 0.15, 1.0) * 255.0);
			fill(0, 0, k, l, 16777215 | n << 24);
			o = MathHelper.clamp(h, 0.0F, 1.0F);
		} else {
			fill(0, 0, k, l, -1);
			o = 1.0F;
		}

		int n = (this.client.getWindow().getScaledWidth() - 256) / 2;
		int p = (this.client.getWindow().getScaledHeight() - 256) / 2;
		this.client.getTextureManager().bindTexture(LOGO);
		RenderSystem.enableBlend();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, o);
		this.blit(n, p, 0, 0, 256, 256);
		float q = this.reloadMonitor.getProgress();
		this.field_17770 = this.field_17770 * 0.95F + q * 0.050000012F;
		if (g < 1.0F) {
			this.renderProgressBar(k / 2 - 150, l / 4 * 3, k / 2 + 150, l / 4 * 3 + 10, this.field_17770, 1.0F - MathHelper.clamp(g, 0.0F, 1.0F));
		}

		if (g >= 2.0F) {
			this.client.setOverlay(null);
		}

		if (this.field_17771 == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.field_18219 || h >= 2.0F)) {
			this.reloadMonitor.throwExceptions();
			this.field_17771 = SystemUtil.getMeasuringTimeMs();
			this.field_18218.run();
			if (this.client.currentScreen != null) {
				this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
			}
		}
	}

	private void renderProgressBar(int i, int j, int k, int l, float f, float g) {
		int m = MathHelper.ceil((float)(k - i - 2) * f);
		fill(i - 1, j - 1, k + 1, l + 1, 0xFF000000 | Math.round((1.0F - g) * 255.0F) << 16 | Math.round((1.0F - g) * 255.0F) << 8 | Math.round((1.0F - g) * 255.0F));
		fill(i, j, k, l, -1);
		fill(
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
	public boolean pausesGame() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	static class LogoTexture extends ResourceTexture {
		public LogoTexture() {
			super(SplashScreen.LOGO);
		}

		@Override
		protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackDownloader().getPack();

			try {
				InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, SplashScreen.LOGO);
				Throwable var5 = null;

				ResourceTexture.TextureData var6;
				try {
					var6 = new ResourceTexture.TextureData(null, NativeImage.read(inputStream));
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
				return new ResourceTexture.TextureData(var18);
			}
		}
	}
}
