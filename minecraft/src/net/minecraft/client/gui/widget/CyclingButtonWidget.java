package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CyclingButtonWidget<T> extends PressableWidget implements OrderableTooltip {
	static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
	private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
	private final Text optionText;
	private int index;
	private T value;
	private final CyclingButtonWidget.Values<T> values;
	private final Function<T, Text> valueToText;
	private final Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory;
	private final CyclingButtonWidget.UpdateCallback<T> callback;
	private final SimpleOption.TooltipFactory<T> tooltipFactory;
	private final boolean optionTextOmitted;

	CyclingButtonWidget(
		int x,
		int y,
		int width,
		int height,
		Text message,
		Text optionText,
		int index,
		T value,
		CyclingButtonWidget.Values<T> values,
		Function<T, Text> valueToText,
		Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory,
		CyclingButtonWidget.UpdateCallback<T> callback,
		SimpleOption.TooltipFactory<T> tooltipFactory,
		boolean optionTextOmitted
	) {
		super(x, y, width, height, message);
		this.optionText = optionText;
		this.index = index;
		this.value = value;
		this.values = values;
		this.valueToText = valueToText;
		this.narrationMessageFactory = narrationMessageFactory;
		this.callback = callback;
		this.tooltipFactory = tooltipFactory;
		this.optionTextOmitted = optionTextOmitted;
	}

	@Override
	public void onPress() {
		if (Screen.hasShiftDown()) {
			this.cycle(-1);
		} else {
			this.cycle(1);
		}
	}

	private void cycle(int amount) {
		List<T> list = this.values.getCurrent();
		this.index = MathHelper.floorMod(this.index + amount, list.size());
		T object = (T)list.get(this.index);
		this.internalSetValue(object);
		this.callback.onValueChange(this, object);
	}

	private T getValue(int offset) {
		List<T> list = this.values.getCurrent();
		return (T)list.get(MathHelper.floorMod(this.index + offset, list.size()));
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (amount > 0.0) {
			this.cycle(-1);
		} else if (amount < 0.0) {
			this.cycle(1);
		}

		return true;
	}

	public void setValue(T value) {
		List<T> list = this.values.getCurrent();
		int i = list.indexOf(value);
		if (i != -1) {
			this.index = i;
		}

		this.internalSetValue(value);
	}

	private void internalSetValue(T value) {
		Text text = this.composeText(value);
		this.setMessage(text);
		this.value = value;
	}

	private Text composeText(T value) {
		return (Text)(this.optionTextOmitted ? (Text)this.valueToText.apply(value) : this.composeGenericOptionText(value));
	}

	private MutableText composeGenericOptionText(T value) {
		return ScreenTexts.composeGenericOptionText(this.optionText, (Text)this.valueToText.apply(value));
	}

	public T getValue() {
		return this.value;
	}

	@Override
	protected MutableText getNarrationMessage() {
		return (MutableText)this.narrationMessageFactory.apply(this);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (this.active) {
			T object = this.getValue(1);
			Text text = this.composeText(object);
			if (this.isFocused()) {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.cycle_button.usage.focused", text));
			} else {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.cycle_button.usage.hovered", text));
			}
		}
	}

	/**
	 * {@return a generic narration message for this button}
	 * 
	 * <p>If the button omits the option text in rendering, such as showing only
	 * "Value", this narration message will still read out the option like
	 * "Option: Value".
	 */
	public MutableText getGenericNarrationMessage() {
		return getNarrationMessage((Text)(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage()));
	}

	@Override
	public List<OrderedText> getOrderedTooltip() {
		return (List<OrderedText>)this.tooltipFactory.apply(this.value);
	}

	/**
	 * Creates a new builder for a cycling button widget.
	 */
	public static <T> CyclingButtonWidget.Builder<T> builder(Function<T, Text> valueToText) {
		return new CyclingButtonWidget.Builder<>(valueToText);
	}

	/**
	 * Creates a builder for a cycling button widget that only has {@linkplain Boolean#TRUE}
	 * and {@linkplain Boolean#FALSE} values. It displays
	 * {@code on} for {@code true} and {@code off} for {@code false}.
	 * Its current initial value is {@code true}.
	 */
	public static CyclingButtonWidget.Builder<Boolean> onOffBuilder(Text on, Text off) {
		return new CyclingButtonWidget.Builder<Boolean>(value -> value ? on : off).values(BOOLEAN_VALUES);
	}

	/**
	 * Creates a builder for a cycling button widget that only has {@linkplain Boolean#TRUE}
	 * and {@linkplain Boolean#FALSE} values. It displays
	 * {@link net.minecraft.client.gui.screen.ScreenTexts#ON} for {@code true} and
	 * {@link net.minecraft.client.gui.screen.ScreenTexts#OFF} for {@code false}.
	 * Its current initial value is {@code true}.
	 */
	public static CyclingButtonWidget.Builder<Boolean> onOffBuilder() {
		return new CyclingButtonWidget.Builder<Boolean>(value -> value ? ScreenTexts.ON : ScreenTexts.OFF).values(BOOLEAN_VALUES);
	}

	/**
	 * Creates a builder for a cycling button widget that only has {@linkplain Boolean#TRUE}
	 * and {@linkplain Boolean#FALSE} values. It displays
	 * {@link net.minecraft.client.gui.screen.ScreenTexts#ON} for {@code true} and
	 * {@link net.minecraft.client.gui.screen.ScreenTexts#OFF} for {@code false}.
	 * Its current initial value is set to {@code initialValue}.
	 */
	public static CyclingButtonWidget.Builder<Boolean> onOffBuilder(boolean initialValue) {
		return onOffBuilder().initially(initialValue);
	}

	/**
	 * A builder to easily create cycling button widgets.
	 * 
	 * Each builder must have at least one of its {@code values} methods called
	 * with at least one default (non-alternative) value in the list before
	 * building.
	 * 
	 * @see CyclingButtonWidget#builder(Function)
	 */
	@Environment(EnvType.CLIENT)
	public static class Builder<T> {
		private int initialIndex;
		@Nullable
		private T value;
		private final Function<T, Text> valueToText;
		private SimpleOption.TooltipFactory<T> tooltipFactory = value -> ImmutableList.of();
		private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
		private CyclingButtonWidget.Values<T> values = CyclingButtonWidget.Values.of(ImmutableList.<T>of());
		private boolean optionTextOmitted;

		/**
		 * Creates a builder.
		 * 
		 * @see CyclingButtonWidget#builder(Function)
		 */
		public Builder(Function<T, Text> valueToText) {
			this.valueToText = valueToText;
		}

		/**
		 * Sets the option values for this button.
		 */
		public CyclingButtonWidget.Builder<T> values(Collection<T> values) {
			return this.values(CyclingButtonWidget.Values.of(values));
		}

		/**
		 * Sets the option values for this button.
		 */
		@SafeVarargs
		public final CyclingButtonWidget.Builder<T> values(T... values) {
			return this.values(ImmutableList.<T>copyOf(values));
		}

		/**
		 * Sets the option values for this button.
		 * 
		 * <p>When the user presses the ALT key, the {@code alternatives} values
		 * will be iterated; otherwise the {@code defaults} values will be iterated
		 * when clicking the built button.
		 */
		public CyclingButtonWidget.Builder<T> values(List<T> defaults, List<T> alternatives) {
			return this.values(CyclingButtonWidget.Values.of(CyclingButtonWidget.HAS_ALT_DOWN, defaults, alternatives));
		}

		/**
		 * Sets the option values for this button.
		 * 
		 * <p>When {@code alternativeToggle} {@linkplain BooleanSupplier#getAsBoolean()
		 * getAsBoolean} returns {@code true}, the {@code alternatives} values
		 * will be iterated; otherwise the {@code defaults} values will be iterated
		 * when clicking the built button.
		 */
		public CyclingButtonWidget.Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
			return this.values(CyclingButtonWidget.Values.of(alternativeToggle, defaults, alternatives));
		}

		public CyclingButtonWidget.Builder<T> values(CyclingButtonWidget.Values<T> values) {
			this.values = values;
			return this;
		}

		/**
		 * Sets the tooltip factory that provides tooltips for any of the values.
		 * 
		 * <p>If this is not called, the values simply won't have tooltips.
		 */
		public CyclingButtonWidget.Builder<T> tooltip(SimpleOption.TooltipFactory<T> tooltipFactory) {
			this.tooltipFactory = tooltipFactory;
			return this;
		}

		/**
		 * Sets the initial value of this button widget.
		 * 
		 * <p>This is not effective if {@code value} is not in the default
		 * values (i.e. excluding alternative values).
		 * 
		 * <p>If this is not called, the initial value defaults to the first
		 * value in the values list supplied.
		 */
		public CyclingButtonWidget.Builder<T> initially(T value) {
			this.value = value;
			int i = this.values.getDefaults().indexOf(value);
			if (i != -1) {
				this.initialIndex = i;
			}

			return this;
		}

		/**
		 * Overrides the narration message of the button to build.
		 * 
		 * <p>If this is not called, the button will use
		 * {@link CyclingButtonWidget#getGenericNarrationMessage()} for narration
		 * messages.
		 */
		public CyclingButtonWidget.Builder<T> narration(Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
			this.narrationMessageFactory = narrationMessageFactory;
			return this;
		}

		/**
		 * Makes the built button omit the option and only display the current value
		 * for its text, such as showing "Jump Mode" than "Mode: Jump Mode".
		 */
		public CyclingButtonWidget.Builder<T> omitKeyText() {
			this.optionTextOmitted = true;
			return this;
		}

		public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText) {
			return this.build(x, y, width, height, optionText, (button, value) -> {
			});
		}

		/**
		 * Builds a cycling button widget.
		 * 
		 * @throws IllegalStateException if no {@code values} call is made, or the
		 * {@code values} has no default values available
		 */
		public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText, CyclingButtonWidget.UpdateCallback<T> callback) {
			List<T> list = this.values.getDefaults();
			if (list.isEmpty()) {
				throw new IllegalStateException("No values for cycle button");
			} else {
				T object = (T)(this.value != null ? this.value : list.get(this.initialIndex));
				Text text = (Text)this.valueToText.apply(object);
				Text text2 = (Text)(this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text));
				return new CyclingButtonWidget<>(
					x,
					y,
					width,
					height,
					text2,
					optionText,
					this.initialIndex,
					object,
					this.values,
					this.valueToText,
					this.narrationMessageFactory,
					callback,
					this.tooltipFactory,
					this.optionTextOmitted
				);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public interface UpdateCallback<T> {
		void onValueChange(CyclingButtonWidget<T> button, T value);
	}

	@Environment(EnvType.CLIENT)
	public interface Values<T> {
		List<T> getCurrent();

		List<T> getDefaults();

		static <T> CyclingButtonWidget.Values<T> of(Collection<T> values) {
			final List<T> list = ImmutableList.copyOf(values);
			return new CyclingButtonWidget.Values<T>() {
				@Override
				public List<T> getCurrent() {
					return list;
				}

				@Override
				public List<T> getDefaults() {
					return list;
				}
			};
		}

		static <T> CyclingButtonWidget.Values<T> of(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
			final List<T> list = ImmutableList.copyOf(defaults);
			final List<T> list2 = ImmutableList.copyOf(alternatives);
			return new CyclingButtonWidget.Values<T>() {
				@Override
				public List<T> getCurrent() {
					return alternativeToggle.getAsBoolean() ? list2 : list;
				}

				@Override
				public List<T> getDefaults() {
					return list;
				}
			};
		}
	}
}
