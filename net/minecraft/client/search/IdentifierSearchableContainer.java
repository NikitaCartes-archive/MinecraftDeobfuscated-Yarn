/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IdentifierSearchableContainer<T>
implements SearchableContainer<T> {
    protected SuffixArray<T> byNamespace = new SuffixArray();
    protected SuffixArray<T> byPath = new SuffixArray();
    private final Function<T, Stream<Identifier>> identifierFinder;
    private final List<T> entries = Lists.newArrayList();
    private final Object2IntMap<T> entryIds = new Object2IntOpenHashMap<T>();

    public IdentifierSearchableContainer(Function<T, Stream<Identifier>> function) {
        this.identifierFinder = function;
    }

    @Override
    public void reload() {
        this.byNamespace = new SuffixArray();
        this.byPath = new SuffixArray();
        for (T object : this.entries) {
            this.index(object);
        }
        this.byNamespace.sort();
        this.byPath.sort();
    }

    @Override
    public void add(T object) {
        this.entryIds.put(object, this.entries.size());
        this.entries.add(object);
        this.index(object);
    }

    @Override
    public void clear() {
        this.entries.clear();
        this.entryIds.clear();
    }

    protected void index(T object) {
        this.identifierFinder.apply(object).forEach(identifier -> {
            this.byNamespace.add(object, identifier.getNamespace().toLowerCase(Locale.ROOT));
            this.byPath.add(object, identifier.getPath().toLowerCase(Locale.ROOT));
        });
    }

    protected int compare(T object1, T object2) {
        return Integer.compare(this.entryIds.getInt(object1), this.entryIds.getInt(object2));
    }

    @Override
    public List<T> findAll(String text) {
        int i = text.indexOf(58);
        if (i == -1) {
            return this.byPath.findAll(text);
        }
        List<T> list = this.byNamespace.findAll(text.substring(0, i).trim());
        String string = text.substring(i + 1).trim();
        List<T> list2 = this.byPath.findAll(string);
        return Lists.newArrayList(new Iterator<T>(list.iterator(), list2.iterator(), this::compare));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Iterator<T>
    extends AbstractIterator<T> {
        private final PeekingIterator<T> field_5490;
        private final PeekingIterator<T> field_5491;
        private final Comparator<T> field_5492;

        public Iterator(java.util.Iterator<T> iterator, java.util.Iterator<T> iterator2, Comparator<T> comparator) {
            this.field_5490 = Iterators.peekingIterator(iterator);
            this.field_5491 = Iterators.peekingIterator(iterator2);
            this.field_5492 = comparator;
        }

        @Override
        protected T computeNext() {
            while (this.field_5490.hasNext() && this.field_5491.hasNext()) {
                int i = this.field_5492.compare(this.field_5490.peek(), this.field_5491.peek());
                if (i == 0) {
                    this.field_5491.next();
                    return this.field_5490.next();
                }
                if (i < 0) {
                    this.field_5490.next();
                    continue;
                }
                this.field_5491.next();
            }
            return this.endOfData();
        }
    }
}

