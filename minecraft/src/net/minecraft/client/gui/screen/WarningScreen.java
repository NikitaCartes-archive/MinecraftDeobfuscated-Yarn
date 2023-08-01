package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class WarningScreen extends Screen {
	private final Text message;
	@Nullable
	private final Text checkMessage;
	private final Text narratedText;
	@Nullable
	protected CheckboxWidget checkbox;
	private MultilineText messageText = MultilineText.EMPTY;

	protected WarningScreen(Text header, Text message, Text narratedText) {
		this(header, message, null, narratedText);
	}

	protected WarningScreen(Text header, Text message, @Nullable Text checkMessage, Text narratedText) {
		super(header);
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
		if (this.checkMessage != null) {
			int j = this.textRenderer.getWidth(this.checkMessage);
			this.checkbox = new CheckboxWidget(this.width / 2 - j / 2 - 8, 76 + i, j + 24, 20, this.checkMessage, false);
			this.addDrawableChild(this.checkbox);
		}

		this.initButtons(i);
	}

	@Override
	public Text getNarratedTitle() {
		return this.narratedText;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawTitle(context);
		int i = this.width / 2 - this.messageText.getMaxWidth() / 2;
		this.messageText.drawWithShadow(context, i, 70, this.getLineHeight(), 16777215);
	}

	protected void drawTitle(DrawContext context) {
		context.drawTextWithShadow(this.textRenderer, this.title, 25, 30, 16777215);
	}

	protected int getLineHeight() {
		return 9 * 2;
	}
}
