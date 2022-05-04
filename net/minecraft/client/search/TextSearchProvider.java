/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.IdentifierSearchProvider;
import net.minecraft.client.search.IdentifierSearchableIterator;
import net.minecraft.client.search.TextSearchableIterator;
import net.minecraft.client.search.TextSearcher;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TextSearchProvider<T>
extends IdentifierSearchProvider<T> {
    private final List<T> values;
    private final Function<T, Stream<String>> textsGetter;
    private TextSearcher<T> textSearcher = TextSearcher.of();

    public TextSearchProvider(Function<T, Stream<String>> textsGetter, Function<T, Stream<Identifier>> identifiersGetter, List<T> values) {
        super(identifiersGetter, values);
        this.values = values;
        this.textsGetter = textsGetter;
    }

    @Override
    public void reload() {
        super.reload();
        this.textSearcher = TextSearcher.of(this.values, this.textsGetter);
    }

    @Override
    protected List<T> search(String text) {
        return this.textSearcher.search(text);
    }

    @Override
    protected List<T> search(String namespace, String path) {
        List list = this.idSearcher.searchNamespace(namespace);
        List list2 = this.idSearcher.searchPath(path);
        List<T> list3 = this.textSearcher.search(path);
        TextSearchableIterator iterator = new TextSearchableIterator(list2.iterator(), list3.iterator(), this.lastIndexComparator);
        return ImmutableList.copyOf(new IdentifierSearchableIterator(list.iterator(), iterator, this.lastIndexComparator));
    }
}

