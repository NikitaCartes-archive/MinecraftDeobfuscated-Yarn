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
	protected void init() {
		int i = -16;
		int j = 98;
		this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame"), buttonWidgetx -> {
			this.minecraft.openScreen(null);
			this.minecraft.mouse.lockCursor();
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				this.height / 4 + 48 + -16,
				98,
				20,
				I18n.translate("gui.advancements"),
				buttonWidgetx -> this.minecraft.openScreen(new AdvancementsScreen(this.minecraft.player.networkHandler.getAdvancementHandler()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height / 4 + 48 + -16,
				98,
				20,
				I18n.translate("gui.stats"),
				buttonWidgetx -> this.minecraft.openScreen(new StatsScreen(this, this.minecraft.player.getStats()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				this.height / 4 + 72 + -16,
				98,
				20,
				I18n.translate("menu.sendFeedback"),
				buttonWidgetx -> this.minecraft.openScreen(new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotfeedback?ref=game");
						}

						this.minecraft.openScreen(this);
					}, "https://aka.ms/snapshotfeedback?ref=game", true))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height / 4 + 72 + -16,
				98,
				20,
				I18n.translate("menu.reportBugs"),
				buttonWidgetx -> this.minecraft.openScreen(new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
						}

						this.minecraft.openScreen(this);
					}, "https://aka.ms/snapshotbugs?ref=game", true))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				this.height / 4 + 96 + -16,
				98,
				20,
				I18n.translate("menu.options"),
				buttonWidgetx -> this.minecraft.openScreen(new SettingsScreen(this, this.minecraft.options))
			)
		);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height / 4 + 96 + -16,
				98,
				20,
				I18n.translate("menu.shareToLan"),
				buttonWidgetx -> this.minecraft.openScreen(new OpenToLanScreen(this))
			)
		);
		buttonWidget.active = this.minecraft.isIntegratedServerRunning() && !this.minecraft.getServer().isRemote();
		ButtonWidget buttonWidget2 = this.addButton(
			new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.translate("menu.returnToMenu"), buttonWidgetx -> {
				boolean bl = this.minecraft.isInSingleplayer();
				boolean bl2 = this.minecraft.isConnectedToRealms();
				buttonWidgetx.active = false;
				this.minecraft.world.disconnect();
				if (bl) {
					this.minecraft.disconnect(new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel")));
				} else {
					this.minecraft.disconnect();
				}

				if (bl) {
					this.minecraft.openScreen(new MainMenuScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new MainMenuScreen());
				} else {
					this.minecraft.openScreen(new MultiplayerScreen(new MainMenuScreen()));
				}
			})
		);
		if (!this.minecraft.isInSingleplayer()) {
			buttonWidget2.setMessage(I18n.translate("menu.disconnect"));
		}
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
		super.render(i, j, f);
	}
}
