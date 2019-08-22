/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TextSearchableContainer<T>
extends IdentifierSearchableContainer<T> {
    protected SuffixArray<T> byText = new SuffixArray();
    private final Function<T, Stream<String>> textFinder;

    public TextSearchableContainer(Function<T, Stream<String>> function, Function<T, Stream<Identifier>> function2) {
        super(function2);
        this.textFinder = function;
    }

    @Override
    public void reload() {
        this.byText = new SuffixArray();
        super.reload();
        this.byText.reload();
    }

    @Override
    protected void index(T object) {
        super.index(object);
        this.textFinder.apply(object).forEach(string -> this.byText.add(object, string.toLowerCase(Locale.ROOT)));
    }

    @Override
    public List<T> findAll(String string) {
        int i = string.indexOf(58);
        if (i < 0) {
            return this.byText.findAll(string);
        }
        List list = this.byNamespace.findAll(string.substring(0, i).trim());
        String string2 = string.substring(i + 1).trim();
        List list2 = this.byPath.findAll(string2);
        List<T> list3 = this.byText.findAll(string2);
        return Lists.newArrayList(new IdentifierSearchableContainer.Iterator(list.iterator(), new Iterator(list2.iterator(), list3.iterator(), this::compare), this::compare));
    }

    @Environment(value=EnvType.CLIENT)
    static class Iterator<T>
    extends AbstractIterator<T> {
        private final PeekingIterator<T> field_5499;
        private final PeekingIterator<T> field_5500;
        private final Comparator<T> field_5501;

        public Iterator(java.util.Iterator<T> iterator, java.util.Iterator<T> iterator2, Comparator<T> comparator) {
            this.field_5499 = Iterators.peekingIterator(iterator);
            this.field_5500 = Iterators.peekingIterator(iterator2);
            this.field_5501 = comparator;
        }

        @Override
        protected T computeNext() {
            boolean bl2;
            boolean bl = !this.field_5499.hasNext();
            boolean bl3 = bl2 = !this.field_5500.hasNext();
            if (bl && bl2) {
                return this.endOfData();
            }
            if (bl) {
                return this.field_5500.next();
            }
            if (bl2) {
                return this.field_5499.next();
            }
            int i = this.field_5501.compare(this.field_5499.peek(), this.field_5500.peek());
            if (i == 0) {
                this.field_5500.next();
            }
            return i <= 0 ? this.field_5499.next() : this.field_5500.next();
        }
    }
}

