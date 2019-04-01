package net.minecraft;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;

public class class_176 {
	private final Set<class_169<?>> field_1216;
	private final Set<class_169<?>> field_1215;

	private class_176(Set<class_169<?>> set, Set<class_169<?>> set2) {
		this.field_1216 = ImmutableSet.copyOf(set);
		this.field_1215 = ImmutableSet.copyOf(Sets.union(set, set2));
	}

	public Set<class_169<?>> method_778() {
		return this.field_1216;
	}

	public Set<class_169<?>> method_777() {
		return this.field_1215;
	}

	public String toString() {
		return "[" + Joiner.on(", ").join(this.field_1215.stream().map(arg -> (this.field_1216.contains(arg) ? "!" : "") + arg.method_746()).iterator()) + "]";
	}

	public void method_776(class_58 arg, class_46 arg2) {
		Set<class_169<?>> set = arg2.method_293();
		Set<class_169<?>> set2 = Sets.<class_169<?>>difference(set, this.field_1215);
		if (!set2.isEmpty()) {
			arg.method_360("Parameters " + set2 + " are not provided in this context");
		}
	}

	public static class class_177 {
		private final Set<class_169<?>> field_1218 = Sets.newIdentityHashSet();
		private final Set<class_169<?>> field_1217 = Sets.newIdentityHashSet();

		public class_176.class_177 method_781(class_169<?> arg) {
			if (this.field_1217.contains(arg)) {
				throw new IllegalArgumentException("Parameter " + arg.method_746() + " is already optional");
			} else {
				this.field_1218.add(arg);
				return this;
			}
		}

		public class_176.class_177 method_780(class_169<?> arg) {
			if (this.field_1218.contains(arg)) {
				throw new IllegalArgumentException("Parameter " + arg.method_746() + " is already required");
			} else {
				this.field_1217.add(arg);
				return this;
			}
		}

		public class_176 method_782() {
			return new class_176(this.field_1218, this.field_1217);
		}
	}
}
