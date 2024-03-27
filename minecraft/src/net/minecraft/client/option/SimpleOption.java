package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
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
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

/**
 * A class representing an option of a client. Exactly one instance of this class
 * should be created per option. See below for how to create an instance.
 * 
 * <h2 id="option-values">Option values</h2>
 * 
 * <p>This class allows querying and storing of the option value via {@link #getValue()}
 * and {@link #setValue(Object)} respectively. Option values are automatically validated,
 * and if the value is invalid, the value resets back to the default. Some validators will
 * coerce the invalid value (e.g. by clamping) into a valid one instead of failing; in this
 * case the new value is used.
 * 
 * <p>Option values are serialized using codecs. Check DataFixerUpper code for the list of
 * available codecs. For serialization of enums, you can check the code in {@link GameOptions}.
 * 
 * <p>If the value has changed, the {@code changeCallback} passed to the constructor will
 * trigger with the new value.
 * 
 * <h2 id="callbacks">Callbacks</h2>
 * <p>This class itself cannot be extended by default; instead, the behavior of the option,
 * such as validation or rendering of the {@link ClickableWidget} associated with the
 * option, is customized by passing a {@link SimpleOption.Callbacks} instance. There are
 * several existing callbacks that should cover most of the needs:
 * 
 * <ul>
 * <li>{@link SimpleOption.PotentialValuesBasedCallbacks}: The most simple cycling option.
 * Useful for enums.</li>
 * <li>{@link SimpleOption.AlternateValuesSupportingCyclingCallbacks}: An option with
 * alternate potential values that are used only when certain conditions are met. This is
 * used in vanilla to hide "Fabulous!" graphics option after the user cancelled the
 * warning.</li>
 * <li>{@link SimpleOption.LazyCyclingCallbacks}: An option whose potential values are
 * determined lazily.</li>
 * <li>{@link SimpleOption.MaxSuppliableIntCallbacks}: A cycling option with an integer value
 * that has a fixed minimum value and a dynamic maximum value. Values outside the range are
 * clamped. This is used in vanilla to implement the GUI Scale option.</li>
 * <li>{@link SimpleOption.DoubleSliderCallbacks}: A slider option of a {@code double}
 * between {@code 0.0} and {@code 1.0}. Values outside this range are considered invalid.</li>
 * <li>{@link SimpleOption.ValidatingIntSliderCallbacks}: A slider option of an {@code int}
 * with a minimum and maximum values. Values outside this range are considered invalid.</li>
 * </ul>
 * 
 * <p>See also several {@code ofBoolean} methods for options using boolean values.
 * 
 * <h3 id="callbacks-modifiers">Modifiers</h3>
 * <p>Slider option callbacks allow "modifiers" to be applied. The "slider progress value"
 * is the value used to calculate the slider's progress and must be linear. The real value,
 * simply called "value" in {@code withModifier} methods, is the value used in
 * {@link #getValue()} and passed to {@link #textGetter}.
 * 
 * <h2 id="text">Text</h2>
 * Options themselves have names; the translation key of the option needs to be passed when
 * constructing this class. This corresponds to {@link #text}. Options also have the
 * rendered text, composed of the option name and the value; the code obtains the value using
 * the {@code valueTextGetter} in the constructor. The getter takes the option's name and the
 * current value, and returns the text. Several static methods in {@link GameOptions} can
 * be used to format the name and the value. For options backed by an enum that implements
 * {@link TranslatableOption}, {@link #enumValueText()} can be passed to {@code valueTextGetter}
 * to obtain the rendered text from the enum.
 * 
 * Options can also have a tooltip, specified by passing {@code tooltipFactoryGetter}. If the
 * option has no tooltips, you can pass {@link #emptyTooltip()}, and if the option always
 * uses one tooltip, you can pass {@link #constantTooltip(Text)}.
 */
@Environment(EnvType.CLIENT)
public final class SimpleOption<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	/**
	 * A set of callbacks for boolean options.
	 * 
	 * @apiNote See also several {@code ofBoolean} methods in this class which provide easier ways
	 * of creating a boolean option.
	 */
	public static final SimpleOption.PotentialValuesBasedCallbacks<Boolean> BOOLEAN = new SimpleOption.PotentialValuesBasedCallbacks<>(
		ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL
	);
	public static final SimpleOption.ValueTextGetter<Boolean> BOOLEAN_TEXT_GETTER = (optionText, value) -> value ? ScreenTexts.ON : ScreenTexts.OFF;
	private final SimpleOption.TooltipFactory<T> tooltipFactory;
	final Function<T, Text> textGetter;
	private final SimpleOption.Callbacks<T> callbacks;
	private final Codec<T> codec;
	private final T defaultValue;
	private final Consumer<T> changeCallback;
	final Text text;
	T value;

	/**
	 * Creates a boolean option.
	 */
	public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue, Consumer<Boolean> changeCallback) {
		return ofBoolean(key, emptyTooltip(), defaultValue, changeCallback);
	}

	/**
	 * Creates a boolean option.
	 */
	public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue) {
		return ofBoolean(key, emptyTooltip(), defaultValue, value -> {
		});
	}

	/**
	 * Creates a boolean option.
	 */
	public static SimpleOption<Boolean> ofBoolean(String key, SimpleOption.TooltipFactory<Boolean> tooltipFactory, boolean defaultValue) {
		return ofBoolean(key, tooltipFactory, defaultValue, value -> {
		});
	}

	/**
	 * Creates a boolean option.
	 */
	public static SimpleOption<Boolean> ofBoolean(
		String key, SimpleOption.TooltipFactory<Boolean> tooltipFactory, boolean defaultValue, Consumer<Boolean> changeCallback
	) {
		return ofBoolean(key, tooltipFactory, BOOLEAN_TEXT_GETTER, defaultValue, changeCallback);
	}

	public static SimpleOption<Boolean> ofBoolean(
		String key,
		SimpleOption.TooltipFactory<Boolean> tooltipFactory,
		SimpleOption.ValueTextGetter<Boolean> valueTextGetter,
		boolean defaultValue,
		Consumer<Boolean> changeCallback
	) {
		return new SimpleOption<>(key, tooltipFactory, valueTextGetter, BOOLEAN, defaultValue, changeCallback);
	}

	public SimpleOption(
		String key,
		SimpleOption.TooltipFactory<T> tooltipFactory,
		SimpleOption.ValueTextGetter<T> valueTextGetter,
		SimpleOption.Callbacks<T> callbacks,
		T defaultValue,
		Consumer<T> changeCallback
	) {
		this(key, tooltipFactory, valueTextGetter, callbacks, callbacks.codec(), defaultValue, changeCallback);
	}

	public SimpleOption(
		String key,
		SimpleOption.TooltipFactory<T> tooltipFactory,
		SimpleOption.ValueTextGetter<T> valueTextGetter,
		SimpleOption.Callbacks<T> callbacks,
		Codec<T> codec,
		T defaultValue,
		Consumer<T> changeCallback
	) {
		this.text = Text.translatable(key);
		this.tooltipFactory = tooltipFactory;
		this.textGetter = value -> valueTextGetter.toString(this.text, (T)value);
		this.callbacks = callbacks;
		this.codec = codec;
		this.defaultValue = defaultValue;
		this.changeCallback = changeCallback;
		this.value = this.defaultValue;
	}

	/**
	 * {@return the getter for the {@code tooltipFactoryGetter} parameter of the constructor
	 * to indicate empty tooltips}
	 */
	public static <T> SimpleOption.TooltipFactory<T> emptyTooltip() {
		return value -> null;
	}

	/**
	 * {@return the getter for the {@code tooltipFactoryGetter} parameter of the constructor
	 * to indicate constant tooltips}
	 */
	public static <T> SimpleOption.TooltipFactory<T> constantTooltip(Text text) {
		return value -> Tooltip.of(text);
	}

	/**
	 * {@return the getter for the {@code valueTextGetter} parameter of the constructor
	 * to use the value's text as the option's rendered text}
	 * 
	 * @apiNote This requires the value to be an enum implementing {@link TranslatableOption}.
	 */
	public static <T extends TranslatableOption> SimpleOption.ValueTextGetter<T> enumValueText() {
		return (optionText, value) -> value.getText();
	}

	public ClickableWidget createWidget(GameOptions options) {
		return this.createWidget(options, 0, 0, 150);
	}

	public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
		return this.createWidget(options, x, y, width, value -> {
		});
	}

	public ClickableWidget createWidget(GameOptions options, int x, int y, int width, Consumer<T> changeCallback) {
		return (ClickableWidget)this.callbacks.getWidgetCreator(this.tooltipFactory, options, x, y, width, changeCallback).apply(this);
	}

	/**
	 * {@return the option's current value}
	 */
	public T getValue() {
		return this.value;
	}

	public Codec<T> getCodec() {
		return this.codec;
	}

	public String toString() {
		return this.text.getString();
	}

	/**
	 * Sets the option's value.
	 * 
	 * <p>The behavior for invalid {@code value} varies; the validator can either coerce
	 * the passed value into a valid one by clamping etc, or fail the validation and reset to
	 * the default value.
	 * 
	 * <p>If the new value differs from the new value, {@link #changeCallback} will trigger.
	 */
	public void setValue(T value) {
		T object = (T)this.callbacks.validate(value).orElseGet(() -> {
			LOGGER.error("Illegal option value " + value + " for " + this.text);
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

	/**
	 * A set of callbacks for a cycling option with alternate potential values that are used
	 * only when certain conditions are met.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	public static record AlternateValuesSupportingCyclingCallbacks<T>(
		List<T> values, List<T> altValues, BooleanSupplier altCondition, SimpleOption.CyclingCallbacks.ValueSetter<T> valueSetter, Codec<T> codec
	) implements SimpleOption.CyclingCallbacks<T> {
		@Override
		public CyclingButtonWidget.Values<T> getValues() {
			return CyclingButtonWidget.Values.of(this.altCondition, this.values, this.altValues);
		}

		@Override
		public Optional<T> validate(T value) {
			return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
		}
	}

	/**
	 * A set of callbacks to customize an option's behavior.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	interface Callbacks<T> {
		/**
		 * {@return the widget creator}
		 * 
		 * <p>Widget creators are responsible for rendering the option into
		 * a {@link ClickableWidget}.
		 */
		Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback
		);

		/**
		 * {@return the validated value}
		 * 
		 * <p>Returning {@link Optional#empty()} indicates the passed value is invalid and it
		 * should reset to the default value. This method can also coerce the invalid value
		 * into a valid one by clamping, etc.
		 */
		Optional<T> validate(T value);

		Codec<T> codec();
	}

	@Environment(EnvType.CLIENT)
	interface CyclingCallbacks<T> extends SimpleOption.Callbacks<T> {
		CyclingButtonWidget.Values<T> getValues();

		default SimpleOption.CyclingCallbacks.ValueSetter<T> valueSetter() {
			return SimpleOption::setValue;
		}

		@Override
		default Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback
		) {
			return option -> CyclingButtonWidget.<T>builder(option.textGetter)
					.values(this.getValues())
					.tooltip(tooltipFactory)
					.initially(option.value)
					.build(x, y, width, 20, option.text, (button, value) -> {
						this.valueSetter().set(option, value);
						gameOptions.write();
						changeCallback.accept(value);
					});
		}

		@Environment(EnvType.CLIENT)
		public interface ValueSetter<T> {
			void set(SimpleOption<T> option, T value);
		}
	}

	/**
	 * A set of callbacks for a slider of values from {@code 0.0} to {@code 1.0} (both
	 * inclusive). There is only one instance of this callbacks.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	public static enum DoubleSliderCallbacks implements SimpleOption.SliderCallbacks<Double> {
		INSTANCE;

		public Optional<Double> validate(Double double_) {
			return double_ >= 0.0 && double_ <= 1.0 ? Optional.of(double_) : Optional.empty();
		}

		public double toSliderProgress(Double double_) {
			return double_;
		}

		public Double toValue(double d) {
			return d;
		}

		/**
		 * Creates a slider callback with the modifier applied.
		 * 
		 * @see <a href="SimpleOption.html#callbacks-modifiers">Callback Modifiers</a>
		 */
		public <R> SimpleOption.SliderCallbacks<R> withModifier(
			DoubleFunction<? extends R> sliderProgressValueToValue, ToDoubleFunction<? super R> valueToSliderProgressValue
		) {
			return new SimpleOption.SliderCallbacks<R>() {
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
			return Codec.withAlternative(Codec.doubleRange(0.0, 1.0), Codec.BOOL, value -> value ? 1.0 : 0.0);
		}
	}

	@Environment(EnvType.CLIENT)
	interface IntSliderCallbacks extends SimpleOption.SliderCallbacks<Integer> {
		int minInclusive();

		int maxInclusive();

		default double toSliderProgress(Integer integer) {
			return (double)MathHelper.map((float)integer.intValue(), (float)this.minInclusive(), (float)this.maxInclusive(), 0.0F, 1.0F);
		}

		default Integer toValue(double d) {
			return MathHelper.floor(MathHelper.map(d, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive()));
		}

		/**
		 * Creates a slider callback with the modifier applied. Note that when using this,
		 * {@link SimpleOption.IntSliderCallbacks} must be constructed using the slider progress
		 * value; the modifier is applied to that callbacks.
		 * 
		 * @see <a href="SimpleOption.html#callbacks-modifiers">Callback Modifiers</a>
		 */
		default <R> SimpleOption.SliderCallbacks<R> withModifier(
			IntFunction<? extends R> sliderProgressValueToValue, ToIntFunction<? super R> valueToSliderProgressValue
		) {
			return new SimpleOption.SliderCallbacks<R>() {
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

	/**
	 * A set of callbacks for a cycling option whose potential values are determined lazily.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	public static record LazyCyclingCallbacks<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec)
		implements SimpleOption.CyclingCallbacks<T> {
		@Override
		public Optional<T> validate(T value) {
			return (Optional<T>)this.validateValue.apply(value);
		}

		@Override
		public CyclingButtonWidget.Values<T> getValues() {
			return CyclingButtonWidget.Values.of((Collection<T>)this.values.get());
		}
	}

	/**
	 * A set of callbacks for a cycling option with a fixed minimum value and a dynamic
	 * maximum value. This clamps the value during validation.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	public static record MaxSuppliableIntCallbacks(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive)
		implements SimpleOption.IntSliderCallbacks,
		SimpleOption.TypeChangeableCallbacks<Integer> {
		public Optional<Integer> validate(Integer integer) {
			return Optional.of(MathHelper.clamp(integer, this.minInclusive(), this.maxInclusive()));
		}

		@Override
		public int maxInclusive() {
			return this.maxSupplier.getAsInt();
		}

		@Override
		public Codec<Integer> codec() {
			return Codec.INT
				.validate(
					value -> {
						int i = this.encodableMaxInclusive + 1;
						return value.compareTo(this.minInclusive) >= 0 && value.compareTo(i) <= 0
							? DataResult.success(value)
							: DataResult.error(() -> "Value " + value + " outside of range [" + this.minInclusive + ":" + i + "]", value);
					}
				);
		}

		@Override
		public boolean isCycling() {
			return true;
		}

		@Override
		public CyclingButtonWidget.Values<Integer> getValues() {
			return CyclingButtonWidget.Values.of(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
		}
	}

	@Environment(EnvType.CLIENT)
	static final class OptionSliderWidgetImpl<N> extends OptionSliderWidget {
		private final SimpleOption<N> option;
		private final SimpleOption.SliderCallbacks<N> callbacks;
		private final SimpleOption.TooltipFactory<N> tooltipFactory;
		private final Consumer<N> changeCallback;

		OptionSliderWidgetImpl(
			GameOptions options,
			int x,
			int y,
			int width,
			int height,
			SimpleOption<N> option,
			SimpleOption.SliderCallbacks<N> callbacks,
			SimpleOption.TooltipFactory<N> tooltipFactory,
			Consumer<N> changeCallback
		) {
			super(options, x, y, width, height, callbacks.toSliderProgress(option.getValue()));
			this.option = option;
			this.callbacks = callbacks;
			this.tooltipFactory = tooltipFactory;
			this.changeCallback = changeCallback;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage((Text)this.option.textGetter.apply(this.option.getValue()));
			this.setTooltip(this.tooltipFactory.apply(this.callbacks.toValue(this.value)));
		}

		@Override
		protected void applyValue() {
			this.option.setValue(this.callbacks.toValue(this.value));
			this.options.write();
			this.changeCallback.accept(this.option.getValue());
		}
	}

	/**
	 * A set of callbacks for a cycling option.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	public static record PotentialValuesBasedCallbacks<T>(List<T> values, Codec<T> codec) implements SimpleOption.CyclingCallbacks<T> {
		@Override
		public Optional<T> validate(T value) {
			return this.values.contains(value) ? Optional.of(value) : Optional.empty();
		}

		@Override
		public CyclingButtonWidget.Values<T> getValues() {
			return CyclingButtonWidget.Values.of(this.values);
		}
	}

	/**
	 * A set of callbacks for slider options.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
	@Environment(EnvType.CLIENT)
	interface SliderCallbacks<T> extends SimpleOption.Callbacks<T> {
		/**
		 * {@return the progress ({@code 0.0} to {@code 1.0}, both inclusive) of the slider}
		 */
		double toSliderProgress(T value);

		/**
		 * {@return the value from the progress ({@code 0.0} to {@code 1.0}, both inclusive) of the slider}
		 */
		T toValue(double sliderProgress);

		@Override
		default Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback
		) {
			return option -> new SimpleOption.OptionSliderWidgetImpl<>(gameOptions, x, y, width, 20, option, this, tooltipFactory, changeCallback);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface TooltipFactory<T> {
		@Nullable
		Tooltip apply(T value);
	}

	@Environment(EnvType.CLIENT)
	interface TypeChangeableCallbacks<T> extends SimpleOption.CyclingCallbacks<T>, SimpleOption.SliderCallbacks<T> {
		boolean isCycling();

		@Override
		default Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(
			SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback
		) {
			return this.isCycling()
				? SimpleOption.CyclingCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback)
				: SimpleOption.SliderCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback);
		}
	}

	/**
	 * A set of callbacks for a slider of integer values with a fixed minimum and maximum values.
	 * 
	 * @see <a href="SimpleOption.html#callbacks">Callbacks</a>
	 */
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
	public interface ValueTextGetter<T> {
		Text toString(Text optionText, T value);
	}
}
