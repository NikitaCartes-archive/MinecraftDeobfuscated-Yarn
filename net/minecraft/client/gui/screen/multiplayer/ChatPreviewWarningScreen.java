/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ChatPreviewMode;
import net.minecraft.client.option.ServerList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatPreviewWarningScreen
extends WarningScreen {
    private static final Text TITLE = Text.translatable("chatPreview.warning.title").formatted(Formatting.BOLD);
    private static final Text CHECK_MESSAGE = Text.translatable("chatPreview.warning.check");
    private final ServerInfo serverInfo;
    @Nullable
    private final Screen parent;

    private static Text getWarningContent() {
        ChatPreviewMode chatPreviewMode = MinecraftClient.getInstance().options.getChatPreview().getValue();
        return Text.translatable("chatPreview.warning.content", chatPreviewMode.getText());
    }

    public ChatPreviewWarningScreen(@Nullable Screen parent, ServerInfo serverInfo) {
        super(TITLE, ChatPreviewWarningScreen.getWarningContent(), CHECK_MESSAGE, ScreenTexts.joinSentences(TITLE, ChatPreviewWarningScreen.getWarningContent()));
        this.serverInfo = serverInfo;
        this.parent = parent;
    }

    @Override
    protected void initButtons(int yOffset) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, Text.translatable("menu.disconnect"), button -> {
            this.client.world.disconnect();
            this.client.disconnect();
            this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 100 + yOffset, 150, 20, ScreenTexts.PROCEED, button -> {
            this.acknowledge();
            this.close();
        }));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void acknowledge() {
        ServerInfo.ChatPreview chatPreview;
        if (this.checkbox != null && this.checkbox.isChecked() && (chatPreview = this.serverInfo.getChatPreview()) != null) {
            chatPreview.setAcknowledged();
            ServerList.updateServerListEntry(this.serverInfo);
        }
    }

    @Override
    protected int getLineHeight() {
        return this.textRenderer.fontHeight * 3 / 2;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}

