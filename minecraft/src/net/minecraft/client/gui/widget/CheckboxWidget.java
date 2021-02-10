package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CheckboxWidget extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier("textures/gui/checkbox.png");
	private boolean checked;
	private final boolean showMessage;

	public CheckboxWidget(int x, int y, int width, int height, Text message, boolean checked) {
		this(x, y, width, height, message, checked, true);
	}

	public CheckboxWidget(int x, int y, int width, int height, Text message, boolean checked, boolean showMessage) {
		super(x, y, width, height, message);
		this.checked = checked;
		this.showMessage = showMessage;
	}

	@Override
	public void onPress() {
		this.checked = !this.checked;
	}

	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		drawTexture(matrices, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 64, 64);
		this.renderBg(matrices, minecraftClient, mouseX, mouseY);
		if (this.showMessage) {
			drawTextWithShadow(
				matrices, textRenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24
			);
		}
	}
}
