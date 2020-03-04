/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private final Screen parent;
    private ButtonWidget buttonAllowCommands;
    private ButtonWidget buttonGameMode;
    private String gameMode = "survival";
    private boolean allowCommands;

    public OpenToLanScreen(Screen parent) {
        super(new TranslatableText("lanServer.title", new Object[0]));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start", new Object[0]), buttonWidget -> {
            this.client.openScreen(null);
            int i = NetworkUtils.findLocalPort();
            TranslatableText text = this.client.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i) ? new TranslatableText("commands.publish.started", i) : new TranslatableText("commands.publish.failed", new Object[0]);
            this.client.inGameHud.getChatHud().addMessage(text);
            this.client.updateWindowTitle();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.client.openScreen(this.parent)));
        this.buttonGameMode = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode", new Object[0]), buttonWidget -> {
            this.gameMode = "spectator".equals(this.gameMode) ? "creative" : ("creative".equals(this.gameMode) ? "adventure" : ("adventure".equals(this.gameMode) ? "survival" : "spectator"));
            this.updateButtonText();
        }));
        this.buttonAllowCommands = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands", new Object[0]), buttonWidget -> {
            this.allowCommands = !this.allowCommands;
            this.updateButtonText();
        }));
        this.updateButtonText();
    }

    private void updateButtonText() {
        this.buttonGameMode.setMessage(I18n.translate("selectWorld.gameMode", new Object[0]) + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode, new Object[0]));
        this.buttonAllowCommands.setMessage(I18n.translate("selectWorld.allowCommands", new Object[0]) + ' ' + I18n.translate(this.allowCommands ? "options.on" : "options.off", new Object[0]));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 50, 0xFFFFFF);
        this.drawCenteredString(this.textRenderer, I18n.translate("lanServer.otherPlayers", new Object[0]), this.width / 2, 82, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}

