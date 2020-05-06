package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends ConfirmScreen {
	private final Text warning;
	private final Text copy;
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkScreen(BooleanConsumer callback, String link, boolean trusted) {
		super(callback, new TranslatableText(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm"), new LiteralText(link));
		this.yesTranslated = (Text)(trusted ? new TranslatableText("chat.link.open") : ScreenTexts.YES);
		this.noTranslated = trusted ? ScreenTexts.CANCEL : ScreenTexts.NO;
		this.copy = new TranslatableText("chat.copy");
		this.warning = new TranslatableText("chat.link.warning");
		this.drawWarning = !trusted;
		this.link = link;
	}

	@Override
	protected void init() {
		super.init();
		this.buttons.clear();
		this.children.clear();
		this.addButton(new ButtonWidget(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated, buttonWidget -> this.callback.accept(true)));
		this.addButton(new ButtonWidget(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copy, buttonWidget -> {
			this.copyToClipboard();
			this.callback.accept(false);
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noTranslated, buttonWidget -> this.callback.accept(false)));
	}

	public void copyToClipboard() {
		this.client.keyboard.setClipboard(this.link);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		if (this.drawWarning) {
			this.drawCenteredText(matrices, this.textRenderer, this.warning, this.width / 2, 110, 16764108);
		}
	}
}
