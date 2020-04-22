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
	private final boolean field_24253;

	public CheckboxWidget(int x, int y, int width, int height, Text text, boolean checked) {
		this(x, y, width, height, text, checked, true);
	}

	public CheckboxWidget(int i, int j, int k, int l, Text text, boolean bl, boolean bl2) {
		super(i, j, k, l, text);
		this.checked = bl;
		this.field_24253 = bl2;
	}

	@Override
	public void onPress() {
		this.checked = !this.checked;
	}

	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		drawTexture(matrixStack, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 64, 64);
		this.renderBg(matrixStack, minecraftClient, i, j);
		if (this.field_24253) {
			this.method_27535(
				matrixStack, textRenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24
			);
		}
	}
}
