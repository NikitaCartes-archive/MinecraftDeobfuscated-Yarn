/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen lastScreen;
    private List<Backup> backups = Collections.emptyList();
    private String toolTip;
    private BackupObjectSelectionList backupObjectSelectionList;
    private int selectedBackup = -1;
    private final int slotId;
    private RealmsButton downloadButton;
    private RealmsButton restoreButton;
    private RealmsButton changesButton;
    private Boolean noBackups = false;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;

    public RealmsBackupScreen(RealmsConfigureWorldScreen lastscreen, RealmsServer serverData, int slotId) {
        this.lastScreen = lastscreen;
        this.serverData = serverData;
        this.slotId = slotId;
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (lastScrollPosition != -1) {
            this.backupObjectSelectionList.scroll(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                try {
                    List<Backup> list = realmsClient.backupsFor((long)((RealmsBackupScreen)RealmsBackupScreen.this).serverData.id).backups;
                    Realms.execute(() -> {
                        RealmsBackupScreen.this.backups = list;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        RealmsBackupScreen.this.backupObjectSelectionList.clear();
                        for (Backup backup : RealmsBackupScreen.this.backups) {
                            RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
                        }
                        RealmsBackupScreen.this.generateChangeList();
                    });
                } catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't request backups", (Throwable)realmsServiceException);
                }
            }
        }.start();
        this.postInit();
    }

    private void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int i = 0; i < this.backups.size() - 1; ++i) {
            Backup backup = this.backups.get(i);
            Backup backup2 = this.backups.get(i + 1);
            if (backup.metadata.isEmpty() || backup2.metadata.isEmpty()) continue;
            for (String string : backup.metadata.keySet()) {
                if (!string.contains("Uploaded") && backup2.metadata.containsKey(string)) {
                    if (backup.metadata.get(string).equals(backup2.metadata.get(string))) continue;
                    this.addToChangeList(backup, string);
                    continue;
                }
                this.addToChangeList(backup, string);
            }
        }
    }

    private void addToChangeList(Backup backup, String key) {
        if (key.contains("Uploaded")) {
            String string = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
            backup.changeList.put(key, string);
            backup.setUploadedVersion(true);
        } else {
            backup.changeList.put(key, backup.metadata.get(key));
        }
    }

    private void postInit() {
        this.downloadButton = new RealmsButton(2, this.width() - 135, RealmsConstants.row(1), 120, 20, RealmsBackupScreen.getLocalizedString("mco.backup.button.download")){

            @Override
            public void onPress() {
                RealmsBackupScreen.this.downloadClicked();
            }
        };
        this.buttonsAdd(this.downloadButton);
        this.restoreButton = new RealmsButton(3, this.width() - 135, RealmsConstants.row(3), 120, 20, RealmsBackupScreen.getLocalizedString("mco.backup.button.restore")){

            @Override
            public void onPress() {
                RealmsBackupScreen.this.restoreClicked(RealmsBackupScreen.this.selectedBackup);
            }
        };
        this.buttonsAdd(this.restoreButton);
        this.changesButton = new RealmsButton(4, this.width() - 135, RealmsConstants.row(5), 120, 20, RealmsBackupScreen.getLocalizedString("mco.backup.changes.tooltip")){

            @Override
            public void onPress() {
                Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(RealmsBackupScreen.this.selectedBackup)));
                RealmsBackupScreen.this.selectedBackup = -1;
            }
        };
        this.buttonsAdd(this.changesButton);
        this.buttonsAdd(new RealmsButton(0, this.width() - 100, this.height() - 35, 85, 20, RealmsBackupScreen.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                Realms.setScreen(RealmsBackupScreen.this.lastScreen);
            }
        });
        this.addWidget(this.backupObjectSelectionList);
        this.titleLabel = new RealmsLabel(RealmsBackupScreen.getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 0xFFFFFF);
        this.addWidget(this.titleLabel);
        this.focusOn(this.backupObjectSelectionList);
        this.updateButtonStates();
        this.narrateLabels();
    }

    private void updateButtonStates() {
        this.restoreButton.setVisible(this.shouldRestoreButtonBeVisible());
        this.changesButton.setVisible(this.shouldChangesButtonBeVisible());
    }

    private boolean shouldChangesButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.backups.get((int)this.selectedBackup).changeList.isEmpty();
    }

    private boolean shouldRestoreButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.serverData.expired;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        if (eventKey == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    private void restoreClicked(int selectedBackup) {
        if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = selectedBackup;
            Date date = this.backups.get((int)selectedBackup).lastModifiedDate;
            String string = DateFormat.getDateTimeInstance(3, 3).format(date);
            String string2 = RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date.getTime());
            String string3 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.question.line1", string, string2);
            String string4 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, string3, string4, true, 1));
        }
    }

    private void downloadClicked() {
        String string = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
        String string2 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
        Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, string, string2, true, 2));
    }

    private void downloadWorldData() {
        RealmsTasks.DownloadTask downloadTask = new RealmsTasks.DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this);
        RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), downloadTask);
        realmsLongRunningMcoTaskScreen.start();
        Realms.setScreen(realmsLongRunningMcoTaskScreen);
    }

    @Override
    public void confirmResult(boolean result, int id) {
        if (result && id == 1) {
            this.restore();
        } else if (id == 1) {
            this.selectedBackup = -1;
            Realms.setScreen(this);
        } else if (result && id == 2) {
            this.downloadWorldData();
        } else {
            Realms.setScreen(this);
        }
    }

    private void restore() {
        Backup backup = this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        RealmsTasks.RestoreTask restoreTask = new RealmsTasks.RestoreTask(backup, this.serverData.id, this.lastScreen);
        RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), restoreTask);
        realmsLongRunningMcoTaskScreen.start();
        Realms.setScreen(realmsLongRunningMcoTaskScreen);
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.toolTip = null;
        this.renderBackground();
        this.backupObjectSelectionList.render(xm, ym, a);
        this.titleLabel.render(this);
        this.drawString(RealmsBackupScreen.getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.drawString(RealmsBackupScreen.getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 0xFFFFFF);
        }
        this.downloadButton.active(this.noBackups == false);
        super.render(xm, ym, a);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm, ym);
        }
    }

    protected void renderMousehoverTooltip(String msg, int x, int y) {
        if (msg == null) {
            return;
        }
        int i = x + 12;
        int j = y - 12;
        int k = this.fontWidth(msg);
        this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(msg, i, j, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionListEntry
    extends RealmListEntry {
        final Backup mBackup;

        public BackupObjectSelectionListEntry(Backup backup) {
            this.mBackup = backup;
        }

        @Override
        public void render(int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float a) {
            this.renderBackupItem(this.mBackup, rowLeft - 40, rowTop, mouseX, mouseY);
        }

        private void renderBackupItem(Backup backup, int x, int y, int mouseX, int mouseY) {
            int i = backup.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.drawString("Backup (" + RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - backup.lastModifiedDate.getTime()) + ")", x + 40, y + 1, i);
            RealmsBackupScreen.this.drawString(this.getMediumDatePresentation(backup.lastModifiedDate), x + 40, y + 12, 0x808080);
            int j = RealmsBackupScreen.this.width() - 175;
            int k = -3;
            int l = j - 10;
            boolean m = false;
            if (!((RealmsBackupScreen)RealmsBackupScreen.this).serverData.expired) {
                this.drawRestore(j, y + -3, mouseX, mouseY);
            }
            if (!backup.changeList.isEmpty()) {
                this.drawInfo(l, y + 0, mouseX, mouseY);
            }
        }

        private String getMediumDatePresentation(Date lastModifiedDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
        }

        private void drawRestore(int x, int y, int xm, int ym) {
            boolean bl = xm >= x && xm <= x + 12 && ym >= y && ym <= y + 14 && ym < RealmsBackupScreen.this.height() - 15 && ym > 32;
            RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(x * 2, y * 2, 0.0f, bl ? 28.0f : 0.0f, 23, 28, 23, 56);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.button.restore");
            }
        }

        private void drawInfo(int x, int y, int xm, int ym) {
            boolean bl = xm >= x && xm <= x + 8 && ym >= y && ym <= y + 8 && ym < RealmsBackupScreen.this.height() - 15 && ym > 32;
            RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(x * 2, y * 2, 0.0f, bl ? 15.0f : 0.0f, 15, 15, 15, 30);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionList
    extends RealmsObjectSelectionList {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width() - 150, RealmsBackupScreen.this.height(), 32, RealmsBackupScreen.this.height() - 15, 36);
        }

        public void addEntry(Backup backup) {
            this.addEntry(new BackupObjectSelectionListEntry(backup));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width() * 0.93);
        }

        @Override
        public boolean isFocused() {
            return RealmsBackupScreen.this.isFocused(this);
        }

        @Override
        public int getItemCount() {
            return RealmsBackupScreen.this.backups.size();
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public void renderBackground() {
            RealmsBackupScreen.this.renderBackground();
        }

        @Override
        public boolean mouseClicked(double xm, double ym, int buttonNum) {
            if (buttonNum != 0) {
                return false;
            }
            if (xm < (double)this.getScrollbarPosition() && ym >= (double)this.y0() && ym <= (double)this.y1()) {
                int i = this.width() / 2 - 92;
                int j = this.width();
                int k = (int)Math.floor(ym - (double)this.y0()) - this.headerHeight() + this.getScroll();
                int l = k / this.itemHeight();
                if (xm >= (double)i && xm <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
                    this.selectItem(l);
                    this.itemClicked(k, l, xm, ym, this.width());
                }
                return true;
            }
            return false;
        }

        @Override
        public int getScrollbarPosition() {
            return this.width() - 5;
        }

        @Override
        public void itemClicked(int clickSlotPos, int slot, double xm, double ym, int width) {
            int i = this.width() - 35;
            int j = slot * this.itemHeight() + 36 - this.getScroll();
            int k = i + 10;
            int l = j - 3;
            if (xm >= (double)i && xm <= (double)(i + 9) && ym >= (double)j && ym <= (double)(j + 9)) {
                if (!((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)slot)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = this.getScroll();
                    Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(slot)));
                }
            } else if (xm >= (double)k && xm < (double)(k + 13) && ym >= (double)l && ym < (double)(l + 15)) {
                lastScrollPosition = this.getScroll();
                RealmsBackupScreen.this.restoreClicked(slot);
            }
        }

        @Override
        public void selectItem(int item) {
            this.setSelected(item);
            if (item != -1) {
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)item)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(item);
        }

        public void selectInviteListItem(int item) {
            RealmsBackupScreen.this.selectedBackup = item;
            RealmsBackupScreen.this.updateButtonStates();
        }
    }
}

