/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.DownloadTask;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RestoreTask;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier field_22686 = new Identifier("realms", "textures/gui/realms/plus_icon.png");
    private static final Identifier field_22687 = new Identifier("realms", "textures/gui/realms/restore_icon.png");
    private static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen lastScreen;
    private List<Backup> backups = Collections.emptyList();
    private String toolTip;
    private BackupObjectSelectionList backupObjectSelectionList;
    private int selectedBackup = -1;
    private final int slotId;
    private ButtonWidget downloadButton;
    private ButtonWidget restoreButton;
    private ButtonWidget changesButton;
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
        this.client.keyboard.enableRepeatEvents(true);
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (lastScrollPosition != -1) {
            this.backupObjectSelectionList.setScrollAmount(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                try {
                    List<Backup> list = realmsClient.backupsFor((long)((RealmsBackupScreen)RealmsBackupScreen.this).serverData.id).backups;
                    RealmsBackupScreen.this.client.execute(() -> {
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
        this.downloadButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(1), 120, 20, I18n.translate("mco.backup.button.download", new Object[0]), buttonWidget -> this.downloadClicked()));
        this.restoreButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(3), 120, 20, I18n.translate("mco.backup.button.restore", new Object[0]), buttonWidget -> this.restoreClicked(this.selectedBackup)));
        this.changesButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(5), 120, 20, I18n.translate("mco.backup.changes.tooltip", new Object[0]), buttonWidget -> {
            this.client.openScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }));
        this.addButton(new ButtonWidget(this.width - 100, this.height - 35, 85, 20, I18n.translate("gui.back", new Object[0]), buttonWidget -> this.client.openScreen(this.lastScreen)));
        this.addChild(this.backupObjectSelectionList);
        this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.configure.world.backup", new Object[0]), this.width / 2, 12, 0xFFFFFF));
        this.focusOn(this.backupObjectSelectionList);
        this.updateButtonStates();
        this.narrateLabels();
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

    private void updateButtonStates() {
        this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
        this.changesButton.visible = this.shouldChangesButtonBeVisible();
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void restoreClicked(int selectedBackup) {
        if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = selectedBackup;
            Date date = this.backups.get((int)selectedBackup).lastModifiedDate;
            String string = DateFormat.getDateTimeInstance(3, 3).format(date);
            String string2 = RealmsUtil.method_25282(date);
            String string3 = I18n.translate("mco.configure.world.restore.question.line1", string, string2);
            String string4 = I18n.translate("mco.configure.world.restore.question.line2", new Object[0]);
            this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                if (bl) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.client.openScreen(this);
                }
            }, RealmsLongConfirmationScreen.Type.Warning, string3, string4, true));
        }
    }

    private void downloadClicked() {
        String string = I18n.translate("mco.configure.world.restore.download.question.line1", new Object[0]);
        String string2 = I18n.translate("mco.configure.world.restore.download.question.line2", new Object[0]);
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.downloadWorldData();
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, string, string2, true));
    }

    private void downloadWorldData() {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup backup = this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.lastScreen)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.toolTip = null;
        this.renderBackground();
        this.backupObjectSelectionList.render(mouseX, mouseY, delta);
        this.titleLabel.render(this);
        this.textRenderer.draw(I18n.translate("mco.configure.world.backup", new Object[0]), (this.width - 150) / 2 - 90, 20.0f, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.textRenderer.draw(I18n.translate("mco.backup.nobackups", new Object[0]), 20.0f, this.height / 2 - 10, 0xFFFFFF);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render(mouseX, mouseY, delta);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
        }
    }

    protected void renderMousehoverTooltip(String msg, int x, int y) {
        if (msg == null) {
            return;
        }
        int i = x + 12;
        int j = y - 12;
        int k = this.textRenderer.getStringWidth(msg);
        this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(msg, i, j, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<BackupObjectSelectionListEntry> {
        private final Backup mBackup;

        public BackupObjectSelectionListEntry(Backup backup) {
            this.mBackup = backup;
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
            this.renderBackupItem(this.mBackup, x - 40, y, mouseX, mouseY);
        }

        private void renderBackupItem(Backup backup, int x, int y, int mouseX, int mouseY) {
            int i = backup.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.textRenderer.draw("Backup (" + RealmsUtil.method_25282(backup.lastModifiedDate) + ")", x + 40, y + 1, i);
            RealmsBackupScreen.this.textRenderer.draw(this.getMediumDatePresentation(backup.lastModifiedDate), x + 40, y + 12, 0x4C4C4C);
            int j = RealmsBackupScreen.this.width - 175;
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
            boolean bl = xm >= x && xm <= x + 12 && ym >= y && ym <= y + 14 && ym < RealmsBackupScreen.this.height - 15 && ym > 32;
            RealmsBackupScreen.this.client.getTextureManager().bindTexture(field_22687);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 28.0f : 0.0f;
            DrawableHelper.drawTexture(x * 2, y * 2, 0.0f, f, 23, 28, 23, 56);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = I18n.translate("mco.backup.button.restore", new Object[0]);
            }
        }

        private void drawInfo(int x, int y, int xm, int ym) {
            boolean bl = xm >= x && xm <= x + 8 && ym >= y && ym <= y + 8 && ym < RealmsBackupScreen.this.height - 15 && ym > 32;
            RealmsBackupScreen.this.client.getTextureManager().bindTexture(field_22686);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 15.0f : 0.0f;
            DrawableHelper.drawTexture(x * 2, y * 2, 0.0f, f, 15, 15, 15, 30);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = I18n.translate("mco.backup.changes.tooltip", new Object[0]);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionList
    extends RealmsObjectSelectionList<BackupObjectSelectionListEntry> {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
        }

        public void addEntry(Backup backup) {
            this.addEntry(new BackupObjectSelectionListEntry(backup));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 0.93);
        }

        @Override
        public boolean isFocused() {
            return RealmsBackupScreen.this.getFocused() == this;
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
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            if (mouseX < (double)this.getScrollbarPositionX() && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
                int i = this.width / 2 - 92;
                int j = this.width;
                int k = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount();
                int l = k / this.itemHeight;
                if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
                    this.setSelected(l);
                    this.itemClicked(k, l, mouseX, mouseY, this.width);
                }
                return true;
            }
            return false;
        }

        @Override
        public int getScrollbarPositionX() {
            return this.width - 5;
        }

        @Override
        public void itemClicked(int i, int j, double d, double e, int k) {
            int l = this.width - 35;
            int m = j * this.itemHeight + 36 - (int)this.getScrollAmount();
            int n = l + 10;
            int o = m - 3;
            if (d >= (double)l && d <= (double)(l + 9) && e >= (double)m && e <= (double)(m + 9)) {
                if (!((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)j)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.client.openScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(j)));
                }
            } else if (d >= (double)n && d < (double)(n + 13) && e >= (double)o && e < (double)(o + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked(j);
            }
        }

        @Override
        public void setSelected(int i) {
            this.setSelectedItem(i);
            if (i != -1) {
                Realms.narrateNow(I18n.translate("narrator.select", ((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)i)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(i);
        }

        public void selectInviteListItem(int item) {
            RealmsBackupScreen.this.selectedBackup = item;
            RealmsBackupScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable BackupObjectSelectionListEntry backupObjectSelectionListEntry) {
            super.setSelected(backupObjectSelectionListEntry);
            RealmsBackupScreen.this.selectedBackup = this.children().indexOf(backupObjectSelectionListEntry);
            RealmsBackupScreen.this.updateButtonStates();
        }
    }
}

