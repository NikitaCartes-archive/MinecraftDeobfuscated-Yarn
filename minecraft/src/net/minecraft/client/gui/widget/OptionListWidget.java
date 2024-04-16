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
import net.minecraft.client.gui.screen.Screen;
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
		this.addEntry(OptionListWidget.OptionWidgetEntry.create(this.client.options, option, this.optionsScreen));
	}

	public void addAll(SimpleOption<?>... options) {
		for (int i = 0; i < options.length; i += 2) {
			SimpleOption<?> simpleOption = i < options.length - 1 ? options[i + 1] : null;
			this.addEntry(OptionListWidget.OptionWidgetEntry.create(this.client.options, options[i], simpleOption, this.optionsScreen));
		}
	}

	public void addAll(List<ClickableWidget> widgets) {
		for (int i = 0; i < widgets.size(); i += 2) {
			this.addWidgetEntry((ClickableWidget)widgets.get(i), i < widgets.size() - 1 ? (ClickableWidget)widgets.get(i + 1) : null);
		}
	}

	public void addWidgetEntry(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget) {
		this.addEntry(OptionListWidget.WidgetEntry.create(firstWidget, secondWidget, this.optionsScreen));
	}

	@Override
	public int getRowWidth() {
		return 310;
	}

	@Nullable
	public ClickableWidget getWidgetFor(SimpleOption<?> option) {
		for (OptionListWidget.WidgetEntry widgetEntry : this.children()) {
			if (widgetEntry instanceof OptionListWidget.OptionWidgetEntry optionWidgetEntry) {
				ClickableWidget clickableWidget = (ClickableWidget)optionWidgetEntry.optionWidgets.get(option);
				if (clickableWidget != null) {
					return clickableWidget;
				}
			}
		}

		return null;
	}

	public void applyAllPendingValues() {
		for (OptionListWidget.WidgetEntry widgetEntry : this.children()) {
			if (widgetEntry instanceof OptionListWidget.OptionWidgetEntry) {
				OptionListWidget.OptionWidgetEntry optionWidgetEntry = (OptionListWidget.OptionWidgetEntry)widgetEntry;

				for (ClickableWidget clickableWidget : optionWidgetEntry.optionWidgets.values()) {
					if (clickableWidget instanceof SimpleOption.OptionSliderWidgetImpl<?> optionSliderWidgetImpl) {
						optionSliderWidgetImpl.applyPendingValue();
					}
				}
			}
		}
	}

	public Optional<Element> getHoveredWidget(double mouseX, double mouseY) {
		for (OptionListWidget.WidgetEntry widgetEntry : this.children()) {
			for (Element element : widgetEntry.children()) {
				if (element.isMouseOver(mouseX, mouseY)) {
					return Optional.of(element);
				}
			}
		}

		return Optional.empty();
	}

	@Environment(EnvType.CLIENT)
	protected static class OptionWidgetEntry extends OptionListWidget.WidgetEntry {
		final Map<SimpleOption<?>, ClickableWidget> optionWidgets;

		private OptionWidgetEntry(Map<SimpleOption<?>, ClickableWidget> widgets, GameOptionsScreen optionsScreen) {
			super(ImmutableList.copyOf(widgets.values()), optionsScreen);
			this.optionWidgets = widgets;
		}

		public static OptionListWidget.OptionWidgetEntry create(GameOptions gameOptions, SimpleOption<?> option, GameOptionsScreen optionsScreen) {
			return new OptionListWidget.OptionWidgetEntry(ImmutableMap.of(option, option.createWidget(gameOptions, 0, 0, 310)), optionsScreen);
		}

		public static OptionListWidget.OptionWidgetEntry create(
			GameOptions gameOptions, SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption, GameOptionsScreen optionsScreen
		) {
			ClickableWidget clickableWidget = firstOption.createWidget(gameOptions);
			return secondOption == null
				? new OptionListWidget.OptionWidgetEntry(ImmutableMap.of(firstOption, clickableWidget), optionsScreen)
				: new OptionListWidget.OptionWidgetEntry(ImmutableMap.of(firstOption, clickableWidget, secondOption, secondOption.createWidget(gameOptions)), optionsScreen);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class WidgetEntry extends ElementListWidget.Entry<OptionListWidget.WidgetEntry> {
		private final List<ClickableWidget> widgets;
		private final Screen screen;
		private static final int WIDGET_X_SPACING = 160;

		WidgetEntry(List<ClickableWidget> widgets, Screen screen) {
			this.widgets = ImmutableList.copyOf(widgets);
			this.screen = screen;
		}

		public static OptionListWidget.WidgetEntry create(List<ClickableWidget> widgets, Screen screen) {
			return new OptionListWidget.WidgetEntry(widgets, screen);
		}

		public static OptionListWidget.WidgetEntry create(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget, Screen screen) {
			return secondWidget == null
				? new OptionListWidget.WidgetEntry(ImmutableList.of(firstWidget), screen)
				: new OptionListWidget.WidgetEntry(ImmutableList.of(firstWidget, secondWidget), screen);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = 0;
			int j = this.screen.width / 2 - 155;

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
