package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LoopingStream<T> {
	private final List<T> field_15745 = Lists.<T>newArrayList();
	private final Iterator<T> field_15746;

	public LoopingStream(Stream<T> stream) {
		this.field_15746 = stream.iterator();
	}

	public Stream<T> getStream() {
		return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
			private int field_15747;

			public boolean tryAdvance(Consumer<? super T> consumer) {
				T object;
				if (this.field_15747 >= LoopingStream.this.field_15745.size()) {
					if (!LoopingStream.this.field_15746.hasNext()) {
						return false;
					}

					object = (T)LoopingStream.this.field_15746.next();
					LoopingStream.this.field_15745.add(object);
				} else {
					object = (T)LoopingStream.this.field_15745.get(this.field_15747);
				}

				this.field_15747++;
				consumer.accept(object);
				return true;
			}
		}, false);
	}
}
