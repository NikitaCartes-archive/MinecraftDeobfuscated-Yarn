/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OptionListWidget
extends ElementListWidget<WidgetEntry> {
    public OptionListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.centerListVertically = false;
    }

    public int addSingleOptionEntry(SimpleOption<?> option) {
        return this.addEntry(WidgetEntry.create(this.client.options, this.width, option));
    }

    public void addOptionEntry(SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption) {
        this.addEntry(WidgetEntry.create(this.client.options, this.width, firstOption, secondOption));
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
    public ClickableWidget getWidgetFor(SimpleOption<?> option) {
        for (WidgetEntry widgetEntry : this.children()) {
            ClickableWidget clickableWidget = widgetEntry.optionsToWidgets.get(option);
            if (clickableWidget == null) continue;
            return clickableWidget;
        }
        return null;
    }

    public Optional<ClickableWidget> getHoveredWidget(double mouseX, double mouseY) {
        for (WidgetEntry widgetEntry : this.children()) {
            for (ClickableWidget clickableWidget : widgetEntry.widgets) {
                if (!clickableWidget.isMouseOver(mouseX, mouseY)) continue;
                return Optional.of(clickableWidget);
            }
        }
        return Optional.empty();
    }

    @Environment(value=EnvType.CLIENT)
    protected static class WidgetEntry
    extends ElementListWidget.Entry<WidgetEntry> {
        final Map<SimpleOption<?>, ClickableWidget> optionsToWidgets;
        final List<ClickableWidget> widgets;

        private WidgetEntry(Map<SimpleOption<?>, ClickableWidget> optionsToWidgets) {
            this.optionsToWidgets = optionsToWidgets;
            this.widgets = ImmutableList.copyOf(optionsToWidgets.values());
        }

        public static WidgetEntry create(GameOptions options, int width, SimpleOption<?> option) {
            return new WidgetEntry(ImmutableMap.of(option, option.createWidget(options, width / 2 - 155, 0, 310)));
        }

        public static WidgetEntry create(GameOptions options, int width, SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption) {
            ClickableWidget clickableWidget = firstOption.createWidget(options, width / 2 - 155, 0, 150);
            if (secondOption == null) {
                return new WidgetEntry(ImmutableMap.of(firstOption, clickableWidget));
            }
            return new WidgetEntry(ImmutableMap.of(firstOption, clickableWidget, secondOption, secondOption.createWidget(options, width / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.widgets.forEach(widget -> {
                widget.setY(y);
                widget.render(matrices, mouseX, mouseY, tickDelta);
            });
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

