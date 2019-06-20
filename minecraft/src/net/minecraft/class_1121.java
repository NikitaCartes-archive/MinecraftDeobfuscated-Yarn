package net.minecraft;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1121<T> implements class_1123<T> {
	protected class_1128<T> field_5489 = new class_1128<>();
	protected class_1128<T> field_5485 = new class_1128<>();
	private final Function<T, Stream<class_2960>> field_5487;
	private final List<T> field_5486 = Lists.<T>newArrayList();
	private final Object2IntMap<T> field_5488 = new Object2IntOpenHashMap<>();

	public class_1121(Function<T, Stream<class_2960>> function) {
		this.field_5487 = function;
	}

	@Override
	public void method_4799() {
		this.field_5489 = new class_1128<>();
		this.field_5485 = new class_1128<>();

		for (T object : this.field_5486) {
			this.method_4795(object);
		}

		this.field_5489.method_4807();
		this.field_5485.method_4807();
	}

	@Override
	public void method_4798(T object) {
		this.field_5488.put(object, this.field_5486.size());
		this.field_5486.add(object);
		this.method_4795(object);
	}

	@Override
	public void method_4797() {
		this.field_5486.clear();
		this.field_5488.clear();
	}

	protected void method_4795(T object) {
		((Stream)this.field_5487.apply(object)).forEach(arg -> {
			this.field_5489.method_4806(object, arg.method_12836().toLowerCase(Locale.ROOT));
			this.field_5485.method_4806(object, arg.method_12832().toLowerCase(Locale.ROOT));
		});
	}

	protected int method_4796(T object, T object2) {
		return Integer.compare(this.field_5488.getInt(object), this.field_5488.getInt(object2));
	}

	@Override
	public List<T> method_4810(String string) {
		int i = string.indexOf(58);
		if (i == -1) {
			return this.field_5485.method_4804(string);
		} else {
			List<T> list = this.field_5489.method_4804(string.substring(0, i).trim());
			String string2 = string.substring(i + 1).trim();
			List<T> list2 = this.field_5485.method_4804(string2);
			return Lists.<T>newArrayList(new class_1121.class_1122<>(list.iterator(), list2.iterator(), this::method_4796));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_1122<T> extends AbstractIterator<T> {
		private final PeekingIterator<T> field_5490;
		private final PeekingIterator<T> field_5491;
		private final Comparator<T> field_5492;

		public class_1122(Iterator<T> iterator, Iterator<T> iterator2, Comparator<T> comparator) {
			this.field_5490 = Iterators.peekingIterator(iterator);
			this.field_5491 = Iterators.peekingIterator(iterator2);
			this.field_5492 = comparator;
		}

		@Override
		protected T computeNext() {
			while (this.field_5490.hasNext() && this.field_5491.hasNext()) {
				int i = this.field_5492.compare(this.field_5490.peek(), this.field_5491.peek());
				if (i == 0) {
					this.field_5491.next();
					return this.field_5490.next();
				}

				if (i < 0) {
					this.field_5490.next();
				} else {
					this.field_5491.next();
				}
			}

			return this.endOfData();
		}
	}
}
