package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.menu.YesNoGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkGui extends YesNoGui {
	private final String warning;
	private final String copy;
	private final String link;
	private final boolean drawWarning;

	public ConfirmChatLinkGui(YesNoCallback yesNoCallback, String string, int i, boolean bl) {
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
		this.addButton(new ButtonWidget(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated) {
			@Override
			public void onPressed(double d, double e) {
				ConfirmChatLinkGui.this.callback.handle(true, ConfirmChatLinkGui.this.callbackId);
			}
		});
		this.addButton(new ButtonWidget(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copy) {
			@Override
			public void onPressed(double d, double e) {
				ConfirmChatLinkGui.this.copyToClipboard();
				ConfirmChatLinkGui.this.callback.handle(false, ConfirmChatLinkGui.this.callbackId);
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noTranslated) {
			@Override
			public void onPressed(double d, double e) {
				ConfirmChatLinkGui.this.callback.handle(false, ConfirmChatLinkGui.this.callbackId);
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
			this.drawStringCentered(this.fontRenderer, this.warning, this.width / 2, 110, 16764108);
		}
	}
}
