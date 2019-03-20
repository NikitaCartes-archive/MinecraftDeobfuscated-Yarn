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
	protected String title = "Options";

	public SettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.title");
		int i = 0;

		for (GameOption gameOption : field_2504) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 - 12 + 24 * (i >> 1);
			this.addButton(gameOption.createOptionButton(this.client.options, j, k, 150));
			i++;
		}

		if (this.client.world != null) {
			this.field_18745 = this.client.world.getDifficulty();
			this.difficultyButton = new ButtonWidget(
				this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 - 12 + 24 * (i >> 1), 150, 20, this.method_2189(this.field_18745)
			) {
				@Override
				public void onPressed() {
					SettingsScreen.this.field_18745 = Difficulty.getDifficulty(SettingsScreen.this.field_18745.getId() + 1);
					SettingsScreen.this.client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(SettingsScreen.this.field_18745));
					SettingsScreen.this.difficultyButton.setMessage(SettingsScreen.this.method_2189(SettingsScreen.this.field_18745));
				}
			};
			this.addButton(this.difficultyButton);
			if (this.client.isIntegratedServerRunning() && !this.client.world.getLevelProperties().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = new LockButtonWidget(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y) {
					@Override
					public void onPressed() {
						TranslatableTextComponent translatableTextComponent = new TranslatableTextComponent("difficulty.lock.title");
						TranslatableTextComponent translatableTextComponent2 = new TranslatableTextComponent(
							"options.difficulty." + SettingsScreen.this.client.world.getLevelProperties().getDifficulty().getTranslationKey()
						);
						TranslatableTextComponent translatableTextComponent3 = new TranslatableTextComponent("difficulty.lock.question", translatableTextComponent2);
						YesNoScreen yesNoScreen = new YesNoScreen(
							SettingsScreen.this, translatableTextComponent.getFormattedText(), translatableTextComponent3.getFormattedText(), 109
						);
						SettingsScreen.this.client.openScreen(yesNoScreen);
					}
				};
				this.addButton(this.lockDifficultyButton);
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
					GameOption.REALMS_NOTIFICATIONS.method_18495(this.settings)
				) {
					@Override
					public void onPressed() {
						GameOption.REALMS_NOTIFICATIONS.method_18491(SettingsScreen.this.settings);
						SettingsScreen.this.settings.write();
						this.setMessage(GameOption.REALMS_NOTIFICATIONS.method_18495(SettingsScreen.this.settings));
					}
				}
			);
		}

		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new SkinSettingsScreen(SettingsScreen.this));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new AudioSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight / 6 + 72 - 6, 150, 20, I18n.translate("options.video")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new VideoSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight / 6 + 72 - 6, 150, 20, I18n.translate("options.controls")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new ControlsSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight / 6 + 96 - 6, 150, 20, I18n.translate("options.language")) {
				@Override
				public void onPressed() {
					SettingsScreen.this.client
						.openScreen(new LanguageSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings, SettingsScreen.this.client.getLanguageManager()));
				}
			}
		);
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new ChatSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new ResourcePackSettingsScreen(SettingsScreen.this));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight / 6 + 120 - 6, 150, 20, I18n.translate("options.accessibility.title")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(new AccessibilityScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				SettingsScreen.this.client.openScreen(SettingsScreen.this.parent);
			}
		});
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
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 15, 16777215);
		super.render(i, j, f);
	}
}
