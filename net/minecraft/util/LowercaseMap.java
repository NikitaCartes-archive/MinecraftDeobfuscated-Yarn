/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LowercaseMap<V>
implements Map<String, V> {
    private final Map<String, V> delegate = Maps.newLinkedHashMap();

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object object) {
        return this.delegate.containsKey(object.toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean containsValue(Object object) {
        return this.delegate.containsValue(object);
    }

    @Override
    public V get(Object object) {
        return this.delegate.get(object.toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public V put(String string, V object) {
        return this.delegate.put(string.toLowerCase(Locale.ROOT), object);
    }

    @Override
    public V remove(Object object) {
        return this.delegate.remove(object.toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        for (Map.Entry<String, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.delegate.values();
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return this.delegate.entrySet();
    }

    @Override
    public /* synthetic */ Object put(Object object, Object object2) {
        return this.put((String)object, object2);
    }
}

