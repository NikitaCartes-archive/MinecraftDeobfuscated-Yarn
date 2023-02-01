/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
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
        private final int width;
        private final List<OptionBuilder> options = new ArrayList<OptionBuilder>();
        int marginLeft;

        public Builder(int width) {
            this.width = width;
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

        public WorldScreenOptionGrid build(Consumer<Widget> widgetConsumer) {
            GridWidget gridWidget = new GridWidget().setRowSpacing(4);
            gridWidget.add(EmptyWidget.ofWidth(this.width - 44), 0, 0);
            gridWidget.add(EmptyWidget.ofWidth(44), 0, 1);
            ArrayList<Option> list = new ArrayList<Option>();
            int i = 0;
            for (OptionBuilder optionBuilder : this.options) {
                list.add(optionBuilder.build(this, gridWidget, i++, 0));
            }
            gridWidget.refreshPositions();
            widgetConsumer.accept(gridWidget);
            return new WorldScreenOptionGrid(list);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Option {
        private final CyclingButtonWidget<Boolean> button;
        private final BooleanSupplier getter;
        @Nullable
        private final BooleanSupplier toggleable;

        public Option(CyclingButtonWidget<Boolean> button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
            this.button = button;
            this.getter = getter;
            this.toggleable = toggleable;
        }

        public void refresh() {
            this.button.setValue(this.getter.getAsBoolean());
            if (this.toggleable != null) {
                this.button.active = this.toggleable.getAsBoolean();
            }
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

        Option build(Builder gridBuilder, GridWidget gridWidget, int row, int column) {
            TextWidget textWidget = new TextWidget(this.text, MinecraftClient.getInstance().textRenderer).alignLeft();
            gridWidget.add(textWidget, row, column, gridWidget.copyPositioner().relative(0.0f, 0.5f).marginLeft(gridBuilder.marginLeft));
            CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
            builder.omitKeyText();
            builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage()));
            if (this.tooltip != null) {
                builder.tooltip((T value) -> Tooltip.of(this.tooltip));
            }
            CyclingButtonWidget<Boolean> cyclingButtonWidget = builder.build(0, 0, this.buttonWidth, 20, Text.empty(), (button, value) -> this.setter.accept((Boolean)value));
            if (this.toggleable != null) {
                cyclingButtonWidget.active = this.toggleable.getAsBoolean();
            }
            gridWidget.add(cyclingButtonWidget, row, column + 1, gridWidget.copyPositioner().alignRight());
            return new Option(cyclingButtonWidget, this.getter, this.toggleable);
        }
    }
}

