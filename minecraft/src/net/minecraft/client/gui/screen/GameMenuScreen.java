package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class GameMenuScreen extends Screen {
	private final boolean showMenu;

	public GameMenuScreen(boolean showMenu) {
		super(showMenu ? new TranslatableText("menu.game") : new TranslatableText("menu.paused"));
		this.showMenu = showMenu;
	}

	@Override
	protected void init() {
		if (this.showMenu) {
			this.initWidgets();
		}
	}

	private void initWidgets() {
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
				buttonWidgetx -> this.minecraft.openScreen(new StatsScreen(this, this.minecraft.player.getStatHandler()))
			)
		);
		String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				this.height / 4 + 72 + -16,
				98,
				20,
				I18n.translate("menu.sendFeedback"),
				buttonWidgetx -> this.minecraft.openScreen(new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							Util.getOperatingSystem().open(string);
						}

						this.minecraft.openScreen(this);
					}, string, true))
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
							Util.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
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
					this.minecraft.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
				} else {
					this.minecraft.disconnect();
				}

				if (bl) {
					this.minecraft.openScreen(new TitleScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new TitleScreen());
				} else {
					this.minecraft.openScreen(new MultiplayerScreen(new TitleScreen()));
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
	public void render(int mouseX, int mouseY, float delta) {
		if (this.showMenu) {
			this.renderBackground();
			this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 40, 16777215);
		} else {
			this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 10, 16777215);
		}

		super.render(mouseX, mouseY, delta);
	}
}
