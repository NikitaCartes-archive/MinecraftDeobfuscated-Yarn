package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends YesNoScreen {
	private final String warning;
	private final String copy;
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkScreen(YesNoCallback yesNoCallback, String string, int i, boolean bl) {
		super(yesNoCallback, I18n.translate(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), string, i);
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
		this.addButton(new class_4185(this.screenWidth / 2 - 50 - 105, this.screenHeight / 6 + 96, 100, 20, this.yesTranslated) {
			@Override
			public void method_1826() {
				ConfirmChatLinkScreen.this.callback.confirmResult(true, ConfirmChatLinkScreen.this.callbackId);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 50, this.screenHeight / 6 + 96, 100, 20, this.copy) {
			@Override
			public void method_1826() {
				ConfirmChatLinkScreen.this.copyToClipboard();
				ConfirmChatLinkScreen.this.callback.confirmResult(false, ConfirmChatLinkScreen.this.callbackId);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 50 + 105, this.screenHeight / 6 + 96, 100, 20, this.noTranslated) {
			@Override
			public void method_1826() {
				ConfirmChatLinkScreen.this.callback.confirmResult(false, ConfirmChatLinkScreen.this.callbackId);
			}
		});
	}

	public void copyToClipboard() {
		this.client.keyboard.setClipboard(this.link);
	}

	@Override
	public void draw(int i, int j, float f) {
		super.draw(i, j, f);
		if (this.drawWarning) {
			this.drawStringCentered(this.fontRenderer, this.warning, this.screenWidth / 2, 110, 16764108);
		}
	}
}
