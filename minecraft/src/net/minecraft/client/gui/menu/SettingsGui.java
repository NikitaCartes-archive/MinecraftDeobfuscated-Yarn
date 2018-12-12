package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.menu.settings.AudioSettingsGui;
import net.minecraft.client.gui.menu.settings.ChatSettingsGui;
import net.minecraft.client.gui.menu.settings.ControlsSettingsGui;
import net.minecraft.client.gui.menu.settings.LanguageSettingsGui;
import net.minecraft.client.gui.menu.settings.ResourcePackSettingsGui;
import net.minecraft.client.gui.menu.settings.SkinSettingsGui;
import net.minecraft.client.gui.menu.settings.SnooperSettingsGui;
import net.minecraft.client.gui.menu.settings.VideoSettingsGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class SettingsGui extends Gui {
	private static final GameOptions.Option[] field_2504 = new GameOptions.Option[]{GameOptions.Option.FOV};
	private final Gui parent;
	private final GameOptions settings;
	private ButtonWidget difficultyButton;
	private LockButtonWidget lockDifficultyButton;
	protected String title = "Options";

	public SettingsGui(Gui gui, GameOptions gameOptions) {
		this.parent = gui;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.title");
		int i = 0;

		for (GameOptions.Option option : field_2504) {
			if (option.isSlider()) {
				this.addButton(new OptionSliderWidget(option.getId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), option));
			} else {
				OptionButtonWidget optionButtonWidget = new OptionButtonWidget(
					option.getId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), option, this.settings.getTranslatedName(option)
				) {
					@Override
					public void onPressed(double d, double e) {
						SettingsGui.this.settings.updateOption(this.getOption(), 1);
						this.text = SettingsGui.this.settings.getTranslatedName(GameOptions.Option.byId(this.id));
					}
				};
				this.addButton(optionButtonWidget);
			}

			i++;
		}

		if (this.client.world != null) {
			Difficulty difficulty = this.client.world.getDifficulty();
			this.difficultyButton = new ButtonWidget(
				108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.method_2189(difficulty)
			) {
				@Override
				public void onPressed(double d, double e) {
					SettingsGui.this.client.world.getLevelProperties().setDifficulty(Difficulty.byId(SettingsGui.this.client.world.getDifficulty().getId() + 1));
					SettingsGui.this.difficultyButton.text = SettingsGui.this.method_2189(SettingsGui.this.client.world.getDifficulty());
				}
			};
			this.addButton(this.difficultyButton);
			if (this.client.method_1496() && !this.client.world.getLevelProperties().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = new LockButtonWidget(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y) {
					@Override
					public void onPressed(double d, double e) {
						SettingsGui.this.client
							.openGui(
								new YesNoGui(
									SettingsGui.this,
									new TranslatableTextComponent("difficulty.lock.title").getFormattedText(),
									new TranslatableTextComponent(
											"difficulty.lock.question", new TranslatableTextComponent(SettingsGui.this.client.world.getLevelProperties().getDifficulty().getTranslationKey())
										)
										.getFormattedText(),
									109
								)
							);
					}
				};
				this.addButton(this.lockDifficultyButton);
				this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
				this.lockDifficultyButton.enabled = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.enabled = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.enabled = false;
			}
		} else {
			this.addButton(
				new OptionButtonWidget(
					GameOptions.Option.REALMS_NOTIFICATIONS.getId(),
					this.width / 2 - 155 + i % 2 * 160,
					this.height / 6 - 12 + 24 * (i >> 1),
					GameOptions.Option.REALMS_NOTIFICATIONS,
					this.settings.getTranslatedName(GameOptions.Option.REALMS_NOTIFICATIONS)
				) {
					@Override
					public void onPressed(double d, double e) {
						SettingsGui.this.settings.updateOption(this.getOption(), 1);
						this.text = SettingsGui.this.settings.getTranslatedName(GameOptions.Option.byId(this.id));
					}
				}
			);
		}

		this.addButton(new ButtonWidget(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new SkinSettingsGui(SettingsGui.this));
			}
		});
		this.addButton(new ButtonWidget(106, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new AudioSettingsGui(SettingsGui.this, SettingsGui.this.settings));
			}
		});
		this.addButton(new ButtonWidget(101, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.video")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new VideoSettingsGui(SettingsGui.this, SettingsGui.this.settings));
			}
		});
		this.addButton(new ButtonWidget(100, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.controls")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new ControlsSettingsGui(SettingsGui.this, SettingsGui.this.settings));
			}
		});
		this.addButton(new ButtonWidget(102, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.language")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new LanguageSettingsGui(SettingsGui.this, SettingsGui.this.settings, SettingsGui.this.client.getLanguageManager()));
			}
		});
		this.addButton(new ButtonWidget(103, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new ChatSettingsGui(SettingsGui.this, SettingsGui.this.settings));
			}
		});
		this.addButton(new ButtonWidget(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new ResourcePackSettingsGui(SettingsGui.this));
			}
		});
		this.addButton(new ButtonWidget(104, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.snooper.view")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(new SnooperSettingsGui(SettingsGui.this, SettingsGui.this.settings));
			}
		});
		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				SettingsGui.this.client.field_1690.write();
				SettingsGui.this.client.openGui(SettingsGui.this.parent);
			}
		});
	}

	public String method_2189(Difficulty difficulty) {
		return new TranslatableTextComponent("options.difficulty").append(": ").append(difficulty.method_5463()).getFormattedText();
	}

	@Override
	public void handle(boolean bl, int i) {
		this.client.openGui(this);
		if (i == 109 && bl && this.client.world != null) {
			this.client.world.getLevelProperties().setDifficultyLocked(true);
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.enabled = false;
			this.difficultyButton.enabled = false;
		}
	}

	@Override
	public void close() {
		this.client.field_1690.write();
		super.close();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
		super.draw(i, j, f);
	}
}
