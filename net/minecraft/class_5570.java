/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.class_5575;
import net.minecraft.entity.EntityLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class class_5570<T extends EntityLike> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Int2ObjectMap<T> entitiesById = new Int2ObjectLinkedOpenHashMap<T>();
    private final Map<UUID, T> entitiesByUuid = Maps.newHashMap();

    public <U extends T> void method_31754(class_5575<T, U> arg, Consumer<U> consumer) {
        for (EntityLike entityLike : this.entitiesById.values()) {
            EntityLike entityLike2 = (EntityLike)arg.method_31796(entityLike);
            if (entityLike2 == null) continue;
            consumer.accept(entityLike2);
        }
    }

    public Iterable<T> method_31751() {
        return Iterables.unmodifiableIterable(this.entitiesById.values());
    }

    public void addEntity(T entity) {
        UUID uUID = entity.getUuid();
        if (this.entitiesByUuid.containsKey(uUID)) {
            LOGGER.warn("Duplicate entity UUID {}: {}", (Object)uUID, (Object)entity);
            return;
        }
        this.entitiesByUuid.put(uUID, entity);
        this.entitiesById.put(entity.getId(), entity);
    }

    public void removeEntity(T entity) {
        this.entitiesByUuid.remove(entity.getUuid());
        this.entitiesById.remove(entity.getId());
    }

    @Nullable
    public T getEntity(int id) {
        return (T)((EntityLike)this.entitiesById.get(id));
    }

    @Nullable
    public T getEntity(UUID uuid) {
        return (T)((EntityLike)this.entitiesByUuid.get(uuid));
    }

    public int getEntityCount() {
        return this.entitiesByUuid.size();
    }
}

