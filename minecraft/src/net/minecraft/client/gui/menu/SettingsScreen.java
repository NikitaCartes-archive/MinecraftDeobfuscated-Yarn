package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.settings.AudioSettingsScreen;
import net.minecraft.client.gui.menu.settings.ChatSettingsScreen;
import net.minecraft.client.gui.menu.settings.ControlsSettingsScreen;
import net.minecraft.client.gui.menu.settings.LanguageSettingsScreen;
import net.minecraft.client.gui.menu.settings.ResourcePackSettingsScreen;
import net.minecraft.client.gui.menu.settings.SkinSettingsScreen;
import net.minecraft.client.gui.menu.settings.VideoSettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen {
	private static final GameOption[] field_2504 = new GameOption[]{GameOption.FOV};
	private final Screen parent;
	private final GameOptions settings;
	private ButtonWidget difficultyButton;
	private LockButtonWidget lockDifficultyButton;
	private Difficulty field_18745;

	public SettingsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableTextComponent("options.title"));
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		int i = 0;

		for (GameOption gameOption : field_2504) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 - 12 + 24 * (i >> 1);
			this.addButton(gameOption.createOptionButton(this.client.options, j, k, 150));
			i++;
		}

		if (this.client.world != null) {
			this.field_18745 = this.client.world.getDifficulty();
			this.difficultyButton = this.addButton(
				new ButtonWidget(
					this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 - 12 + 24 * (i >> 1), 150, 20, this.method_2189(this.field_18745), buttonWidget -> {
						this.field_18745 = Difficulty.getDifficulty(this.field_18745.getId() + 1);
						this.client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(this.field_18745));
						this.difficultyButton.setMessage(this.method_2189(this.field_18745));
					}
				)
			);
			if (this.client.isIntegratedServerRunning() && !this.client.world.getLevelProperties().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = this.addButton(
					new LockButtonWidget(
						this.difficultyButton.x + this.difficultyButton.getWidth(),
						this.difficultyButton.y,
						buttonWidget -> this.client
								.openScreen(
									new YesNoScreen(
										this,
										new TranslatableTextComponent("difficulty.lock.title"),
										new TranslatableTextComponent(
											"difficulty.lock.question",
											new TranslatableTextComponent("options.difficulty." + this.client.world.getLevelProperties().getDifficulty().getTranslationKey())
										),
										109
									)
								)
					)
				);
				this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
				this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.active = false;
			}
		} else {
			this.addButton(
				new OptionButtonWidget(
					this.screenWidth / 2 - 155 + i % 2 * 160,
					this.screenHeight / 6 - 12 + 24 * (i >> 1),
					150,
					20,
					GameOption.REALMS_NOTIFICATIONS,
					GameOption.REALMS_NOTIFICATIONS.method_18495(this.settings),
					buttonWidget -> {
						GameOption.REALMS_NOTIFICATIONS.method_18491(this.settings);
						this.settings.write();
						buttonWidget.setMessage(GameOption.REALMS_NOTIFICATIONS.method_18495(this.settings));
					}
				)
			);
		}

		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155,
				this.screenHeight / 6 + 48 - 6,
				150,
				20,
				I18n.translate("options.skinCustomisation"),
				buttonWidget -> this.client.openScreen(new SkinSettingsScreen(this))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 5,
				this.screenHeight / 6 + 48 - 6,
				150,
				20,
				I18n.translate("options.sounds"),
				buttonWidget -> this.client.openScreen(new AudioSettingsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155,
				this.screenHeight / 6 + 72 - 6,
				150,
				20,
				I18n.translate("options.video"),
				buttonWidget -> this.client.openScreen(new VideoSettingsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 5,
				this.screenHeight / 6 + 72 - 6,
				150,
				20,
				I18n.translate("options.controls"),
				buttonWidget -> this.client.openScreen(new ControlsSettingsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155,
				this.screenHeight / 6 + 96 - 6,
				150,
				20,
				I18n.translate("options.language"),
				buttonWidget -> this.client.openScreen(new LanguageSettingsScreen(this, this.settings, this.client.getLanguageManager()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 5,
				this.screenHeight / 6 + 96 - 6,
				150,
				20,
				I18n.translate("options.chat.title"),
				buttonWidget -> this.client.openScreen(new ChatSettingsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155,
				this.screenHeight / 6 + 120 - 6,
				150,
				20,
				I18n.translate("options.resourcepack"),
				buttonWidget -> this.client.openScreen(new ResourcePackSettingsScreen(this))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 5,
				this.screenHeight / 6 + 120 - 6,
				150,
				20,
				I18n.translate("options.accessibility.title"),
				buttonWidget -> this.client.openScreen(new AccessibilityScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100, this.screenHeight / 6 + 168, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.client.openScreen(this.parent)
			)
		);
	}

	public String method_2189(Difficulty difficulty) {
		return new TranslatableTextComponent("options.difficulty").append(": ").append(difficulty.toTextComponent()).getFormattedText();
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.client.openScreen(this);
		if (i == 109 && bl && this.client.world != null) {
			this.client.getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.active = false;
			this.difficultyButton.active = false;
		}
	}

	@Override
	public void onClosed() {
		this.settings.write();
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 15, 16777215);
		super.render(i, j, f);
	}
}
