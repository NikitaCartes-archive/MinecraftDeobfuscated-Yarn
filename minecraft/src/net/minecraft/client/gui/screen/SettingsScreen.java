package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen {
	private static final Option[] OPTIONS = new Option[]{Option.field_1964};
	private final Screen field_2501;
	private final GameOptions settings;
	private ButtonWidget difficultyButton;
	private LockButtonWidget lockDifficultyButton;
	private Difficulty difficulty;

	public SettingsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableText("options.title"));
		this.field_2501 = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void init() {
		int i = 0;

		for (Option option : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 - 12 + 24 * (i >> 1);
			this.addButton(option.method_18520(this.minecraft.field_1690, j, k, 150));
			i++;
		}

		if (this.minecraft.field_1687 != null) {
			this.difficulty = this.minecraft.field_1687.getDifficulty();
			this.difficultyButton = this.addButton(
				new ButtonWidget(
					this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyButtonText(this.difficulty), buttonWidget -> {
						this.difficulty = Difficulty.byOrdinal(this.difficulty.getId() + 1);
						this.minecraft.method_1562().sendPacket(new UpdateDifficultyC2SPacket(this.difficulty));
						this.difficultyButton.setMessage(this.getDifficultyButtonText(this.difficulty));
					}
				)
			);
			if (this.minecraft.isIntegratedServerRunning() && !this.minecraft.field_1687.method_8401().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = this.addButton(
					new LockButtonWidget(
						this.difficultyButton.x + this.difficultyButton.getWidth(),
						this.difficultyButton.y,
						buttonWidget -> this.minecraft
								.method_1507(
									new ConfirmScreen(
										this::lockDifficulty,
										new TranslatableText("difficulty.lock.title"),
										new TranslatableText(
											"difficulty.lock.question", new TranslatableText("options.difficulty." + this.minecraft.field_1687.method_8401().getDifficulty().getName())
										)
									)
								)
					)
				);
				this.lockDifficultyButton.setLocked(this.minecraft.field_1687.method_8401().isDifficultyLocked());
				this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.active = false;
			}
		} else {
			this.addButton(
				new OptionButtonWidget(
					this.width / 2 - 155 + i % 2 * 160,
					this.height / 6 - 12 + 24 * (i >> 1),
					150,
					20,
					Option.REALMS_NOTIFICATIONS,
					Option.REALMS_NOTIFICATIONS.method_18495(this.settings),
					buttonWidget -> {
						Option.REALMS_NOTIFICATIONS.method_18491(this.settings);
						this.settings.write();
						buttonWidget.setMessage(Option.REALMS_NOTIFICATIONS.method_18495(this.settings));
					}
				)
			);
		}

		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 48 - 6,
				150,
				20,
				I18n.translate("options.skinCustomisation"),
				buttonWidget -> this.minecraft.method_1507(new SkinOptionsScreen(this))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 48 - 6,
				150,
				20,
				I18n.translate("options.sounds"),
				buttonWidget -> this.minecraft.method_1507(new SoundOptionsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 72 - 6,
				150,
				20,
				I18n.translate("options.video"),
				buttonWidget -> this.minecraft.method_1507(new VideoOptionsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 72 - 6,
				150,
				20,
				I18n.translate("options.controls"),
				buttonWidget -> this.minecraft.method_1507(new ControlsOptionsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 96 - 6,
				150,
				20,
				I18n.translate("options.language"),
				buttonWidget -> this.minecraft.method_1507(new LanguageOptionsScreen(this, this.settings, this.minecraft.method_1526()))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 96 - 6,
				150,
				20,
				I18n.translate("options.chat.title"),
				buttonWidget -> this.minecraft.method_1507(new ChatOptionsScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 120 - 6,
				150,
				20,
				I18n.translate("options.resourcepack"),
				buttonWidget -> this.minecraft.method_1507(new ResourcePackOptionsScreen(this))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 120 - 6,
				150,
				20,
				I18n.translate("options.accessibility.title"),
				buttonWidget -> this.minecraft.method_1507(new AccessibilityScreen(this, this.settings))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.method_1507(this.field_2501)
			)
		);
	}

	public String getDifficultyButtonText(Difficulty difficulty) {
		return new TranslatableText("options.difficulty").append(": ").append(difficulty.getTranslatableName()).asFormattedString();
	}

	private void lockDifficulty(boolean bl) {
		this.minecraft.method_1507(this);
		if (bl && this.minecraft.field_1687 != null) {
			this.minecraft.method_1562().sendPacket(new UpdateDifficultyLockC2SPacket(true));
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.active = false;
			this.difficultyButton.active = false;
		}
	}

	@Override
	public void removed() {
		this.settings.write();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 15, 16777215);
		super.render(i, j, f);
	}
}
