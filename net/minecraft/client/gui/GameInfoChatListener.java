/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameInfoChatListener
implements ClientChatListener {
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

