/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.ChatMessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatHudListener
implements ClientChatListener {
    private final MinecraftClient client;

    public ChatHudListener(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void onChatMessage(MessageType type, Text message, @Nullable ChatMessageSender sender) {
        if (type != MessageType.CHAT) {
            this.client.inGameHud.getChatHud().addMessage(message);
        } else {
            Text text = sender != null ? ChatHudListener.format(message, sender) : message;
            this.client.inGameHud.getChatHud().queueMessage(text);
        }
    }

    /**
     * {@return the text formatted for displaying in the chat hud}
     */
    private static Text format(Text message, ChatMessageSender sender) {
        return Text.translatable("chat.type.text", sender.name(), message);
    }
}

