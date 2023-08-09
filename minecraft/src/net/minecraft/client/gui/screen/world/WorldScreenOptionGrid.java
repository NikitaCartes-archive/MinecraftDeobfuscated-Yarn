package net.minecraft.client.gui.screen.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
class WorldScreenOptionGrid {
	private static final int BUTTON_WIDTH = 44;
	private final List<WorldScreenOptionGrid.Option> options;

	WorldScreenOptionGrid(List<WorldScreenOptionGrid.Option> options) {
		this.options = options;
	}

	public void refresh() {
		this.options.forEach(WorldScreenOptionGrid.Option::refresh);
	}

	public static WorldScreenOptionGrid.Builder builder(int width) {
		return new WorldScreenOptionGrid.Builder(width);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		final int width;
		private final List<WorldScreenOptionGrid.OptionBuilder> options = new ArrayList();
		int marginLeft;
		int rowSpacing = 4;
		int rows;
		Optional<WorldScreenOptionGrid.TooltipBoxDisplay> tooltipBoxDisplay = Optional.empty();

		public Builder(int width) {
			this.width = width;
		}

		void incrementRows() {
			this.rows++;
		}

		public WorldScreenOptionGrid.OptionBuilder add(Text text, BooleanSupplier getter, Consumer<Boolean> setter) {
			WorldScreenOptionGrid.OptionBuilder optionBuilder = new WorldScreenOptionGrid.OptionBuilder(text, getter, setter, 44);
			this.options.add(optionBuilder);
			return optionBuilder;
		}

		public WorldScreenOptionGrid.Builder marginLeft(int marginLeft) {
			this.marginLeft = marginLeft;
			return this;
		}

		public WorldScreenOptionGrid.Builder setRowSpacing(int rowSpacing) {
			this.rowSpacing = rowSpacing;
			return this;
		}

		public WorldScreenOptionGrid build(Consumer<Widget> widgetConsumer) {
			GridWidget gridWidget = new GridWidget().setRowSpacing(this.rowSpacing);
			gridWidget.add(EmptyWidget.ofWidth(this.width - 44), 0, 0);
			gridWidget.add(EmptyWidget.ofWidth(44), 0, 1);
			List<WorldScreenOptionGrid.Option> list = new ArrayList();
			this.rows = 0;

			for (WorldScreenOptionGrid.OptionBuilder optionBuilder : this.options) {
				list.add(optionBuilder.build(this, gridWidget, 0));
			}

			gridWidget.refreshPositions();
			widgetConsumer.accept(gridWidget);
			WorldScreenOptionGrid worldScreenOptionGrid = new WorldScreenOptionGrid(list);
			worldScreenOptionGrid.refresh();
			return worldScreenOptionGrid;
		}

		public WorldScreenOptionGrid.Builder withTooltipBox(int maxInfoRows, boolean alwaysMaxHeight) {
			this.tooltipBoxDisplay = Optional.of(new WorldScreenOptionGrid.TooltipBoxDisplay(maxInfoRows, alwaysMaxHeight));
			return this;
		}
	}

	@Environment(EnvType.CLIENT)
	static record Option(CyclingButtonWidget<Boolean> button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
		public void refresh() {
			this.button.setValue(this.getter.getAsBoolean());
			if (this.toggleable != null) {
				this.button.active = this.toggleable.getAsBoolean();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class OptionBuilder {
		private final Text text;
		private final BooleanSupplier getter;
		private final Consumer<Boolean> setter;
		@Nullable
		private Text tooltip;
		@Nullable
		private BooleanSupplier toggleable;
		private final int buttonWidth;

		OptionBuilder(Text text, BooleanSupplier getter, Consumer<Boolean> setter, int buttonWidth) {
			this.text = text;
			this.getter = getter;
			this.setter = setter;
			this.buttonWidth = buttonWidth;
		}

		public WorldScreenOptionGrid.OptionBuilder toggleable(BooleanSupplier toggleable) {
			this.toggleable = toggleable;
			return this;
		}

		public WorldScreenOptionGrid.OptionBuilder tooltip(Text tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		WorldScreenOptionGrid.Option build(WorldScreenOptionGrid.Builder gridBuilder, GridWidget gridWidget, int row) {
			gridBuilder.incrementRows();
			TextWidget textWidget = new TextWidget(this.text, MinecraftClient.getInstance().textRenderer).alignLeft();
			gridWidget.add(textWidget, gridBuilder.rows, row, gridWidget.copyPositioner().relative(0.0F, 0.5F).marginLeft(gridBuilder.marginLeft));
			Optional<WorldScreenOptionGrid.TooltipBoxDisplay> optional = gridBuilder.tooltipBoxDisplay;
			CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
			builder.omitKeyText();
			boolean bl = this.tooltip != null && optional.isEmpty();
			if (bl) {
				Tooltip tooltip = Tooltip.of(this.tooltip);
				builder.tooltip(value -> tooltip);
			}

			if (this.tooltip != null && !bl) {
				builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage(), this.tooltip));
			} else {
				builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage()));
			}

			CyclingButtonWidget<Boolean> cyclingButtonWidget = builder.build(0, 0, this.buttonWidth, 20, Text.empty(), (button, value) -> this.setter.accept(value));
			if (this.toggleable != null) {
				cyclingButtonWidget.active = this.toggleable.getAsBoolean();
			}

			gridWidget.add(cyclingButtonWidget, gridBuilder.rows, row + 1, gridWidget.copyPositioner().alignRight());
			if (this.tooltip != null) {
				optional.ifPresent(tooltipBoxDisplay -> {
					Text text = this.tooltip.copy().formatted(Formatting.GRAY);
					TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
					MultilineTextWidget multilineTextWidget = new MultilineTextWidget(text, textRenderer);
					multilineTextWidget.setMaxWidth(gridBuilder.width - gridBuilder.marginLeft - this.buttonWidth);
					multilineTextWidget.setMaxRows(tooltipBoxDisplay.maxInfoRows());
					gridBuilder.incrementRows();
					int j = tooltipBoxDisplay.alwaysMaxHeight ? 9 * tooltipBoxDisplay.maxInfoRows - multilineTextWidget.getHeight() : 0;
					gridWidget.add(multilineTextWidget, gridBuilder.rows, row, gridWidget.copyPositioner().marginTop(-gridBuilder.rowSpacing).marginBottom(j));
				});
			}

			return new WorldScreenOptionGrid.Option(cyclingButtonWidget, this.getter, this.toggleable);
		}
	}

	@Environment(EnvType.CLIENT)
	static record TooltipBoxDisplay(int maxInfoRows, boolean alwaysMaxHeight) {
	}
}
