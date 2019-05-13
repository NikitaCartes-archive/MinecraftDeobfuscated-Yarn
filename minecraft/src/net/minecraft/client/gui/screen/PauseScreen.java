package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class PauseScreen extends Screen {
	private final boolean field_19319;

	public PauseScreen(boolean bl) {
		super(bl ? new TranslatableComponent("menu.game") : new TranslatableComponent("menu.paused"));
		this.field_19319 = bl;
	}

	@Override
	protected void init() {
		if (this.field_19319) {
			this.method_20543();
		}
	}

	private void method_20543() {
		int i = -16;
		int j = 98;
		this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.translate("menu.returnToGame"), buttonWidgetx -> {
			this.minecraft.method_1507(null);
			this.minecraft.mouse.lockCursor();
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 102,
				this.height / 4 + 48 + -16,
				98,
				20,
				I18n.translate("gui.advancements"),
				buttonWidgetx -> this.minecraft.method_1507(new AdvancementsScreen(this.minecraft.player.networkHandler.getAdvancementHandler()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height / 4 + 48 + -16,
				98,
				20,
				I18n.translate("gui.stats"),
				buttonWidgetx -> this.minecraft.method_1507(new StatsScreen(this, this.minecraft.player.getStats()))
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
				buttonWidgetx -> this.minecraft.method_1507(new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open(string);
						}

						this.minecraft.method_1507(this);
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
				buttonWidgetx -> this.minecraft.method_1507(new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
						}

						this.minecraft.method_1507(this);
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
				buttonWidgetx -> this.minecraft.method_1507(new SettingsScreen(this, this.minecraft.options))
			)
		);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(
				this.width / 2 + 4,
				this.height / 4 + 96 + -16,
				98,
				20,
				I18n.translate("menu.shareToLan"),
				buttonWidgetx -> this.minecraft.method_1507(new OpenToLanScreen(this))
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
					this.minecraft.method_18096(new SaveLevelScreen(new TranslatableComponent("menu.savingLevel")));
				} else {
					this.minecraft.disconnect();
				}

				if (bl) {
					this.minecraft.method_1507(new TitleScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new TitleScreen());
				} else {
					this.minecraft.method_1507(new MultiplayerScreen(new TitleScreen()));
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
		if (this.field_19319) {
			this.renderBackground();
			this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
		} else {
			this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
		}

		super.render(i, j, f);
	}
}
