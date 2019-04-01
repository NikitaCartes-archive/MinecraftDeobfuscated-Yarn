package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class class_3538<T> {
	private final List<T> field_15745 = Lists.<T>newArrayList();
	private final Spliterator<T> field_15746;

	public class_3538(Stream<T> stream) {
		this.field_15746 = stream.spliterator();
	}

	public Stream<T> method_15418() {
		return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
			private int field_15747;

			public boolean tryAdvance(Consumer<? super T> consumer) {
				while (this.field_15747 >= class_3538.this.field_15745.size()) {
					if (!class_3538.this.field_15746.tryAdvance(class_3538.this.field_15745::add)) {
						return false;
					}
				}

				consumer.accept(class_3538.this.field_15745.get(this.field_15747++));
				return true;
			}
		}, false);
	}
}
