package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
	private final Screen parent;
	private final Backup backup;
	private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

	public RealmsBackupInfoScreen(Screen parent, Backup backup) {
		this.parent = parent;
		this.backup = backup;
	}

	@Override
	public void tick() {
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent))
		);
		this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.client);
		this.addChild(this.backupInfoList);
		this.focusOn(this.backupInfoList);
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredString(matrices, this.textRenderer, "Changes from last backup", this.width / 2, 10, 16777215);
		this.backupInfoList.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private Text checkForSpecificMetadata(String key, String value) {
		String string = key.toLowerCase(Locale.ROOT);
		if (string.contains("game") && string.contains("mode")) {
			return this.gameModeMetadata(value);
		} else {
			return (Text)(string.contains("game") && string.contains("difficulty") ? this.gameDifficultyMetadata(value) : new LiteralText(value));
		}
	}

	private Text gameDifficultyMetadata(String value) {
		try {
			return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(value)];
		} catch (Exception var3) {
			return new LiteralText("UNKNOWN");
		}
	}

	private Text gameModeMetadata(String value) {
		try {
			return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(value)];
		} catch (Exception var3) {
			return new LiteralText("UNKNOWN");
		}
	}

	@Environment(EnvType.CLIENT)
	class BackupInfoList extends AlwaysSelectedEntryListWidget<RealmsBackupInfoScreen.class_5344> {
		public BackupInfoList(MinecraftClient minecraftClient) {
			super(minecraftClient, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
			this.setRenderSelection(false);
			if (RealmsBackupInfoScreen.this.backup.changeList != null) {
				RealmsBackupInfoScreen.this.backup.changeList.forEach((string, string2) -> this.addEntry(RealmsBackupInfoScreen.this.new class_5344(string, string2)));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_5344 extends AlwaysSelectedEntryListWidget.Entry<RealmsBackupInfoScreen.class_5344> {
		private final String field_25258;
		private final String field_25259;

		public class_5344(String string, String string2) {
			this.field_25258 = string;
			this.field_25259 = string2;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer textRenderer = RealmsBackupInfoScreen.this.client.textRenderer;
			RealmsBackupInfoScreen.this.drawStringWithShadow(matrices, textRenderer, this.field_25258, x, y, 10526880);
			RealmsBackupInfoScreen.this.drawTextWithShadow(
				matrices, textRenderer, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.field_25258, this.field_25259), x, y + 12, 16777215
			);
		}
	}
}
