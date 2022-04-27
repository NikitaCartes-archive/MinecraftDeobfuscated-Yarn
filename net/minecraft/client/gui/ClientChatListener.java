/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ChatMessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * A listener for received chat messages and game messages.
 * 
 * <p>Listeners are registered at {@link net.minecraft.client.gui.hud.InGameHud#listeners}
 * per message type.
 * 
 * @see net.minecraft.client.gui.hud.InGameHud#onChatMessage
 * @see net.minecraft.client.gui.hud.InGameHud#onGameMessage
 */
@Environment(value=EnvType.CLIENT)
public interface ClientChatListener {
    /**
     * Called when a message is received.
     * 
     * @param sender the chat message's sender, or {@code null} for game messages
     */
    public void onChatMessage(MessageType var1, Text var2, @Nullable ChatMessageSender var3);
}

