package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2715 implements Predicate<class_2680> {
	public static final Predicate<class_2680> field_12419 = arg -> true;
	private final class_2689<class_2248, class_2680> field_12420;
	private final Map<class_2769<?>, Predicate<Object>> field_12421 = Maps.<class_2769<?>, Predicate<Object>>newHashMap();

	private class_2715(class_2689<class_2248, class_2680> arg) {
		this.field_12420 = arg;
	}

	public static class_2715 method_11758(class_2248 arg) {
		return new class_2715(arg.method_9595());
	}

	public boolean method_11760(@Nullable class_2680 arg) {
		if (arg != null && arg.method_11614().equals(this.field_12420.method_11660())) {
			if (this.field_12421.isEmpty()) {
				return true;
			} else {
				for (Entry<class_2769<?>, Predicate<Object>> entry : this.field_12421.entrySet()) {
					if (!this.method_11761(arg, (class_2769)entry.getKey(), (Predicate<Object>)entry.getValue())) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	protected <T extends Comparable<T>> boolean method_11761(class_2680 arg, class_2769<T> arg2, Predicate<Object> predicate) {
		T comparable = arg.method_11654(arg2);
		return predicate.test(comparable);
	}

	public <V extends Comparable<V>> class_2715 method_11762(class_2769<V> arg, Predicate<Object> predicate) {
		if (!this.field_12420.method_11659().contains(arg)) {
			throw new IllegalArgumentException(this.field_12420 + " cannot support property " + arg);
		} else {
			this.field_12421.put(arg, predicate);
			return this;
		}
	}
}
