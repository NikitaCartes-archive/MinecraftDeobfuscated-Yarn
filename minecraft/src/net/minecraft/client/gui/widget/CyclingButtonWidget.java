package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CyclingButtonWidget<T> extends AbstractPressableButtonWidget implements OrderableTooltip {
	private static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
	private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
	private final Text field_27963;
	private int index;
	private T value;
	private final CyclingButtonWidget.class_5680<T> field_27966;
	private final Function<T, Text> field_27967;
	private final Function<CyclingButtonWidget<T>, MutableText> field_27968;
	private final CyclingButtonWidget.class_5678<T> field_27969;
	private final CyclingButtonWidget.class_5679<T> field_27970;
	private final boolean field_27971;

	private CyclingButtonWidget(
		int i,
		int j,
		int k,
		int l,
		Text text,
		Text text2,
		int index,
		T value,
		CyclingButtonWidget.class_5680<T> arg,
		Function<T, Text> function,
		Function<CyclingButtonWidget<T>, MutableText> function2,
		CyclingButtonWidget.class_5678<T> arg2,
		CyclingButtonWidget.class_5679<T> arg3,
		boolean bl
	) {
		super(i, j, k, l, text);
		this.field_27963 = text2;
		this.index = index;
		this.value = value;
		this.field_27966 = arg;
		this.field_27967 = function;
		this.field_27968 = function2;
		this.field_27969 = arg2;
		this.field_27970 = arg3;
		this.field_27971 = bl;
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
		List<T> list = this.field_27966.method_32626();
		this.index = MathHelper.floorMod(this.index + amount, list.size());
		T object = (T)list.get(this.index);
		this.method_32609(object);
		this.field_27969.onValueChange(this, object);
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
		List<T> list = this.field_27966.method_32626();
		int i = list.indexOf(value);
		if (i != -1) {
			this.index = i;
		}

		this.method_32609(value);
	}

	private void method_32609(T value) {
		Text text = (Text)(this.field_27971 ? (Text)this.field_27967.apply(value) : this.getGenericTextForValue(value));
		this.setMessage(text);
		this.value = value;
	}

	private MutableText getGenericTextForValue(T value) {
		return ScreenTexts.composeGenericOptionText(this.field_27963, (Text)this.field_27967.apply(value));
	}

	public T getValue() {
		return this.value;
	}

	@Override
	protected MutableText getNarrationMessage() {
		return (MutableText)this.field_27968.apply(this);
	}

	public MutableText method_32611() {
		return getNarrationMessage((Text)(this.field_27971 ? this.getGenericTextForValue(this.value) : this.getMessage()));
	}

	@Override
	public List<OrderedText> getOrderedTooltip() {
		return (List<OrderedText>)this.field_27970.apply(this.value);
	}

	public static <T> CyclingButtonWidget.Builder<T> method_32606(Function<T, Text> function) {
		return new CyclingButtonWidget.Builder<>(function);
	}

	public static CyclingButtonWidget.Builder<Boolean> method_32607(Text text, Text text2) {
		return new CyclingButtonWidget.Builder<Boolean>(value -> value ? text : text2).method_32620(BOOLEAN_VALUES);
	}

	public static CyclingButtonWidget.Builder<Boolean> method_32614() {
		return new CyclingButtonWidget.Builder<Boolean>(value -> value ? ScreenTexts.ON : ScreenTexts.OFF).method_32620(BOOLEAN_VALUES);
	}

	public static CyclingButtonWidget.Builder<Boolean> method_32613(boolean bl) {
		return method_32614().value(bl);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder<T> {
		private int field_27972;
		@Nullable
		private T value;
		private final Function<T, Text> field_27974;
		private CyclingButtonWidget.class_5679<T> field_27975 = value -> ImmutableList.of();
		private Function<CyclingButtonWidget<T>, MutableText> field_27976 = CyclingButtonWidget::method_32611;
		private CyclingButtonWidget.class_5680<T> field_27977 = CyclingButtonWidget.class_5680.method_32627(ImmutableList.of());
		private boolean field_27978;

		public Builder(Function<T, Text> function) {
			this.field_27974 = function;
		}

		public CyclingButtonWidget.Builder<T> method_32620(List<T> list) {
			this.field_27977 = CyclingButtonWidget.class_5680.method_32627(list);
			return this;
		}

		@SafeVarargs
		public final CyclingButtonWidget.Builder<T> method_32624(T... objects) {
			return this.method_32620(ImmutableList.copyOf(objects));
		}

		public CyclingButtonWidget.Builder<T> method_32621(List<T> list, List<T> list2) {
			this.field_27977 = CyclingButtonWidget.class_5680.method_32628(CyclingButtonWidget.HAS_ALT_DOWN, list, list2);
			return this;
		}

		public CyclingButtonWidget.Builder<T> method_32622(BooleanSupplier booleanSupplier, List<T> list, List<T> list2) {
			this.field_27977 = CyclingButtonWidget.class_5680.method_32628(booleanSupplier, list, list2);
			return this;
		}

		public CyclingButtonWidget.Builder<T> method_32618(CyclingButtonWidget.class_5679<T> arg) {
			this.field_27975 = arg;
			return this;
		}

		public CyclingButtonWidget.Builder<T> value(T value) {
			this.value = value;
			int i = this.field_27977.method_32629().indexOf(value);
			if (i != -1) {
				this.field_27972 = i;
			}

			return this;
		}

		public CyclingButtonWidget.Builder<T> method_32623(Function<CyclingButtonWidget<T>, MutableText> function) {
			this.field_27976 = function;
			return this;
		}

		public CyclingButtonWidget.Builder<T> method_32616() {
			this.field_27978 = true;
			return this;
		}

		public CyclingButtonWidget<T> build(int i, int j, int k, int l, Text text, CyclingButtonWidget.class_5678<T> arg) {
			List<T> list = this.field_27977.method_32629();
			if (list.isEmpty()) {
				throw new IllegalStateException("No values for cycle button");
			} else {
				T object = (T)(this.value != null ? this.value : list.get(this.field_27972));
				Text text2 = (Text)this.field_27974.apply(object);
				Text text3 = (Text)(this.field_27978 ? text2 : ScreenTexts.composeGenericOptionText(text, text2));
				return new CyclingButtonWidget<>(
					i, j, k, l, text3, text, this.field_27972, object, this.field_27977, this.field_27974, this.field_27976, arg, this.field_27975, this.field_27978
				);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_5678<T> {
		void onValueChange(CyclingButtonWidget button, T value);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface class_5679<T> extends Function<T, List<OrderedText>> {
	}

	@Environment(EnvType.CLIENT)
	interface class_5680<T> {
		List<T> method_32626();

		List<T> method_32629();

		static <T> CyclingButtonWidget.class_5680<T> method_32627(List<T> list) {
			final List<T> list2 = ImmutableList.copyOf(list);
			return new CyclingButtonWidget.class_5680<T>() {
				@Override
				public List<T> method_32626() {
					return list2;
				}

				@Override
				public List<T> method_32629() {
					return list2;
				}
			};
		}

		static <T> CyclingButtonWidget.class_5680<T> method_32628(BooleanSupplier booleanSupplier, List<T> list, List<T> list2) {
			final List<T> list3 = ImmutableList.copyOf(list);
			final List<T> list4 = ImmutableList.copyOf(list2);
			return new CyclingButtonWidget.class_5680<T>() {
				@Override
				public List<T> method_32626() {
					return booleanSupplier.getAsBoolean() ? list4 : list3;
				}

				@Override
				public List<T> method_32629() {
					return list3;
				}
			};
		}
	}
}
