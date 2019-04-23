/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public class GameInfoChatListener
implements ClientChatListener {
    private final MinecraftClient client;

    public GameInfoChatListener(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void onChatMessage(ChatMessageType chatMessageType, Component component) {
        this.client.inGameHud.setOverlayMessage(component, false);
    }
}

