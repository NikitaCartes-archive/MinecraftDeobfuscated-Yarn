/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
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
    public void onChatMessage(MessageType type, Text message, @Nullable MessageSender sender) {
        type.chat().ifPresent(displayRule -> {
            Text text2 = displayRule.apply(message, sender);
            if (sender == null) {
                this.client.inGameHud.getChatHud().addMessage(text2);
            } else {
                this.client.inGameHud.getChatHud().queueMessage(text2);
            }
        });
    }
}

