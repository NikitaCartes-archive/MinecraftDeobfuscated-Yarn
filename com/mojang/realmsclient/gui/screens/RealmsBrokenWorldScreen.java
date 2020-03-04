/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.OpenServerTask;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.SwitchSlotTask;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private RealmsServer field_20492;
    private final long serverId;
    private String title = I18n.translate("mco.brokenworld.title", new Object[0]);
    private final String message = I18n.translate("mco.brokenworld.message.line1", new Object[0]) + "\\n" + I18n.translate("mco.brokenworld.message.line2", new Object[0]);
    private int left_x;
    private int right_x;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen screen, RealmsMainScreen mainScreen, long serverId) {
        this.lastScreen = screen;
        this.mainScreen = mainScreen;
        this.serverId = serverId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void init() {
        this.left_x = this.width / 2 - 150;
        this.right_x = this.width / 2 + 190;
        this.addButton(new ButtonWidget(this.right_x - 80 + 8, RealmsBrokenWorldScreen.row(13) - 5, 70, 20, I18n.translate("gui.back", new Object[0]), buttonWidget -> this.backButtonClicked()));
        if (this.field_20492 == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
        this.client.keyboard.enableRepeatEvents(true);
    }

    public void addButtons() {
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            int i = entry.getKey();
            boolean bl = i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
            ButtonWidget buttonWidget2 = bl ? new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20, I18n.translate("mco.brokenworld.play", new Object[0]), buttonWidget -> {
                if (this.field_20492.slots.get((Object)Integer.valueOf((int)i)).empty) {
                    RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.field_20492, I18n.translate("mco.configure.world.switch.slot", new Object[0]), I18n.translate("mco.configure.world.switch.slot.subtitle", new Object[0]), 0xA0A0A0, I18n.translate("gui.cancel", new Object[0]), this::method_25123, () -> {
                        this.client.openScreen(this);
                        this.method_25123();
                    });
                    realmsResetWorldScreen.setSlot(i);
                    realmsResetWorldScreen.setResetTitle(I18n.translate("mco.create.world.reset.title", new Object[0]));
                    this.client.openScreen(realmsResetWorldScreen);
                } else {
                    this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.field_20492.id, i, this::method_25123)));
                }
            }) : new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20, I18n.translate("mco.brokenworld.download", new Object[0]), buttonWidget -> {
                String string = I18n.translate("mco.configure.world.restore.download.question.line1", new Object[0]);
                String string2 = I18n.translate("mco.configure.world.restore.download.question.line2", new Object[0]);
                this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                    if (bl) {
                        this.downloadWorld(i);
                    } else {
                        this.client.openScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, string, string2, true));
            });
            if (this.slotsThatHasBeenDownloaded.contains(i)) {
                buttonWidget2.active = false;
                buttonWidget2.setMessage(I18n.translate("mco.brokenworld.downloaded", new Object[0]));
            }
            this.addButton(buttonWidget2);
            this.addButton(new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(10), 80, 20, I18n.translate("mco.brokenworld.reset", new Object[0]), buttonWidget -> {
                RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.field_20492, this::method_25123, () -> {
                    this.client.openScreen(this);
                    this.method_25123();
                });
                if (i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME) {
                    realmsResetWorldScreen.setSlot(i);
                }
                this.client.openScreen(realmsResetWorldScreen);
            }));
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 17, 0xFFFFFF);
        String[] strings = this.message.split("\\\\n");
        for (int i = 0; i < strings.length; ++i) {
            this.drawCenteredString(this.textRenderer, strings[i], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + i * 12, 0xA0A0A0);
        }
        if (this.field_20492 == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            if (entry.getValue().templateImage != null && entry.getValue().templateId != -1L) {
                this.drawSlotFrame(this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), entry.getValue().templateId, entry.getValue().templateImage, entry.getValue().empty);
                continue;
            }
            this.drawSlotFrame(this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().empty);
        }
    }

    private int getFramePositionX(int i) {
        return this.left_x + (i - 1) * 110;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void backButtonClicked() {
        this.client.openScreen(this.lastScreen);
    }

    private void fetchServerData(long worldId) {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            try {
                this.field_20492 = realmsClient.getOwnWorld(worldId);
                this.addButtons();
            } catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't get own world");
                this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException.getMessage(), this.lastScreen));
            }
        }).start();
    }

    public void method_25123() {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            if (this.field_20492.state == RealmsServer.State.CLOSED) {
                this.client.execute(() -> this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.field_20492, this, this.mainScreen, true))));
            } else {
                try {
                    this.mainScreen.newScreen().play(realmsClient.getOwnWorld(this.serverId), this);
                } catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't get own world");
                    this.client.execute(() -> this.client.openScreen(this.lastScreen));
                }
            }
        }).start();
    }

    private void downloadWorld(int slotId) {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            WorldDownload worldDownload = realmsClient.download(this.field_20492.id, slotId);
            RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(this, worldDownload, this.field_20492.getWorldName(slotId), bl -> {
                if (bl) {
                    this.slotsThatHasBeenDownloaded.add(slotId);
                    this.children.clear();
                    this.addButtons();
                } else {
                    this.client.openScreen(this);
                }
            });
            this.client.openScreen(realmsDownloadLatestWorldScreen);
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't download world data");
            this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
        }
    }

    private boolean isMinigame() {
        return this.field_20492 != null && this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void drawSlotFrame(int x, int y, int xm, int ym, boolean active, String string, int i, long imageId, String image, boolean empty) {
        if (empty) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.field_22682);
        } else if (image != null && imageId != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
        } else if (i == 1) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.field_22683);
        } else if (i == 2) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.field_22684);
        } else if (i == 3) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.field_22685);
        } else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(this.field_20492.minigameId), this.field_20492.minigameImage);
        }
        if (!active) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else if (active) {
            float f = 0.9f + 0.1f * MathHelper.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        }
        DrawableHelper.blit(x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.field_22681);
        if (active) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        DrawableHelper.blit(x, y, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(this.textRenderer, string, x + 40, y + 66, 0xFFFFFF);
    }
}

