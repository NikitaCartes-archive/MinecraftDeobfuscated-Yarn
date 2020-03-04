/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsInviteScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private TextFieldWidget field_22696;
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    private final Screen lastScreen;
    private String errorMsg;
    private boolean showError;

    public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, Screen screen, RealmsServer serverData) {
        this.configureScreen = configureScreen;
        this.lastScreen = screen;
        this.serverData = serverData;
    }

    @Override
    public void tick() {
        this.field_22696.tick();
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.field_22696 = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, RealmsInviteScreen.row(2), 200, 20, null, I18n.translate("mco.configure.world.invite.profile.name", new Object[0]));
        this.addChild(this.field_22696);
        this.setInitialFocus(this.field_22696);
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsInviteScreen.row(10), 200, 20, I18n.translate("mco.configure.world.buttons.invite", new Object[0]), buttonWidget -> this.onInvite()));
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsInviteScreen.row(12), 200, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.client.openScreen(this.lastScreen)));
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void onInvite() {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        if (this.field_22696.getText() == null || this.field_22696.getText().isEmpty()) {
            this.showError(I18n.translate("mco.configure.world.players.error", new Object[0]));
            return;
        }
        try {
            RealmsServer realmsServer = realmsClient.invite(this.serverData.id, this.field_22696.getText().trim());
            if (realmsServer != null) {
                this.serverData.players = realmsServer.players;
                this.client.openScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
                this.showError(I18n.translate("mco.configure.world.players.error", new Object[0]));
            }
        } catch (Exception exception) {
            LOGGER.error("Couldn't invite user");
            this.showError(I18n.translate("mco.configure.world.players.error", new Object[0]));
        }
    }

    private void showError(String errorMsg) {
        this.showError = true;
        this.errorMsg = errorMsg;
        Realms.narrateNow(errorMsg);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.textRenderer.draw(I18n.translate("mco.configure.world.invite.profile.name", new Object[0]), this.width / 2 - 100, RealmsInviteScreen.row(1), 0xA0A0A0);
        if (this.showError) {
            this.drawCenteredString(this.textRenderer, this.errorMsg, this.width / 2, RealmsInviteScreen.row(5), 0xFF0000);
        }
        this.field_22696.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}

