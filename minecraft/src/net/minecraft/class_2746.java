package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class class_2746 extends class_2733<Boolean> {
	private final ImmutableSet<Boolean> field_12575 = ImmutableSet.of(true, false);

	protected class_2746(String string) {
		super(string, Boolean.class);
	}

	@Override
	public Collection<Boolean> method_11898() {
		return this.field_12575;
	}

	public static class_2746 method_11825(String string) {
		return new class_2746(string);
	}

	@Override
	public Optional<Boolean> method_11900(String string) {
		return !"true".equals(string) && !"false".equals(string) ? Optional.empty() : Optional.of(Boolean.valueOf(string));
	}

	public String method_11826(Boolean boolean_) {
		return boolean_.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_2746 && super.equals(object)) {
			class_2746 lv = (class_2746)object;
			return this.field_12575.equals(lv.field_12575);
		} else {
			return false;
		}
	}

	@Override
	public int method_11799() {
		return 31 * super.method_11799() + this.field_12575.hashCode();
	}
}
