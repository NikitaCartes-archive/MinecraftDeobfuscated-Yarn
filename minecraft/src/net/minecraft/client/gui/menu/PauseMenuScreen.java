package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class PauseMenuScreen extends Screen {
	@Override
	protected void onInitialized() {
		int i = -16;
		int j = 98;
		this.addButton(new class_4185(this.screenWidth / 2 - 102, this.screenHeight / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(null);
				PauseMenuScreen.this.client.field_1729.lockCursor();
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 102, this.screenHeight / 4 + 48 + -16, 98, 20, I18n.translate("gui.advancements")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(new AdvancementsScreen(PauseMenuScreen.this.client.field_1724.networkHandler.getAdvancementHandler()));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 4, this.screenHeight / 4 + 48 + -16, 98, 20, I18n.translate("gui.stats")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(new StatsScreen(PauseMenuScreen.this, PauseMenuScreen.this.client.field_1724.method_3143()));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 102, this.screenHeight / 4 + 72 + -16, 98, 20, I18n.translate("menu.sendFeedback")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(new ConfirmChatLinkScreen((bl, i) -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotfeedback?ref=game");
					}

					PauseMenuScreen.this.client.method_1507(PauseMenuScreen.this);
				}, "https://aka.ms/snapshotfeedback?ref=game", 0, true));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 4, this.screenHeight / 4 + 72 + -16, 98, 20, I18n.translate("menu.reportBugs")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(new ConfirmChatLinkScreen((bl, i) -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
					}

					PauseMenuScreen.this.client.method_1507(PauseMenuScreen.this);
				}, "https://aka.ms/snapshotbugs?ref=game", 0, true));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 102, this.screenHeight / 4 + 96 + -16, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(new SettingsScreen(PauseMenuScreen.this, PauseMenuScreen.this.client.field_1690));
			}
		});
		class_4185 lv = this.addButton(new class_4185(this.screenWidth / 2 + 4, this.screenHeight / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan")) {
			@Override
			public void method_1826() {
				PauseMenuScreen.this.client.method_1507(new OpenToLanScreen(PauseMenuScreen.this));
			}
		});
		lv.enabled = this.client.isIntegratedServerRunning() && !this.client.method_1576().isRemote();
		class_4185 lv2 = this.addButton(new class_4185(this.screenWidth / 2 - 102, this.screenHeight / 4 + 120 + -16, 204, 20, I18n.translate("menu.returnToMenu")) {
			@Override
			public void method_1826() {
				boolean bl = PauseMenuScreen.this.client.isInSingleplayer();
				boolean bl2 = PauseMenuScreen.this.client.isConnectedToRealms();
				this.enabled = false;
				PauseMenuScreen.this.client.field_1687.disconnect();
				if (bl) {
					PauseMenuScreen.this.client.method_18096(new CloseWorldScreen(I18n.translate("menu.savingLevel")));
				} else {
					PauseMenuScreen.this.client.openWorkingScreen();
				}

				if (bl) {
					PauseMenuScreen.this.client.method_1507(new MainMenuScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new MainMenuScreen());
				} else {
					PauseMenuScreen.this.client.method_1507(new MultiplayerScreen(new MainMenuScreen()));
				}
			}
		});
		if (!this.client.isInSingleplayer()) {
			lv2.setText(I18n.translate("menu.disconnect"));
		}
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("menu.game"), this.screenWidth / 2, 40, 16777215);
		super.draw(i, j, f);
	}
}
