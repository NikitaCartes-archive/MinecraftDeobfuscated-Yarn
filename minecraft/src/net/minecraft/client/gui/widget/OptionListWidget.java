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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(EnvType.CLIENT)
public class OptionListWidget extends ElementListWidget<OptionListWidget.WidgetEntry> {
	private static final int field_49481 = 310;
	private static final int field_49482 = 25;
	private final GameOptionsScreen optionsScreen;

	public OptionListWidget(MinecraftClient client, int width, int i, GameOptionsScreen optionsScreen) {
		super(client, width, optionsScreen.layout.getContentHeight(), optionsScreen.layout.getHeaderHeight(), 25);
		this.centerListVertically = false;
		this.optionsScreen = optionsScreen;
	}

	public void addSingleOptionEntry(SimpleOption<?> option) {
		this.addEntry(OptionListWidget.WidgetEntry.create(this.client.options, option, this.optionsScreen));
	}

	public void addOptionEntry(SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption) {
		this.addEntry(OptionListWidget.WidgetEntry.create(this.client.options, firstOption, secondOption, this.optionsScreen));
	}

	public void addAll(SimpleOption<?>[] options) {
		for (int i = 0; i < options.length; i += 2) {
			this.addOptionEntry(options[i], i < options.length - 1 ? options[i + 1] : null);
		}
	}

	@Override
	public int getRowWidth() {
		return 310;
	}

	@Nullable
	public ClickableWidget getWidgetFor(SimpleOption<?> option) {
		for (OptionListWidget.WidgetEntry widgetEntry : this.children()) {
			ClickableWidget clickableWidget = (ClickableWidget)widgetEntry.optionsToWidgets.get(option);
			if (clickableWidget != null) {
				return clickableWidget;
			}
		}

		return null;
	}

	public Optional<ClickableWidget> getHoveredWidget(double mouseX, double mouseY) {
		for (OptionListWidget.WidgetEntry widgetEntry : this.children()) {
			for (ClickableWidget clickableWidget : widgetEntry.widgets) {
				if (clickableWidget.isMouseOver(mouseX, mouseY)) {
					return Optional.of(clickableWidget);
				}
			}
		}

		return Optional.empty();
	}

	@Environment(EnvType.CLIENT)
	protected static class WidgetEntry extends ElementListWidget.Entry<OptionListWidget.WidgetEntry> {
		final Map<SimpleOption<?>, ClickableWidget> optionsToWidgets;
		final List<ClickableWidget> widgets;
		private static final int field_49484 = 160;
		private final GameOptionsScreen optionsScreen;

		private WidgetEntry(Map<SimpleOption<?>, ClickableWidget> optionsToWidgets, GameOptionsScreen optionsScreen) {
			this.optionsToWidgets = optionsToWidgets;
			this.widgets = ImmutableList.copyOf(optionsToWidgets.values());
			this.optionsScreen = optionsScreen;
		}

		public static OptionListWidget.WidgetEntry create(GameOptions options, SimpleOption<?> option, GameOptionsScreen optionsSCreen) {
			return new OptionListWidget.WidgetEntry(ImmutableMap.of(option, option.createWidget(options, 0, 0, 310)), optionsSCreen);
		}

		public static OptionListWidget.WidgetEntry create(
			GameOptions options, SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption, GameOptionsScreen optionsScreen
		) {
			ClickableWidget clickableWidget = firstOption.createWidget(options);
			return secondOption == null
				? new OptionListWidget.WidgetEntry(ImmutableMap.of(firstOption, clickableWidget), optionsScreen)
				: new OptionListWidget.WidgetEntry(ImmutableMap.of(firstOption, clickableWidget, secondOption, secondOption.createWidget(options)), optionsScreen);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = 0;
			int j = this.optionsScreen.width / 2 - 155;

			for (ClickableWidget clickableWidget : this.widgets) {
				clickableWidget.setPosition(j + i, y);
				clickableWidget.render(context, mouseX, mouseY, tickDelta);
				i += 160;
			}
		}

		@Override
		public List<? extends Element> children() {
			return this.widgets;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.widgets;
		}
	}
}
