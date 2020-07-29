/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.DownloadTask;
import net.minecraft.client.realms.task.RestoreTask;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
    private static final Text field_26471 = new TranslatableText("mco.backup.button.restore");
    private static final Text field_26472 = new TranslatableText("mco.backup.changes.tooltip");
    private static final Text field_26473 = new TranslatableText("mco.configure.world.backup");
    private static final Text field_26474 = new TranslatableText("mco.backup.nobackups");
    private static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen parent;
    private List<Backup> backups = Collections.emptyList();
    @Nullable
    private Text toolTip;
    private BackupObjectSelectionList backupObjectSelectionList;
    private int selectedBackup = -1;
    private final int slotId;
    private ButtonWidget downloadButton;
    private ButtonWidget restoreButton;
    private ButtonWidget changesButton;
    private Boolean noBackups = false;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;

    public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
        this.parent = parent;
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
        this.downloadButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(1), 120, 20, new TranslatableText("mco.backup.button.download"), buttonWidget -> this.downloadClicked()));
        this.restoreButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(3), 120, 20, new TranslatableText("mco.backup.button.restore"), buttonWidget -> this.restoreClicked(this.selectedBackup)));
        this.changesButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(5), 120, 20, new TranslatableText("mco.backup.changes.tooltip"), buttonWidget -> {
            this.client.openScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }));
        this.addButton(new ButtonWidget(this.width - 100, this.height - 35, 85, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
        this.addChild(this.backupObjectSelectionList);
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.backup"), this.width / 2, 12, 0xFFFFFF));
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
            this.client.openScreen(this.parent);
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
            TranslatableText text = new TranslatableText("mco.configure.world.restore.question.line1", string, string2);
            TranslatableText text2 = new TranslatableText("mco.configure.world.restore.question.line2");
            this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                if (bl) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.client.openScreen(this);
                }
            }, RealmsLongConfirmationScreen.Type.Warning, text, text2, true));
        }
    }

    private void downloadClicked() {
        TranslatableText text = new TranslatableText("mco.configure.world.restore.download.question.line1");
        TranslatableText text2 = new TranslatableText("mco.configure.world.restore.download.question.line2");
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.downloadWorldData();
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
    }

    private void downloadWorldData() {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup backup = this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.toolTip = null;
        this.renderBackground(matrices);
        this.backupObjectSelectionList.render(matrices, mouseX, mouseY, delta);
        this.titleLabel.render(this, matrices);
        this.textRenderer.draw(matrices, field_26473, (float)((this.width - 150) / 2 - 90), 20.0f, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.textRenderer.draw(matrices, field_26474, 20.0f, (float)(this.height / 2 - 10), 0xFFFFFF);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render(matrices, mouseX, mouseY, delta);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(matrices, this.toolTip, mouseX, mouseY);
        }
    }

    protected void renderMousehoverTooltip(MatrixStack matrixStack, @Nullable Text text, int i, int j) {
        if (text == null) {
            return;
        }
        int k = i + 12;
        int l = j - 12;
        int m = this.textRenderer.getWidth(text);
        this.fillGradient(matrixStack, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(matrixStack, text, (float)k, (float)l, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<BackupObjectSelectionListEntry> {
        private final Backup mBackup;

        public BackupObjectSelectionListEntry(Backup backup) {
            this.mBackup = backup;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderBackupItem(matrices, this.mBackup, x - 40, y, mouseX, mouseY);
        }

        private void renderBackupItem(MatrixStack matrixStack, Backup backup, int i, int j, int k, int l) {
            int m = backup.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.textRenderer.draw(matrixStack, "Backup (" + RealmsUtil.method_25282(backup.lastModifiedDate) + ")", (float)(i + 40), (float)(j + 1), m);
            RealmsBackupScreen.this.textRenderer.draw(matrixStack, this.getMediumDatePresentation(backup.lastModifiedDate), (float)(i + 40), (float)(j + 12), 0x4C4C4C);
            int n = RealmsBackupScreen.this.width - 175;
            int o = -3;
            int p = n - 10;
            boolean q = false;
            if (!((RealmsBackupScreen)RealmsBackupScreen.this).serverData.expired) {
                this.drawRestore(matrixStack, n, j + -3, k, l);
            }
            if (!backup.changeList.isEmpty()) {
                this.drawInfo(matrixStack, p, j + 0, k, l);
            }
        }

        private String getMediumDatePresentation(Date lastModifiedDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
        }

        private void drawRestore(MatrixStack matrixStack, int i, int j, int k, int l) {
            boolean bl = k >= i && k <= i + 12 && l >= j && l <= j + 14 && l < RealmsBackupScreen.this.height - 15 && l > 32;
            RealmsBackupScreen.this.client.getTextureManager().bindTexture(field_22687);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 28.0f : 0.0f;
            DrawableHelper.drawTexture(matrixStack, i * 2, j * 2, 0.0f, f, 23, 28, 23, 56);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = field_26471;
            }
        }

        private void drawInfo(MatrixStack matrixStack, int i, int j, int k, int l) {
            boolean bl = k >= i && k <= i + 8 && l >= j && l <= j + 8 && l < RealmsBackupScreen.this.height - 15 && l > 32;
            RealmsBackupScreen.this.client.getTextureManager().bindTexture(field_22686);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 15.0f : 0.0f;
            DrawableHelper.drawTexture(matrixStack, i * 2, j * 2, 0.0f, f, 15, 15, 15, 30);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = field_26472;
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
        public void renderBackground(MatrixStack matrices) {
            RealmsBackupScreen.this.renderBackground(matrices);
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
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
            int i = this.width - 35;
            int j = selectionIndex * this.itemHeight + 36 - (int)this.getScrollAmount();
            int k = i + 10;
            int l = j - 3;
            if (mouseX >= (double)i && mouseX <= (double)(i + 9) && mouseY >= (double)j && mouseY <= (double)(j + 9)) {
                if (!((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)selectionIndex)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.client.openScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(selectionIndex)));
                }
            } else if (mouseX >= (double)k && mouseX < (double)(k + 13) && mouseY >= (double)l && mouseY < (double)(l + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked(selectionIndex);
            }
        }

        @Override
        public void setSelected(int index) {
            this.setSelectedItem(index);
            if (index != -1) {
                Realms.narrateNow(I18n.translate("narrator.select", ((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)index)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(index);
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

