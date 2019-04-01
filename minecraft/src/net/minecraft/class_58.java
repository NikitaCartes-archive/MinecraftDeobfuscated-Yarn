package net.minecraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;

public class class_58 {
	private final Multimap<String, String> field_965;
	private final Supplier<String> field_966;
	private String field_964;

	public class_58() {
		this(HashMultimap.create(), () -> "");
	}

	public class_58(Multimap<String, String> multimap, Supplier<String> supplier) {
		this.field_965 = multimap;
		this.field_966 = supplier;
	}

	private String method_359() {
		if (this.field_964 == null) {
			this.field_964 = (String)this.field_966.get();
		}

		return this.field_964;
	}

	public void method_360(String string) {
		this.field_965.put(this.method_359(), string);
	}

	public class_58 method_364(String string) {
		return new class_58(this.field_965, () -> this.method_359() + string);
	}

	public Multimap<String, String> method_361() {
		return ImmutableMultimap.copyOf(this.field_965);
	}
}
