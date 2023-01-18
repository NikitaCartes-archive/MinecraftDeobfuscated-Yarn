package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.task.CloseServerTask;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.task.SwitchMinigameTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConfigureWorldScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
	private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
	private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
	private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
	private static final Text WORLDS_TITLE = Text.translatable("mco.configure.worlds.title");
	private static final Text CONFIGURE_REALM_TITLE = Text.translatable("mco.configure.world.title");
	private static final Text CURRENT_MINIGAME_TEXT = Text.translatable("mco.configure.current.minigame").append(": ");
	private static final Text EXPIRED_TEXT = Text.translatable("mco.selectServer.expired");
	private static final Text EXPIRES_SOON_TEXT = Text.translatable("mco.selectServer.expires.soon");
	private static final Text EXPIRES_IN_A_DAY_TEXT = Text.translatable("mco.selectServer.expires.day");
	private static final Text OPEN_TEXT = Text.translatable("mco.selectServer.open");
	private static final Text CLOSED_TEXT = Text.translatable("mco.selectServer.closed");
	private static final int field_32121 = 80;
	private static final int field_32122 = 5;
	@Nullable
	private Text tooltip;
	private final RealmsMainScreen parent;
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
	private final List<RealmsWorldSlotButton> slotButtons = Lists.<RealmsWorldSlotButton>newArrayList();

	public RealmsConfigureWorldScreen(RealmsMainScreen parent, long serverId) {
		super(CONFIGURE_REALM_TITLE);
		this.parent = parent;
		this.serverId = serverId;
	}

	@Override
	public void init() {
		if (this.server == null) {
			this.fetchServerData(this.serverId);
		}

		this.left_x = this.width / 2 - 187;
		this.right_x = this.width / 2 + 190;
		this.playersButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.players"), button -> this.client.setScreen(new RealmsPlayerScreen(this, this.server)))
				.dimensions(this.buttonCenter(0, 3), row(0), 100, 20)
				.build()
		);
		this.settingsButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.settings"), button -> this.client.setScreen(new RealmsSettingsScreen(this, this.server.clone()))
				)
				.dimensions(this.buttonCenter(1, 3), row(0), 100, 20)
				.build()
		);
		this.subscriptionButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.subscription"),
					button -> this.client.setScreen(new RealmsSubscriptionInfoScreen(this, this.server.clone(), this.parent))
				)
				.dimensions(this.buttonCenter(2, 3), row(0), 100, 20)
				.build()
		);
		this.slotButtons.clear();

		for (int i = 1; i < 5; i++) {
			this.slotButtons.add(this.addSlotButton(i));
		}

		this.switchMinigameButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.switchminigame"),
					button -> this.client
							.setScreen(new RealmsSelectWorldTemplateScreen(Text.translatable("mco.template.title.minigame"), this::switchMinigame, RealmsServer.WorldType.MINIGAME))
				)
				.dimensions(this.buttonLeft(0), row(13) - 5, 100, 20)
				.build()
		);
		this.optionsButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.options"),
					button -> this.client
							.setScreen(
								new RealmsSlotOptionsScreen(
									this, ((RealmsWorldOptions)this.server.slots.get(this.server.activeSlot)).clone(), this.server.worldType, this.server.activeSlot
								)
							)
				)
				.dimensions(this.buttonLeft(0), row(13) - 5, 90, 20)
				.build()
		);
		this.backupButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.backup"),
					button -> this.client.setScreen(new RealmsBackupScreen(this, this.server.clone(), this.server.activeSlot))
				)
				.dimensions(this.buttonLeft(1), row(13) - 5, 90, 20)
				.build()
		);
		this.resetWorldButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.resetworld"),
					button -> this.client
							.setScreen(
								new RealmsResetWorldScreen(
									this,
									this.server.clone(),
									() -> this.client.execute(() -> this.client.setScreen(this.getNewScreen())),
									() -> this.client.setScreen(this.getNewScreen())
								)
							)
				)
				.dimensions(this.buttonLeft(2), row(13) - 5, 90, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.backButtonClicked()).dimensions(this.right_x - 80 + 8, row(13) - 5, 70, 20).build()
		);
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

	private RealmsWorldSlotButton addSlotButton(int slotIndex) {
		int i = this.frame(slotIndex);
		int j = row(5) + 5;
		RealmsWorldSlotButton realmsWorldSlotButton = new RealmsWorldSlotButton(
			i, j, 80, 80, () -> this.server, tooltip -> this.tooltip = tooltip, slotIndex, button -> {
				RealmsWorldSlotButton.State state = ((RealmsWorldSlotButton)button).getState();
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
		return this.addDrawableChild(realmsWorldSlotButton);
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

		this.slotButtons.forEach(RealmsWorldSlotButton::tick);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltip = null;
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, WORLDS_TITLE, this.width / 2, row(4), 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		if (this.server == null) {
			drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
		} else {
			String string = this.server.getName();
			int i = this.textRenderer.getWidth(string);
			int j = this.server.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
			int k = this.textRenderer.getWidth(this.title);
			drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 12, 16777215);
			drawCenteredText(matrices, this.textRenderer, string, this.width / 2, 24, j);
			int l = Math.min(this.buttonCenter(2, 3) + 80 - 11, this.width / 2 + i / 2 + k / 2 + 10);
			this.drawServerStatus(matrices, l, 7, mouseX, mouseY);
			if (this.isMinigame()) {
				this.textRenderer
					.draw(matrices, CURRENT_MINIGAME_TEXT.copy().append(this.server.getMinigameName()), (float)(this.left_x + 80 + 20 + 10), (float)row(13), 16777215);
			}

			if (this.tooltip != null) {
				this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
			}
		}
	}

	private int frame(int ordinal) {
		return this.left_x + (ordinal - 1) * 98;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.parent.removeSelection();
		}

		this.client.setScreen(this.parent);
	}

	private void fetchServerData(long worldId) {
		new Thread(() -> {
			RealmsClient realmsClient = RealmsClient.create();

			try {
				RealmsServer realmsServer = realmsClient.getOwnWorld(worldId);
				this.client.execute(() -> {
					this.server = realmsServer;
					this.disableButtons();
					if (this.isMinigame()) {
						this.addButton(this.switchMinigameButton);
					} else {
						this.addButton(this.optionsButton);
						this.addButton(this.backupButton);
						this.addButton(this.resetWorldButton);
					}
				});
			} catch (RealmsServiceException var5) {
				LOGGER.error("Couldn't get own world");
				this.client.execute(() -> this.client.setScreen(new RealmsGenericErrorScreen(Text.of(var5.getMessage()), this.parent)));
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
			this.parent.play(serverData, new RealmsConfigureWorldScreen(this.parent.newScreen(), this.serverId));
		} else {
			this.openTheWorld(true, new RealmsConfigureWorldScreen(this.parent.newScreen(), this.serverId));
		}
	}

	private void switchToMinigame() {
		RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
			Text.translatable("mco.template.title.minigame"), this::switchMinigame, RealmsServer.WorldType.MINIGAME
		);
		realmsSelectWorldTemplateScreen.setWarning(Text.translatable("mco.minigame.world.info.line1"), Text.translatable("mco.minigame.world.info.line2"));
		this.client.setScreen(realmsSelectWorldTemplateScreen);
	}

	private void switchToFullSlot(int selectedSlot, RealmsServer serverData) {
		Text text = Text.translatable("mco.configure.world.slot.switch.question.line1");
		Text text2 = Text.translatable("mco.configure.world.slot.switch.question.line2");
		this.client
			.setScreen(
				new RealmsLongConfirmationScreen(
					confirmed -> {
						if (confirmed) {
							this.client
								.setScreen(
									new RealmsLongRunningMcoTaskScreen(
										this.parent, new SwitchSlotTask(serverData.id, selectedSlot, () -> this.client.execute(() -> this.client.setScreen(this.getNewScreen())))
									)
								);
						} else {
							this.client.setScreen(this);
						}
					},
					RealmsLongConfirmationScreen.Type.INFO,
					text,
					text2,
					true
				)
			);
	}

	private void switchToEmptySlot(int selectedSlot, RealmsServer serverData) {
		Text text = Text.translatable("mco.configure.world.slot.switch.question.line1");
		Text text2 = Text.translatable("mco.configure.world.slot.switch.question.line2");
		this.client
			.setScreen(
				new RealmsLongConfirmationScreen(
					confirmed -> {
						if (confirmed) {
							RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
								this,
								serverData,
								Text.translatable("mco.configure.world.switch.slot"),
								Text.translatable("mco.configure.world.switch.slot.subtitle"),
								10526880,
								ScreenTexts.CANCEL,
								() -> this.client.execute(() -> this.client.setScreen(this.getNewScreen())),
								() -> this.client.setScreen(this.getNewScreen())
							);
							realmsResetWorldScreen.setSlot(selectedSlot);
							realmsResetWorldScreen.setResetTitle(Text.translatable("mco.create.world.reset.title"));
							this.client.setScreen(realmsResetWorldScreen);
						} else {
							this.client.setScreen(this);
						}
					},
					RealmsLongConfirmationScreen.Type.INFO,
					text,
					text2,
					true
				)
			);
	}

	protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text text, int mouseX, int mouseY) {
		int i = mouseX + 12;
		int j = mouseY - 12;
		int k = this.textRenderer.getWidth(text);
		if (i + k + 3 > this.right_x) {
			i = i - k - 20;
		}

		this.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
		this.textRenderer.drawWithShadow(matrices, text, (float)i, (float)j, 16777215);
	}

	private void drawServerStatus(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (this.server.expired) {
			this.drawExpired(matrices, x, y, mouseX, mouseY);
		} else if (this.server.state == RealmsServer.State.CLOSED) {
			this.drawClosed(matrices, x, y, mouseX, mouseY);
		} else if (this.server.state == RealmsServer.State.OPEN) {
			if (this.server.daysLeft < 7) {
				this.drawExpiring(matrices, x, y, mouseX, mouseY, this.server.daysLeft);
			} else {
				this.drawOpen(matrices, x, y, mouseX, mouseY);
			}
		}
	}

	private void drawExpired(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, EXPIRED_ICON);
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27) {
			this.tooltip = EXPIRED_TEXT;
		}
	}

	private void drawExpiring(MatrixStack matrices, int x, int y, int mouseX, int mouseY, int remainingDays) {
		RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON);
		if (this.animTick % 20 < 10) {
			DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			DrawableHelper.drawTexture(matrices, x, y, 10.0F, 0.0F, 10, 28, 20, 28);
		}

		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27) {
			if (remainingDays <= 0) {
				this.tooltip = EXPIRES_SOON_TEXT;
			} else if (remainingDays == 1) {
				this.tooltip = EXPIRES_IN_A_DAY_TEXT;
			} else {
				this.tooltip = Text.translatable("mco.selectServer.expires.days", remainingDays);
			}
		}
	}

	private void drawOpen(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, ON_ICON);
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27) {
			this.tooltip = OPEN_TEXT;
		}
	}

	private void drawClosed(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, OFF_ICON);
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27) {
			this.tooltip = CLOSED_TEXT;
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
		this.remove(button);
	}

	private void addButton(ButtonWidget button) {
		button.visible = true;
		this.addDrawableChild(button);
	}

	private void hideMinigameButtons() {
		this.removeButton(this.switchMinigameButton);
	}

	public void saveSlotSettings(RealmsWorldOptions options) {
		RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)this.server.slots.get(this.server.activeSlot);
		options.templateId = realmsWorldOptions.templateId;
		options.templateImage = realmsWorldOptions.templateImage;
		RealmsClient realmsClient = RealmsClient.create();

		try {
			realmsClient.updateSlot(this.server.id, this.server.activeSlot, options);
			this.server.slots.put(this.server.activeSlot, options);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't save slot settings");
			this.client.setScreen(new RealmsGenericErrorScreen(var5, this));
			return;
		}

		this.client.setScreen(this);
	}

	public void saveSettings(String name, String desc) {
		String string = desc.trim().isEmpty() ? null : desc;
		RealmsClient realmsClient = RealmsClient.create();

		try {
			realmsClient.update(this.server.id, name, string);
			this.server.setName(name);
			this.server.setDescription(string);
		} catch (RealmsServiceException var6) {
			LOGGER.error("Couldn't save settings");
			this.client.setScreen(new RealmsGenericErrorScreen(var6, this));
			return;
		}

		this.client.setScreen(this);
	}

	public void openTheWorld(boolean join, Screen screen) {
		this.client.setScreen(new RealmsLongRunningMcoTaskScreen(screen, new OpenServerTask(this.server, this, this.parent, join, this.client)));
	}

	public void closeTheWorld(Screen screen) {
		this.client.setScreen(new RealmsLongRunningMcoTaskScreen(screen, new CloseServerTask(this.server, this)));
	}

	public void stateChanged() {
		this.stateChanged = true;
	}

	private void switchMinigame(@Nullable WorldTemplate template) {
		if (template != null && WorldTemplate.WorldTemplateType.MINIGAME == template.type) {
			this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchMinigameTask(this.server.id, template, this.getNewScreen())));
		} else {
			this.client.setScreen(this);
		}
	}

	public RealmsConfigureWorldScreen getNewScreen() {
		return new RealmsConfigureWorldScreen(this.parent, this.serverId);
	}
}
