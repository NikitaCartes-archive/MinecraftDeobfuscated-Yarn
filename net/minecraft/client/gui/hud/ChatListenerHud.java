/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatListenerHud
implements ClientChatListener {
    private final MinecraftClient client;

    public ChatListenerHud(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void onChatMessage(MessageType messageType, Text text) {
        this.client.inGameHud.getChatHud().addMessage(text);
    }
}

