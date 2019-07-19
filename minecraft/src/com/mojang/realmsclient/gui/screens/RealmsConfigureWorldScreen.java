package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
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
import javax.annotation.Nonnull;
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
	private RealmsServer serverData;
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

	public RealmsConfigureWorldScreen(RealmsMainScreen lastScreen, long serverId) {
		this.lastScreen = lastScreen;
		this.serverId = serverId;
	}

	@Override
	public void init() {
		if (this.serverData == null) {
			this.fetchServerData(this.serverId);
		}

		this.left_x = this.width() / 2 - 187;
		this.right_x = this.width() / 2 + 190;
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(
			this.playersButton = new RealmsButton(2, this.centerButton(0, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.players")) {
				@Override
				public void onPress() {
					Realms.setScreen(new RealmsPlayerScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData));
				}
			}
		);
		this.buttonsAdd(
			this.settingsButton = new RealmsButton(
				3, this.centerButton(1, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.settings")
			) {
				@Override
				public void onPress() {
					Realms.setScreen(new RealmsSettingsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone()));
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
							RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.lastScreen
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
							((RealmsWorldOptions)RealmsConfigureWorldScreen.this.serverData.slots.get(RealmsConfigureWorldScreen.this.serverData.activeSlot)).clone(),
							RealmsConfigureWorldScreen.this.serverData.worldType,
							RealmsConfigureWorldScreen.this.serverData.activeSlot
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
							RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.serverData.activeSlot
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
							RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.getNewScreen()
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
		if (this.serverData == null) {
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
		RealmsWorldSlotButton realmsWorldSlotButton = new RealmsWorldSlotButton(j, k, 80, 80, () -> this.serverData, string -> this.toolTip = string, l, i, this);
		this.getProxy().buttonsAdd(realmsWorldSlotButton);
	}

	private int leftButton(int i) {
		return this.left_x + i * 95;
	}

	private int centerButton(int i, int total) {
		return this.width() / 2 - (total * 105 - 5) / 2 + i * 105;
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
	public void render(int xm, int ym, float a) {
		this.toolTip = null;
		this.renderBackground();
		this.drawCenteredString(getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.row(4), 16777215);
		super.render(xm, ym, a);
		if (this.serverData == null) {
			this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
		} else {
			String string = this.serverData.getName();
			int i = this.fontWidth(string);
			int j = this.serverData.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
			int k = this.fontWidth(getLocalizedString("mco.configure.world.title"));
			this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 16777215);
			this.drawCenteredString(string, this.width() / 2, 24, j);
			int l = Math.min(this.centerButton(2, 3) + 80 - 11, this.width() / 2 + i / 2 + k / 2 + 10);
			this.drawServerStatus(l, 7, xm, ym);
			if (this.isMinigame()) {
				this.drawString(
					getLocalizedString("mco.configure.current.minigame") + ": " + this.serverData.getMinigameName(),
					this.left_x + 80 + 20 + 10,
					RealmsConstants.row(13),
					16777215
				);
			}

			if (this.toolTip != null) {
				this.renderMousehoverTooltip(this.toolTip, xm, ym);
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
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.lastScreen.removeSelection();
		}

		Realms.setScreen(this.lastScreen);
	}

	private void fetchServerData(long worldId) {
		(new Thread() {
			public void run() {
				RealmsClient realmsClient = RealmsClient.createRealmsClient();

				try {
					RealmsConfigureWorldScreen.this.serverData = realmsClient.getOwnWorld(worldId);
					RealmsConfigureWorldScreen.this.disableButtons();
					if (RealmsConfigureWorldScreen.this.isMinigame()) {
						RealmsConfigureWorldScreen.this.showMinigameButtons();
					} else {
						RealmsConfigureWorldScreen.this.showRegularButtons();
					}
				} catch (RealmsServiceException var3) {
					RealmsConfigureWorldScreen.LOGGER.error("Couldn't get own world");
					Realms.setScreen(new RealmsGenericErrorScreen(var3.getMessage(), RealmsConfigureWorldScreen.this.lastScreen));
				} catch (IOException var4) {
					RealmsConfigureWorldScreen.LOGGER.error("Couldn't parse response getting own world");
				}
			}
		}).start();
	}

	private void disableButtons() {
		this.playersButton.active(!this.serverData.expired);
		this.settingsButton.active(!this.serverData.expired);
		this.subscriptionButton.active(true);
		this.switchMinigameButton.active(!this.serverData.expired);
		this.optionsButton.active(!this.serverData.expired);
		this.resetWorldButton.active(!this.serverData.expired);
	}

	@Override
	public boolean mouseClicked(double x, double y, int buttonNum) {
		return super.mouseClicked(x, y, buttonNum);
	}

	private void joinRealm(RealmsServer serverData) {
		if (this.serverData.state == RealmsServer.State.OPEN) {
			this.lastScreen.play(serverData, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
		} else {
			this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
		}
	}

	@Override
	public void onSlotClick(int slotIndex, @Nonnull RealmsWorldSlotButton.Action action, boolean minigame, boolean empty) {
		switch (action) {
			case NOTHING:
				break;
			case JOIN:
				this.joinRealm(this.serverData);
				break;
			case SWITCH_SLOT:
				if (minigame) {
					this.switchToMinigame();
				} else if (empty) {
					this.switchToEmptySlot(slotIndex, this.serverData);
				} else {
					this.switchToFullSlot(slotIndex, this.serverData);
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

	private void switchToFullSlot(int selectedSlot, RealmsServer serverData) {
		String string = getLocalizedString("mco.configure.world.slot.switch.question.line1");
		String string2 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
		Realms.setScreen(new RealmsLongConfirmationScreen((bl, j) -> {
			if (bl) {
				this.switchSlot(serverData.id, selectedSlot);
			} else {
				Realms.setScreen(this);
			}
		}, RealmsLongConfirmationScreen.Type.Info, string, string2, true, 9));
	}

	private void switchToEmptySlot(int selectedSlot, RealmsServer serverData) {
		String string = getLocalizedString("mco.configure.world.slot.switch.question.line1");
		String string2 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
		Realms.setScreen(
			new RealmsLongConfirmationScreen(
				(bl, j) -> {
					if (bl) {
						RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
							this,
							serverData,
							this.getNewScreen(),
							getLocalizedString("mco.configure.world.switch.slot"),
							getLocalizedString("mco.configure.world.switch.slot.subtitle"),
							10526880,
							getLocalizedString("gui.cancel")
						);
						realmsResetWorldScreen.setSlot(selectedSlot);
						realmsResetWorldScreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
						Realms.setScreen(realmsResetWorldScreen);
					} else {
						Realms.setScreen(this);
					}
				},
				RealmsLongConfirmationScreen.Type.Info,
				string,
				string2,
				true,
				10
			)
		);
	}

	protected void renderMousehoverTooltip(String msg, int x, int y) {
		if (msg != null) {
			int i = x + 12;
			int j = y - 12;
			int k = this.fontWidth(msg);
			if (i + k + 3 > this.right_x) {
				i = i - k - 20;
			}

			this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(msg, i, j, 16777215);
		}
	}

	private void drawServerStatus(int x, int y, int xm, int ym) {
		if (this.serverData.expired) {
			this.drawExpired(x, y, xm, ym);
		} else if (this.serverData.state == RealmsServer.State.CLOSED) {
			this.drawClose(x, y, xm, ym);
		} else if (this.serverData.state == RealmsServer.State.OPEN) {
			if (this.serverData.daysLeft < 7) {
				this.drawExpiring(x, y, xm, ym, this.serverData.daysLeft);
			} else {
				this.drawOpen(x, y, xm, ym);
			}
		}
	}

	private void drawExpired(int x, int y, int xm, int ym) {
		RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		GlStateManager.popMatrix();
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			this.toolTip = getLocalizedString("mco.selectServer.expired");
		}
	}

	private void drawExpiring(int x, int y, int xm, int ym, int daysLeft) {
		RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		if (this.animTick % 20 < 10) {
			RealmsScreen.blit(x, y, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			RealmsScreen.blit(x, y, 10.0F, 0.0F, 10, 28, 20, 28);
		}

		GlStateManager.popMatrix();
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			if (daysLeft <= 0) {
				this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
			} else if (daysLeft == 1) {
				this.toolTip = getLocalizedString("mco.selectServer.expires.day");
			} else {
				this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{daysLeft});
			}
		}
	}

	private void drawOpen(int x, int y, int xm, int ym) {
		RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		GlStateManager.popMatrix();
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			this.toolTip = getLocalizedString("mco.selectServer.open");
		}
	}

	private void drawClose(int x, int y, int xm, int ym) {
		RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		RealmsScreen.blit(x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		GlStateManager.popMatrix();
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			this.toolTip = getLocalizedString("mco.selectServer.closed");
		}
	}

	private boolean isMinigame() {
		return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
	}

	private void hideRegularButtons() {
		this.hide(this.optionsButton);
		this.hide(this.backupButton);
		this.hide(this.resetWorldButton);
	}

	private void hide(RealmsButton button) {
		button.setVisible(false);
		this.removeButton(button);
	}

	private void showRegularButtons() {
		this.show(this.optionsButton);
		this.show(this.backupButton);
		this.show(this.resetWorldButton);
	}

	private void show(RealmsButton button) {
		button.setVisible(true);
		this.buttonsAdd(button);
	}

	private void hideMinigameButtons() {
		this.hide(this.switchMinigameButton);
	}

	private void showMinigameButtons() {
		this.show(this.switchMinigameButton);
	}

	public void saveSlotSettings(RealmsWorldOptions options) {
		RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot);
		options.templateId = realmsWorldOptions.templateId;
		options.templateImage = realmsWorldOptions.templateImage;
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.updateSlot(this.serverData.id, this.serverData.activeSlot, options);
			this.serverData.slots.put(this.serverData.activeSlot, options);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't save slot settings");
			Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
			return;
		} catch (UnsupportedEncodingException var6) {
			LOGGER.error("Couldn't save slot settings");
		}

		Realms.setScreen(this);
	}

	public void saveSettings(String name, String desc) {
		String string = desc != null && !desc.trim().isEmpty() ? desc : null;
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.update(this.serverData.id, name, string);
			this.serverData.setName(name);
			this.serverData.setDescription(string);
		} catch (RealmsServiceException var6) {
			LOGGER.error("Couldn't save settings");
			Realms.setScreen(new RealmsGenericErrorScreen(var6, this));
			return;
		} catch (UnsupportedEncodingException var7) {
			LOGGER.error("Couldn't save settings");
		}

		Realms.setScreen(this);
	}

	public void openTheWorld(boolean join, RealmsScreen screenInCaseOfCancel) {
		RealmsTasks.OpenServerTask openServerTask = new RealmsTasks.OpenServerTask(this.serverData, this, this.lastScreen, join);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(screenInCaseOfCancel, openServerTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public void closeTheWorld(RealmsScreen screenInCaseOfCancel) {
		RealmsTasks.CloseServerTask closeServerTask = new RealmsTasks.CloseServerTask(this.serverData, this);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(screenInCaseOfCancel, closeServerTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public void stateChanged() {
		this.stateChanged = true;
	}

	void callback(WorldTemplate worldTemplate) {
		if (worldTemplate != null) {
			if (WorldTemplate.WorldTemplateType.MINIGAME.equals(worldTemplate.type)) {
				this.switchMinigame(worldTemplate);
			}
		}
	}

	private void switchSlot(long worldId, int selectedSlot) {
		RealmsConfigureWorldScreen realmsConfigureWorldScreen = this.getNewScreen();
		RealmsTasks.SwitchSlotTask switchSlotTask = new RealmsTasks.SwitchSlotTask(worldId, selectedSlot, (bl, i) -> Realms.setScreen(realmsConfigureWorldScreen), 11);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchSlotTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	private void switchMinigame(WorldTemplate selectedWorldTemplate) {
		RealmsTasks.SwitchMinigameTask switchMinigameTask = new RealmsTasks.SwitchMinigameTask(this.serverData.id, selectedWorldTemplate, this.getNewScreen());
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchMinigameTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public RealmsConfigureWorldScreen getNewScreen() {
		return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
	}
}
