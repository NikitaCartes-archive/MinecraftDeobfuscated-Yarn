package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.CloseServerTask;
import net.minecraft.realms.OpenServerTask;
import net.minecraft.realms.SwitchMinigameTask;
import net.minecraft.realms.SwitchSlotTask;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
	private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
	private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
	private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
	private String toolTip;
	private final RealmsMainScreen lastScreen;
	@Nullable
	private RealmsServer server;
	private final long serverId;
	private int left_x;
	private int right_x;
	private ButtonWidget playersButton;
	private ButtonWidget settingsButton;
	private ButtonWidget subscriptionButton;
	private ButtonWidget optionsButton;
	private ButtonWidget backupButton;
	private ButtonWidget resetWorldButton;
	private ButtonWidget switchMinigameButton;
	private boolean stateChanged;
	private int animTick;
	private int clicks;

	public RealmsConfigureWorldScreen(RealmsMainScreen lastScreen, long serverId) {
		this.lastScreen = lastScreen;
		this.serverId = serverId;
	}

	@Override
	public void init() {
		if (this.server == null) {
			this.fetchServerData(this.serverId);
		}

		this.left_x = this.width / 2 - 187;
		this.right_x = this.width / 2 + 190;
		this.client.keyboard.enableRepeatEvents(true);
		this.playersButton = this.addButton(
			new ButtonWidget(
				this.buttonCenter(0, 3),
				row(0),
				100,
				20,
				I18n.translate("mco.configure.world.buttons.players"),
				buttonWidget -> this.client.openScreen(new RealmsPlayerScreen(this, this.server))
			)
		);
		this.settingsButton = this.addButton(
			new ButtonWidget(
				this.buttonCenter(1, 3),
				row(0),
				100,
				20,
				I18n.translate("mco.configure.world.buttons.settings"),
				buttonWidget -> this.client.openScreen(new RealmsSettingsScreen(this, this.server.clone()))
			)
		);
		this.subscriptionButton = this.addButton(
			new ButtonWidget(
				this.buttonCenter(2, 3),
				row(0),
				100,
				20,
				I18n.translate("mco.configure.world.buttons.subscription"),
				buttonWidget -> this.client.openScreen(new RealmsSubscriptionInfoScreen(this, this.server.clone(), this.lastScreen))
			)
		);

		for (int i = 1; i < 5; i++) {
			this.addSlotButton(i);
		}

		this.switchMinigameButton = this.addButton(
			new ButtonWidget(this.buttonLeft(0), row(13) - 5, 100, 20, I18n.translate("mco.configure.world.buttons.switchminigame"), buttonWidget -> {
				RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
				realmsSelectWorldTemplateScreen.setTitle(I18n.translate("mco.template.title.minigame"));
				this.client.openScreen(realmsSelectWorldTemplateScreen);
			})
		);
		this.optionsButton = this.addButton(
			new ButtonWidget(
				this.buttonLeft(0),
				row(13) - 5,
				90,
				20,
				I18n.translate("mco.configure.world.buttons.options"),
				buttonWidget -> this.client
						.openScreen(
							new RealmsSlotOptionsScreen(
								this, ((RealmsWorldOptions)this.server.slots.get(this.server.activeSlot)).clone(), this.server.worldType, this.server.activeSlot
							)
						)
			)
		);
		this.backupButton = this.addButton(
			new ButtonWidget(
				this.buttonLeft(1),
				row(13) - 5,
				90,
				20,
				I18n.translate("mco.configure.world.backup"),
				buttonWidget -> this.client.openScreen(new RealmsBackupScreen(this, this.server.clone(), this.server.activeSlot))
			)
		);
		this.resetWorldButton = this.addButton(
			new ButtonWidget(
				this.buttonLeft(2),
				row(13) - 5,
				90,
				20,
				I18n.translate("mco.configure.world.buttons.resetworld"),
				buttonWidget -> this.client
						.openScreen(
							new RealmsResetWorldScreen(
								this, this.server.clone(), () -> this.client.openScreen(this.getNewScreen()), () -> this.client.openScreen(this.getNewScreen())
							)
						)
			)
		);
		this.addButton(new ButtonWidget(this.right_x - 80 + 8, row(13) - 5, 70, 20, I18n.translate("gui.back"), buttonWidget -> this.backButtonClicked()));
		this.backupButton.active = true;
		if (this.server == null) {
			this.hideMinigameButtons();
			this.hideRegularButtons();
			this.playersButton.active = false;
			this.settingsButton.active = false;
			this.subscriptionButton.active = false;
		} else {
			this.disableButtons();
			if (this.isMinigame()) {
				this.hideRegularButtons();
			} else {
				this.hideMinigameButtons();
			}
		}
	}

	private void addSlotButton(int slotIndex) {
		int i = this.frame(slotIndex);
		int j = row(5) + 5;
		RealmsWorldSlotButton realmsWorldSlotButton = new RealmsWorldSlotButton(
			i, j, 80, 80, () -> this.server, string -> this.toolTip = string, slotIndex, buttonWidget -> {
				RealmsWorldSlotButton.State state = ((RealmsWorldSlotButton)buttonWidget).getState();
				if (state != null) {
					switch (state.action) {
						case NOTHING:
							break;
						case JOIN:
							this.joinRealm(this.server);
							break;
						case SWITCH_SLOT:
							if (state.minigame) {
								this.switchToMinigame();
							} else if (state.empty) {
								this.switchToEmptySlot(slotIndex, this.server);
							} else {
								this.switchToFullSlot(slotIndex, this.server);
							}
							break;
						default:
							throw new IllegalStateException("Unknown action " + state.action);
					}
				}
			}
		);
		this.addButton(realmsWorldSlotButton);
	}

	private int buttonLeft(int i) {
		return this.left_x + i * 95;
	}

	private int buttonCenter(int i, int total) {
		return this.width / 2 - (total * 105 - 5) / 2 + i * 105;
	}

	@Override
	public void tick() {
		super.tick();
		this.animTick++;
		this.clicks--;
		if (this.clicks < 0) {
			this.clicks = 0;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.toolTip = null;
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("mco.configure.worlds.title"), this.width / 2, row(4), 16777215);
		super.render(mouseX, mouseY, delta);
		if (this.server == null) {
			this.drawCenteredString(this.textRenderer, I18n.translate("mco.configure.world.title"), this.width / 2, 17, 16777215);
		} else {
			String string = this.server.getName();
			int i = this.textRenderer.getStringWidth(string);
			int j = this.server.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
			int k = this.textRenderer.getStringWidth(I18n.translate("mco.configure.world.title"));
			this.drawCenteredString(this.textRenderer, I18n.translate("mco.configure.world.title"), this.width / 2, 12, 16777215);
			this.drawCenteredString(this.textRenderer, string, this.width / 2, 24, j);
			int l = Math.min(this.buttonCenter(2, 3) + 80 - 11, this.width / 2 + i / 2 + k / 2 + 10);
			this.drawServerStatus(l, 7, mouseX, mouseY);
			if (this.isMinigame()) {
				this.textRenderer
					.draw(
						I18n.translate("mco.configure.current.minigame") + ": " + this.server.getMinigameName(), (float)(this.left_x + 80 + 20 + 10), (float)row(13), 16777215
					);
			}

			if (this.toolTip != null) {
				this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
			}
		}
	}

	private int frame(int ordinal) {
		return this.left_x + (ordinal - 1) * 98;
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.lastScreen.removeSelection();
		}

		this.client.openScreen(this.lastScreen);
	}

	private void fetchServerData(long worldId) {
		new Thread(() -> {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			try {
				this.server = realmsClient.getOwnWorld(worldId);
				this.disableButtons();
				if (this.isMinigame()) {
					this.addButton(this.switchMinigameButton);
				} else {
					this.addButton(this.optionsButton);
					this.addButton(this.backupButton);
					this.addButton(this.resetWorldButton);
				}
			} catch (RealmsServiceException var5) {
				LOGGER.error("Couldn't get own world");
				this.client.execute(() -> this.client.openScreen(new RealmsGenericErrorScreen(var5.getMessage(), this.lastScreen)));
			}
		}).start();
	}

	private void disableButtons() {
		this.playersButton.active = !this.server.expired;
		this.settingsButton.active = !this.server.expired;
		this.subscriptionButton.active = true;
		this.switchMinigameButton.active = !this.server.expired;
		this.optionsButton.active = !this.server.expired;
		this.resetWorldButton.active = !this.server.expired;
	}

	private void joinRealm(RealmsServer serverData) {
		if (this.server.state == RealmsServer.State.OPEN) {
			this.lastScreen.play(serverData, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
		} else {
			this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
		}
	}

	private void switchToMinigame() {
		RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
		realmsSelectWorldTemplateScreen.setTitle(I18n.translate("mco.template.title.minigame"));
		realmsSelectWorldTemplateScreen.setWarning(I18n.translate("mco.minigame.world.info.line1") + "\\n" + I18n.translate("mco.minigame.world.info.line2"));
		this.client.openScreen(realmsSelectWorldTemplateScreen);
	}

	private void switchToFullSlot(int selectedSlot, RealmsServer serverData) {
		String string = I18n.translate("mco.configure.world.slot.switch.question.line1");
		String string2 = I18n.translate("mco.configure.world.slot.switch.question.line2");
		this.client
			.openScreen(
				new RealmsLongConfirmationScreen(
					bl -> {
						if (bl) {
							this.client
								.openScreen(
									new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(serverData.id, selectedSlot, () -> this.client.openScreen(this.getNewScreen())))
								);
						} else {
							this.client.openScreen(this);
						}
					},
					RealmsLongConfirmationScreen.Type.Info,
					string,
					string2,
					true
				)
			);
	}

	private void switchToEmptySlot(int selectedSlot, RealmsServer serverData) {
		String string = I18n.translate("mco.configure.world.slot.switch.question.line1");
		String string2 = I18n.translate("mco.configure.world.slot.switch.question.line2");
		this.client
			.openScreen(
				new RealmsLongConfirmationScreen(
					bl -> {
						if (bl) {
							RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
								this,
								serverData,
								I18n.translate("mco.configure.world.switch.slot"),
								I18n.translate("mco.configure.world.switch.slot.subtitle"),
								10526880,
								I18n.translate("gui.cancel"),
								() -> this.client.openScreen(this.getNewScreen()),
								() -> this.client.openScreen(this.getNewScreen())
							);
							realmsResetWorldScreen.setSlot(selectedSlot);
							realmsResetWorldScreen.setResetTitle(I18n.translate("mco.create.world.reset.title"));
							this.client.openScreen(realmsResetWorldScreen);
						} else {
							this.client.openScreen(this);
						}
					},
					RealmsLongConfirmationScreen.Type.Info,
					string,
					string2,
					true
				)
			);
	}

	protected void renderMousehoverTooltip(String msg, int x, int y) {
		int i = x + 12;
		int j = y - 12;
		int k = this.textRenderer.getStringWidth(msg);
		if (i + k + 3 > this.right_x) {
			i = i - k - 20;
		}

		this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
		this.textRenderer.drawWithShadow(msg, (float)i, (float)j, 16777215);
	}

	private void drawServerStatus(int x, int y, int xm, int ym) {
		if (this.server.expired) {
			this.drawExpired(x, y, xm, ym);
		} else if (this.server.state == RealmsServer.State.CLOSED) {
			this.drawClosed(x, y, xm, ym);
		} else if (this.server.state == RealmsServer.State.OPEN) {
			if (this.server.daysLeft < 7) {
				this.drawExpiring(x, y, xm, ym, this.server.daysLeft);
			} else {
				this.drawOpen(x, y, xm, ym);
			}
		}
	}

	private void drawExpired(int x, int y, int xm, int ym) {
		this.client.getTextureManager().bindTexture(EXPIRED_ICON);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			this.toolTip = I18n.translate("mco.selectServer.expired");
		}
	}

	private void drawExpiring(int x, int y, int xm, int ym, int daysLeft) {
		this.client.getTextureManager().bindTexture(EXPIRES_SOON_ICON);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.animTick % 20 < 10) {
			DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			DrawableHelper.drawTexture(x, y, 10.0F, 0.0F, 10, 28, 20, 28);
		}

		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			if (daysLeft <= 0) {
				this.toolTip = I18n.translate("mco.selectServer.expires.soon");
			} else if (daysLeft == 1) {
				this.toolTip = I18n.translate("mco.selectServer.expires.day");
			} else {
				this.toolTip = I18n.translate("mco.selectServer.expires.days", daysLeft);
			}
		}
	}

	private void drawOpen(int x, int y, int xm, int ym) {
		this.client.getTextureManager().bindTexture(ON_ICON);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			this.toolTip = I18n.translate("mco.selectServer.open");
		}
	}

	private void drawClosed(int x, int y, int xm, int ym) {
		this.client.getTextureManager().bindTexture(OFF_ICON);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (xm >= x && xm <= x + 9 && ym >= y && ym <= y + 27) {
			this.toolTip = I18n.translate("mco.selectServer.closed");
		}
	}

	private boolean isMinigame() {
		return this.server != null && this.server.worldType == RealmsServer.WorldType.MINIGAME;
	}

	private void hideRegularButtons() {
		this.removeButton(this.optionsButton);
		this.removeButton(this.backupButton);
		this.removeButton(this.resetWorldButton);
	}

	private void removeButton(ButtonWidget button) {
		button.visible = false;
		this.children.remove(button);
		this.buttons.remove(button);
	}

	private void addButton(ButtonWidget button) {
		button.visible = true;
		this.addButton(button);
	}

	private void hideMinigameButtons() {
		this.removeButton(this.switchMinigameButton);
	}

	public void saveSlotSettings(RealmsWorldOptions options) {
		RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)this.server.slots.get(this.server.activeSlot);
		options.templateId = realmsWorldOptions.templateId;
		options.templateImage = realmsWorldOptions.templateImage;
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.updateSlot(this.server.id, this.server.activeSlot, options);
			this.server.slots.put(this.server.activeSlot, options);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't save slot settings");
			this.client.openScreen(new RealmsGenericErrorScreen(var5, this));
			return;
		}

		this.client.openScreen(this);
	}

	public void saveSettings(String name, String desc) {
		String string = desc.trim().isEmpty() ? null : desc;
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.update(this.server.id, name, string);
			this.server.setName(name);
			this.server.setDescription(string);
		} catch (RealmsServiceException var6) {
			LOGGER.error("Couldn't save settings");
			this.client.openScreen(new RealmsGenericErrorScreen(var6, this));
			return;
		}

		this.client.openScreen(this);
	}

	public void openTheWorld(boolean join, Screen screen) {
		this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new OpenServerTask(this.server, this, this.lastScreen, join)));
	}

	public void closeTheWorld(Screen screen) {
		this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new CloseServerTask(this.server, this)));
	}

	public void stateChanged() {
		this.stateChanged = true;
	}

	@Override
	protected void callback(@Nullable WorldTemplate template) {
		if (template != null) {
			if (WorldTemplate.WorldTemplateType.MINIGAME == template.type) {
				this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameTask(this.server.id, template, this.getNewScreen())));
			}
		}
	}

	public RealmsConfigureWorldScreen getNewScreen() {
		return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
	}
}
