package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_444;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
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
			new class_444(
				this.client, SoundCategory.field_15250.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.field_15250, 310
			)
		);
		i += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.field_15250) {
				this.addButton(
					new class_444(this.client, soundCategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, 150)
				);
				i++;
			}
		}

		this.addButton(
			new OptionButtonWidget(
				201,
				this.width / 2 - 75,
				this.height / 6 - 12 + 24 * (++i >> 1),
				GameOptions.Option.SHOW_SUBTITLES,
				this.settings.getTranslatedName(GameOptions.Option.SHOW_SUBTITLES)
			) {
				@Override
				public void onPressed(double d, double e) {
					AudioSettingsScreen.this.client.options.setInteger(GameOptions.Option.SHOW_SUBTITLES, 1);
					this.setText(AudioSettingsScreen.this.client.options.getTranslatedName(GameOptions.Option.SHOW_SUBTITLES));
					AudioSettingsScreen.this.client.options.write();
				}
			}
		);
		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				AudioSettingsScreen.this.client.options.write();
				AudioSettingsScreen.this.client.openScreen(AudioSettingsScreen.this.parent);
			}
		});
	}

	@Override
	public void close() {
		this.client.options.write();
		super.close();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
		super.draw(i, j, f);
	}
}
