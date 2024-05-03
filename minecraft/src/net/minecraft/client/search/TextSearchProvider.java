package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TextSearchProvider<T> extends IdentifierSearchProvider<T> {
	private final SearchProvider<T> textSearcher;

	public TextSearchProvider(Function<T, Stream<String>> textsGetter, Function<T, Stream<Identifier>> identifiersGetter, List<T> values) {
		super(identifiersGetter, values);
		this.textSearcher = SearchProvider.plainText(values, textsGetter);
	}

	@Override
	protected List<T> search(String text) {
		return this.textSearcher.findAll(text);
	}

	@Override
	protected List<T> search(String namespace, String path) {
		List<T> list = this.idSearcher.searchNamespace(namespace);
		List<T> list2 = this.idSearcher.searchPath(path);
		List<T> list3 = this.textSearcher.findAll(path);
		Iterator<T> iterator = new TextSearchableIterator<>(list2.iterator(), list3.iterator(), this.lastIndexComparator);
		return ImmutableList.copyOf(new IdentifierSearchableIterator<>(list.iterator(), iterator, this.lastIndexComparator));
	}
}
