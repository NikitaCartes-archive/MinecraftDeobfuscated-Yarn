/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final Identifier field_22698 = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    private static final Identifier field_22699 = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    private static final Identifier field_22700 = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
    private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
    private volatile int numberOfPendingInvites;
    private static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    private static boolean validClient;
    private static boolean hasUnreadNews;

    @Override
    public void init() {
        this.checkIfMcoEnabled();
        this.client.keyboard.enableRepeatEvents(true);
    }

    @Override
    public void tick() {
        if (!(this.method_25169() && this.method_25170() && validClient || realmsDataFetcher.isStopped())) {
            realmsDataFetcher.stop();
            return;
        }
        if (!validClient || !this.method_25169()) {
            return;
        }
        realmsDataFetcher.initWithSpecificTaskList();
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = realmsDataFetcher.isTrialAvailable();
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = realmsDataFetcher.hasUnreadNews();
        }
        realmsDataFetcher.markClean();
    }

    private boolean method_25169() {
        return this.client.options.realmsNotifications;
    }

    private boolean method_25170() {
        return this.client.currentScreen instanceof TitleScreen;
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            return;
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        if (realmsServiceException.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (validClient) {
            this.drawIcons(mouseX, mouseY);
        }
        super.render(mouseX, mouseY, delta);
    }

    private void drawIcons(int xm, int ym) {
        int i = this.numberOfPendingInvites;
        int j = 24;
        int k = this.height / 4 + 48;
        int l = this.width / 2 + 80;
        int m = k + 48 + 2;
        int n = 0;
        if (hasUnreadNews) {
            this.client.getTextureManager().bindTexture(field_22700);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.4f, 0.4f, 0.4f);
            DrawableHelper.drawTexture((int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            RenderSystem.popMatrix();
            n += 14;
        }
        if (i != 0) {
            this.client.getTextureManager().bindTexture(field_22698);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(l - n, m - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            n += 16;
        }
        if (trialAvailable) {
            this.client.getTextureManager().bindTexture(field_22699);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int o = 0;
            if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
                o = 8;
            }
            DrawableHelper.drawTexture(l + 4 - n, m + 4, 0.0f, o, 8, 8, 8, 16);
        }
    }

    @Override
    public void removed() {
        realmsDataFetcher.stop();
    }
}

