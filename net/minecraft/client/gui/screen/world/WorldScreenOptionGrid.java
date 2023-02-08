/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class WorldScreenOptionGrid {
    private static final int BUTTON_WIDTH = 44;
    private final List<Option> options;

    WorldScreenOptionGrid(List<Option> options) {
        this.options = options;
    }

    public void refresh() {
        this.options.forEach(Option::refresh);
    }

    public static Builder builder(int width) {
        return new Builder(width);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final int width;
        private final List<OptionBuilder> options = new ArrayList<OptionBuilder>();
        int marginLeft;
        int rowSpacing = 4;
        int rows;
        Optional<TooltipBoxDisplay> tooltipBoxDisplay = Optional.empty();

        public Builder(int width) {
            this.width = width;
        }

        void incrementRows() {
            ++this.rows;
        }

        public OptionBuilder add(Text text, BooleanSupplier getter, Consumer<Boolean> setter) {
            OptionBuilder optionBuilder = new OptionBuilder(text, getter, setter, 44);
            this.options.add(optionBuilder);
            return optionBuilder;
        }

        public Builder marginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder setRowSpacing(int rowSpacing) {
            this.rowSpacing = rowSpacing;
            return this;
        }

        public WorldScreenOptionGrid build(Consumer<Widget> widgetConsumer) {
            GridWidget gridWidget = new GridWidget().setRowSpacing(this.rowSpacing);
            gridWidget.add(EmptyWidget.ofWidth(this.width - 44), 0, 0);
            gridWidget.add(EmptyWidget.ofWidth(44), 0, 1);
            ArrayList<Option> list = new ArrayList<Option>();
            this.rows = 0;
            for (OptionBuilder optionBuilder : this.options) {
                list.add(optionBuilder.build(this, gridWidget, 0));
            }
            gridWidget.refreshPositions();
            widgetConsumer.accept(gridWidget);
            WorldScreenOptionGrid worldScreenOptionGrid = new WorldScreenOptionGrid(list);
            worldScreenOptionGrid.refresh();
            return worldScreenOptionGrid;
        }

        public Builder withTooltipBox(int maxInfoRows, boolean alwaysMaxHeight) {
            this.tooltipBoxDisplay = Optional.of(new TooltipBoxDisplay(maxInfoRows, alwaysMaxHeight));
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record TooltipBoxDisplay(int maxInfoRows, boolean alwaysMaxHeight) {
    }

    @Environment(value=EnvType.CLIENT)
    record Option(CyclingButtonWidget<Boolean> button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
        public void refresh() {
            this.button.setValue(this.getter.getAsBoolean());
            if (this.toggleable != null) {
                this.button.active = this.toggleable.getAsBoolean();
            }
        }

        @Nullable
        public BooleanSupplier toggleable() {
            return this.toggleable;
        }
    }

    @Environment(value=EnvType.CLIENT)
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

        public OptionBuilder toggleable(BooleanSupplier toggleable) {
            this.toggleable = toggleable;
            return this;
        }

        public OptionBuilder tooltip(Text tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        Option build(Builder gridBuilder, GridWidget gridWidget, int row) {
            boolean bl;
            gridBuilder.incrementRows();
            TextWidget textWidget = new TextWidget(this.text, MinecraftClient.getInstance().textRenderer).alignLeft();
            gridWidget.add(textWidget, gridBuilder.rows, row, gridWidget.copyPositioner().relative(0.0f, 0.5f).marginLeft(gridBuilder.marginLeft));
            Optional<TooltipBoxDisplay> optional = gridBuilder.tooltipBoxDisplay;
            CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
            builder.omitKeyText();
            boolean bl2 = bl = this.tooltip != null && !optional.isPresent();
            if (bl) {
                Tooltip tooltip = Tooltip.of(this.tooltip);
                builder.tooltip((T value) -> tooltip);
            }
            if (this.tooltip != null && !bl) {
                builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage(), this.tooltip));
            } else {
                builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage()));
            }
            CyclingButtonWidget<Boolean> cyclingButtonWidget = builder.build(0, 0, this.buttonWidth, 20, Text.empty(), (button, value) -> this.setter.accept((Boolean)value));
            if (this.toggleable != null) {
                cyclingButtonWidget.active = this.toggleable.getAsBoolean();
            }
            gridWidget.add(cyclingButtonWidget, gridBuilder.rows, row + 1, gridWidget.copyPositioner().alignRight());
            if (this.tooltip != null) {
                optional.ifPresent(tooltipBoxDisplay -> {
                    MutableText text = this.tooltip.copy().formatted(Formatting.GRAY);
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    MultilineTextWidget multilineTextWidget = new MultilineTextWidget(text, textRenderer);
                    multilineTextWidget.setMaxWidth(builder.width - builder.marginLeft - this.buttonWidth);
                    multilineTextWidget.setMaxRows(tooltipBoxDisplay.maxInfoRows());
                    gridBuilder.incrementRows();
                    int j = tooltipBoxDisplay.alwaysMaxHeight ? textRenderer.fontHeight * tooltipBoxDisplay.maxInfoRows - multilineTextWidget.getHeight() : 0;
                    gridWidget.add(multilineTextWidget, builder.rows, row, gridWidget.copyPositioner().marginTop(-builder.rowSpacing).marginBottom(j));
                });
            }
            return new Option(cyclingButtonWidget, this.getter, this.toggleable);
        }
    }
}

