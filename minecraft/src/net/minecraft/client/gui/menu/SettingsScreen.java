package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.class_4189;
import net.minecraft.class_4210;
import net.minecraft.class_4211;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.settings.AudioSettingsScreen;
import net.minecraft.client.gui.menu.settings.ChatSettingsScreen;
import net.minecraft.client.gui.menu.settings.ControlsSettingsScreen;
import net.minecraft.client.gui.menu.settings.LanguageSettingsScreen;
import net.minecraft.client.gui.menu.settings.ResourcePackSettingsScreen;
import net.minecraft.client.gui.menu.settings.SkinSettingsScreen;
import net.minecraft.client.gui.menu.settings.VideoSettingsScreen;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen {
	private static final GameOption[] field_2504 = new GameOption[]{GameOption.field_1964};
	private final Screen field_2501;
	private final GameOptions settings;
	private class_4185 difficultyButton;
	private LockButtonWidget lockDifficultyButton;
	private Difficulty field_18745;
	protected String title = "Options";

	public SettingsScreen(Screen screen, GameOptions gameOptions) {
		this.field_2501 = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.title");
		int i = 0;

		for (GameOption gameOption : field_2504) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = this.screenHeight / 6 - 12 + 24 * (i >> 1);
			this.addButton(gameOption.method_18520(this.client.field_1690, j, k, 150));
			i++;
		}

		if (this.client.field_1687 != null) {
			this.field_18745 = this.client.field_1687.getDifficulty();
			this.difficultyButton = new class_4185(
				this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 - 12 + 24 * (i >> 1), 150, 20, this.method_2189(this.field_18745)
			) {
				@Override
				public void method_1826() {
					SettingsScreen.this.field_18745 = Difficulty.getDifficulty(SettingsScreen.this.field_18745.getId() + 1);
					SettingsScreen.this.client.method_1562().method_2883(new class_4210(SettingsScreen.this.field_18745));
					SettingsScreen.this.difficultyButton.setText(SettingsScreen.this.method_2189(SettingsScreen.this.field_18745));
				}
			};
			this.addButton(this.difficultyButton);
			if (this.client.isIntegratedServerRunning() && !this.client.field_1687.method_8401().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = new LockButtonWidget(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y) {
					@Override
					public void method_1826() {
						TranslatableTextComponent translatableTextComponent = new TranslatableTextComponent("difficulty.lock.title");
						TranslatableTextComponent translatableTextComponent2 = new TranslatableTextComponent(
							"options.difficulty." + SettingsScreen.this.client.field_1687.method_8401().getDifficulty().getTranslationKey()
						);
						TranslatableTextComponent translatableTextComponent3 = new TranslatableTextComponent("difficulty.lock.question", translatableTextComponent2);
						YesNoScreen yesNoScreen = new YesNoScreen(
							SettingsScreen.this, translatableTextComponent.getFormattedText(), translatableTextComponent3.getFormattedText(), 109
						);
						SettingsScreen.this.client.method_1507(yesNoScreen);
					}
				};
				this.addButton(this.lockDifficultyButton);
				this.lockDifficultyButton.setLocked(this.client.field_1687.method_8401().isDifficultyLocked());
				this.lockDifficultyButton.enabled = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.enabled = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.enabled = false;
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
					public void method_1826() {
						GameOption.REALMS_NOTIFICATIONS.method_18491(SettingsScreen.this.settings);
						SettingsScreen.this.settings.write();
						this.setText(GameOption.REALMS_NOTIFICATIONS.method_18495(SettingsScreen.this.settings));
					}
				}
			);
		}

		this.addButton(new class_4185(this.screenWidth / 2 - 155, this.screenHeight / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new SkinSettingsScreen(SettingsScreen.this));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 5, this.screenHeight / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new AudioSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 155, this.screenHeight / 6 + 72 - 6, 150, 20, I18n.translate("options.video")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new VideoSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 5, this.screenHeight / 6 + 72 - 6, 150, 20, I18n.translate("options.controls")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new ControlsSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(
			new class_4185(this.screenWidth / 2 - 155, this.screenHeight / 6 + 96 - 6, 150, 20, I18n.translate("options.language")) {
				@Override
				public void method_1826() {
					SettingsScreen.this.client
						.method_1507(new LanguageSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings, SettingsScreen.this.client.method_1526()));
				}
			}
		);
		this.addButton(new class_4185(this.screenWidth / 2 + 5, this.screenHeight / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new ChatSettingsScreen(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 155, this.screenHeight / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new ResourcePackSettingsScreen(SettingsScreen.this));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 5, this.screenHeight / 6 + 120 - 6, 150, 20, I18n.translate("options.accessibility.title")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(new class_4189(SettingsScreen.this, SettingsScreen.this.settings));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				SettingsScreen.this.client.method_1507(SettingsScreen.this.field_2501);
			}
		});
	}

	public String method_2189(Difficulty difficulty) {
		return new TranslatableTextComponent("options.difficulty").append(": ").append(difficulty.method_5463()).getFormattedText();
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.client.method_1507(this);
		if (i == 109 && bl && this.client.field_1687 != null) {
			this.client.method_1562().method_2883(new class_4211(true));
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.enabled = false;
			this.difficultyButton.enabled = false;
		}
	}

	@Override
	public void onClosed() {
		this.settings.write();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 15, 16777215);
		super.draw(i, j, f);
	}
}
