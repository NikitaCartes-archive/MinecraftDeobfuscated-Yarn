package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;

final class class_3210 {
	private final Set<class_3222> field_13910 = Sets.<class_3222>newHashSet();
	private final Set<class_3222> field_13909 = Sets.<class_3222>newHashSet();

	public Stream<class_3222> method_14083(long l) {
		return this.field_13910.stream();
	}

	public void method_14085(long l, class_3222 arg, boolean bl) {
		(bl ? this.field_13909 : this.field_13910).add(arg);
	}

	public void method_14084(long l, class_3222 arg) {
		this.field_13910.remove(arg);
		this.field_13909.remove(arg);
	}

	public void method_14086(class_3222 arg) {
		this.field_13909.add(arg);
		this.field_13910.remove(arg);
	}

	public void method_14087(class_3222 arg) {
		this.field_13909.remove(arg);
		this.field_13910.add(arg);
	}

	public boolean method_14082(class_3222 arg) {
		return !this.field_13910.contains(arg);
	}

	public void method_14081(long l, long m, class_3222 arg) {
	}
}
