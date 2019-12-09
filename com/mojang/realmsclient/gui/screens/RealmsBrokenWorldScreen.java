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
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsScreen lastScreen;
    private final RealmsMainScreen mainScreen;
    private RealmsServer field_20492;
    private final long serverId;
    private String title = RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.title");
    private final String message = RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.message.line1") + "\\n" + RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.message.line2");
    private int left_x;
    private int right_x;
    private final int default_button_width = 80;
    private final int default_button_offset = 5;
    private static final List<Integer> playButtonIds = Arrays.asList(1, 2, 3);
    private static final List<Integer> resetButtonIds = Arrays.asList(4, 5, 6);
    private static final List<Integer> downloadButtonIds = Arrays.asList(7, 8, 9);
    private static final List<Integer> downloadConfirmationIds = Arrays.asList(10, 11, 12);
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(RealmsScreen lastScreen, RealmsMainScreen mainScreen, long serverId) {
        this.lastScreen = lastScreen;
        this.mainScreen = mainScreen;
        this.serverId = serverId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void init() {
        this.left_x = this.width() / 2 - 150;
        this.right_x = this.width() / 2 + 190;
        this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, RealmsBrokenWorldScreen.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                RealmsBrokenWorldScreen.this.backButtonClicked();
            }
        });
        if (this.field_20492 == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
        this.setKeyboardHandlerSendRepeatsToGui(true);
    }

    public void addButtons() {
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            RealmsWorldOptions realmsWorldOptions = entry.getValue();
            boolean bl = entry.getKey() != this.field_20492.activeSlot || this.field_20492.worldType.equals((Object)RealmsServer.WorldType.MINIGAME);
            RealmsButton realmsButton = bl ? new PlayButton((int)playButtonIds.get(entry.getKey() - 1), this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.play")) : new DownloadButton((int)downloadButtonIds.get(entry.getKey() - 1), this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.download"));
            if (this.slotsThatHasBeenDownloaded.contains(entry.getKey())) {
                realmsButton.active(false);
                realmsButton.setMessage(RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.downloaded"));
            }
            this.buttonsAdd(realmsButton);
            this.buttonsAdd(new RealmsButton(resetButtonIds.get(entry.getKey() - 1), this.getFramePositionX(entry.getKey()), RealmsConstants.row(10), 80, 20, RealmsBrokenWorldScreen.getLocalizedString("mco.brokenworld.reset")){

                @Override
                public void onPress() {
                    int i = resetButtonIds.indexOf(this.id()) + 1;
                    RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_20492, RealmsBrokenWorldScreen.this);
                    if (i != ((RealmsBrokenWorldScreen)RealmsBrokenWorldScreen.this).field_20492.activeSlot || ((RealmsBrokenWorldScreen)RealmsBrokenWorldScreen.this).field_20492.worldType.equals((Object)RealmsServer.WorldType.MINIGAME)) {
                        realmsResetWorldScreen.setSlot(i);
                    }
                    realmsResetWorldScreen.setConfirmationId(14);
                    Realms.setScreen(realmsResetWorldScreen);
                }
            });
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.renderBackground();
        super.render(xm, ym, a);
        this.drawCenteredString(this.title, this.width() / 2, 17, 0xFFFFFF);
        String[] strings = this.message.split("\\\\n");
        for (int i = 0; i < strings.length; ++i) {
            this.drawCenteredString(strings[i], this.width() / 2, RealmsConstants.row(-1) + 3 + i * 12, 0xA0A0A0);
        }
        if (this.field_20492 == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            if (entry.getValue().templateImage != null && entry.getValue().templateId != -1L) {
                this.drawSlotFrame(this.getFramePositionX(entry.getKey()), RealmsConstants.row(1) + 5, xm, ym, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), entry.getValue().templateId, entry.getValue().templateImage, entry.getValue().empty);
                continue;
            }
            this.drawSlotFrame(this.getFramePositionX(entry.getKey()), RealmsConstants.row(1) + 5, xm, ym, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().empty);
        }
    }

    private int getFramePositionX(int i) {
        return this.left_x + (i - 1) * 110;
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        if (eventKey == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    private void backButtonClicked() {
        Realms.setScreen(this.lastScreen);
    }

    private void fetchServerData(long worldId) {
        new Thread(() -> {
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            try {
                this.field_20492 = realmsClient.getOwnWorld(worldId);
                this.addButtons();
            } catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't get own world");
                Realms.setScreen(new RealmsGenericErrorScreen(realmsServiceException.getMessage(), this.lastScreen));
            } catch (IOException iOException) {
                LOGGER.error("Couldn't parse response getting own world");
            }
        }).start();
    }

    @Override
    public void confirmResult(boolean result, int id) {
        if (!result) {
            Realms.setScreen(this);
            return;
        }
        if (id == 13 || id == 14) {
            new Thread(() -> {
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                if (this.field_20492.state.equals((Object)RealmsServer.State.CLOSED)) {
                    RealmsTasks.OpenServerTask openServerTask = new RealmsTasks.OpenServerTask(this.field_20492, this, this.lastScreen, true);
                    RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this, openServerTask);
                    realmsLongRunningMcoTaskScreen.start();
                    Realms.setScreen(realmsLongRunningMcoTaskScreen);
                } else {
                    try {
                        this.mainScreen.newScreen().play(realmsClient.getOwnWorld(this.serverId), this);
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't get own world");
                        Realms.setScreen(this.lastScreen);
                    } catch (IOException iOException) {
                        LOGGER.error("Couldn't parse response getting own world");
                        Realms.setScreen(this.lastScreen);
                    }
                }
            }).start();
        } else if (downloadButtonIds.contains(id)) {
            this.downloadWorld(downloadButtonIds.indexOf(id) + 1);
        } else if (downloadConfirmationIds.contains(id)) {
            this.slotsThatHasBeenDownloaded.add(downloadConfirmationIds.indexOf(id) + 1);
            this.childrenClear();
            this.addButtons();
        }
    }

    private void downloadWorld(int slotId) {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            WorldDownload worldDownload = realmsClient.download(this.field_20492.id, slotId);
            RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(this, worldDownload, this.field_20492.name + " (" + this.field_20492.slots.get(slotId).getSlotName(slotId) + ")");
            realmsDownloadLatestWorldScreen.setConfirmationId(downloadConfirmationIds.get(slotId - 1));
            Realms.setScreen(realmsDownloadLatestWorldScreen);
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't download world data");
            Realms.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (RealmsScreen)this));
        }
    }

    private boolean isMinigame() {
        return this.field_20492 != null && this.field_20492.worldType.equals((Object)RealmsServer.WorldType.MINIGAME);
    }

    private void drawSlotFrame(int x, int y, int xm, int ym, boolean active, String text, int i, long imageId, String image, boolean empty) {
        if (empty) {
            RealmsBrokenWorldScreen.bind("realms:textures/gui/realms/empty_frame.png");
        } else if (image != null && imageId != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
        } else if (i == 1) {
            RealmsBrokenWorldScreen.bind("textures/gui/title/background/panorama_0.png");
        } else if (i == 2) {
            RealmsBrokenWorldScreen.bind("textures/gui/title/background/panorama_2.png");
        } else if (i == 3) {
            RealmsBrokenWorldScreen.bind("textures/gui/title/background/panorama_3.png");
        } else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(this.field_20492.minigameId), this.field_20492.minigameImage);
        }
        if (!active) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else if (active) {
            float f = 0.9f + 0.1f * RealmsMth.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        }
        RealmsScreen.blit(x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        RealmsBrokenWorldScreen.bind("realms:textures/gui/realms/slot_frame.png");
        if (active) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsScreen.blit(x, y, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(text, x + 40, y + 66, 0xFFFFFF);
    }

    private void switchSlot(int id) {
        RealmsTasks.SwitchSlotTask switchSlotTask = new RealmsTasks.SwitchSlotTask(this.field_20492.id, id, this, 13);
        RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchSlotTask);
        realmsLongRunningMcoTaskScreen.start();
        Realms.setScreen(realmsLongRunningMcoTaskScreen);
    }

    @Environment(value=EnvType.CLIENT)
    class DownloadButton
    extends RealmsButton {
        public DownloadButton(int id, int x, String msg) {
            super(id, x, RealmsConstants.row(8), 80, 20, msg);
        }

        @Override
        public void onPress() {
            String string = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
            String string2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.Info, string, string2, true, this.id()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PlayButton
    extends RealmsButton {
        public PlayButton(int id, int x, String msg) {
            super(id, x, RealmsConstants.row(8), 80, 20, msg);
        }

        @Override
        public void onPress() {
            int i = playButtonIds.indexOf(this.id()) + 1;
            if (((RealmsBrokenWorldScreen)RealmsBrokenWorldScreen.this).field_20492.slots.get((Object)Integer.valueOf((int)i)).empty) {
                RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_20492, RealmsBrokenWorldScreen.this, RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, RealmsScreen.getLocalizedString("gui.cancel"));
                realmsResetWorldScreen.setSlot(i);
                realmsResetWorldScreen.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
                realmsResetWorldScreen.setConfirmationId(14);
                Realms.setScreen(realmsResetWorldScreen);
            } else {
                RealmsBrokenWorldScreen.this.switchSlot(i);
            }
        }
    }
}

