package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextSearchableIterator<T> extends AbstractIterator<T> {
	private final PeekingIterator<T> idPathsIterator;
	private final PeekingIterator<T> textsIterator;
	private final Comparator<T> lastIndexComparator;

	public TextSearchableIterator(Iterator<T> idPathsIterator, Iterator<T> textsIterator, Comparator<T> lastIndexComparator) {
		this.idPathsIterator = Iterators.peekingIterator(idPathsIterator);
		this.textsIterator = Iterators.peekingIterator(textsIterator);
		this.lastIndexComparator = lastIndexComparator;
	}

	@Override
	protected T computeNext() {
		boolean bl = !this.idPathsIterator.hasNext();
		boolean bl2 = !this.textsIterator.hasNext();
		if (bl && bl2) {
			return this.endOfData();
		} else if (bl) {
			return this.textsIterator.next();
		} else if (bl2) {
			return this.idPathsIterator.next();
		} else {
			int i = this.lastIndexComparator.compare(this.idPathsIterator.peek(), this.textsIterator.peek());
			if (i == 0) {
				this.textsIterator.next();
			}

			return i <= 0 ? this.idPathsIterator.next() : this.textsIterator.next();
		}
	}
}
