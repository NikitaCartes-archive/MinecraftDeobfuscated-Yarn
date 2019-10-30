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
import net.minecraft.util.Util;
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

	public static void init(MinecraftClient client) {
		client.getTextureManager().registerTexture(LOGO, new SplashScreen.LogoTexture());
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		int i = this.client.getWindow().getScaledWidth();
		int j = this.client.getWindow().getScaledHeight();
		long l = Util.getMeasuringTimeMs();
		if (this.field_18219 && (this.reloadMonitor.isLoadStageComplete() || this.client.currentScreen != null) && this.field_18220 == -1L) {
			this.field_18220 = l;
		}

		float f = this.field_17771 > -1L ? (float)(l - this.field_17771) / 1000.0F : -1.0F;
		float g = this.field_18220 > -1L ? (float)(l - this.field_18220) / 500.0F : -1.0F;
		float h;
		if (f >= 1.0F) {
			if (this.client.currentScreen != null) {
				this.client.currentScreen.render(0, 0, delta);
			}

			int k = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
			fill(0, 0, i, j, 16777215 | k << 24);
			h = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.field_18219) {
			if (this.client.currentScreen != null && g < 1.0F) {
				this.client.currentScreen.render(mouseX, mouseY, delta);
			}

			int k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
			fill(0, 0, i, j, 16777215 | k << 24);
			h = MathHelper.clamp(g, 0.0F, 1.0F);
		} else {
			fill(0, 0, i, j, -1);
			h = 1.0F;
		}

		int k = (this.client.getWindow().getScaledWidth() - 256) / 2;
		int m = (this.client.getWindow().getScaledHeight() - 256) / 2;
		this.client.getTextureManager().bindTexture(LOGO);
		RenderSystem.enableBlend();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, h);
		this.blit(k, m, 0, 0, 256, 256);
		float n = this.reloadMonitor.getProgress();
		this.field_17770 = MathHelper.clamp(this.field_17770 * 0.95F + n * 0.050000012F, 0.0F, 1.0F);
		if (f < 1.0F) {
			this.renderProgressBar(i / 2 - 150, j / 4 * 3, i / 2 + 150, j / 4 * 3 + 10, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
		}

		if (f >= 2.0F) {
			this.client.setOverlay(null);
		}

		if (this.field_17771 == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.field_18219 || g >= 2.0F)) {
			this.reloadMonitor.throwExceptions();
			this.field_17771 = Util.getMeasuringTimeMs();
			this.field_18218.run();
			if (this.client.currentScreen != null) {
				this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
			}
		}
	}

	private void renderProgressBar(int minX, int minY, int maxX, int maxY, float progress) {
		int i = MathHelper.ceil((float)(maxX - minX - 1) * this.field_17770);
		fill(
			minX - 1,
			minY - 1,
			maxX + 1,
			maxY + 1,
			0xFF000000 | Math.round((1.0F - progress) * 255.0F) << 16 | Math.round((1.0F - progress) * 255.0F) << 8 | Math.round((1.0F - progress) * 255.0F)
		);
		fill(minX, minY, maxX, maxY, -1);
		fill(
			minX + 1,
			minY + 1,
			minX + i,
			maxY - 1,
			0xFF000000
				| (int)MathHelper.lerp(1.0F - progress, 226.0F, 255.0F) << 16
				| (int)MathHelper.lerp(1.0F - progress, 40.0F, 255.0F) << 8
				| (int)MathHelper.lerp(1.0F - progress, 55.0F, 255.0F)
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
