package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends ConfirmScreen {
	private final String warning;
	private final String copy;
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkScreen(BooleanConsumer booleanConsumer, String string, boolean bl) {
		super(booleanConsumer, new TranslatableText(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), new LiteralText(string));
		this.yesTranslated = I18n.translate(bl ? "chat.link.open" : "gui.yes");
		this.noTranslated = I18n.translate(bl ? "gui.cancel" : "gui.no");
		this.copy = I18n.translate("chat.copy");
		this.warning = I18n.translate("chat.link.warning");
		this.drawWarning = !bl;
		this.link = string;
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
		this.minecraft.keyboard.setClipboard(this.link);
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		if (this.drawWarning) {
			this.drawCenteredString(this.font, this.warning, this.width / 2, 110, 16764108);
		}
	}
}
