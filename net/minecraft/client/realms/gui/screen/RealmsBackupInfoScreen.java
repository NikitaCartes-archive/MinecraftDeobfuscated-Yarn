/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupInfoScreen
extends RealmsScreen {
    private final Screen parent;
    private final Backup backup;
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen parent, Backup backup) {
        this.parent = parent;
        this.backup = backup;
    }

    @Override
    public void tick() {
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
        this.backupInfoList = new BackupInfoList(this.client);
        this.addChild(this.backupInfoList);
        this.focusOn(this.backupInfoList);
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsBackupInfoScreen.drawCenteredString(matrices, this.textRenderer, "Changes from last backup", this.width / 2, 10, 0xFFFFFF);
        this.backupInfoList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private Text checkForSpecificMetadata(String key, String value) {
        String string = key.toLowerCase(Locale.ROOT);
        if (string.contains("game") && string.contains("mode")) {
            return this.gameModeMetadata(value);
        }
        if (string.contains("game") && string.contains("difficulty")) {
            return this.gameDifficultyMetadata(value);
        }
        return new LiteralText(value);
    }

    private Text gameDifficultyMetadata(String value) {
        try {
            return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(value)];
        } catch (Exception exception) {
            return new LiteralText("UNKNOWN");
        }
    }

    private Text gameModeMetadata(String value) {
        try {
            return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(value)];
        } catch (Exception exception) {
            return new LiteralText("UNKNOWN");
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupInfoList
    extends AlwaysSelectedEntryListWidget<BackupInfoListEntry> {
        public BackupInfoList(MinecraftClient minecraftClient) {
            super(minecraftClient, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
            this.setRenderSelection(false);
            if (((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).backup.changeList != null) {
                ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).backup.changeList.forEach((key, value) -> this.addEntry(new BackupInfoListEntry((String)key, (String)value)));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupInfoListEntry
    extends AlwaysSelectedEntryListWidget.Entry<BackupInfoListEntry> {
        private final String key;
        private final String value;

        public BackupInfoListEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).client.textRenderer;
            DrawableHelper.drawStringWithShadow(matrices, textRenderer, this.key, x, y, 0xA0A0A0);
            DrawableHelper.drawTextWithShadow(matrices, textRenderer, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), x, y + 12, 0xFFFFFF);
        }
    }
}

