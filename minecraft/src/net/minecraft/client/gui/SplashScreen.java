package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_766;
import net.minecraft.resource.ResourceReloadHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SplashScreen extends Screen {
	private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
	private final ResourceReloadHandler field_17767;
	private final Consumer<SplashScreen> splashScreenConsumer;
	private final class_766 field_17769 = new class_766(MainMenuScreen.field_17774);
	private float field_17770;
	private long field_17771 = -1L;

	public SplashScreen(ResourceReloadHandler resourceReloadHandler, Consumer<SplashScreen> consumer) {
		this.field_17767 = resourceReloadHandler;
		this.splashScreenConsumer = consumer;
	}

	@Override
	public void draw(int i, int j, float f) {
		drawRect(0, 0, this.width, this.height, -1);
		long l = SystemUtil.getMeasuringTimeMs();
		float g = this.field_17771 > -1L ? (float)(l - this.field_17771) / 1000.0F : 0.0F;
		if (g >= 1.0F) {
			this.field_17769.method_3317(f, MathHelper.clamp(g - 1.0F, 0.0F, 1.0F));
		}

		int k = (this.client.window.getScaledWidth() - 256) / 2;
		int m = (this.client.window.getScaledHeight() - 256) / 2;
		this.client.getTextureManager().bindTexture(LOGO);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F));
		this.drawTexturedRect(k, m, 0, 0, 256, 256);
		if (g >= 1.0F) {
			this.client.getTextureManager().bindTexture(new Identifier("textures/gui/title/background/panorama_overlay.png"));
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)MathHelper.ceil(MathHelper.clamp(g - 1.0F, 0.0F, 1.0F)));
			drawTexturedRect(0, 0, 0.0F, 0.0F, 16, 128, this.width, this.height, 16.0F, 128.0F);
		}

		float h = this.field_17767.getProgress();
		this.field_17770 = this.field_17770 * 0.95F + h * 0.050000012F;
		if (g < 1.0F) {
			this.renderProgressBar(
				this.width / 2 - 150, this.height / 4 * 3, this.width / 2 + 150, this.height / 4 * 3 + 10, this.field_17770, 1.0F - MathHelper.clamp(g, 0.0F, 1.0F)
			);
		}

		if (g >= 2.0F) {
			this.splashScreenConsumer.accept(this);
		}

		if (this.field_17771 == -1L && this.field_17767.whenComplete().isDone()) {
			this.field_17771 = SystemUtil.getMeasuringTimeMs();
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
	public boolean doesEscapeKeyClose() {
		return false;
	}

	public class_766 method_18104() {
		return this.field_17769;
	}
}
