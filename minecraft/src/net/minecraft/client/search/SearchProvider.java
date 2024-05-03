package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A functional interface that provides searching.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface SearchProvider<T> {
	static <T> SearchProvider<T> empty() {
		return string -> List.of();
	}

	static <T> SearchProvider<T> plainText(List<T> list, Function<T, Stream<String>> function) {
		if (list.isEmpty()) {
			return empty();
		} else {
			SuffixArray<T> suffixArray = new SuffixArray<>();

			for (T object : list) {
				((Stream)function.apply(object)).forEach(string -> suffixArray.add(object, string.toLowerCase(Locale.ROOT)));
			}

			suffixArray.build();
			return suffixArray::findAll;
		}
	}

	/**
	 * {@return the search result of {@code text}}
	 */
	List<T> findAll(String text);
}
