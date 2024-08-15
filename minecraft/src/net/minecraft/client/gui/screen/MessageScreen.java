package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MessageScreen extends Screen {
	@Nullable
	private NarratedMultilineTextWidget textWidget;

	public MessageScreen(Text text) {
		super(text);
	}

	@Override
	protected void init() {
		this.textWidget = this.addDrawableChild(new NarratedMultilineTextWidget(this.width, this.title, this.textRenderer, 12));
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		if (this.textWidget != null) {
			this.textWidget.initMaxWidth(this.width);
			this.textWidget.setPosition(this.width / 2 - this.textWidget.getWidth() / 2, this.height / 2 - 9 / 2);
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected boolean hasUsageText() {
		return false;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderPanoramaBackground(context, delta);
		this.applyBlur();
		this.renderDarkening(context);
	}
}
