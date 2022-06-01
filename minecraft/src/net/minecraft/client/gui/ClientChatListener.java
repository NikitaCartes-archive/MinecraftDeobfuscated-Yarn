package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;

/**
 * A listener for received chat messages and game messages.
 * 
 * <p>Listeners are registered at {@link net.minecraft.client.gui.hud.InGameHud#listeners}
 * per message type.
 * 
 * @see net.minecraft.client.gui.hud.InGameHud#onChatMessage
 * @see net.minecraft.client.gui.hud.InGameHud#onGameMessage
 */
@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	/**
	 * Called when a message is received.
	 * 
	 * @param sender the chat message's sender, or {@code null} for game messages
	 */
	void onChatMessage(MessageType type, Text message, @Nullable MessageSender sender);
}
