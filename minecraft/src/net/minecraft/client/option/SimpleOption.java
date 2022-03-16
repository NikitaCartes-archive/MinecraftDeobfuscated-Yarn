package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SimpleOption<T> extends Option {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Function<MinecraftClient, Option.TooltipFactory<T>> tooltipFactoryGetter;
	final Function<T, Text> textGetter;
	private final SimpleOption.Callbacks<T> callbacks;
	private final T defaultValue;
	private final Consumer<T> changeCallback;
	T value;

	public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue, Consumer<Boolean> changeCallback) {
		return ofBoolean(key, emptyTooltipFactoryGetter(), defaultValue, changeCallback);
	}

	public static SimpleOption<Boolean> ofBoolean(String key, Function<MinecraftClient, Option.TooltipFactory<Boolean>> tooltipFactoryGetter, boolean defaultValue) {
		return ofBoolean(key, tooltipFactoryGetter, defaultValue, value -> {
		});
	}

	public static SimpleOption<Boolean> ofBoolean(
		String key, Function<MinecraftClient, Option.TooltipFactory<Boolean>> tooltipFactoryGetter, boolean defaultValue, Consumer<Boolean> changeCallback
	) {
		return new SimpleOption<>(
			key,
			tooltipFactoryGetter,
			value -> value ? ScreenTexts.ON : ScreenTexts.OFF,
			new SimpleOption.PotentialValuesBasedCallbacks<>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE)),
			defaultValue,
			changeCallback
		);
	}

	public SimpleOption(
		String key,
		Function<MinecraftClient, Option.TooltipFactory<T>> tooltipFactoryGetter,
		Function<T, Text> textGetter,
		SimpleOption.Callbacks<T> callbacks,
		T defaultValue,
		Consumer<T> changeCallback
	) {
		super(key);
		this.tooltipFactoryGetter = tooltipFactoryGetter;
		this.textGetter = textGetter;
		this.callbacks = callbacks;
		this.defaultValue = defaultValue;
		this.changeCallback = changeCallback;
		this.value = this.defaultValue;
	}

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		Option.TooltipFactory<T> tooltipFactory = (Option.TooltipFactory<T>)this.tooltipFactoryGetter.apply(MinecraftClient.getInstance());
		return (ClickableWidget)this.callbacks.getButtonCreator(tooltipFactory, options, x, y, width).apply(this);
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		if (!this.callbacks.validate(value)) {
			LOGGER.error("Illegal option value " + value + " for " + this.getDisplayPrefix());
			this.value = this.defaultValue;
		}

		if (!MinecraftClient.getInstance().isRunning()) {
			this.value = value;
		} else {
			if (!Objects.equals(this.value, value)) {
				this.value = value;
				this.changeCallback.accept(this.value);
			}
		}
	}

	public SimpleOption.Callbacks<T> getCallbacks() {
		return this.callbacks;
	}

	@Environment(EnvType.CLIENT)
	interface Callbacks<T> {
		Function<SimpleOption<T>, ClickableWidget> getButtonCreator(Option.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width);

		boolean validate(T value);
	}

	@Environment(EnvType.CLIENT)
	public static enum DoubleSliderCallbacks implements SimpleOption.SliderCallbacks<Double> {
		INSTANCE;

		@Override
		public Function<SimpleOption<Double>, ClickableWidget> getButtonCreator(
			Option.TooltipFactory<Double> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
			return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
		}

		public boolean validate(Double double_) {
			return double_ >= 0.0 && double_ <= 1.0;
		}

		public double toSliderProgress(Double double_) {
			return double_;
		}

		public Double toValue(double d) {
			return d;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record IntSliderCallbacks(int minInclusive, int maxInclusive) implements SimpleOption.SliderCallbacks<Integer> {
		@Override
		public Function<SimpleOption<Integer>, ClickableWidget> getButtonCreator(
			Option.TooltipFactory<Integer> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
			return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
		}

		public boolean validate(Integer integer) {
			return integer.compareTo(this.minInclusive) >= 0 && integer.compareTo(this.maxInclusive) <= 0;
		}

		public double toSliderProgress(Integer integer) {
			return (double)MathHelper.lerpFromProgress((float)integer.intValue(), (float)this.minInclusive, (float)this.maxInclusive, 0.0F, 1.0F);
		}

		public Integer toValue(double d) {
			return MathHelper.floor(MathHelper.lerpFromProgress(d, 0.0, 1.0, (double)this.minInclusive, (double)this.maxInclusive));
		}

		public <R> SimpleOption.SliderCallbacks<R> withModifier(
			IntFunction<? extends R> sliderProgressValueToValue, ToIntFunction<? super R> valueToSliderProgressValue
		) {
			return new SimpleOption.SliderCallbacks<R>() {
				@Override
				public Function<SimpleOption<R>, ClickableWidget> getButtonCreator(
					Option.TooltipFactory<R> tooltipFactory, GameOptions gameOptions, int x, int y, int width
				) {
					return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
				}

				@Override
				public boolean validate(R value) {
					return IntSliderCallbacks.this.validate(valueToSliderProgressValue.applyAsInt(value));
				}

				@Override
				public double toSliderProgress(R value) {
					return IntSliderCallbacks.this.toSliderProgress(valueToSliderProgressValue.applyAsInt(value));
				}

				@Override
				public R toValue(double sliderProgress) {
					return (R)sliderProgressValueToValue.apply(IntSliderCallbacks.this.toValue(sliderProgress));
				}
			};
		}
	}

	@Environment(EnvType.CLIENT)
	static final class OptionSliderWidgetImpl<N> extends OptionSliderWidget implements OrderableTooltip {
		private final SimpleOption<N> option;
		private final SimpleOption.SliderCallbacks<N> callbacks;
		private final Option.TooltipFactory<N> tooltipFactory;

		OptionSliderWidgetImpl(
			GameOptions options,
			int x,
			int y,
			int width,
			int height,
			SimpleOption<N> option,
			SimpleOption.SliderCallbacks<N> callbacks,
			Option.TooltipFactory<N> tooltipFactory
		) {
			super(options, x, y, width, height, callbacks.toSliderProgress(option.getValue()));
			this.option = option;
			this.callbacks = callbacks;
			this.tooltipFactory = tooltipFactory;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage((Text)this.option.textGetter.apply(this.option.getValue()));
		}

		@Override
		protected void applyValue() {
			this.option.setValue(this.callbacks.toValue(this.value));
			this.options.write();
		}

		@Override
		public List<OrderedText> getOrderedTooltip() {
			return (List<OrderedText>)this.tooltipFactory.apply(this.callbacks.toValue(this.value));
		}
	}

	@Environment(EnvType.CLIENT)
	public static record PotentialValuesBasedCallbacks<T>(List<T> values) implements SimpleOption.Callbacks<T> {
		@Override
		public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(Option.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width) {
			return option -> CyclingButtonWidget.<T>builder(option.textGetter)
					.values(this.values)
					.tooltip(tooltipFactory)
					.initially(option.value)
					.build(x, y, width, 20, option.getDisplayPrefix(), (button, value) -> {
						option.setValue(value);
						gameOptions.write();
					});
		}

		@Override
		public boolean validate(T value) {
			return this.values.contains(value);
		}
	}

	@Environment(EnvType.CLIENT)
	interface SliderCallbacks<T> extends SimpleOption.Callbacks<T> {
		double toSliderProgress(T value);

		T toValue(double sliderProgress);
	}
}
