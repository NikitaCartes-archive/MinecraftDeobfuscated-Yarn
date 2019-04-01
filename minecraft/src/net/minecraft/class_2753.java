package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class class_2753 extends class_2754<class_2350> {
	protected class_2753(String string, Collection<class_2350> collection) {
		super(string, class_2350.class, collection);
	}

	public static class_2753 method_11844(String string, Predicate<class_2350> predicate) {
		return method_11843(string, (Collection<class_2350>)Arrays.stream(class_2350.values()).filter(predicate).collect(Collectors.toList()));
	}

	public static class_2753 method_11845(String string, class_2350... args) {
		return method_11843(string, Lists.<class_2350>newArrayList(args));
	}

	public static class_2753 method_11843(String string, Collection<class_2350> collection) {
		return new class_2753(string, collection);
	}
}
