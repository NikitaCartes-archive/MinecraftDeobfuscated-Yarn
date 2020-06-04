package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends ElementListWidget<ButtonListWidget.ButtonEntry> {
	public ButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.centerListVertically = false;
	}

	public int addSingleOptionEntry(Option option) {
		return this.addEntry(ButtonListWidget.ButtonEntry.create(this.client.options, this.width, option));
	}

	public void addOptionEntry(Option firstOption, @Nullable Option secondOption) {
		this.addEntry(ButtonListWidget.ButtonEntry.create(this.client.options, this.width, firstOption, secondOption));
	}

	public void addAll(Option[] options) {
		for (int i = 0; i < options.length; i += 2) {
			this.addOptionEntry(options[i], i < options.length - 1 ? options[i + 1] : null);
		}
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 32;
	}

	public Optional<AbstractButtonWidget> method_29624(double d, double e) {
		for (ButtonListWidget.ButtonEntry buttonEntry : this.children()) {
			for (AbstractButtonWidget abstractButtonWidget : buttonEntry.buttons) {
				if (abstractButtonWidget.isMouseOver(d, e)) {
					return Optional.of(abstractButtonWidget);
				}
			}
		}

		return Optional.empty();
	}

	@Environment(EnvType.CLIENT)
	public static class ButtonEntry extends ElementListWidget.Entry<ButtonListWidget.ButtonEntry> {
		private final List<AbstractButtonWidget> buttons;

		private ButtonEntry(List<AbstractButtonWidget> buttons) {
			this.buttons = buttons;
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int width, Option option) {
			return new ButtonListWidget.ButtonEntry(ImmutableList.of(option.createButton(options, width / 2 - 155, 0, 310)));
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int width, Option firstOption, @Nullable Option secondOption) {
			AbstractButtonWidget abstractButtonWidget = firstOption.createButton(options, width / 2 - 155, 0, 150);
			return secondOption == null
				? new ButtonListWidget.ButtonEntry(ImmutableList.of(abstractButtonWidget))
				: new ButtonListWidget.ButtonEntry(ImmutableList.of(abstractButtonWidget, secondOption.createButton(options, width / 2 - 155 + 160, 0, 150)));
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.buttons.forEach(abstractButtonWidget -> {
				abstractButtonWidget.y = y;
				abstractButtonWidget.render(matrices, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public List<? extends Element> children() {
			return this.buttons;
		}
	}
}
