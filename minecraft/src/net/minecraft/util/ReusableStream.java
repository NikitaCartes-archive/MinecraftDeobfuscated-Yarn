package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReusableStream<T> {
	private final List<T> collectedElements = Lists.<T>newArrayList();
	private final Spliterator<T> source;

	public ReusableStream(Stream<T> stream) {
		this.source = stream.spliterator();
	}

	public Stream<T> stream() {
		return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
			private int pos;

			public boolean tryAdvance(Consumer<? super T> consumer) {
				while (this.pos >= ReusableStream.this.collectedElements.size()) {
					if (!ReusableStream.this.source.tryAdvance(ReusableStream.this.collectedElements::add)) {
						return false;
					}
				}

				consumer.accept(ReusableStream.this.collectedElements.get(this.pos++));
				return true;
			}
		}, false);
	}
}
