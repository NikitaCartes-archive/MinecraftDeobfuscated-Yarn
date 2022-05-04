package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GameInfoChatListener implements ClientChatListener {
	private final MinecraftClient client;

	public GameInfoChatListener(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void onChatMessage(MessageType type, Text message, @Nullable MessageSender sender) {
		type.overlay().ifPresent(displayRule -> {
			Text text2 = displayRule.apply(message, sender);
			this.client.inGameHud.setOverlayMessage(text2, false);
		});
	}
}
