package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class PauseMenuScreen extends Screen {
	@Override
	protected void onInitialized() {
		int i = -16;
		int j = 98;
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(1, this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.translate("menu.returnToMenu")) {
				@Override
				public void onPressed(double d, double e) {
					boolean bl = PauseMenuScreen.this.client.isInSingleplayer();
					boolean bl2 = PauseMenuScreen.this.client.isConnectedToRealms();
					this.enabled = false;
					PauseMenuScreen.this.client.world.method_8525();
					if (bl) {
						PauseMenuScreen.this.client.method_1550(null, new CloseWorldScreen(I18n.translate("menu.savingLevel")));
					} else {
						PauseMenuScreen.this.client.method_1481(null);
					}

					if (bl) {
						PauseMenuScreen.this.client.openScreen(new MainMenuScreen());
					} else if (bl2) {
						RealmsBridge realmsBridge = new RealmsBridge();
						realmsBridge.switchToRealms(new MainMenuScreen());
					} else {
						PauseMenuScreen.this.client.openScreen(new MultiplayerScreen(new MainMenuScreen()));
					}
				}
			}
		);
		if (!this.client.isInSingleplayer()) {
			buttonWidget.text = I18n.translate("menu.disconnect");
		}

		this.addButton(new ButtonWidget(4, this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(null);
				PauseMenuScreen.this.client.mouse.lockCursor();
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(new SettingsScreen(PauseMenuScreen.this, PauseMenuScreen.this.client.options));
			}
		});
		ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(7, this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(new OpenToLanScreen(PauseMenuScreen.this));
			}
		});
		buttonWidget2.enabled = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
		this.addButton(new ButtonWidget(5, this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.advancements")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(new AdvancementsScreen(PauseMenuScreen.this.client.player.networkHandler.getAdvancementHandler()));
			}
		});
		this.addButton(new ButtonWidget(6, this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.stats")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(new StatsScreen(PauseMenuScreen.this, PauseMenuScreen.this.client.player.getStats()));
			}
		});
		this.addButton(new ButtonWidget(8, this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, I18n.translate("menu.sendFeedback")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(new ConfirmChatLinkScreen((bl, i) -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotfeedback?ref=game");
					}

					PauseMenuScreen.this.client.openScreen(PauseMenuScreen.this);
				}, "https://aka.ms/snapshotfeedback?ref=game", 0, true));
			}
		});
		this.addButton(new ButtonWidget(9, this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, I18n.translate("menu.reportBugs")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuScreen.this.client.openScreen(new ConfirmChatLinkScreen((bl, i) -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
					}

					PauseMenuScreen.this.client.openScreen(PauseMenuScreen.this);
				}, "https://aka.ms/snapshotbugs?ref=game", 0, true));
			}
		});
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("menu.game"), this.width / 2, 40, 16777215);
		super.draw(i, j, f);
	}
}
