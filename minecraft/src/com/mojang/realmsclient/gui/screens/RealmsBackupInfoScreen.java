package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.Backup;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

@Environment(EnvType.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
	private final RealmsScreen lastScreen;
	private final int BUTTON_BACK_ID = 0;
	private final Backup backup;
	private final List<String> keys = Lists.<String>newArrayList();
	private RealmsBackupInfoScreen.BackupInfoList backupInfoList;
	String[] difficulties = new String[]{
		getLocalizedString("options.difficulty.peaceful"),
		getLocalizedString("options.difficulty.easy"),
		getLocalizedString("options.difficulty.normal"),
		getLocalizedString("options.difficulty.hard")
	};
	String[] gameModes = new String[]{
		getLocalizedString("selectWorld.gameMode.survival"),
		getLocalizedString("selectWorld.gameMode.creative"),
		getLocalizedString("selectWorld.gameMode.adventure")
	};

	public RealmsBackupInfoScreen(RealmsScreen lastScreen, Backup backup) {
		this.lastScreen = lastScreen;
		this.backup = backup;
		if (backup.changeList != null) {
			for (Entry<String, String> entry : backup.changeList.entrySet()) {
				this.keys.add(entry.getKey());
			}
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 24, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(RealmsBackupInfoScreen.this.lastScreen);
			}
		});
		this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList();
		this.addWidget(this.backupInfoList);
		this.focusOn(this.backupInfoList);
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			Realms.setScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		this.drawCenteredString("Changes from last backup", this.width() / 2, 10, 16777215);
		this.backupInfoList.render(xm, ym, a);
		super.render(xm, ym, a);
	}

	private String checkForSpecificMetadata(String key, String value) {
		String string = key.toLowerCase(Locale.ROOT);
		if (string.contains("game") && string.contains("mode")) {
			return this.gameModeMetadata(value);
		} else {
			return string.contains("game") && string.contains("difficulty") ? this.gameDifficultyMetadata(value) : value;
		}
	}

	private String gameDifficultyMetadata(String value) {
		try {
			return this.difficulties[Integer.parseInt(value)];
		} catch (Exception var3) {
			return "UNKNOWN";
		}
	}

	private String gameModeMetadata(String value) {
		try {
			return this.gameModes[Integer.parseInt(value)];
		} catch (Exception var3) {
			return "UNKNOWN";
		}
	}

	@Environment(EnvType.CLIENT)
	class BackupInfoList extends RealmsSimpleScrolledSelectionList {
		public BackupInfoList() {
			super(RealmsBackupInfoScreen.this.width(), RealmsBackupInfoScreen.this.height(), 32, RealmsBackupInfoScreen.this.height() - 64, 36);
		}

		@Override
		public int getItemCount() {
			return RealmsBackupInfoScreen.this.backup.changeList.size();
		}

		@Override
		public boolean isSelectedItem(int item) {
			return false;
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public void renderBackground() {
		}

		@Override
		public void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
			String string = (String)RealmsBackupInfoScreen.this.keys.get(i);
			RealmsBackupInfoScreen.this.drawString(string, this.width() / 2 - 40, y, 10526880);
			String string2 = (String)RealmsBackupInfoScreen.this.backup.changeList.get(string);
			RealmsBackupInfoScreen.this.drawString(RealmsBackupInfoScreen.this.checkForSpecificMetadata(string, string2), this.width() / 2 - 40, y + 12, 16777215);
		}
	}
}
