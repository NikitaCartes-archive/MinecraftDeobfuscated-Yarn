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
	protected final Screen parent;
	@Nullable
	protected CheckboxWidget checkbox;
	private MultilineText messageText = MultilineText.EMPTY;

	protected WarningScreen(Text header, Text message, Text checkMessage, Text narratedText, Screen parent) {
		super(NarratorManager.EMPTY);
		this.header = header;
		this.message = message;
		this.checkMessage = checkMessage;
		this.narratedText = narratedText;
		this.parent = parent;
	}

	protected abstract void initButtons(int yOffset);

	@Override
	protected void init() {
		super.init();
		this.messageText = MultilineText.create(this.textRenderer, this.message, this.width - 50);
		int i = (this.messageText.count() + 1) * 9 * 2;
		this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.checkMessage, false);
		this.addDrawableChild(this.checkbox);
		this.initButtons(i);
	}

	@Override
	public Text getNarratedTitle() {
		return this.narratedText;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawTextWithShadow(matrices, this.textRenderer, this.header, 25, 30, 16777215);
		this.messageText.drawWithShadow(matrices, 25, 70, 9 * 2, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
