/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.IdentifierSearchableIterator;
import net.minecraft.client.search.IdentifierSearcher;
import net.minecraft.client.search.ReloadableSearchProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class IdentifierSearchProvider<T>
implements ReloadableSearchProvider<T> {
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
        if (i == -1) {
            return this.search(text);
        }
        return this.search(text.substring(0, i).trim(), text.substring(i + 1).trim());
    }

    protected List<T> search(String text) {
        return this.idSearcher.searchPath(text);
    }

    protected List<T> search(String namespace, String path) {
        List<T> list = this.idSearcher.searchNamespace(namespace);
        List<T> list2 = this.idSearcher.searchPath(path);
        return ImmutableList.copyOf(new IdentifierSearchableIterator<T>(list.iterator(), list2.iterator(), this.lastIndexComparator));
    }
}

