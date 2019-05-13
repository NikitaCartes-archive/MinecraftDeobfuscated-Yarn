/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SystemUtil;

@Environment(value=EnvType.CLIENT)
public class PauseScreen
extends Screen {
    private final boolean field_19319;

    public PauseScreen(boolean bl) {
        super(bl ? new TranslatableComponent("menu.game", new Object[0]) : new TranslatableComponent("menu.paused", new Object[0]));
        this.field_19319 = bl;
    }

    @Override
    protected void init() {
        if (this.field_19319) {
            this.method_20543();
        }
    }

    private void method_20543() {
        int i = -16;
        int j = 98;
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame", new Object[0]), buttonWidget -> {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.advancements", new Object[0]), buttonWidget -> this.minecraft.openScreen(new AdvancementsScreen(this.minecraft.player.networkHandler.getAdvancementHandler()))));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.stats", new Object[0]), buttonWidget -> this.minecraft.openScreen(new StatsScreen(this, this.minecraft.player.getStats()))));
        String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, I18n.translate("menu.sendFeedback", new Object[0]), buttonWidget -> this.minecraft.openScreen(new ConfirmChatLinkScreen(bl -> {
            if (bl) {
                SystemUtil.getOperatingSystem().open(string);
            }
            this.minecraft.openScreen(this);
        }, string, true))));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, I18n.translate("menu.reportBugs", new Object[0]), buttonWidget -> this.minecraft.openScreen(new ConfirmChatLinkScreen(bl -> {
            if (bl) {
                SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
            }
            this.minecraft.openScreen(this);
        }, "https://aka.ms/snapshotbugs?ref=game", true))));
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.options", new Object[0]), buttonWidget -> this.minecraft.openScreen(new SettingsScreen(this, this.minecraft.options))));
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan", new Object[0]), buttonWidget -> this.minecraft.openScreen(new OpenToLanScreen(this))));
        buttonWidget2.active = this.minecraft.isIntegratedServerRunning() && !this.minecraft.getServer().isRemote();
        ButtonWidget buttonWidget22 = this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.translate("menu.returnToMenu", new Object[0]), buttonWidget -> {
            boolean bl = this.minecraft.isInSingleplayer();
            boolean bl2 = this.minecraft.isConnectedToRealms();
            buttonWidget.active = false;
            this.minecraft.world.disconnect();
            if (bl) {
                this.minecraft.disconnect(new SaveLevelScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
            } else {
                this.minecraft.disconnect();
            }
            if (bl) {
                this.minecraft.openScreen(new TitleScreen());
            } else if (bl2) {
                RealmsBridge realmsBridge = new RealmsBridge();
                realmsBridge.switchToRealms(new TitleScreen());
            } else {
                this.minecraft.openScreen(new MultiplayerScreen(new TitleScreen()));
            }
        }));
        if (!this.minecraft.isInSingleplayer()) {
            buttonWidget22.setMessage(I18n.translate("menu.disconnect", new Object[0]));
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(int i, int j, float f) {
        if (this.field_19319) {
            this.renderBackground();
            this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 0xFFFFFF);
        } else {
            this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 0xFFFFFF);
        }
        super.render(i, j, f);
    }
}

