/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.data.client.model.PropertiesEntry;

/**
 * Represents a set of property to value pairs, used as conditions for model
 * application.
 * 
 * <p>This object is immutable.
 */
public final class PropertiesMap {
    private static final PropertiesMap EMPTY = new PropertiesMap(ImmutableList.of());
    private static final Comparator<PropertiesEntry<?>> COMPARATOR = Comparator.comparing(propertiesEntry -> propertiesEntry.getProperty().getName());
    private final List<PropertiesEntry<?>> propertyValues;

    public PropertiesMap with(PropertiesEntry<?> propertiesEntry) {
        return new PropertiesMap((List<PropertiesEntry<?>>)((Object)((ImmutableList.Builder)((ImmutableList.Builder)ImmutableList.builder().addAll(this.propertyValues)).add(propertiesEntry)).build()));
    }

    public PropertiesMap with(PropertiesMap propertiesMap) {
        return new PropertiesMap((List<PropertiesEntry<?>>)((Object)((ImmutableList.Builder)((ImmutableList.Builder)ImmutableList.builder().addAll(this.propertyValues)).addAll(propertiesMap.propertyValues)).build()));
    }

    private PropertiesMap(List<PropertiesEntry<?>> list) {
        this.propertyValues = list;
    }

    public static PropertiesMap empty() {
        return EMPTY;
    }

    public static PropertiesMap create(PropertiesEntry<?> ... propertiesEntrys) {
        return new PropertiesMap(ImmutableList.copyOf(propertiesEntrys));
    }

    public boolean equals(Object object) {
        return this == object || object instanceof PropertiesMap && this.propertyValues.equals(((PropertiesMap)object).propertyValues);
    }

    public int hashCode() {
        return this.propertyValues.hashCode();
    }

    public String asString() {
        return this.propertyValues.stream().sorted(COMPARATOR).map(PropertiesEntry::toString).collect(Collectors.joining(","));
    }

    public String toString() {
        return this.asString();
    }
}

