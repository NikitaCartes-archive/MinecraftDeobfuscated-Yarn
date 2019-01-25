package net.minecraft.client.gui.menu.settings;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AudioSettingsScreen extends Screen {
	private final Screen parent;
	private final GameOptions settings;
	protected String title = "Options";
	private String field_2616;

	public AudioSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.title = I18n.translate("options.sounds.title");
		this.field_2616 = I18n.translate("options.off");
		int i = 0;
		this.addButton(
			new AudioSettingsScreen.class_444(
				SoundCategory.field_15250.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.field_15250, true
			)
		);
		i += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.field_15250) {
				this.addButton(
					new AudioSettingsScreen.class_444(soundCategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, false)
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
					AudioSettingsScreen.this.client.options.updateOption(GameOptions.Option.SHOW_SUBTITLES, 1);
					this.text = AudioSettingsScreen.this.client.options.getTranslatedName(GameOptions.Option.SHOW_SUBTITLES);
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

	protected String method_2256(SoundCategory soundCategory) {
		float f = this.settings.getSoundVolume(soundCategory);
		return f == 0.0F ? this.field_2616 : (int)(f * 100.0F) + "%";
	}

	@Environment(EnvType.CLIENT)
	class class_444 extends ButtonWidget {
		private final SoundCategory field_2622;
		private final String field_2621;
		public double field_2620;
		public boolean field_2623;

		public class_444(int i, int j, int k, SoundCategory soundCategory, boolean bl) {
			super(i, j, k, bl ? 310 : 150, 20, "");
			this.field_2622 = soundCategory;
			this.field_2621 = I18n.translate("soundCategory." + soundCategory.getName());
			this.text = this.field_2621 + ": " + AudioSettingsScreen.this.method_2256(soundCategory);
			this.field_2620 = (double)AudioSettingsScreen.this.settings.getSoundVolume(soundCategory);
		}

		@Override
		protected int getTextureId(boolean bl) {
			return 0;
		}

		@Override
		protected void drawBackground(MinecraftClient minecraftClient, int i, int j) {
			if (this.visible) {
				if (this.field_2623) {
					this.field_2620 = (double)((float)(i - (this.x + 4)) / (float)(this.width - 8));
					this.field_2620 = MathHelper.clamp(this.field_2620, 0.0, 1.0);
					minecraftClient.options.setSoundVolume(this.field_2622, (float)this.field_2620);
					minecraftClient.options.write();
					this.text = this.field_2621 + ": " + AudioSettingsScreen.this.method_2256(this.field_2622);
				}

				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.drawTexturedRect(this.x + (int)(this.field_2620 * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
				this.drawTexturedRect(this.x + (int)(this.field_2620 * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
			}
		}

		@Override
		public void onPressed(double d, double e) {
			this.field_2620 = (d - (double)(this.x + 4)) / (double)(this.width - 8);
			this.field_2620 = MathHelper.clamp(this.field_2620, 0.0, 1.0);
			AudioSettingsScreen.this.client.options.setSoundVolume(this.field_2622, (float)this.field_2620);
			AudioSettingsScreen.this.client.options.write();
			this.text = this.field_2621 + ": " + AudioSettingsScreen.this.method_2256(this.field_2622);
			this.field_2623 = true;
		}

		@Override
		public void playPressedSound(SoundLoader soundLoader) {
		}

		@Override
		public void onReleased(double d, double e) {
			if (this.field_2623) {
				AudioSettingsScreen.this.client.getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
			}

			this.field_2623 = false;
		}
	}
}
