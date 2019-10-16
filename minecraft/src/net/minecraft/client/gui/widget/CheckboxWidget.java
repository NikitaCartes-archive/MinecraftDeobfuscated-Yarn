package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CheckboxWidget extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier("textures/gui/checkbox.png");
	boolean checked;

	public CheckboxWidget(int i, int j, int k, int l, String string, boolean bl) {
		super(i, j, k, l, string);
		this.checked = bl;
	}

	@Override
	public void onPress() {
		this.checked = !this.checked;
	}

	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		blit(this.x, this.y, 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 32, 64);
		this.renderBg(minecraftClient, i, j);
		int k = 14737632;
		this.drawString(textRenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}
}
