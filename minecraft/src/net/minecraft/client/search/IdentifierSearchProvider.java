package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class IdentifierSearchProvider<T> implements SearchProvider<T> {
	protected final Comparator<T> lastIndexComparator;
	protected final IdentifierSearcher<T> idSearcher;

	public IdentifierSearchProvider(Function<T, Stream<Identifier>> identifiersGetter, List<T> values) {
		ToIntFunction<T> toIntFunction = Util.lastIndexGetter(values);
		this.lastIndexComparator = Comparator.comparingInt(toIntFunction);
		this.idSearcher = IdentifierSearcher.of(values, identifiersGetter);
	}

	@Override
	public List<T> findAll(String text) {
		int i = text.indexOf(58);
		return i == -1 ? this.search(text) : this.search(text.substring(0, i).trim(), text.substring(i + 1).trim());
	}

	protected List<T> search(String text) {
		return this.idSearcher.searchPath(text);
	}

	protected List<T> search(String namespace, String path) {
		List<T> list = this.idSearcher.searchNamespace(namespace);
		List<T> list2 = this.idSearcher.searchPath(path);
		return ImmutableList.copyOf(new IdentifierSearchableIterator<>(list.iterator(), list2.iterator(), this.lastIndexComparator));
	}
}
