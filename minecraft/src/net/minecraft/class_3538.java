package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class class_3538<T> {
	private final List<T> field_15745 = Lists.<T>newArrayList();
	private final Iterator<T> field_15746;

	public class_3538(Stream<T> stream) {
		this.field_15746 = stream.iterator();
	}

	public Stream<T> method_15418() {
		return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
			private int field_15747 = 0;

			public boolean tryAdvance(Consumer<? super T> consumer) {
				T object;
				if (this.field_15747 >= class_3538.this.field_15745.size()) {
					if (!class_3538.this.field_15746.hasNext()) {
						return false;
					}

					object = (T)class_3538.this.field_15746.next();
					class_3538.this.field_15745.add(object);
				} else {
					object = (T)class_3538.this.field_15745.get(this.field_15747);
				}

				this.field_15747++;
				consumer.accept(object);
				return true;
			}
		}, false);
	}
}
