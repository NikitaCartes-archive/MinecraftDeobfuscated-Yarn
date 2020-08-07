/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryCodec;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final ObjectList<T> field_26682 = new ObjectArrayList<T>(256);
    private final Object2IntMap<T> field_26683 = new Object2IntOpenCustomHashMap<T>(Util.identityHashStrategy());
    private final BiMap<Identifier, T> entriesById;
    private final BiMap<RegistryKey<T>, T> entriesByKey;
    private final Map<T, Lifecycle> field_26731;
    private Lifecycle field_26732;
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
        this.field_26683.defaultReturnValue(-1);
        this.entriesById = HashBiMap.create();
        this.entriesByKey = HashBiMap.create();
        this.field_26731 = Maps.newIdentityHashMap();
        this.field_26732 = lifecycle;
    }

    public static <T> MapCodec<class_5501<T>> method_30929(RegistryKey<? extends Registry<T>> registryKey, MapCodec<T> mapCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue).fieldOf("name")).forGetter(arg -> arg.field_26684), ((MapCodec)Codec.INT.fieldOf("id")).forGetter(arg -> arg.field_26685), mapCodec.forGetter(arg -> arg.field_26686)).apply((Applicative<class_5501, ?>)instance, class_5501::new));
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle) {
        return this.method_31051(rawId, key, entry, lifecycle, true);
    }

    private <V extends T> V method_31051(int i, RegistryKey<T> registryKey, V object, Lifecycle lifecycle, boolean bl) {
        Validate.notNull(registryKey);
        Validate.notNull(object);
        this.field_26682.size(Math.max(this.field_26682.size(), i + 1));
        this.field_26682.set(i, object);
        this.field_26683.put((T)object, i);
        this.randomEntries = null;
        if (bl && this.entriesByKey.containsKey(registryKey)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", (Object)registryKey);
        }
        if (this.entriesById.containsValue(object)) {
            LOGGER.error("Adding duplicate value '{}' to registry", (Object)object);
        }
        this.entriesById.put(registryKey.getValue(), object);
        this.entriesByKey.put(registryKey, object);
        this.field_26731.put(object, lifecycle);
        this.field_26732 = this.field_26732.add(lifecycle);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }
        return object;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle) {
        return this.set(this.nextId, key, entry, lifecycle);
    }

    @Override
    public <V extends T> V method_31062(OptionalInt optionalInt, RegistryKey<T> registryKey, V object, Lifecycle lifecycle) {
        int i;
        Validate.notNull(registryKey);
        Validate.notNull(object);
        Object object2 = this.entriesByKey.get(registryKey);
        if (object2 == null) {
            i = optionalInt.isPresent() ? optionalInt.getAsInt() : this.nextId;
        } else {
            i = this.field_26683.getInt(object2);
            if (optionalInt.isPresent() && optionalInt.getAsInt() != i) {
                throw new IllegalStateException("ID mismatch");
            }
            this.field_26683.removeInt(object2);
            this.field_26731.remove(object2);
        }
        return this.method_31051(i, registryKey, object, lifecycle, false);
    }

    @Override
    @Nullable
    public Identifier getId(T entry) {
        return (Identifier)this.entriesById.inverse().get(entry);
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T value) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(value));
    }

    @Override
    public int getRawId(@Nullable T object) {
        return this.field_26683.getInt(object);
    }

    @Override
    @Nullable
    public T get(@Nullable RegistryKey<T> key) {
        return (T)this.entriesByKey.get(key);
    }

    @Override
    @Nullable
    public T get(int index) {
        if (index < 0 || index >= this.field_26682.size()) {
            return null;
        }
        return (T)this.field_26682.get(index);
    }

    @Override
    public Lifecycle method_31139(T object) {
        return this.field_26731.get(object);
    }

    @Override
    public Lifecycle method_31138() {
        return this.field_26732;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.field_26682.iterator(), Objects::nonNull);
    }

    @Override
    @Nullable
    public T get(@Nullable Identifier id) {
        return (T)this.entriesById.get(id);
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entriesById.keySet());
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntries() {
        return Collections.unmodifiableMap(this.entriesByKey).entrySet();
    }

    @Nullable
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Collection collection = this.entriesById.values();
            if (collection.isEmpty()) {
                return null;
            }
            this.randomEntries = collection.toArray(new Object[collection.size()]);
        }
        return (T)Util.getRandom(this.randomEntries, random);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsId(Identifier id) {
        return this.entriesById.containsKey(id);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
        return SimpleRegistry.method_30929(registryKey, codec.fieldOf("element")).codec().listOf().xmap(list -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(registryKey, lifecycle);
            for (class_5501 lv : list) {
                simpleRegistry.set(lv.field_26685, lv.field_26684, lv.field_26686, lifecycle);
            }
            return simpleRegistry;
        }, simpleRegistry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : simpleRegistry) {
                builder.add(new class_5501(simpleRegistry.getKey(object).get(), simpleRegistry.getRawId(object), object));
            }
            return builder.build();
        });
    }

    public static <T> Codec<SimpleRegistry<T>> createCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> codec) {
        return RegistryCodec.of(registryRef, lifecycle, codec);
    }

    public static <T> Codec<SimpleRegistry<T>> method_31059(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.unboundedMap(Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue), codec).xmap(map -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(registryKey, lifecycle);
            map.forEach((? super K registryKey, ? super V object) -> simpleRegistry.add((RegistryKey)registryKey, (Object)object, lifecycle));
            return simpleRegistry;
        }, simpleRegistry -> ImmutableMap.copyOf(simpleRegistry.entriesByKey));
    }

    public static class class_5501<T> {
        public final RegistryKey<T> field_26684;
        public final int field_26685;
        public final T field_26686;

        public class_5501(RegistryKey<T> registryKey, int i, T object) {
            this.field_26684 = registryKey;
            this.field_26685 = i;
            this.field_26686 = object;
        }
    }
}

