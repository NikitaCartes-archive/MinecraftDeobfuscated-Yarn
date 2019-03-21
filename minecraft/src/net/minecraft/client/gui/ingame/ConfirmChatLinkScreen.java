package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends YesNoScreen {
	private final String warning;
	private final String copy;
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkScreen(YesNoCallback yesNoCallback, String string, int i, boolean bl) {
		super(yesNoCallback, new TranslatableTextComponent(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), new StringTextComponent(string), i);
		this.yesTranslated = I18n.translate(bl ? "chat.link.open" : "gui.yes");
		this.noTranslated = I18n.translate(bl ? "gui.cancel" : "gui.no");
		this.copy = I18n.translate("chat.copy");
		this.warning = I18n.translate("chat.link.warning");
		this.drawWarning = !bl;
		this.link = string;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.buttons.clear();
		this.listeners.clear();
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 50 - 105,
				this.screenHeight / 6 + 96,
				100,
				20,
				this.yesTranslated,
				buttonWidget -> this.callback.confirmResult(true, this.callbackId)
			)
		);
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 50, this.screenHeight / 6 + 96, 100, 20, this.copy, buttonWidget -> {
			this.copyToClipboard();
			this.callback.confirmResult(false, this.callbackId);
		}));
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 50 + 105,
				this.screenHeight / 6 + 96,
				100,
				20,
				this.noTranslated,
				buttonWidget -> this.callback.confirmResult(false, this.callbackId)
			)
		);
	}

	public void copyToClipboard() {
		this.client.keyboard.setClipboard(this.link);
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		if (this.drawWarning) {
			this.drawStringCentered(this.fontRenderer, this.warning, this.screenWidth / 2, 110, 16764108);
		}
	}
}
