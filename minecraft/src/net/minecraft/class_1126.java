package net.minecraft;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1126<T> extends class_1121<T> {
	protected class_1128<T> field_5498 = new class_1128<>();
	private final Function<T, Stream<String>> field_5497;

	public class_1126(Function<T, Stream<String>> function, Function<T, Stream<class_2960>> function2) {
		super(function2);
		this.field_5497 = function;
	}

	@Override
	public void method_4799() {
		this.field_5498 = new class_1128<>();
		super.method_4799();
		this.field_5498.method_4807();
	}

	@Override
	protected void method_4795(T object) {
		super.method_4795(object);
		((Stream)this.field_5497.apply(object)).forEach(string -> this.field_5498.method_4806(object, string.toLowerCase(Locale.ROOT)));
	}

	@Override
	public List<T> method_4810(String string) {
		int i = string.indexOf(58);
		if (i < 0) {
			return this.field_5498.method_4804(string);
		} else {
			List<T> list = this.field_5489.method_4804(string.substring(0, i).trim());
			String string2 = string.substring(i + 1).trim();
			List<T> list2 = this.field_5485.method_4804(string2);
			List<T> list3 = this.field_5498.method_4804(string2);
			return Lists.<T>newArrayList(
				new class_1121.class_1122<>(list.iterator(), new class_1126.class_1127<>(list2.iterator(), list3.iterator(), this::method_4796), this::method_4796)
			);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1127<T> extends AbstractIterator<T> {
		private final PeekingIterator<T> field_5499;
		private final PeekingIterator<T> field_5500;
		private final Comparator<T> field_5501;

		public class_1127(Iterator<T> iterator, Iterator<T> iterator2, Comparator<T> comparator) {
			this.field_5499 = Iterators.peekingIterator(iterator);
			this.field_5500 = Iterators.peekingIterator(iterator2);
			this.field_5501 = comparator;
		}

		@Override
		protected T computeNext() {
			boolean bl = !this.field_5499.hasNext();
			boolean bl2 = !this.field_5500.hasNext();
			if (bl && bl2) {
				return this.endOfData();
			} else if (bl) {
				return this.field_5500.next();
			} else if (bl2) {
				return this.field_5499.next();
			} else {
				int i = this.field_5501.compare(this.field_5499.peek(), this.field_5500.peek());
				if (i == 0) {
					this.field_5500.next();
				}

				return i <= 0 ? this.field_5499.next() : this.field_5500.next();
			}
		}
	}
}
