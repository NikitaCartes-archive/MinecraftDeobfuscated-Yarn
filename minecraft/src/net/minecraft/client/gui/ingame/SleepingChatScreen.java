package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.ClientCommandServerPacket;

@Environment(EnvType.CLIENT)
public class SleepingChatScreen extends ChatScreen {
	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height - 40, I18n.translate("multiplayer.stopSleeping")) {
			@Override
			public void onPressed(double d, double e) {
				SleepingChatScreen.this.method_2180();
			}
		});
	}

	@Override
	public void close() {
		this.method_2180();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.method_2180();
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.client.player.sendChatMessage(string);
			}

			this.chatField.setText("");
			this.client.inGameHud.getHudChat().method_1820();
			return true;
		}

		return super.keyPressed(i, j, k);
	}

	private void method_2180() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		clientPlayNetworkHandler.sendPacket(new ClientCommandServerPacket(this.client.player, ClientCommandServerPacket.Mode.field_12986));
	}
}
