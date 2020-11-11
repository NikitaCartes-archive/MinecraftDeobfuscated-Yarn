package net.minecraft.client.options;

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
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CyclingOption<T> extends Option {
	private final CyclingOption.class_5675<T> setter;
	private final Function<GameOptions, T> getter;
	private final Supplier<CyclingButtonWidget.Builder<T>> field_27954;
	private Function<MinecraftClient, CyclingButtonWidget.class_5679<T>> field_27955 = client -> value -> ImmutableList.of();

	private CyclingOption(String key, Function<GameOptions, T> getter, CyclingOption.class_5675<T> setter, Supplier<CyclingButtonWidget.Builder<T>> supplier) {
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.field_27954 = supplier;
	}

	public static <T> CyclingOption<T> create(
		String key, Supplier<List<T>> supplier, Function<T, Text> function, Function<GameOptions, T> getter, CyclingOption.class_5675<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.method_32606(function).method_32620((List<T>)supplier.get()));
	}

	public static <T> CyclingOption<T> create(
		String key,
		List<T> list,
		List<T> list2,
		BooleanSupplier booleanSupplier,
		Function<T, Text> function,
		Function<GameOptions, T> getter,
		CyclingOption.class_5675<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.method_32606(function).method_32622(booleanSupplier, list, list2));
	}

	public static <T> CyclingOption<T> create(
		String key, T[] objects, Function<T, Text> function, Function<GameOptions, T> getter, CyclingOption.class_5675<T> setter
	) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.method_32606(function).method_32624(objects));
	}

	public static CyclingOption<Boolean> create(String key, Text text, Text text2, Function<GameOptions, Boolean> getter, CyclingOption.class_5675<Boolean> setter) {
		return new CyclingOption<>(key, getter, setter, () -> CyclingButtonWidget.method_32607(text, text2));
	}

	public static CyclingOption<Boolean> create(String key, Function<GameOptions, Boolean> getter, CyclingOption.class_5675<Boolean> setter) {
		return new CyclingOption<>(key, getter, setter, CyclingButtonWidget::method_32614);
	}

	public static CyclingOption<Boolean> create(String key, Text text, Function<GameOptions, Boolean> getter, CyclingOption.class_5675<Boolean> setter) {
		return create(key, getter, setter).method_32528(client -> {
			List<OrderedText> list = client.textRenderer.wrapLines(text, 200);
			return value -> list;
		});
	}

	public CyclingOption<T> method_32528(Function<MinecraftClient, CyclingButtonWidget.class_5679<T>> function) {
		this.field_27955 = function;
		return this;
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
		CyclingButtonWidget.class_5679<T> lv = (CyclingButtonWidget.class_5679<T>)this.field_27955.apply(MinecraftClient.getInstance());
		return ((CyclingButtonWidget.Builder)this.field_27954.get())
			.method_32618(lv)
			.value((T)this.getter.apply(options))
			.build(x, y, width, 20, this.getDisplayPrefix(), (button, value) -> {
				this.setter.accept(options, this, value);
				options.write();
			});
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface class_5675<T> {
		void accept(GameOptions gameOptions, Option option, T value);
	}
}
