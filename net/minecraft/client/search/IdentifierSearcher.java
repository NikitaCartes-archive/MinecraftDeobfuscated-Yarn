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
import net.minecraft.util.Identifier;

/**
 * An interface used for searching with an identifier's path or namespace.
 */
@Environment(value=EnvType.CLIENT)
public interface IdentifierSearcher<T> {
    /**
     * {@return a searcher that always returns no results}
     */
    public static <T> IdentifierSearcher<T> of() {
        return new IdentifierSearcher<T>(){

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
    public static <T> IdentifierSearcher<T> of(List<T> values, Function<T, Stream<Identifier>> identifiersGetter) {
        if (values.isEmpty()) {
            return IdentifierSearcher.of();
        }
        final SuffixArray suffixArray = new SuffixArray();
        final SuffixArray suffixArray2 = new SuffixArray();
        for (Object object : values) {
            identifiersGetter.apply(object).forEach(id -> {
                suffixArray.add(object, id.getNamespace().toLowerCase(Locale.ROOT));
                suffixArray2.add(object, id.getPath().toLowerCase(Locale.ROOT));
            });
        }
        suffixArray.build();
        suffixArray2.build();
        return new IdentifierSearcher<T>(){

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

    /**
     * {@return the results of searching from the namespaces of the ids}
     */
    public List<T> searchNamespace(String var1);

    /**
     * {@return the results of searching from the paths of the ids}
     */
    public List<T> searchPath(String var1);
}

