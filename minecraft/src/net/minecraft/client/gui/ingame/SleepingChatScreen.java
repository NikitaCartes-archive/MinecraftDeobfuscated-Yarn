package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;

@Environment(EnvType.CLIENT)
public class SleepingChatScreen extends ChatScreen {
	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight - 40, I18n.translate("multiplayer.stopSleeping")) {
			@Override
			public void onPressed(double d, double e) {
				SleepingChatScreen.this.stopSleeping();
			}
		});
	}

	@Override
	public void close() {
		this.stopSleeping();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.stopSleeping();
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.client.player.sendChatMessage(string);
			}

			this.chatField.setText("");
			this.client.inGameHud.getChatHud().method_1820();
			return true;
		}

		return super.keyPressed(i, j, k);
	}

	private void stopSleeping() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(this.client.player, ClientCommandC2SPacket.Mode.field_12986));
	}
}
