package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;

@Environment(EnvType.CLIENT)
public class SleepingChatScreen extends ChatScreen {
	public SleepingChatScreen() {
		super("");
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, I18n.translate("multiplayer.stopSleeping"), buttonWidget -> this.stopSleeping())
		);
	}

	@Override
	public void onClose() {
		this.stopSleeping();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.stopSleeping();
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.minecraft.player.sendChatMessage(string);
			}

			this.chatField.setText("");
			this.minecraft.inGameHud.getChatHud().method_1820();
			return true;
		}

		return super.keyPressed(i, j, k);
	}

	private void stopSleeping() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.player.networkHandler;
		clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(this.minecraft.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
	}
}
