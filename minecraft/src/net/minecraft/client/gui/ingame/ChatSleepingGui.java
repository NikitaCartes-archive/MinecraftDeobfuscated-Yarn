package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2848;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class ChatSleepingGui extends ChatGui {
	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height - 40, I18n.translate("multiplayer.stopSleeping")) {
			@Override
			public void onPressed(double d, double e) {
				ChatSleepingGui.this.leaveBed();
			}
		});
	}

	@Override
	public void close() {
		this.leaveBed();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.leaveBed();
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.client.player.sendChatMessage(string);
			}

			this.chatField.setText("");
			this.client.hudInGame.getHudChat().method_1820();
			return true;
		}

		return super.keyPressed(i, j, k);
	}

	private void leaveBed() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		clientPlayNetworkHandler.sendPacket(new class_2848(this.client.player, class_2848.class_2849.field_12986));
	}
}
