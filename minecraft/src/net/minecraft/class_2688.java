package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface class_2688<C> {
	Logger field_12313 = LogManager.getLogger();

	<T extends Comparable<T>> T method_11654(class_2769<T> arg);

	<T extends Comparable<T>, V extends T> C method_11657(class_2769<T> arg, V comparable);

	ImmutableMap<class_2769<?>, Comparable<?>> method_11656();

	static <T extends Comparable<T>> String method_16551(class_2769<T> arg, Comparable<?> comparable) {
		return arg.method_11901((T)comparable);
	}

	static <S extends class_2688<S>, T extends Comparable<T>> S method_11655(S arg, class_2769<T> arg2, String string, String string2, String string3) {
		Optional<T> optional = arg2.method_11900(string3);
		if (optional.isPresent()) {
			return arg.method_11657(arg2, (Comparable)optional.get());
		} else {
			field_12313.warn("Unable to read property: {} with value: {} for input: {}", string, string3, string2);
			return arg;
		}
	}
}
