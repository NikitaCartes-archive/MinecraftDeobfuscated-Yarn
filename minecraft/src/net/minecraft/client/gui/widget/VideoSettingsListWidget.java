package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class VideoSettingsListWidget extends ElementListWidget<VideoSettingsListWidget.ButtonItem> {
	public VideoSettingsListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, GameOption... gameOptions) {
		super(minecraftClient, i, j, k, l, m);
		this.verticallyCenter = false;
		this.addItem(new VideoSettingsListWidget.ButtonItem(minecraftClient.options, i, GameOption.FULLSCREEN_RESOLUTION));

		for (int n = 0; n < gameOptions.length; n += 2) {
			GameOption gameOption = gameOptions[n];
			if (n < gameOptions.length - 1) {
				this.addItem(new VideoSettingsListWidget.ButtonItem(minecraftClient.options, i, gameOption, gameOptions[n + 1]));
			} else {
				this.addItem(new VideoSettingsListWidget.ButtonItem(minecraftClient.options, i, gameOption));
			}
		}
	}

	@Override
	public int getItemWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	@Environment(EnvType.CLIENT)
	public static class ButtonItem extends ElementListWidget.ElementItem<VideoSettingsListWidget.ButtonItem> {
		private final List<AbstractButtonWidget> buttons;

		private ButtonItem(List<AbstractButtonWidget> list) {
			this.buttons = list;
		}

		public ButtonItem(GameOptions gameOptions, int i, GameOption gameOption) {
			this(ImmutableList.of(gameOption.createOptionButton(gameOptions, i / 2 - 155, 0, 310)));
		}

		public ButtonItem(GameOptions gameOptions, int i, GameOption gameOption, GameOption gameOption2) {
			this(
				ImmutableList.of(gameOption.createOptionButton(gameOptions, i / 2 - 155, 0, 150), gameOption2.createOptionButton(gameOptions, i / 2 - 155 + 160, 0, 150))
			);
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.buttons.forEach(abstractButtonWidget -> {
				abstractButtonWidget.y = j;
				abstractButtonWidget.render(n, o, f);
			});
		}

		@Override
		public List<? extends Element> children() {
			return this.buttons;
		}
	}
}
