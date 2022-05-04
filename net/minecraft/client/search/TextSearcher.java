/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SuffixArray;

/**
 * A functional interface that allows searching with a text.
 */
@Environment(value=EnvType.CLIENT)
public interface TextSearcher<T> {
    /**
     * {@return a searcher that always returns no results}
     */
    public static <T> TextSearcher<T> of() {
        return text -> List.of();
    }

    /**
     * {@return a searcher that searches from {@code values}}
     * 
     * @param textsGetter a function that, when given a value from {@code values}, returns a
     * stream of search texts associated with the value
     */
    public static <T> TextSearcher<T> of(List<T> values, Function<T, Stream<String>> textsGetter) {
        if (values.isEmpty()) {
            return TextSearcher.of();
        }
        SuffixArray suffixArray = new SuffixArray();
        for (Object object : values) {
            textsGetter.apply(object).forEach(text -> suffixArray.add(object, text.toLowerCase(Locale.ROOT)));
        }
        suffixArray.build();
        return suffixArray::findAll;
    }

    /**
     * {@return the results of searching with the provided {@code text}}
     */
    public List<T> search(String var1);
}

