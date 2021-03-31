/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class CyclingOption<T>
extends Option {
    private final Setter<T> setter;
    private final Function<GameOptions, T> getter;
    private final Supplier<CyclingButtonWidget.Builder<T>> buttonBuilderFactory;
    private Function<MinecraftClient, CyclingButtonWidget.TooltipFactory<T>> tooltips = client -> value -> ImmutableList.of();

    private CyclingOption(String key, Function<GameOptions, T> getter, Setter<T> setter, Supplier<CyclingButtonWidget.Builder<T>> buttonBuilderFactory) {
        super(key);
        this.getter = getter;
        this.setter = setter;
        this.buttonBuilderFactory = buttonBuilderFactory;
    }

    public static <T> CyclingOption<T> method_35694(String string, List<T> list, Function<T, Text> function, Function<GameOptions, T> function2, Setter<T> setter) {
        return new CyclingOption<T>(string, function2, setter, () -> CyclingButtonWidget.builder(function).values(list));
    }

    public static <T> CyclingOption<T> create(String key, Supplier<List<T>> valuesSupplier, Function<T, Text> valueToText, Function<GameOptions, T> getter, Setter<T> setter) {
        return new CyclingOption<T>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values((List)valuesSupplier.get()));
    }

    public static <T> CyclingOption<T> create(String key, List<T> defaults, List<T> alternatives, BooleanSupplier alternativeToggle, Function<T, Text> valueToText, Function<GameOptions, T> getter, Setter<T> setter) {
        return new CyclingOption<T>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values(alternativeToggle, defaults, alternatives));
    }

    public static <T> CyclingOption<T> create(String key, T[] values, Function<T, Text> valueToText, Function<GameOptions, T> getter, Setter<T> setter) {
        return new CyclingOption<T>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values(values));
    }

    public static CyclingOption<Boolean> create(String key, Text on, Text off, Function<GameOptions, Boolean> getter, Setter<Boolean> setter) {
        return new CyclingOption<Boolean>(key, getter, setter, () -> CyclingButtonWidget.onOffBuilder(on, off));
    }

    public static CyclingOption<Boolean> create(String key, Function<GameOptions, Boolean> getter, Setter<Boolean> setter) {
        return new CyclingOption<Boolean>(key, getter, setter, CyclingButtonWidget::onOffBuilder);
    }

    public static CyclingOption<Boolean> create(String key, Text tooltip, Function<GameOptions, Boolean> getter, Setter<Boolean> setter) {
        return CyclingOption.create(key, getter, setter).tooltip(client -> {
            List<OrderedText> list = client.textRenderer.wrapLines(tooltip, 200);
            return value -> list;
        });
    }

    public CyclingOption<T> tooltip(Function<MinecraftClient, CyclingButtonWidget.TooltipFactory<T>> tooltips) {
        this.tooltips = tooltips;
        return this;
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
        CyclingButtonWidget.TooltipFactory<T> tooltipFactory = this.tooltips.apply(MinecraftClient.getInstance());
        return this.buttonBuilderFactory.get().tooltip(tooltipFactory).initially(this.getter.apply(options)).build(x, y, width, 20, this.getDisplayPrefix(), (button, value) -> {
            this.setter.accept(options, this, value);
            options.write();
        });
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Setter<T> {
        public void accept(GameOptions var1, Option var2, T var3);
    }
}

