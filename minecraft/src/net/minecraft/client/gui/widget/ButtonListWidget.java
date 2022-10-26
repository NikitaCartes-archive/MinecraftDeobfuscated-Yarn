package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends ElementListWidget<ButtonListWidget.ButtonEntry> {
	public ButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.centerListVertically = false;
	}

	public int addSingleOptionEntry(SimpleOption<?> option) {
		return this.addEntry(ButtonListWidget.ButtonEntry.create(this.client.options, this.width, option));
	}

	public void addOptionEntry(SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption) {
		this.addEntry(ButtonListWidget.ButtonEntry.create(this.client.options, this.width, firstOption, secondOption));
	}

	public void addAll(SimpleOption<?>[] options) {
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

	@Nullable
	public ClickableWidget getButtonFor(SimpleOption<?> option) {
		for (ButtonListWidget.ButtonEntry buttonEntry : this.children()) {
			ClickableWidget clickableWidget = (ClickableWidget)buttonEntry.optionsToButtons.get(option);
			if (clickableWidget != null) {
				return clickableWidget;
			}
		}

		return null;
	}

	public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
		for (ButtonListWidget.ButtonEntry buttonEntry : this.children()) {
			for (ClickableWidget clickableWidget : buttonEntry.buttons) {
				if (clickableWidget.isMouseOver(mouseX, mouseY)) {
					return Optional.of(clickableWidget);
				}
			}
		}

		return Optional.empty();
	}

	@Environment(EnvType.CLIENT)
	protected static class ButtonEntry extends ElementListWidget.Entry<ButtonListWidget.ButtonEntry> {
		final Map<SimpleOption<?>, ClickableWidget> optionsToButtons;
		final List<ClickableWidget> buttons;

		private ButtonEntry(Map<SimpleOption<?>, ClickableWidget> optionsToButtons) {
			this.optionsToButtons = optionsToButtons;
			this.buttons = ImmutableList.copyOf(optionsToButtons.values());
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int width, SimpleOption<?> option) {
			return new ButtonListWidget.ButtonEntry(ImmutableMap.of(option, option.createButton(options, width / 2 - 155, 0, 310)));
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int width, SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption) {
			ClickableWidget clickableWidget = firstOption.createButton(options, width / 2 - 155, 0, 150);
			return secondOption == null
				? new ButtonListWidget.ButtonEntry(ImmutableMap.of(firstOption, clickableWidget))
				: new ButtonListWidget.ButtonEntry(
					ImmutableMap.of(firstOption, clickableWidget, secondOption, secondOption.createButton(options, width / 2 - 155 + 160, 0, 150))
				);
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.buttons.forEach(button -> {
				button.setY(y);
				button.render(matrices, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public List<? extends Element> children() {
			return this.buttons;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.buttons;
		}
	}
}
