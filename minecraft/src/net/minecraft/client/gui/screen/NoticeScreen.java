package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
	private final Runnable actionHandler;
	protected final Text notice;
	private MultilineText noticeLines = MultilineText.EMPTY;
	protected final Text buttonText;

	public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
		this(actionHandler, title, notice, ScreenTexts.BACK);
	}

	public NoticeScreen(Runnable actionHandler, Text title, Text notice, Text buttonText) {
		super(title);
		this.actionHandler = actionHandler;
		this.notice = notice;
		this.buttonText = buttonText;
	}

	@Override
	protected void init() {
		super.init();
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonText, button -> this.actionHandler.run()));
		this.noticeLines = MultilineText.create(this.textRenderer, this.notice, this.width - 50);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		this.noticeLines.drawCenterWithShadow(matrices, this.width / 2, 90);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
