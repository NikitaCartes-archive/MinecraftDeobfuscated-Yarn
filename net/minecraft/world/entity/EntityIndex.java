/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * An index of entities by both their network IDs and UUIDs.
 */
public class EntityIndex<T extends EntityLike> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Int2ObjectMap<T> idToEntity = new Int2ObjectLinkedOpenHashMap<T>();
    private final Map<UUID, T> uuidToEntity = Maps.newHashMap();

    public <U extends T> void forEach(TypeFilter<T, U> filter, LazyIterationConsumer<U> consumer) {
        for (EntityLike entityLike : this.idToEntity.values()) {
            EntityLike entityLike2 = (EntityLike)filter.downcast(entityLike);
            if (entityLike2 == null || !consumer.accept(entityLike2).shouldAbort()) continue;
            return;
        }
    }

    public Iterable<T> iterate() {
        return Iterables.unmodifiableIterable(this.idToEntity.values());
    }

    public void add(T entity) {
        UUID uUID = entity.getUuid();
        if (this.uuidToEntity.containsKey(uUID)) {
            LOGGER.warn("Duplicate entity UUID {}: {}", (Object)uUID, (Object)entity);
            return;
        }
        this.uuidToEntity.put(uUID, entity);
        this.idToEntity.put(entity.getId(), entity);
    }

    public void remove(T entity) {
        this.uuidToEntity.remove(entity.getUuid());
        this.idToEntity.remove(entity.getId());
    }

    @Nullable
    public T get(int id) {
        return (T)((EntityLike)this.idToEntity.get(id));
    }

    @Nullable
    public T get(UUID uuid) {
        return (T)((EntityLike)this.uuidToEntity.get(uuid));
    }

    public int size() {
        return this.uuidToEntity.size();
    }
}

