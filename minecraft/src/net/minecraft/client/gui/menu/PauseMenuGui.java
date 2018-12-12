package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CloseWorldGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.MainMenuGui;
import net.minecraft.client.gui.ingame.ConfirmChatLinkGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class PauseMenuGui extends Gui {
	@Override
	protected void onInitialized() {
		int i = -16;
		int j = 98;
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(1, this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.translate("menu.returnToMenu")) {
				@Override
				public void onPressed(double d, double e) {
					boolean bl = PauseMenuGui.this.client.isIntegratedServerRunning();
					boolean bl2 = PauseMenuGui.this.client.isConnectedToRealms();
					this.enabled = false;
					PauseMenuGui.this.client.world.method_8525();
					if (bl) {
						PauseMenuGui.this.client.method_1550(null, new CloseWorldGui(I18n.translate("menu.savingLevel")));
					} else {
						PauseMenuGui.this.client.method_1481(null);
					}

					if (bl) {
						PauseMenuGui.this.client.openGui(new MainMenuGui());
					} else if (bl2) {
						RealmsBridge realmsBridge = new RealmsBridge();
						realmsBridge.switchToRealms(new MainMenuGui());
					} else {
						PauseMenuGui.this.client.openGui(new MultiplayerGui(new MainMenuGui()));
					}
				}
			}
		);
		if (!this.client.isIntegratedServerRunning()) {
			buttonWidget.text = I18n.translate("menu.disconnect");
		}

		this.addButton(new ButtonWidget(4, this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(null);
				PauseMenuGui.this.client.field_1729.lockCursor();
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(new SettingsGui(PauseMenuGui.this, PauseMenuGui.this.client.field_1690));
			}
		});
		ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(7, this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(new OpenToLanGui(PauseMenuGui.this));
			}
		});
		buttonWidget2.enabled = this.client.method_1496() && !this.client.getServer().isRemote();
		this.addButton(new ButtonWidget(5, this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.advancements")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(new AdvancementsGui(PauseMenuGui.this.client.player.networkHandler.getAdvancementHandler()));
			}
		});
		this.addButton(new ButtonWidget(6, this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.stats")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(new StatsGui(PauseMenuGui.this, PauseMenuGui.this.client.player.getStats()));
			}
		});
		this.addButton(new ButtonWidget(8, this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, I18n.translate("menu.sendFeedback")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(new ConfirmChatLinkGui((bl, i) -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotfeedback?ref=game");
					}

					PauseMenuGui.this.client.openGui(PauseMenuGui.this);
				}, "https://aka.ms/snapshotfeedback?ref=game", 0, true));
			}
		});
		this.addButton(new ButtonWidget(9, this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, I18n.translate("menu.reportBugs")) {
			@Override
			public void onPressed(double d, double e) {
				PauseMenuGui.this.client.openGui(new ConfirmChatLinkGui((bl, i) -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
					}

					PauseMenuGui.this.client.openGui(PauseMenuGui.this);
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
