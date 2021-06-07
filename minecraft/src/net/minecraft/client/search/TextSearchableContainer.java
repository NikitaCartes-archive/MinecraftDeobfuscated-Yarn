package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TextSearchableContainer<T> extends IdentifierSearchableContainer<T> {
	protected SuffixArray<T> byText = new SuffixArray<>();
	private final Function<T, Stream<String>> textFinder;

	public TextSearchableContainer(Function<T, Stream<String>> textFinder, Function<T, Stream<Identifier>> identifierFinder) {
		super(identifierFinder);
		this.textFinder = textFinder;
	}

	@Override
	public void reload() {
		this.byText = new SuffixArray<>();
		super.reload();
		this.byText.build();
	}

	@Override
	protected void index(T object) {
		super.index(object);
		((Stream)this.textFinder.apply(object)).forEach(string -> this.byText.add(object, string.toLowerCase(Locale.ROOT)));
	}

	@Override
	public List<T> findAll(String text) {
		int i = text.indexOf(58);
		if (i < 0) {
			return this.byText.findAll(text);
		} else {
			List<T> list = this.byNamespace.findAll(text.substring(0, i).trim());
			String string = text.substring(i + 1).trim();
			List<T> list2 = this.byPath.findAll(string);
			List<T> list3 = this.byText.findAll(string);
			return Lists.<T>newArrayList(
				new IdentifierSearchableContainer.Iterator<>(
					list.iterator(), new TextSearchableContainer.Iterator<>(list2.iterator(), list3.iterator(), this::compare), this::compare
				)
			);
		}
	}

	@Environment(EnvType.CLIENT)
	static class Iterator<T> extends AbstractIterator<T> {
		private final PeekingIterator<T> field_5499;
		private final PeekingIterator<T> field_5500;
		private final Comparator<T> field_5501;

		public Iterator(java.util.Iterator<T> iterator, java.util.Iterator<T> iterator2, Comparator<T> comparator) {
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
