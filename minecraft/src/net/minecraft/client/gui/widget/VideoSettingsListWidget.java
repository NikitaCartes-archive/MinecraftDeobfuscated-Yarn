package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.settings.GameOptions;

@Environment(EnvType.CLIENT)
public class VideoSettingsListWidget extends EntryListWidget<VideoSettingsListWidget.class_354> {
	public VideoSettingsListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, GameOptions.Option... options) {
		super(minecraftClient, i, j, k, l, m);
		this.field_2173 = false;
		this.method_1901(new VideoSettingsListWidget.class_354(i, GameOptions.Option.FULLSCREEN_RESOLUTION));

		for (int n = 0; n < options.length; n += 2) {
			GameOptions.Option option = options[n];
			GameOptions.Option option2 = n < options.length - 1 ? options[n + 1] : null;
			this.method_1901(new VideoSettingsListWidget.class_354(i, option, option2));
		}
	}

	@Nullable
	private static ButtonWidget method_1914(MinecraftClient minecraftClient, int i, int j, int k, @Nullable GameOptions.Option option) {
		if (option == null) {
			return null;
		} else {
			int l = option.getId();
			return (ButtonWidget)(option.isSlider()
				? new OptionSliderWidget(l, i, j, k, 20, option, 0.0, 1.0)
				: new OptionButtonWidget(l, i, j, k, 20, option, minecraftClient.options.getTranslatedName(option)) {
					@Override
					public void onPressed(double d, double e) {
						minecraftClient.options.updateOption(option, 1);
						this.text = minecraftClient.options.getTranslatedName(GameOptions.Option.byId(this.id));
					}
				});
		}
	}

	@Override
	public int getEntryWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	@Environment(EnvType.CLIENT)
	public final class class_354 extends EntryListWidget.Entry<VideoSettingsListWidget.class_354> {
		@Nullable
		private final ButtonWidget field_2149;
		@Nullable
		private final ButtonWidget field_2150;

		public class_354(@Nullable ButtonWidget buttonWidget, @Nullable ButtonWidget buttonWidget2) {
			this.field_2149 = buttonWidget;
			this.field_2150 = buttonWidget2;
		}

		public class_354(int i, GameOptions.Option option) {
			this(VideoSettingsListWidget.method_1914(VideoSettingsListWidget.this.client, i / 2 - 155, 0, 310, option), null);
		}

		public class_354(int i, GameOptions.Option option, @Nullable GameOptions.Option option2) {
			this(
				VideoSettingsListWidget.method_1914(VideoSettingsListWidget.this.client, i / 2 - 155, 0, 150, option),
				VideoSettingsListWidget.method_1914(VideoSettingsListWidget.this.client, i / 2 - 155 + 160, 0, 150, option2)
			);
		}

		@Override
		public void drawEntry(int i, int j, int k, int l, boolean bl, float f) {
			if (this.field_2149 != null) {
				this.field_2149.y = this.method_1906();
				this.field_2149.draw(k, l, f);
			}

			if (this.field_2150 != null) {
				this.field_2150.y = this.method_1906();
				this.field_2150.draw(k, l, f);
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			return this.field_2149.mouseClicked(d, e, i) ? true : this.field_2150 != null && this.field_2150.mouseClicked(d, e, i);
		}

		@Override
		public boolean mouseReleased(double d, double e, int i) {
			boolean bl = this.field_2149 != null && this.field_2149.mouseReleased(d, e, i);
			boolean bl2 = this.field_2150 != null && this.field_2150.mouseReleased(d, e, i);
			return bl || bl2;
		}

		@Override
		public void method_1904(float f) {
		}
	}
}
