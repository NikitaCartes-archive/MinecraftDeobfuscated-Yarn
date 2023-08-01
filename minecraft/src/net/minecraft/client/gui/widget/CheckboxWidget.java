package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CheckboxWidget extends PressableWidget {
	private static final Identifier SELECTED_HIGHLIGHTED_TEXTURE = new Identifier("widget/checkbox_selected_highlighted");
	private static final Identifier SELECTED_TEXTURE = new Identifier("widget/checkbox_selected");
	private static final Identifier HIGHLIGHTED_TEXTURE = new Identifier("widget/checkbox_highlighted");
	private static final Identifier TEXTURE = new Identifier("widget/checkbox");
	private static final int TEXT_COLOR = 14737632;
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
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.checkbox.usage.focused"));
			} else {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.checkbox.usage.hovered"));
			}
		}
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		RenderSystem.enableDepthTest();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		Identifier identifier;
		if (this.checked) {
			identifier = this.isFocused() ? SELECTED_HIGHLIGHTED_TEXTURE : SELECTED_TEXTURE;
		} else {
			identifier = this.isFocused() ? HIGHLIGHTED_TEXTURE : TEXTURE;
		}

		context.drawGuiTexture(identifier, this.getX(), this.getY(), 20, this.height);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.showMessage) {
			context.drawTextWithShadow(
				textRenderer, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24
			);
		}
	}
}
