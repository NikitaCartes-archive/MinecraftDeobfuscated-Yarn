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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class PauseMenuScreen extends Screen {
	public PauseMenuScreen() {
		super(new TranslatableTextComponent("menu.game"));
	}

	@Override
	protected void onInitialized() {
		int i = -16;
		int j = 98;
		this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 102, this.screenHeight / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame"), buttonWidgetx -> {
				this.client.openScreen(null);
				this.client.mouse.lockCursor();
			})
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 102,
				this.screenHeight / 4 + 48 + -16,
				98,
				20,
				I18n.translate("gui.advancements"),
				buttonWidgetx -> this.client.openScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 4,
				this.screenHeight / 4 + 48 + -16,
				98,
				20,
				I18n.translate("gui.stats"),
				buttonWidgetx -> this.client.openScreen(new StatsScreen(this, this.client.player.getStats()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 102,
				this.screenHeight / 4 + 72 + -16,
				98,
				20,
				I18n.translate("menu.sendFeedback"),
				buttonWidgetx -> this.client.openScreen(new ConfirmChatLinkScreen((bl, ix) -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotfeedback?ref=game");
						}

						this.client.openScreen(this);
					}, "https://aka.ms/snapshotfeedback?ref=game", 0, true))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 4,
				this.screenHeight / 4 + 72 + -16,
				98,
				20,
				I18n.translate("menu.reportBugs"),
				buttonWidgetx -> this.client.openScreen(new ConfirmChatLinkScreen((bl, ix) -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
						}

						this.client.openScreen(this);
					}, "https://aka.ms/snapshotbugs?ref=game", 0, true))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 102,
				this.screenHeight / 4 + 96 + -16,
				98,
				20,
				I18n.translate("menu.options"),
				buttonWidgetx -> this.client.openScreen(new SettingsScreen(this, this.client.options))
			)
		);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 4,
				this.screenHeight / 4 + 96 + -16,
				98,
				20,
				I18n.translate("menu.shareToLan"),
				buttonWidgetx -> this.client.openScreen(new OpenToLanScreen(this))
			)
		);
		buttonWidget.active = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
		ButtonWidget buttonWidget2 = this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 102, this.screenHeight / 4 + 120 + -16, 204, 20, I18n.translate("menu.returnToMenu"), buttonWidgetx -> {
				boolean bl = this.client.isInSingleplayer();
				boolean bl2 = this.client.isConnectedToRealms();
				buttonWidgetx.active = false;
				this.client.world.disconnect();
				if (bl) {
					this.client.method_18096(new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel")));
				} else {
					this.client.openWorkingScreen();
				}

				if (bl) {
					this.client.openScreen(new MainMenuScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new MainMenuScreen());
				} else {
					this.client.openScreen(new MultiplayerScreen(new MainMenuScreen()));
				}
			})
		);
		if (!this.client.isInSingleplayer()) {
			buttonWidget2.setMessage(I18n.translate("menu.disconnect"));
		}
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 40, 16777215);
		super.render(i, j, f);
	}
}
