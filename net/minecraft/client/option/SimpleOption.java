/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
@Environment(value=EnvType.CLIENT)
public final class SimpleOption<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    /**
     * A set of callbacks for boolean options.
     * 
     * @apiNote See also several {@code ofBoolean} methods in this class which provide easier ways
     * of creating a boolean option.
     */
    public static final PotentialValuesBasedCallbacks<Boolean> BOOLEAN = new PotentialValuesBasedCallbacks<Boolean>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
    private static final int TOOLTIP_WIDTH = 200;
    private final TooltipFactoryGetter<T> tooltipFactoryGetter;
    final Function<T, Text> textGetter;
    private final Callbacks<T> callbacks;
    private final Codec<T> codec;
    private final T defaultValue;
    private final Consumer<T> changeCallback;
    final Text text;
    T value;

    /**
     * Creates a boolean option.
     */
    public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return SimpleOption.ofBoolean(key, SimpleOption.emptyTooltip(), defaultValue, changeCallback);
    }

    /**
     * Creates a boolean option.
     */
    public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue) {
        return SimpleOption.ofBoolean(key, SimpleOption.emptyTooltip(), defaultValue, value -> {});
    }

    /**
     * Creates a boolean option.
     */
    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactoryGetter<Boolean> tooltipFactoryGetter, boolean defaultValue) {
        return SimpleOption.ofBoolean(key, tooltipFactoryGetter, defaultValue, value -> {});
    }

    /**
     * Creates a boolean option.
     */
    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactoryGetter<Boolean> tooltipFactoryGetter, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return new SimpleOption<Boolean>(key, tooltipFactoryGetter, (optionText, value) -> value != false ? ScreenTexts.ON : ScreenTexts.OFF, BOOLEAN, defaultValue, changeCallback);
    }

    public SimpleOption(String key, TooltipFactoryGetter<T> tooltipFactoryGetter, ValueTextGetter<T> valueTextGetter, Callbacks<T> callbacks, T defaultValue, Consumer<T> changeCallback) {
        this(key, tooltipFactoryGetter, valueTextGetter, callbacks, callbacks.codec(), defaultValue, changeCallback);
    }

    public SimpleOption(String key, TooltipFactoryGetter<T> tooltipFactoryGetter, ValueTextGetter<T> valueTextGetter, Callbacks<T> callbacks, Codec<T> codec, T defaultValue, Consumer<T> changeCallback) {
        this.text = new TranslatableText(key);
        this.tooltipFactoryGetter = tooltipFactoryGetter;
        this.textGetter = value -> valueTextGetter.toString(this.text, value);
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
    public static <T> TooltipFactoryGetter<T> emptyTooltip() {
        return client -> value -> ImmutableList.of();
    }

    /**
     * {@return the getter for the {@code tooltipFactoryGetter} parameter of the constructor
     * to indicate constant tooltips}
     */
    public static <T> TooltipFactoryGetter<T> constantTooltip(Text text) {
        return client -> {
            List<OrderedText> list = SimpleOption.wrapLines(client, text);
            return value -> list;
        };
    }

    /**
     * {@return the getter for the {@code valueTextGetter} parameter of the constructor
     * to use the value's text as the option's rendered text}
     * 
     * @apiNote This requires the value to be an enum implementing {@link TranslatableOption}.
     */
    public static <T extends TranslatableOption> ValueTextGetter<T> enumValueText() {
        return (optionText, value) -> value.getText();
    }

    protected static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
        return client.textRenderer.wrapLines(text, 200);
    }

    public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
        TooltipFactory tooltipFactory = (TooltipFactory)this.tooltipFactoryGetter.apply(MinecraftClient.getInstance());
        return this.callbacks.getButtonCreator(tooltipFactory, options, x, y, width).apply(this);
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
        Object object = this.callbacks.validate(value).orElseGet(() -> {
            LOGGER.error("Illegal option value " + value + " for " + this.text);
            return this.defaultValue;
        });
        if (!MinecraftClient.getInstance().isRunning()) {
            this.value = object;
            return;
        }
        if (!Objects.equals(this.value, object)) {
            this.value = object;
            this.changeCallback.accept(this.value);
        }
    }

    public Callbacks<T> getCallbacks() {
        return this.callbacks;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface TooltipFactoryGetter<T>
    extends Function<MinecraftClient, TooltipFactory<T>> {
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ValueTextGetter<T> {
        public Text toString(Text var1, T var2);
    }

    @Environment(value=EnvType.CLIENT)
    public record PotentialValuesBasedCallbacks<T>(List<T> values, Codec<T> codec) implements CyclingCallbacks<T>
    {
        @Override
        public Optional<T> validate(T value) {
            return this.values.contains(value) ? Optional.of(value) : Optional.empty();
        }

        @Override
        public CyclingButtonWidget.Values<T> getValues() {
            return CyclingButtonWidget.Values.of(this.values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface Callbacks<T> {
        public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(TooltipFactory<T> var1, GameOptions var2, int var3, int var4, int var5);

        public Optional<T> validate(T var1);

        public Codec<T> codec();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface TooltipFactory<T>
    extends Function<T, List<OrderedText>> {
    }

    @Environment(value=EnvType.CLIENT)
    public static enum DoubleSliderCallbacks implements SliderCallbacks<Double>
    {
        INSTANCE;


        @Override
        public Optional<Double> validate(Double double_) {
            return double_ >= 0.0 && double_ <= 1.0 ? Optional.of(double_) : Optional.empty();
        }

        @Override
        public double toSliderProgress(Double double_) {
            return double_;
        }

        @Override
        public Double toValue(double d) {
            return d;
        }

        public <R> SliderCallbacks<R> withModifier(final DoubleFunction<? extends R> sliderProgressValueToValue, final ToDoubleFunction<? super R> valueToSliderProgressValue) {
            return new SliderCallbacks<R>(){

                @Override
                public Optional<R> validate(R value) {
                    return this.validate(valueToSliderProgressValue.applyAsDouble(value)).map(sliderProgressValueToValue::apply);
                }

                @Override
                public double toSliderProgress(R value) {
                    return this.toSliderProgress(valueToSliderProgressValue.applyAsDouble(value));
                }

                @Override
                public R toValue(double sliderProgress) {
                    return sliderProgressValueToValue.apply(this.toValue(sliderProgress));
                }

                @Override
                public Codec<R> codec() {
                    return this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsDouble);
                }
            };
        }

        @Override
        public Codec<Double> codec() {
            return Codec.either(Codec.doubleRange(0.0, 1.0), Codec.BOOL).xmap(either -> either.map(value -> value, value -> value != false ? 1.0 : 0.0), Either::left);
        }

        @Override
        public /* synthetic */ Object toValue(double sliderProgress) {
            return this.toValue(sliderProgress);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record MaxSuppliableIntCallbacks(int minInclusive, IntSupplier maxSupplier) implements IntSliderCallbacks,
    TypeChangeableCallbacks<Integer>
    {
        @Override
        public Optional<Integer> validate(Integer integer) {
            return Optional.of(MathHelper.clamp(integer, this.minInclusive(), this.maxInclusive()));
        }

        @Override
        public int maxInclusive() {
            return this.maxSupplier.getAsInt();
        }

        @Override
        public Codec<Integer> codec() {
            Function<Integer, DataResult> function = value -> {
                int i = this.maxSupplier.getAsInt() + 1;
                if (value.compareTo(this.minInclusive) >= 0 && value.compareTo(i) <= 0) {
                    return DataResult.success(value);
                }
                return DataResult.error("Value " + value + " outside of range [" + this.minInclusive + ":" + i + "]", value);
            };
            return Codec.INT.flatXmap(function, function);
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

    @Environment(value=EnvType.CLIENT)
    public record ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive) implements IntSliderCallbacks
    {
        @Override
        public Optional<Integer> validate(Integer integer) {
            return integer.compareTo(this.minInclusive()) >= 0 && integer.compareTo(this.maxInclusive()) <= 0 ? Optional.of(integer) : Optional.empty();
        }

        @Override
        public Codec<Integer> codec() {
            return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface IntSliderCallbacks
    extends SliderCallbacks<Integer> {
        public int minInclusive();

        public int maxInclusive();

        @Override
        default public double toSliderProgress(Integer integer) {
            return MathHelper.lerpFromProgress(integer.intValue(), this.minInclusive(), this.maxInclusive(), 0.0f, 1.0f);
        }

        @Override
        default public Integer toValue(double d) {
            return MathHelper.floor(MathHelper.lerpFromProgress(d, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive()));
        }

        default public <R> SliderCallbacks<R> withModifier(final IntFunction<? extends R> sliderProgressValueToValue, final ToIntFunction<? super R> valueToSliderProgressValue) {
            return new SliderCallbacks<R>(){

                @Override
                public Optional<R> validate(R value) {
                    return this.validate(valueToSliderProgressValue.applyAsInt(value)).map(sliderProgressValueToValue::apply);
                }

                @Override
                public double toSliderProgress(R value) {
                    return this.toSliderProgress(valueToSliderProgressValue.applyAsInt(value));
                }

                @Override
                public R toValue(double sliderProgress) {
                    return sliderProgressValueToValue.apply(this.toValue(sliderProgress));
                }

                @Override
                public Codec<R> codec() {
                    return this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsInt);
                }
            };
        }

        @Override
        default public /* synthetic */ Object toValue(double sliderProgress) {
            return this.toValue(sliderProgress);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class OptionSliderWidgetImpl<N>
    extends OptionSliderWidget
    implements OrderableTooltip {
        private final SimpleOption<N> option;
        private final SliderCallbacks<N> callbacks;
        private final TooltipFactory<N> tooltipFactory;

        OptionSliderWidgetImpl(GameOptions options, int x, int y, int width, int height, SimpleOption<N> option, SliderCallbacks<N> callbacks, TooltipFactory<N> tooltipFactory) {
            super(options, x, y, width, height, callbacks.toSliderProgress(option.getValue()));
            this.option = option;
            this.callbacks = callbacks;
            this.tooltipFactory = tooltipFactory;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(this.option.textGetter.apply(this.option.getValue()));
        }

        @Override
        protected void applyValue() {
            this.option.setValue(this.callbacks.toValue(this.value));
            this.options.write();
        }

        @Override
        public List<OrderedText> getOrderedTooltip() {
            return (List)this.tooltipFactory.apply(this.callbacks.toValue(this.value));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record LazyCyclingCallbacks<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements CyclingCallbacks<T>
    {
        @Override
        public Optional<T> validate(T value) {
            return this.validateValue.apply(value);
        }

        @Override
        public CyclingButtonWidget.Values<T> getValues() {
            return CyclingButtonWidget.Values.of((Collection)this.values.get());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record AlternateValuesSupportingCyclingCallbacks<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, CyclingCallbacks.ValueSetter<T> valueSetter, Codec<T> codec) implements CyclingCallbacks<T>
    {
        @Override
        public CyclingButtonWidget.Values<T> getValues() {
            return CyclingButtonWidget.Values.of(this.altCondition, this.values, this.altValues);
        }

        @Override
        public Optional<T> validate(T value) {
            return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface TypeChangeableCallbacks<T>
    extends CyclingCallbacks<T>,
    SliderCallbacks<T> {
        public boolean isCycling();

        @Override
        default public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width) {
            if (this.isCycling()) {
                return CyclingCallbacks.super.getButtonCreator(tooltipFactory, gameOptions, x, y, width);
            }
            return SliderCallbacks.super.getButtonCreator(tooltipFactory, gameOptions, x, y, width);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface CyclingCallbacks<T>
    extends Callbacks<T> {
        public CyclingButtonWidget.Values<T> getValues();

        default public ValueSetter<T> valueSetter() {
            return SimpleOption::setValue;
        }

        @Override
        default public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width) {
            return option -> CyclingButtonWidget.builder(option.textGetter).values(this.getValues()).tooltip(tooltipFactory).initially(option.value).build(x, y, width, 20, option.text, (button, value) -> {
                this.valueSetter().set((SimpleOption<Object>)option, value);
                gameOptions.write();
            });
        }

        @Environment(value=EnvType.CLIENT)
        public static interface ValueSetter<T> {
            public void set(SimpleOption<T> var1, T var2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface SliderCallbacks<T>
    extends Callbacks<T> {
        public double toSliderProgress(T var1);

        public T toValue(double var1);

        @Override
        default public Function<SimpleOption<T>, ClickableWidget> getButtonCreator(TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width) {
            return option -> new OptionSliderWidgetImpl(gameOptions, x, y, width, 20, option, this, tooltipFactory);
        }
    }
}

