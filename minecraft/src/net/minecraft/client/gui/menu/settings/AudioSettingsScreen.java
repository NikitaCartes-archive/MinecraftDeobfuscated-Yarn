package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class AudioSettingsScreen extends Screen {
	private final Screen parent;
	private final GameOptions settings;
	protected String title = "Options";

	public AudioSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.sounds.title");
		int i = 0;
		this.addButton(
			new SoundSliderWidget(this.client, this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 - 12 + 24 * (i >> 1), SoundCategory.field_15250, 310)
		);
		i += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.field_15250) {
				this.addButton(new SoundSliderWidget(this.client, this.screenWidth / 2 - 155 + i % 2 * 160, this.screenHeight / 6 - 12 + 24 * (i >> 1), soundCategory, 150));
				i++;
			}
		}

		this.addButton(
			new OptionButtonWidget(
				this.screenWidth / 2 - 75, this.screenHeight / 6 - 12 + 24 * (++i >> 1), 150, 20, GameOption.SUBTITLES, GameOption.SUBTITLES.method_18495(this.settings)
			) {
				@Override
				public void onPressed(double d, double e) {
					GameOption.SUBTITLES.method_18491(AudioSettingsScreen.this.client.options);
					this.setText(GameOption.SUBTITLES.method_18495(AudioSettingsScreen.this.client.options));
					AudioSettingsScreen.this.client.options.write();
				}
			}
		);
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				AudioSettingsScreen.this.client.openScreen(AudioSettingsScreen.this.parent);
			}
		});
	}

	@Override
	public void onClosed() {
		this.client.options.write();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 15, 16777215);
		super.draw(i, j, f);
	}
}
