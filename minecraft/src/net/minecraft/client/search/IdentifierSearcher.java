package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

/**
 * An interface used for searching with an identifier's path or namespace.
 */
@Environment(EnvType.CLIENT)
public interface IdentifierSearcher<T> {
	/**
	 * {@return a searcher that always returns no results}
	 */
	static <T> IdentifierSearcher<T> of() {
		return new IdentifierSearcher<T>() {
			@Override
			public List<T> searchNamespace(String namespace) {
				return List.of();
			}

			@Override
			public List<T> searchPath(String path) {
				return List.of();
			}
		};
	}

	/**
	 * {@return a searcher that searches from {@code values}}
	 * 
	 * @param identifiersGetter a function that, when given a value from {@code values}, returns a
	 * stream of identifiers associated with the value
	 */
	static <T> IdentifierSearcher<T> of(List<T> values, Function<T, Stream<Identifier>> identifiersGetter) {
		if (values.isEmpty()) {
			return of();
		} else {
			final SuffixArray<T> suffixArray = new SuffixArray<>();
			final SuffixArray<T> suffixArray2 = new SuffixArray<>();

			for (T object : values) {
				((Stream)identifiersGetter.apply(object)).forEach(id -> {
					suffixArray.add(object, id.getNamespace().toLowerCase(Locale.ROOT));
					suffixArray2.add(object, id.getPath().toLowerCase(Locale.ROOT));
				});
			}

			suffixArray.build();
			suffixArray2.build();
			return new IdentifierSearcher<T>() {
				@Override
				public List<T> searchNamespace(String namespace) {
					return suffixArray.findAll(namespace);
				}

				@Override
				public List<T> searchPath(String path) {
					return suffixArray2.findAll(path);
				}
			};
		}
	}

	/**
	 * {@return the results of searching from the namespaces of the ids}
	 */
	List<T> searchNamespace(String namespace);

	/**
	 * {@return the results of searching from the paths of the ids}
	 */
	List<T> searchPath(String path);
}
