package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class WarningScreen extends Screen {
	private final Text header;
	private final Text message;
	private final Text checkMessage;
	private final Text narratedText;
	@Nullable
	protected CheckboxWidget checkbox;
	private MultilineText messageText = MultilineText.EMPTY;

	protected WarningScreen(Text header, Text message, Text checkMessage, Text narratedText) {
		super(NarratorManager.EMPTY);
		this.header = header;
		this.message = message;
		this.checkMessage = checkMessage;
		this.narratedText = narratedText;
	}

	protected abstract void initButtons(int yOffset);

	@Override
	protected void init() {
		super.init();
		this.messageText = MultilineText.create(this.textRenderer, this.message, this.width - 100);
		int i = (this.messageText.count() + 1) * this.getLineHeight();
		int j = this.textRenderer.getWidth(this.checkMessage);
		this.checkbox = new CheckboxWidget(this.width / 2 - j / 2 - 8, 76 + i, j + 24, 20, this.checkMessage, false);
		this.addDrawableChild(this.checkbox);
		this.initButtons(i);
	}

	@Override
	public Text getNarratedTitle() {
		return this.narratedText;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawTextWithShadow(matrices, this.textRenderer, this.header, 25, 30, 16777215);
		int i = this.width / 2 - this.messageText.getMaxWidth() / 2;
		this.messageText.drawWithShadow(matrices, i, 70, this.getLineHeight(), 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	protected int getLineHeight() {
		return 9 * 2;
	}
}
