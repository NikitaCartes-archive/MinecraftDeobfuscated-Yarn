package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A functional interface that allows searching with a text.
 */
@Environment(EnvType.CLIENT)
public interface TextSearcher<T> {
	/**
	 * {@return a searcher that always returns no results}
	 */
	static <T> TextSearcher<T> of() {
		return text -> List.of();
	}

	/**
	 * {@return a searcher that searches from {@code values}}
	 * 
	 * @param textsGetter a function that, when given a value from {@code values}, returns a
	 * stream of search texts associated with the value
	 */
	static <T> TextSearcher<T> of(List<T> values, Function<T, Stream<String>> textsGetter) {
		if (values.isEmpty()) {
			return of();
		} else {
			SuffixArray<T> suffixArray = new SuffixArray<>();

			for (T object : values) {
				((Stream)textsGetter.apply(object)).forEach(text -> suffixArray.add(object, text.toLowerCase(Locale.ROOT)));
			}

			suffixArray.build();
			return suffixArray::findAll;
		}
	}

	/**
	 * {@return the results of searching with the provided {@code text}}
	 */
	List<T> search(String text);
}
