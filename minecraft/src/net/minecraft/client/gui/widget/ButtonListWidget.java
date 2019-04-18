package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends ElementListWidget<ButtonListWidget.ButtonItem> {
	public ButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.centerListVertically = false;
	}

	public int method_20406(GameOption gameOption) {
		return this.addEntry(ButtonListWidget.ButtonItem.method_20409(this.minecraft.options, this.width, gameOption));
	}

	public void method_20407(GameOption gameOption, @Nullable GameOption gameOption2) {
		this.addEntry(ButtonListWidget.ButtonItem.method_20410(this.minecraft.options, this.width, gameOption, gameOption2));
	}

	public void addAll(GameOption[] gameOptions) {
		for (int i = 0; i < gameOptions.length; i += 2) {
			this.method_20407(gameOptions[i], i < gameOptions.length - 1 ? gameOptions[i + 1] : null);
		}
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	@Environment(EnvType.CLIENT)
	public static class ButtonItem extends ElementListWidget.Entry<ButtonListWidget.ButtonItem> {
		private final List<AbstractButtonWidget> buttons;

		private ButtonItem(List<AbstractButtonWidget> list) {
			this.buttons = list;
		}

		public static ButtonListWidget.ButtonItem method_20409(GameOptions gameOptions, int i, GameOption gameOption) {
			return new ButtonListWidget.ButtonItem(ImmutableList.of(gameOption.createOptionButton(gameOptions, i / 2 - 155, 0, 310)));
		}

		public static ButtonListWidget.ButtonItem method_20410(GameOptions gameOptions, int i, GameOption gameOption, @Nullable GameOption gameOption2) {
			AbstractButtonWidget abstractButtonWidget = gameOption.createOptionButton(gameOptions, i / 2 - 155, 0, 150);
			return gameOption2 == null
				? new ButtonListWidget.ButtonItem(ImmutableList.of(abstractButtonWidget))
				: new ButtonListWidget.ButtonItem(ImmutableList.of(abstractButtonWidget, gameOption2.createOptionButton(gameOptions, i / 2 - 155 + 160, 0, 150)));
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
