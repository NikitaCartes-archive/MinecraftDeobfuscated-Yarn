package net.minecraft;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.collection.TypeFilterableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5572<T> {
	protected static final Logger field_27247 = LogManager.getLogger();
	private final TypeFilterableList<T> field_27248;
	private class_5584 field_27249;

	public class_5572(Class<T> class_, class_5584 arg) {
		this.field_27249 = arg;
		this.field_27248 = new TypeFilterableList<>(class_);
	}

	public void method_31764(T object) {
		this.field_27248.add(object);
	}

	public boolean method_31767(T object) {
		return this.field_27248.remove(object);
	}

	public void method_31765(Predicate<? super T> predicate, Consumer<T> consumer) {
		for (T object : this.field_27248) {
			if (predicate.test(object)) {
				consumer.accept(object);
			}
		}
	}

	public <U extends T> void method_31762(class_5575<T, U> arg, Predicate<? super U> predicate, Consumer<? super U> consumer) {
		for (T object : this.field_27248.getAllOfType(arg.method_31794())) {
			U object2 = arg.method_31796(object);
			if (object2 != null && predicate.test(object2)) {
				consumer.accept(object2);
			}
		}
	}

	public boolean method_31761() {
		return this.field_27248.isEmpty();
	}

	public Stream<T> method_31766() {
		return this.field_27248.stream();
	}

	public class_5584 method_31768() {
		return this.field_27249;
	}

	public class_5584 method_31763(class_5584 arg) {
		class_5584 lv = this.field_27249;
		this.field_27249 = arg;
		return lv;
	}

	public int method_31769() {
		return this.field_27248.size();
	}
}
