package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.RealmsTasks;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback<WorldTemplate> implements RealmsWorldSlotButton.Listener {
	private static final Logger LOGGER = LogManager.getLogger();
	private String toolTip;
	private final RealmsMainScreen lastScreen;
	private RealmsServer field_20493;
	private final long serverId;
	private int left_x;
	private int right_x;
	private final int default_button_width = 80;
	private final int default_button_offset = 5;
	private RealmsButton playersButton;
	private RealmsButton settingsButton;
	private RealmsButton subscriptionButton;
	private RealmsButton optionsButton;
	private RealmsButton backupButton;
	private RealmsButton resetWorldButton;
	private RealmsButton switchMinigameButton;
	private boolean stateChanged;
	private int animTick;
	private int clicks;

	public RealmsConfigureWorldScreen(RealmsMainScreen realmsMainScreen, long l) {
		this.lastScreen = realmsMainScreen;
		this.serverId = l;
	}

	@Override
	public void init() {
		if (this.field_20493 == null) {
			this.fetchServerData(this.serverId);
		}

		this.left_x = this.width() / 2 - 187;
		this.right_x = this.width() / 2 + 190;
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(
			this.playersButton = new RealmsButton(2, this.centerButton(0, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.players")) {
				@Override
				public void onPress() {
					Realms.setScreen(new RealmsPlayerScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_20493));
				}
			}
		);
		this.buttonsAdd(
			this.settingsButton = new RealmsButton(
				3, this.centerButton(1, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.settings")
			) {
				@Override
				public void onPress() {
					Realms.setScreen(new RealmsSettingsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_20493.clone()));
				}
			}
		);
		this.buttonsAdd(
			this.subscriptionButton = new RealmsButton(
				4, this.centerButton(2, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.subscription")
			) {
				@Override
				public void onPress() {
					Realms.setScreen(
						new RealmsSubscriptionInfoScreen(
							RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_20493.clone(), RealmsConfigureWorldScreen.this.lastScreen
						)
					);
				}
			}
		);

		for (int i = 1; i < 5; i++) {
			this.addSlotButton(i);
		}

		this.buttonsAdd(
			this.switchMinigameButton = new RealmsButton(
				8, this.leftButton(0), RealmsConstants.row(13) - 5, 100, 20, getLocalizedString("mco.configure.world.buttons.switchminigame")
			) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						RealmsConfigureWorldScreen.this, RealmsServer.WorldType.MINIGAME
					);
					realmsSelectWorldTemplateScreen.setTitle(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
					Realms.setScreen(realmsSelectWorldTemplateScreen);
				}
			}
		);
		this.buttonsAdd(
			this.optionsButton = new RealmsButton(5, this.leftButton(0), RealmsConstants.row(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.options")) {
				@Override
				public void onPress() {
					Realms.setScreen(
						new RealmsSlotOptionsScreen(
							RealmsConfigureWorldScreen.this,
							((RealmsWorldOptions)RealmsConfigureWorldScreen.this.field_20493.slots.get(RealmsConfigureWorldScreen.this.field_20493.activeSlot)).clone(),
							RealmsConfigureWorldScreen.this.field_20493.worldType,
							RealmsConfigureWorldScreen.this.field_20493.activeSlot
						)
					);
				}
			}
		);
		this.buttonsAdd(
			this.backupButton = new RealmsButton(6, this.leftButton(1), RealmsConstants.row(13) - 5, 90, 20, getLocalizedString("mco.configure.world.backup")) {
				@Override
				public void onPress() {
					Realms.setScreen(
						new RealmsBackupScreen(
							RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_20493.clone(), RealmsConfigureWorldScreen.this.field_20493.activeSlot
						)
					);
				}
			}
		);
		this.buttonsAdd(
			this.resetWorldButton = new RealmsButton(
				7, this.leftButton(2), RealmsConstants.row(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.resetworld")
			) {
				@Override
				public void onPress() {
					Realms.setScreen(
						new RealmsResetWorldScreen(
							RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_20493.clone(), RealmsConfigureWorldScreen.this.getNewScreen()
						)
					);
				}
			}
		);
		this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				RealmsConfigureWorldScreen.this.backButtonClicked();
			}
		});
		this.backupButton.active(true);
		if (this.field_20493 == null) {
			this.hideMinigameButtons();
			this.hideRegularButtons();
			this.playersButton.active(false);
			this.settingsButton.active(false);
			this.subscriptionButton.active(false);
		} else {
			this.disableButtons();
			if (this.isMinigame()) {
				this.hideRegularButtons();
			} else {
				this.hideMinigameButtons();
			}
		}
	}

	private void addSlotButton(int i) {
		int j = this.frame(i);
		int k = RealmsConstants.row(5) + 5;
		int l = 100 + i;
		RealmsWorldSlotButton realmsWorldSlotButton = new RealmsWorldSlotButton(j, k, 80, 80, () -> this.field_20493, string -> this.toolTip = string, l, i, this);
		this.getProxy().buttonsAdd(realmsWorldSlotButton);
	}

	private int leftButton(int i) {
		return this.left_x + i * 95;
	}

	private int centerButton(int i, int j) {
		return this.width() / 2 - (j * 105 - 5) / 2 + i * 105;
	}

	@Override
	public void tick() {
		this.tickButtons();
		this.animTick++;
		this.clicks--;
		if (this.clicks < 0) {
			this.clicks = 0;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.toolTip = null;
		this.renderBackground();
		this.drawCenteredString(getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.row(4), 16777215);
		super.render(i, j, f);
		if (this.field_20493 == null) {
			this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
		} else {
			String string = this.field_20493.getName();
			int k = this.fontWidth(string);
			int l = this.field_20493.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
			int m = this.fontWidth(getLocalizedString("mco.configure.world.title"));
			this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 16777215);
			this.drawCenteredString(string, this.width() / 2, 24, l);
			int n = Math.min(this.centerButton(2, 3) + 80 - 11, this.width() / 2 + k / 2 + m / 2 + 10);
			this.drawServerStatus(n, 7, i, j);
			if (this.isMinigame()) {
				this.drawString(
					getLocalizedString("mco.configure.current.minigame") + ": " + this.field_20493.getMinigameName(),
					this.left_x + 80 + 20 + 10,
					RealmsConstants.row(13),
					16777215
				);
			}

			if (this.toolTip != null) {
				this.renderMousehoverTooltip(this.toolTip, i, j);
			}
		}
	}

	private int frame(int i) {
		return this.left_x + (i - 1) * 98;
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.lastScreen.removeSelection();
		}

		Realms.setScreen(this.lastScreen);
	}

	private void fetchServerData(long l) {
		new Thread(() -> {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			try {
				this.field_20493 = realmsClient.getOwnWorld(l);
				this.disableButtons();
				if (this.isMinigame()) {
					this.showMinigameButtons();
				} else {
					this.showRegularButtons();
				}
			} catch (RealmsServiceException var5) {
				LOGGER.error("Couldn't get own world");
				Realms.setScreen(new RealmsGenericErrorScreen(var5.getMessage(), this.lastScreen));
			} catch (IOException var6) {
				LOGGER.error("Couldn't parse response getting own world");
			}
		}).start();
	}

	private void disableButtons() {
		this.playersButton.active(!this.field_20493.expired);
		this.settingsButton.active(!this.field_20493.expired);
		this.subscriptionButton.active(true);
		this.switchMinigameButton.active(!this.field_20493.expired);
		this.optionsButton.active(!this.field_20493.expired);
		this.resetWorldButton.active(!this.field_20493.expired);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return super.mouseClicked(d, e, i);
	}

	private void joinRealm(RealmsServer realmsServer) {
		if (this.field_20493.state == RealmsServer.State.OPEN) {
			this.lastScreen.play(realmsServer, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
		} else {
			this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
		}
	}

	@Override
	public void onSlotClick(int i, RealmsWorldSlotButton.Action action, boolean bl, boolean bl2) {
		switch (action) {
			case NOTHING:
				break;
			case JOIN:
				this.joinRealm(this.field_20493);
				break;
			case SWITCH_SLOT:
				if (bl) {
					this.switchToMinigame();
				} else if (bl2) {
					this.switchToEmptySlot(i, this.field_20493);
				} else {
					this.switchToFullSlot(i, this.field_20493);
				}
				break;
			default:
				throw new IllegalStateException("Unknown action " + action);
		}
	}

	private void switchToMinigame() {
		RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
		realmsSelectWorldTemplateScreen.setTitle(getLocalizedString("mco.template.title.minigame"));
		realmsSelectWorldTemplateScreen.setWarning(getLocalizedString("mco.minigame.world.info.line1") + "\\n" + getLocalizedString("mco.minigame.world.info.line2"));
		Realms.setScreen(realmsSelectWorldTemplateScreen);
	}

	private void switchToFullSlot(int i, RealmsServer realmsServer) {
		String string = getLocalizedString("mco.configure.world.slot.switch.question.line1");
		String string2 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
		Realms.setScreen(new RealmsLongConfirmationScreen((bl, j) -> {
			if (bl) {
				this.switchSlot(realmsServer.id, i);
			} else {
				Realms.setScreen(this);
			}
		}, RealmsLongConfirmationScreen.Type.INFO, string, string2, true, 9));
	}

	private void switchToEmptySlot(int i, RealmsServer realmsServer) {
		String string = getLocalizedString("mco.configure.world.slot.switch.question.line1");
		String string2 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
		Realms.setScreen(
			new RealmsLongConfirmationScreen(
				(bl, j) -> {
					if (bl) {
						RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
							this,
							realmsServer,
							this.getNewScreen(),
							getLocalizedString("mco.configure.world.switch.slot"),
							getLocalizedString("mco.configure.world.switch.slot.subtitle"),
							10526880,
							getLocalizedString("gui.cancel")
						);
						realmsResetWorldScreen.setSlot(i);
						realmsResetWorldScreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
						Realms.setScreen(realmsResetWorldScreen);
					} else {
						Realms.setScreen(this);
					}
				},
				RealmsLongConfirmationScreen.Type.INFO,
				string,
				string2,
				true,
				10
			)
		);
	}

	protected void renderMousehoverTooltip(String string, int i, int j) {
		if (string != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.fontWidth(string);
			if (k + m + 3 > this.right_x) {
				k = k - m - 20;
			}

			this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, k, l, 16777215);
		}
	}

	private void drawServerStatus(int i, int j, int k, int l) {
		if (this.field_20493.expired) {
			this.drawExpired(i, j, k, l);
		} else if (this.field_20493.state == RealmsServer.State.CLOSED) {
			this.drawClose(i, j, k, l);
		} else if (this.field_20493.state == RealmsServer.State.OPEN) {
			if (this.field_20493.daysLeft < 7) {
				this.drawExpiring(i, j, k, l, this.field_20493.daysLeft);
			} else {
				this.drawOpen(i, j, k, l);
			}
		}
	}

	private void drawExpired(int i, int j, int k, int l) {
		RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 10, 28);
		RenderSystem.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
			this.toolTip = getLocalizedString("mco.selectServer.expired");
		}
	}

	private void drawExpiring(int i, int j, int k, int l, int m) {
		RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		if (this.animTick % 20 < 10) {
			RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			RealmsScreen.blit(i, j, 10.0F, 0.0F, 10, 28, 20, 28);
		}

		RenderSystem.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
			if (m <= 0) {
				this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
			} else if (m == 1) {
				this.toolTip = getLocalizedString("mco.selectServer.expires.day");
			} else {
				this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{m});
			}
		}
	}

	private void drawOpen(int i, int j, int k, int l) {
		RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 10, 28);
		RenderSystem.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
			this.toolTip = getLocalizedString("mco.selectServer.open");
		}
	}

	private void drawClose(int i, int j, int k, int l) {
		RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		RealmsScreen.blit(i, j, 0.0F, 0.0F, 10, 28, 10, 28);
		RenderSystem.popMatrix();
		if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
			this.toolTip = getLocalizedString("mco.selectServer.closed");
		}
	}

	private boolean isMinigame() {
		return this.field_20493 != null && this.field_20493.worldType.equals(RealmsServer.WorldType.MINIGAME);
	}

	private void hideRegularButtons() {
		this.hide(this.optionsButton);
		this.hide(this.backupButton);
		this.hide(this.resetWorldButton);
	}

	private void hide(RealmsButton realmsButton) {
		realmsButton.setVisible(false);
		this.removeButton(realmsButton);
	}

	private void showRegularButtons() {
		this.show(this.optionsButton);
		this.show(this.backupButton);
		this.show(this.resetWorldButton);
	}

	private void show(RealmsButton realmsButton) {
		realmsButton.setVisible(true);
		this.buttonsAdd(realmsButton);
	}

	private void hideMinigameButtons() {
		this.hide(this.switchMinigameButton);
	}

	private void showMinigameButtons() {
		this.show(this.switchMinigameButton);
	}

	public void saveSlotSettings(RealmsWorldOptions realmsWorldOptions) {
		RealmsWorldOptions realmsWorldOptions2 = (RealmsWorldOptions)this.field_20493.slots.get(this.field_20493.activeSlot);
		realmsWorldOptions.templateId = realmsWorldOptions2.templateId;
		realmsWorldOptions.templateImage = realmsWorldOptions2.templateImage;
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.updateSlot(this.field_20493.id, this.field_20493.activeSlot, realmsWorldOptions);
			this.field_20493.slots.put(this.field_20493.activeSlot, realmsWorldOptions);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't save slot settings");
			Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
			return;
		} catch (UnsupportedEncodingException var6) {
			LOGGER.error("Couldn't save slot settings");
		}

		Realms.setScreen(this);
	}

	public void saveSettings(String string, String string2) {
		String string3 = string2 != null && !string2.trim().isEmpty() ? string2 : null;
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.update(this.field_20493.id, string, string3);
			this.field_20493.setName(string);
			this.field_20493.setDescription(string3);
		} catch (RealmsServiceException var6) {
			LOGGER.error("Couldn't save settings");
			Realms.setScreen(new RealmsGenericErrorScreen(var6, this));
			return;
		} catch (UnsupportedEncodingException var7) {
			LOGGER.error("Couldn't save settings");
		}

		Realms.setScreen(this);
	}

	public void openTheWorld(boolean bl, RealmsScreen realmsScreen) {
		RealmsTasks.OpenServerTask openServerTask = new RealmsTasks.OpenServerTask(this.field_20493, this, this.lastScreen, bl);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(realmsScreen, openServerTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public void closeTheWorld(RealmsScreen realmsScreen) {
		RealmsTasks.CloseServerTask closeServerTask = new RealmsTasks.CloseServerTask(this.field_20493, this);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(realmsScreen, closeServerTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public void stateChanged() {
		this.stateChanged = true;
	}

	void method_21209(WorldTemplate worldTemplate) {
		if (worldTemplate != null) {
			if (WorldTemplate.WorldTemplateType.MINIGAME.equals(worldTemplate.type)) {
				this.switchMinigame(worldTemplate);
			}
		}
	}

	private void switchSlot(long l, int i) {
		RealmsConfigureWorldScreen realmsConfigureWorldScreen = this.getNewScreen();
		RealmsTasks.SwitchSlotTask switchSlotTask = new RealmsTasks.SwitchSlotTask(l, i, (bl, ix) -> Realms.setScreen(realmsConfigureWorldScreen), 11);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchSlotTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	private void switchMinigame(WorldTemplate worldTemplate) {
		RealmsTasks.SwitchMinigameTask switchMinigameTask = new RealmsTasks.SwitchMinigameTask(this.field_20493.id, worldTemplate, this.getNewScreen());
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchMinigameTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public RealmsConfigureWorldScreen getNewScreen() {
		return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
	}
}
