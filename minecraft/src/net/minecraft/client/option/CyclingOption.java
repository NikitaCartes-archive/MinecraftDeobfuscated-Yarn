package net.minecraft.client.option;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Deprecated(
	forRemoval = true
)
@Environment(EnvType.CLIENT)
public class CyclingOption<T> extends Option {
	private final CyclingOption.Setter<T> setter;
	private final Function<GameOptions, T> getter;
	private final Supplier<CyclingButtonWidget.Builder<T>> buttonBuilderFactory;
	private Function<MinecraftClient, Option.TooltipFactory<T>> tooltips = emptyTooltipFactoryGetter();

	private CyclingOption(
		String key, Function<GameOptions, T> getter, CyclingOption.Setter<T> setter, Supplier<CyclingButtonWidget.Builder<T>> buttonBuilderFactory
	) {
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.buttonBuilderFactory = buttonBuilderFactory;
	}

	public static <T> CyclingOption<T> create(
		String key, List<T> values, Function<T, Text> valueToText, Function<GameOptions, T> getter, CyclingOption.Setter<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values(values));
	}

	public static <T> CyclingOption<T> create(
		String key, Supplier<List<T>> valuesSupplier, Function<T, Text> valueToText, Function<GameOptions, T> getter, CyclingOption.Setter<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values((Collection<T>)valuesSupplier.get()));
	}

	public static <T> CyclingOption<T> create(
		String key,
		List<T> defaults,
		List<T> alternatives,
		BooleanSupplier alternativeToggle,
		Function<T, Text> valueToText,
		Function<GameOptions, T> getter,
		CyclingOption.Setter<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values(alternativeToggle, defaults, alternatives));
	}

	public static <T> CyclingOption<T> create(
		String key, T[] values, Function<T, Text> valueToText, Function<GameOptions, T> getter, CyclingOption.Setter<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.builder(valueToText).values(values));
	}

	public static CyclingOption<Boolean> create(String key, Text on, Text off, Function<GameOptions, Boolean> getter, CyclingOption.Setter<Boolean> setter) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.onOffBuilder(on, off));
	}

	public static CyclingOption<Boolean> create(String key, Function<GameOptions, Boolean> getter, CyclingOption.Setter<Boolean> setter) {
		return new CyclingOption<>(key, getter, setter, CyclingButtonWidget::onOffBuilder);
	}

	public static CyclingOption<Boolean> create(String key, Text tooltip, Function<GameOptions, Boolean> getter, CyclingOption.Setter<Boolean> setter) {
		return create(key, getter, setter).tooltip(client -> {
			List<OrderedText> list = client.textRenderer.wrapLines(tooltip, 200);
			return value -> list;
		});
	}

	public CyclingOption<T> tooltip(Function<MinecraftClient, Option.TooltipFactory<T>> tooltips) {
		this.tooltips = tooltips;
		return this;
	}

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		Option.TooltipFactory<T> tooltipFactory = (Option.TooltipFactory<T>)this.tooltips.apply(MinecraftClient.getInstance());
		return ((CyclingButtonWidget.Builder)this.buttonBuilderFactory.get())
			.tooltip(tooltipFactory)
			.initially((T)this.getter.apply(options))
			.build(x, y, width, 20, this.getDisplayPrefix(), (button, value) -> {
				this.setter.accept(options, this, value);
				options.write();
			});
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Setter<T> {
		void accept(GameOptions gameOptions, Option option, T value);
	}
}
