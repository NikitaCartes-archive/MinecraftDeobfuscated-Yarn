/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * A storage of entities that supports modification during iteration.
 * 
 * <p>The entities are stored by their network IDs.
 * 
 * @see EntityList#forEach(Consumer)
 */
public class EntityList {
    private Int2ObjectMap<Entity> entities = new Int2ObjectLinkedOpenHashMap<Entity>();
    private Int2ObjectMap<Entity> temp = new Int2ObjectLinkedOpenHashMap<Entity>();
    @Nullable
    private Int2ObjectMap<Entity> iterating;

    /**
     * Ensures that the modified {@code entities} map is not currently iterated.
     * If {@code entities} is iterated, this moves its value to {@code temp} so
     * modification to {@code entities} is safe.
     */
    private void ensureSafe() {
        if (this.iterating == this.entities) {
            this.temp.clear();
            for (Int2ObjectMap.Entry entry : Int2ObjectMaps.fastIterable(this.entities)) {
                this.temp.put(entry.getIntKey(), (Entity)entry.getValue());
            }
            Int2ObjectMap<Entity> int2ObjectMap = this.entities;
            this.entities = this.temp;
            this.temp = int2ObjectMap;
        }
    }

    public void add(Entity entity) {
        this.ensureSafe();
        this.entities.put(entity.getId(), entity);
    }

    public void remove(Entity entity) {
        this.ensureSafe();
        this.entities.remove(entity.getId());
    }

    public boolean has(Entity entity) {
        return this.entities.containsKey(entity.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    /**
     * Runs an {@code action} on every entity in this storage.
     * 
     * <p>If this storage is updated during the iteration, the iteration will
     * not be updated to reflect updated contents. For example, if an entity
     * is added by the {@code action}, the {@code action} won't run on that
     * entity later.
     * 
     * @throws UnsupportedOperationException if this is called before an iteration
     * has finished, such as within the {@code action} or from another thread
     */
    public void forEach(Consumer<Entity> action) {
        if (this.iterating != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        }
        this.iterating = this.entities;
        try {
            for (Entity entity : this.entities.values()) {
                action.accept(entity);
            }
        } finally {
            this.iterating = null;
        }
    }
}

