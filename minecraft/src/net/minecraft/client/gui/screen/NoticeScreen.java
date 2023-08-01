package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
	private static final int NOTICE_TEXT_Y = 90;
	private final Text notice;
	private MultilineText noticeLines = MultilineText.EMPTY;
	private final Runnable actionHandler;
	private final Text buttonText;
	private final boolean shouldCloseOnEsc;

	public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
		this(actionHandler, title, notice, ScreenTexts.BACK, true);
	}

	public NoticeScreen(Runnable actionHandler, Text title, Text notice, Text buttonText, boolean shouldCloseOnEsc) {
		super(title);
		this.actionHandler = actionHandler;
		this.notice = notice;
		this.buttonText = buttonText;
		this.shouldCloseOnEsc = shouldCloseOnEsc;
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), this.notice);
	}

	@Override
	protected void init() {
		super.init();
		this.noticeLines = MultilineText.create(this.textRenderer, this.notice, this.width - 50);
		int i = this.noticeLines.count() * 9;
		int j = MathHelper.clamp(90 + i + 12, this.height / 6 + 96, this.height - 24);
		int k = 150;
		this.addDrawableChild(ButtonWidget.builder(this.buttonText, button -> this.actionHandler.run()).dimensions((this.width - 150) / 2, j, 150, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 70, 16777215);
		this.noticeLines.drawCenterWithShadow(context, this.width / 2, 90);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return this.shouldCloseOnEsc;
	}
}
