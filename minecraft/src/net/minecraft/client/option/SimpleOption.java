package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class SimpleOption<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final SimpleOption.PotentialValuesBasedCallbacks<Boolean> BOOLEAN = new SimpleOption.PotentialValuesBasedCallbacks<>(
		ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL
	);
	private final Function<MinecraftClient, SimpleOption.TooltipFactory<T>> tooltipFactoryGetter;
	final Function<T, Text> textGetter;
	private final SimpleOption.Callbacks<T> callbacks;
	private final Codec<T> codec;
	private final T defaultValue;
	private final Consumer<T> changeCallback;
	private final Text text;
	T value;

	public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue, Consumer<Boolean> changeCallback) {
		return ofBoolean(key, emptyTooltipFactoryGetter(), defaultValue, changeCallback);
	}

	public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue) {
		return ofBoolean(key, emptyTooltipFactoryGetter(), defaultValue, value -> {
		});
	}

	public static SimpleOption<Boolean> ofBoolean(
		String key, Function<MinecraftClient, SimpleOption.TooltipFactory<Boolean>> tooltipFactoryGetter, boolean defaultValue
	) {
		return ofBoolean(key, tooltipFactoryGetter, defaultValue, value -> {
		});
	}

	public static SimpleOption<Boolean> ofBoolean(
		String key, Function<MinecraftClient, SimpleOption.TooltipFactory<Boolean>> tooltipFactoryGetter, boolean defaultValue, Consumer<Boolean> changeCallback
	) {
		return new SimpleOption<>(key, tooltipFactoryGetter, value -> value ? ScreenTexts.ON : ScreenTexts.OFF, BOOLEAN, defaultValue, changeCallback);
	}

	public SimpleOption(
		String key,
		Function<MinecraftClient, SimpleOption.TooltipFactory<T>> tooltipFactoryGetter,
		Function<T, Text> textGetter,
		SimpleOption.Callbacks<T> callbacks,
		T defaultValue,
		Consumer<T> changeCallback
	) {
		this(key, tooltipFactoryGetter, textGetter, callbacks, callbacks.codec(), defaultValue, changeCallback);
	}

	public SimpleOption(
		String key,
		Function<MinecraftClient, SimpleOption.TooltipFactory<T>> tooltipFactoryGetter,
		Function<T, Text> textGetter,
		SimpleOption.Callbacks<T> callbacks,
		Codec<T> codec,
		T defaultValue,
		Consumer<T> changeCallback
	) {
		this.text = new TranslatableText(key);
		this.tooltipFactoryGetter = tooltipFactoryGetter;
		this.textGetter = textGetter;
		this.callbacks = callbacks;
		this.codec = codec;
		this.defaultValue = defaultValue;
		this.changeCallback = changeCallback;
		this.value = this.defaultValue;
	}

	public static <T> Function<MinecraftClient, SimpleOption.TooltipFactory<T>> emptyTooltipFactoryGetter() {
		return client -> value -> ImmutableList.of();
	}

	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		SimpleOption.TooltipFactory<T> tooltipFactory = (SimpleOption.TooltipFactory<T>)this.tooltipFactoryGetter.apply(MinecraftClient.getInstance());
		return (ClickableWidget)this.callbacks.getButtonCreator(tooltipFactory, options, x, y, width).apply(this);
	}

	public T getValue() {
		return this.value;
	}

	public Codec<T> getCodec() {
		return this.codec;
	}

	public String toString() {
		return this.text.getString();
	}

	public void setValue(T value) {
		T object = (T)this.callbacks.validate(value).orElseGet(() -> {
			LOGGER.error("Illegal option value " + value + " for " + this.getDisplayPrefix());
			return this.defaultValue;
		});
		if (!MinecraftClient.getInstance().isRunning()) {
			this.value = object;
		} else {
			if (!Objects.equals(this.value, object)) {
				this.value = object;
				this.changeCallback.accept(this.value);
			}
		}
	}

	public SimpleOption.Callbacks<T> getCallbacks() {
		return this.callbacks;
	}

	protected Text getDisplayPrefix() {
		return this.text;
	}

	public static SimpleOption.Callbacks<Integer> createClampedIntCallbacks(int minValue, IntSupplier maxValueSupplier) {
		return new SimpleOption.IntSliderCallbacks() {
			public Optional<Integer> validate(Integer integer) {
				return Optional.of(MathHelper.clamp(integer, this.minInclusive(), this.maxInclusive()));
			}

			@Override
			public int minInclusive() {
				return minValue;
			}

			@Override
			public int maxInclusive() {
				return maxValueSupplier.getAsInt();
			}

			@Override
			public Codec<Integer> codec() {
				Function<Integer, DataResult<Integer>> function = value -> {
					int j = maxValueSupplier.getAsInt() + 1;
					return value.compareTo(minValue) >= 0 && value.compareTo(j) <= 0
						? DataResult.success(value)
						: DataResult.error("Value " + value + " outside of range [" + minValue + ":" + j + "]", value);
				};
				return Codec.INT.flatXmap(function, function);
			}
		};
	}

	@Environment(EnvType.CLIENT)
	public static record AlternateValuesSupportingCyclingCallbacks<T>(
		List<T> values, List<T> altValues, BooleanSupplier altCondition, SimpleOption.ValueSetter<T> altSetter, Codec<T> codec
	) implements SimpleOption.Callbacks<T> {
		@Override
		public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
			return option -> CyclingButtonWidget.<T>builder(option.textGetter)
					.values(this.altCondition, this.values, this.altValues)
					.tooltip(tooltipFactory)
					.initially(option.value)
					.build(x, y, width, 20, option.getDisplayPrefix(), (button, value) -> {
						this.altSetter.set(option, value);
						gameOptions.write();
					});
		}

		@Override
		public Optional<T> validate(T value) {
			return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
		}
	}

	@Environment(EnvType.CLIENT)
	interface Callbacks<T> {
		Function<SimpleOption<T>, ClickableWidget> getButtonCreator(SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width);

		Optional<T> validate(T value);

		Codec<T> codec();
	}

	@Environment(EnvType.CLIENT)
	public static record CyclingCallbacks<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec)
		implements SimpleOption.Callbacks<T> {
		@Override
		public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
			return option -> CyclingButtonWidget.<T>builder(option.textGetter)
					.values((Collection<T>)this.values.get())
					.tooltip(tooltipFactory)
					.initially(option.value)
					.build(x, y, width, 20, option.getDisplayPrefix(), (button, value) -> {
						option.setValue(value);
						gameOptions.write();
					});
		}

		@Override
		public Optional<T> validate(T value) {
			return (Optional<T>)this.validateValue.apply(value);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum DoubleSliderCallbacks implements SimpleOption.SliderCallbacks<Double> {
		INSTANCE;

		@Override
		public Function<SimpleOption<Double>, ClickableWidget> getButtonCreator(
			SimpleOption.TooltipFactory<Double> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
			return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
		}

		public Optional<Double> validate(Double double_) {
			return double_ >= 0.0 && double_ <= 1.0 ? Optional.of(double_) : Optional.empty();
		}

		public double toSliderProgress(Double double_) {
			return double_;
		}

		public Double toValue(double d) {
			return d;
		}

		public <R> SimpleOption.SliderCallbacks<R> withModifier(
			DoubleFunction<? extends R> sliderProgressValueToValue, ToDoubleFunction<? super R> valueToSliderProgressValue
		) {
			return new SimpleOption.SliderCallbacks<R>() {
				@Override
				public Function<SimpleOption<R>, ClickableWidget> getButtonCreator(
					SimpleOption.TooltipFactory<R> tooltipFactory, GameOptions gameOptions, int x, int y, int width
				) {
					return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
				}

				@Override
				public Optional<R> validate(R value) {
					return DoubleSliderCallbacks.this.validate(valueToSliderProgressValue.applyAsDouble(value)).map(sliderProgressValueToValue::apply);
				}

				@Override
				public double toSliderProgress(R value) {
					return DoubleSliderCallbacks.this.toSliderProgress(valueToSliderProgressValue.applyAsDouble(value));
				}

				@Override
				public R toValue(double sliderProgress) {
					return (R)sliderProgressValueToValue.apply(DoubleSliderCallbacks.this.toValue(sliderProgress));
				}

				@Override
				public Codec<R> codec() {
					return DoubleSliderCallbacks.this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsDouble);
				}
			};
		}

		@Override
		public Codec<Double> codec() {
			return Codec.either(Codec.doubleRange(0.0, 1.0), Codec.BOOL).xmap(either -> either.map(value -> value, value -> value ? 1.0 : 0.0), Either::left);
		}
	}

	@Environment(EnvType.CLIENT)
	interface IntSliderCallbacks extends SimpleOption.SliderCallbacks<Integer> {
		int minInclusive();

		int maxInclusive();

		@Override
		default Function<SimpleOption<Integer>, ClickableWidget> getButtonCreator(
			SimpleOption.TooltipFactory<Integer> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
			return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
		}

		default double toSliderProgress(Integer integer) {
			return (double)MathHelper.lerpFromProgress((float)integer.intValue(), (float)this.minInclusive(), (float)this.maxInclusive(), 0.0F, 1.0F);
		}

		default Integer toValue(double d) {
			return MathHelper.floor(MathHelper.lerpFromProgress(d, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive()));
		}

		default <R> SimpleOption.SliderCallbacks<R> withModifier(
			IntFunction<? extends R> sliderProgressValueToValue, ToIntFunction<? super R> valueToSliderProgressValue
		) {
			return new SimpleOption.SliderCallbacks<R>() {
				@Override
				public Function<SimpleOption<R>, ClickableWidget> getButtonCreator(
					SimpleOption.TooltipFactory<R> tooltipFactory, GameOptions gameOptions, int x, int y, int width
				) {
					return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory);
				}

				@Override
				public Optional<R> validate(R value) {
					return IntSliderCallbacks.this.validate(Integer.valueOf(valueToSliderProgressValue.applyAsInt(value))).map(sliderProgressValueToValue::apply);
				}

				@Override
				public double toSliderProgress(R value) {
					return IntSliderCallbacks.this.toSliderProgress(valueToSliderProgressValue.applyAsInt(value));
				}

				@Override
				public R toValue(double sliderProgress) {
					return (R)sliderProgressValueToValue.apply(IntSliderCallbacks.this.toValue(sliderProgress));
				}

				@Override
				public Codec<R> codec() {
					return IntSliderCallbacks.this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsInt);
				}
			};
		}
	}

	@Environment(EnvType.CLIENT)
	static final class OptionSliderWidgetImpl<N> extends OptionSliderWidget implements OrderableTooltip {
		private final SimpleOption<N> option;
		private final SimpleOption.SliderCallbacks<N> callbacks;
		private final SimpleOption.TooltipFactory<N> tooltipFactory;

		OptionSliderWidgetImpl(
			GameOptions options,
			int x,
			int y,
			int width,
			int height,
			SimpleOption<N> option,
			SimpleOption.SliderCallbacks<N> callbacks,
			SimpleOption.TooltipFactory<N> tooltipFactory
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
	public static record PotentialValuesBasedCallbacks<T>(List<T> values, Codec<T> codec) implements SimpleOption.Callbacks<T> {
		@Override
		public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width
		) {
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
		public Optional<T> validate(T value) {
			return this.values.contains(value) ? Optional.of(value) : Optional.empty();
		}
	}

	@Environment(EnvType.CLIENT)
	interface SliderCallbacks<T> extends SimpleOption.Callbacks<T> {
		double toSliderProgress(T value);

		T toValue(double sliderProgress);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface TooltipFactory<T> extends Function<T, List<OrderedText>> {
	}

	@Environment(EnvType.CLIENT)
	public static record ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive) implements SimpleOption.IntSliderCallbacks {
		public Optional<Integer> validate(Integer integer) {
			return integer.compareTo(this.minInclusive()) >= 0 && integer.compareTo(this.maxInclusive()) <= 0 ? Optional.of(integer) : Optional.empty();
		}

		@Override
		public Codec<Integer> codec() {
			return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface ValueSetter<T> {
		void set(SimpleOption<T> option, T value);
	}
}
