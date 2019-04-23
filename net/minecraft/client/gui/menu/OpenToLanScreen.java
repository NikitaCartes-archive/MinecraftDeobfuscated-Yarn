/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private final Screen parent;
    private ButtonWidget buttonAllowCommands;
    private ButtonWidget buttonGameMode;
    private String gameMode = "survival";
    private boolean allowCommands;

    public OpenToLanScreen(Screen screen) {
        super(new TranslatableComponent("lanServer.title", new Object[0]));
        this.parent = screen;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start", new Object[0]), buttonWidget -> {
            this.minecraft.openScreen(null);
            int i = NetworkUtils.findLocalPort();
            TranslatableComponent component = this.minecraft.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i) ? new TranslatableComponent("commands.publish.started", i) : new TranslatableComponent("commands.publish.failed", new Object[0]);
            this.minecraft.inGameHud.getChatHud().addMessage(component);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
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
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 50, 0xFFFFFF);
        this.drawCenteredString(this.font, I18n.translate("lanServer.otherPlayers", new Object[0]), this.width / 2, 82, 0xFFFFFF);
        super.render(i, j, f);
    }
}

