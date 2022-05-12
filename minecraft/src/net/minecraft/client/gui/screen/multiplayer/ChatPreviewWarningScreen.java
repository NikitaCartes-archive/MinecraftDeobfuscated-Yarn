package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ChatPreviewWarningScreen extends WarningScreen {
	private static final Text TITLE = Text.translatable("chatPreview.warning.title").formatted(Formatting.BOLD);
	private static final Text CONTENT = Text.translatable("chatPreview.warning.content");
	private static final Text CHECK_MESSAGE = Text.translatable("chatPreview.warning.check");
	private static final Text NARRATED_TEXT = TITLE.shallowCopy().append("\n").append(CONTENT);
	private final ServerInfo serverInfo;

	public ChatPreviewWarningScreen(ServerInfo serverInfo) {
		super(TITLE, CONTENT, CHECK_MESSAGE, NARRATED_TEXT);
		this.serverInfo = serverInfo;
	}

	@Override
	protected void initButtons(int yOffset) {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, Text.translatable("menu.disconnect"), button -> {
			this.client.world.disconnect();
			this.client.disconnect();
			this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 100 + yOffset, 150, 20, ScreenTexts.PROCEED, button -> {
			this.acknowledge();
			this.close();
		}));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void acknowledge() {
		if (this.checkbox != null && this.checkbox.isChecked()) {
			ServerInfo.ChatPreview chatPreview = this.serverInfo.getChatPreview();
			if (chatPreview != null) {
				chatPreview.setAcknowledged();
				ServerList.updateServerListEntry(this.serverInfo);
			}
		}
	}

	@Override
	protected int getLineHeight() {
		return 9 * 3 / 2;
	}
}
