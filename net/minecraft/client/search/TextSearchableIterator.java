/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class TextSearchableIterator<T>
extends AbstractIterator<T> {
    private final PeekingIterator<T> idPathsIterator;
    private final PeekingIterator<T> textsIterator;
    private final Comparator<T> lastIndexComparator;

    public TextSearchableIterator(Iterator<T> idPathsIterator, Iterator<T> textsIterator, Comparator<T> lastIndexComparator) {
        this.idPathsIterator = Iterators.peekingIterator(idPathsIterator);
        this.textsIterator = Iterators.peekingIterator(textsIterator);
        this.lastIndexComparator = lastIndexComparator;
    }

    @Override
    protected T computeNext() {
        boolean bl2;
        boolean bl = !this.idPathsIterator.hasNext();
        boolean bl3 = bl2 = !this.textsIterator.hasNext();
        if (bl && bl2) {
            return this.endOfData();
        }
        if (bl) {
            return this.textsIterator.next();
        }
        if (bl2) {
            return this.idPathsIterator.next();
        }
        int i = this.lastIndexComparator.compare(this.idPathsIterator.peek(), this.textsIterator.peek());
        if (i == 0) {
            this.textsIterator.next();
        }
        return i <= 0 ? this.idPathsIterator.next() : this.textsIterator.next();
    }
}

