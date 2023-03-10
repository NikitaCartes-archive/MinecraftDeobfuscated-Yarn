/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Identifier PLUS_ICON = new Identifier("realms", "textures/gui/realms/plus_icon.png");
    static final Identifier RESTORE_ICON = new Identifier("realms", "textures/gui/realms/restore_icon.png");
    static final Text RESTORE_TEXT = Text.translatable("mco.backup.button.restore");
    static final Text CHANGES_TOOLTIP = Text.translatable("mco.backup.changes.tooltip");
    private static final Text BACKUPS_TEXT = Text.translatable("mco.configure.world.backup");
    private static final Text NO_BACKUPS_TEXT = Text.translatable("mco.backup.nobackups");
    static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen parent;
    List<Backup> backups = Collections.emptyList();
    @Nullable
    Text tooltip;
    BackupObjectSelectionList backupObjectSelectionList;
    int selectedBackup = -1;
    private final int slotId;
    private ButtonWidget downloadButton;
    private ButtonWidget restoreButton;
    private ButtonWidget changesButton;
    Boolean noBackups = false;
    final RealmsServer serverData;
    private static final String UPLOADED = "Uploaded";

    public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
        super(Text.translatable("mco.configure.world.backup"));
        this.parent = parent;
        this.serverData = serverData;
        this.slotId = slotId;
    }

    @Override
    public void init() {
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (lastScrollPosition != -1) {
            this.backupObjectSelectionList.setScrollAmount(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    List<Backup> list = realmsClient.backupsFor((long)RealmsBackupScreen.this.serverData.id).backups;
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
                    LOGGER.error("Couldn't request backups", realmsServiceException);
                }
            }
        }.start();
        this.downloadButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.backup.button.download"), button -> this.downloadClicked()).dimensions(this.width - 135, RealmsBackupScreen.row(1), 120, 20).build());
        this.restoreButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.backup.button.restore"), button -> this.restoreClicked(this.selectedBackup)).dimensions(this.width - 135, RealmsBackupScreen.row(3), 120, 20).build());
        this.changesButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.backup.changes.tooltip"), button -> {
            this.client.setScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }).dimensions(this.width - 135, RealmsBackupScreen.row(5), 120, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width - 100, this.height - 35, 85, 20).build());
        this.addSelectableChild(this.backupObjectSelectionList);
        this.focusOn(this.backupObjectSelectionList);
        this.updateButtonStates();
    }

    void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int i = 0; i < this.backups.size() - 1; ++i) {
            Backup backup = this.backups.get(i);
            Backup backup2 = this.backups.get(i + 1);
            if (backup.metadata.isEmpty() || backup2.metadata.isEmpty()) continue;
            for (String string : backup.metadata.keySet()) {
                if (!string.contains(UPLOADED) && backup2.metadata.containsKey(string)) {
                    if (backup.metadata.get(string).equals(backup2.metadata.get(string))) continue;
                    this.addToChangeList(backup, string);
                    continue;
                }
                this.addToChangeList(backup, string);
            }
        }
    }

    private void addToChangeList(Backup backup, String key) {
        if (key.contains(UPLOADED)) {
            String string = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
            backup.changeList.put(key, string);
            backup.setUploadedVersion(true);
        } else {
            backup.changeList.put(key, backup.metadata.get(key));
        }
    }

    void updateButtonStates() {
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
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    void restoreClicked(int selectedBackup) {
        if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = selectedBackup;
            Date date = this.backups.get((int)selectedBackup).lastModifiedDate;
            String string = DateFormat.getDateTimeInstance(3, 3).format(date);
            String string2 = RealmsUtil.convertToAgePresentation(date);
            MutableText text = Text.translatable("mco.configure.world.restore.question.line1", string, string2);
            MutableText text2 = Text.translatable("mco.configure.world.restore.question.line2");
            this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
                if (confirmed) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.client.setScreen(this);
                }
            }, RealmsLongConfirmationScreen.Type.WARNING, text, text2, true));
        }
    }

    private void downloadClicked() {
        MutableText text = Text.translatable("mco.configure.world.restore.download.question.line1");
        MutableText text2 = Text.translatable("mco.configure.world.restore.download.question.line2");
        this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
            if (confirmed) {
                this.downloadWorldData();
            } else {
                this.client.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.INFO, text, text2, true));
    }

    private void downloadWorldData() {
        this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup backup = this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltip = null;
        this.renderBackground(matrices);
        this.backupObjectSelectionList.render(matrices, mouseX, mouseY, delta);
        RealmsBackupScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
        this.textRenderer.draw(matrices, BACKUPS_TEXT, (float)((this.width - 150) / 2 - 90), 20.0f, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.textRenderer.draw(matrices, NO_BACKUPS_TEXT, 20.0f, (float)(this.height / 2 - 10), 0xFFFFFF);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltip != null) {
            this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
        }
    }

    protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text text, int mouseX, int mouseY) {
        if (text == null) {
            return;
        }
        int i = mouseX + 12;
        int j = mouseY - 12;
        int k = this.textRenderer.getWidth(text);
        RealmsBackupScreen.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(matrices, text, (float)i, (float)j, 0xFFFFFF);
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
        public int getMaxPosition() {
            return this.getEntryCount() * 36;
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
                if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount()) {
                    this.setSelected(l);
                    this.itemClicked(k, l, mouseX, mouseY, this.width, button);
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
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth, int i) {
            int j = this.width - 35;
            int k = selectionIndex * this.itemHeight + 36 - (int)this.getScrollAmount();
            int l = j + 10;
            int m = k - 3;
            if (mouseX >= (double)j && mouseX <= (double)(j + 9) && mouseY >= (double)k && mouseY <= (double)(k + 9)) {
                if (!RealmsBackupScreen.this.backups.get((int)selectionIndex).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.client.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.backups.get(selectionIndex)));
                }
            } else if (mouseX >= (double)l && mouseX < (double)(l + 13) && mouseY >= (double)m && mouseY < (double)(m + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked(selectionIndex);
            }
        }

        @Override
        public void setSelected(int index) {
            super.setSelected(index);
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

        private void renderBackupItem(MatrixStack matrices, Backup backup, int x, int y, int mouseX, int mouseY) {
            int i = backup.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.textRenderer.draw(matrices, "Backup (" + RealmsUtil.convertToAgePresentation(backup.lastModifiedDate) + ")", (float)(x + 40), (float)(y + 1), i);
            RealmsBackupScreen.this.textRenderer.draw(matrices, this.getMediumDatePresentation(backup.lastModifiedDate), (float)(x + 40), (float)(y + 12), 0x4C4C4C);
            int j = RealmsBackupScreen.this.width - 175;
            int k = -3;
            int l = j - 10;
            boolean m = false;
            if (!RealmsBackupScreen.this.serverData.expired) {
                this.drawRestore(matrices, j, y + -3, mouseX, mouseY);
            }
            if (!backup.changeList.isEmpty()) {
                this.drawInfo(matrices, l, y + 0, mouseX, mouseY);
            }
        }

        private String getMediumDatePresentation(Date lastModifiedDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
        }

        private void drawRestore(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            boolean bl = mouseX >= x && mouseX <= x + 12 && mouseY >= y && mouseY <= y + 14 && mouseY < RealmsBackupScreen.this.height - 15 && mouseY > 32;
            RenderSystem.setShaderTexture(0, RESTORE_ICON);
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            float f = bl ? 28.0f : 0.0f;
            DrawableHelper.drawTexture(matrices, x * 2, y * 2, 0.0f, f, 23, 28, 23, 56);
            matrices.pop();
            if (bl) {
                RealmsBackupScreen.this.tooltip = RESTORE_TEXT;
            }
        }

        private void drawInfo(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            boolean bl = mouseX >= x && mouseX <= x + 8 && mouseY >= y && mouseY <= y + 8 && mouseY < RealmsBackupScreen.this.height - 15 && mouseY > 32;
            RenderSystem.setShaderTexture(0, PLUS_ICON);
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            float f = bl ? 15.0f : 0.0f;
            DrawableHelper.drawTexture(matrices, x * 2, y * 2, 0.0f, f, 15, 15, 15, 30);
            matrices.pop();
            if (bl) {
                RealmsBackupScreen.this.tooltip = CHANGES_TOOLTIP;
            }
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.mBackup.lastModifiedDate.toString());
        }
    }
}

