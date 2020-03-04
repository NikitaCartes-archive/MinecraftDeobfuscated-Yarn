/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsGetServerDetailsTask;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen field_22727;
    private final RealmsMainScreen mainScreen;
    private final RealmsServer realmsServer;
    private boolean onLink;
    private final String realmsToSUrl = "https://minecraft.net/realms/terms";

    public RealmsTermsScreen(Screen screen, RealmsMainScreen mainScreen, RealmsServer realmsServer) {
        this.field_22727 = screen;
        this.mainScreen = mainScreen;
        this.realmsServer = realmsServer;
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        int i = this.width / 4 - 2;
        this.addButton(new ButtonWidget(this.width / 4, RealmsTermsScreen.row(12), i, 20, I18n.translate("mco.terms.buttons.agree", new Object[0]), buttonWidget -> this.agreedToTos()));
        this.addButton(new ButtonWidget(this.width / 2 + 4, RealmsTermsScreen.row(12), i, 20, I18n.translate("mco.terms.buttons.disagree", new Object[0]), buttonWidget -> this.client.openScreen(this.field_22727)));
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.field_22727);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void agreedToTos() {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            realmsClient.agreeToTos();
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.field_22727, new RealmsGetServerDetailsTask(this.mainScreen, this.field_22727, this.realmsServer, new ReentrantLock())));
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't agree to TOS");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.onLink) {
            this.client.keyboard.setClipboard("https://minecraft.net/realms/terms");
            Util.getOperatingSystem().open("https://minecraft.net/realms/terms");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, I18n.translate("mco.terms.title", new Object[0]), this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(I18n.translate("mco.terms.sentence.1", new Object[0]), this.width / 2 - 120, RealmsTermsScreen.row(5), 0xFFFFFF);
        int i = this.textRenderer.getStringWidth(I18n.translate("mco.terms.sentence.1", new Object[0]));
        int j = this.width / 2 - 121 + i;
        int k = RealmsTermsScreen.row(5);
        int l = j + this.textRenderer.getStringWidth("mco.terms.sentence.2") + 1;
        int m = k + 1 + this.textRenderer.fontHeight;
        if (j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m) {
            this.onLink = true;
            this.textRenderer.draw(" " + I18n.translate("mco.terms.sentence.2", new Object[0]), this.width / 2 - 120 + i, RealmsTermsScreen.row(5), 7107012);
        } else {
            this.onLink = false;
            this.textRenderer.draw(" " + I18n.translate("mco.terms.sentence.2", new Object[0]), this.width / 2 - 120 + i, RealmsTermsScreen.row(5), 0x3366BB);
        }
        super.render(mouseX, mouseY, delta);
    }
}

