package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SplashScreen extends Overlay {
	private static final Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");
	private static final int field_25041 = BackgroundHelper.ColorMixer.getArgb(255, 239, 50, 61);
	private static final int field_25042 = field_25041 & 16777215;
	private final MinecraftClient client;
	private final ResourceReloadMonitor reloadMonitor;
	private final Consumer<Optional<Throwable>> exceptionHandler;
	private final boolean reloading;
	private float progress;
	private long applyCompleteTime = -1L;
	private long prepareCompleteTime = -1L;

	public SplashScreen(MinecraftClient client, ResourceReloadMonitor monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading) {
		this.client = client;
		this.reloadMonitor = monitor;
		this.exceptionHandler = exceptionHandler;
		this.reloading = reloading;
	}

	public static void init(MinecraftClient client) {
		client.getTextureManager().registerTexture(LOGO, new SplashScreen.LogoTexture());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int i = this.client.getWindow().getScaledWidth();
		int j = this.client.getWindow().getScaledHeight();
		long l = Util.getMeasuringTimeMs();
		if (this.reloading && (this.reloadMonitor.isPrepareStageComplete() || this.client.currentScreen != null) && this.prepareCompleteTime == -1L) {
			this.prepareCompleteTime = l;
		}

		float f = this.applyCompleteTime > -1L ? (float)(l - this.applyCompleteTime) / 1000.0F : -1.0F;
		float g = this.prepareCompleteTime > -1L ? (float)(l - this.prepareCompleteTime) / 500.0F : -1.0F;
		float h;
		if (f >= 1.0F) {
			if (this.client.currentScreen != null) {
				this.client.currentScreen.render(matrices, 0, 0, delta);
			}

			int k = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
			fill(matrices, 0, 0, i, j, field_25042 | k << 24);
			h = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.reloading) {
			if (this.client.currentScreen != null && g < 1.0F) {
				this.client.currentScreen.render(matrices, mouseX, mouseY, delta);
			}

			int k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
			fill(matrices, 0, 0, i, j, field_25042 | k << 24);
			h = MathHelper.clamp(g, 0.0F, 1.0F);
		} else {
			fill(matrices, 0, 0, i, j, field_25041);
			h = 1.0F;
		}

		int k = (this.client.getWindow().getScaledWidth() - 322) / 2;
		int m = (this.client.getWindow().getScaledHeight() + 161) / 4;
		this.client.getTextureManager().bindTexture(LOGO);
		RenderSystem.enableBlend();
		RenderSystem.blendEquation(32774);
		RenderSystem.blendFunc(770, 1);
		RenderSystem.alphaFunc(516, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, h);
		float n = 0.0625F;
		drawTexture(matrices, k, m, 161, 80, -0.0625F, 0.0F, 161, 80, 161, 161);
		drawTexture(matrices, k + 161, m, 161, 80, 0.0625F, 80.5F, 161, 80, 161, 161);
		RenderSystem.defaultBlendFunc();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.disableBlend();
		float o = this.reloadMonitor.getProgress();
		this.progress = MathHelper.clamp(this.progress * 0.95F + o * 0.050000012F, 0.0F, 1.0F);
		if (f < 1.0F) {
			int p = j * 648 / 801;
			this.renderProgressBar(matrices, i / 2 - 161, p, i / 2 + 161, p + 12, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
		}

		if (f >= 2.0F) {
			this.client.setOverlay(null);
		}

		if (this.applyCompleteTime == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.reloading || g >= 2.0F)) {
			try {
				this.reloadMonitor.throwExceptions();
				this.exceptionHandler.accept(Optional.empty());
			} catch (Throwable var17) {
				this.exceptionHandler.accept(Optional.of(var17));
			}

			this.applyCompleteTime = Util.getMeasuringTimeMs();
			if (this.client.currentScreen != null) {
				this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
			}
		}
	}

	private void renderProgressBar(MatrixStack matrixStack, int i, int j, int k, int l, float f) {
		int m = MathHelper.ceil((float)(k - i - 2) * this.progress);
		int n = Math.round(f * 255.0F);
		int o = BackgroundHelper.ColorMixer.getArgb(n, 255, 255, 255);
		fill(matrixStack, i, j, k, l, o);
		fill(matrixStack, i + 1, j + 1, k - 1, l - 1, field_25042 | n << 24);
		fill(matrixStack, i + 2, j + 2, i + m, l - 2, o);
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
					var6 = new ResourceTexture.TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
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
