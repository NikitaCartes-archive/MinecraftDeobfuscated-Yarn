package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends ElementListWidget<ButtonListWidget.ButtonItem> {
	public ButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.centerListVertically = false;
	}

	public int method_20406(Option option) {
		return this.addEntry(ButtonListWidget.ButtonItem.method_20409(this.minecraft.options, this.width, option));
	}

	public void method_20407(Option option, @Nullable Option option2) {
		this.addEntry(ButtonListWidget.ButtonItem.method_20410(this.minecraft.options, this.width, option, option2));
	}

	public void addAll(Option[] options) {
		for (int i = 0; i < options.length; i += 2) {
			this.method_20407(options[i], i < options.length - 1 ? options[i + 1] : null);
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

		public static ButtonListWidget.ButtonItem method_20409(GameOptions gameOptions, int i, Option option) {
			return new ButtonListWidget.ButtonItem(ImmutableList.of(option.createButton(gameOptions, i / 2 - 155, 0, 310)));
		}

		public static ButtonListWidget.ButtonItem method_20410(GameOptions gameOptions, int i, Option option, @Nullable Option option2) {
			AbstractButtonWidget abstractButtonWidget = option.createButton(gameOptions, i / 2 - 155, 0, 150);
			return option2 == null
				? new ButtonListWidget.ButtonItem(ImmutableList.of(abstractButtonWidget))
				: new ButtonListWidget.ButtonItem(ImmutableList.of(abstractButtonWidget, option2.createButton(gameOptions, i / 2 - 155 + 160, 0, 150)));
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
