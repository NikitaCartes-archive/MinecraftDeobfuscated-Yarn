/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DataTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object2IntMap<Class<? extends Entity>> TRACKED_ENTITIES = new Object2IntOpenHashMap<Class<? extends Entity>>();
    private static final int MAX_DATA_VALUE_ID = 254;
    private final Entity trackedEntity;
    private final Int2ObjectMap<Entry<?>> entries = new Int2ObjectOpenHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean dirty;

    public DataTracker(Entity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public static <T> TrackedData<T> registerData(Class<? extends Entity> entityClass, TrackedDataHandler<T> dataHandler) {
        int i;
        if (LOGGER.isDebugEnabled()) {
            try {
                Class<?> class_ = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
                if (!class_.equals(entityClass)) {
                    LOGGER.debug("defineId called for: {} from {}", entityClass, class_, new RuntimeException());
                }
            } catch (ClassNotFoundException class_) {
                // empty catch block
            }
        }
        if (TRACKED_ENTITIES.containsKey(entityClass)) {
            i = TRACKED_ENTITIES.getInt(entityClass) + 1;
        } else {
            int j = 0;
            Class<? extends Entity> class2 = entityClass;
            while (class2 != Entity.class) {
                if (!TRACKED_ENTITIES.containsKey(class2 = class2.getSuperclass())) continue;
                j = TRACKED_ENTITIES.getInt(class2) + 1;
                break;
            }
            i = j;
        }
        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
        }
        TRACKED_ENTITIES.put(entityClass, i);
        return dataHandler.create(i);
    }

    public <T> void startTracking(TrackedData<T> key, T initialValue) {
        int i = key.getId();
        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
        }
        if (this.entries.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        }
        if (TrackedDataHandlerRegistry.getId(key.getType()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + key.getType() + " for " + i + "!");
        }
        this.addTrackedData(key, initialValue);
    }

    private <T> void addTrackedData(TrackedData<T> key, T value) {
        Entry<T> entry = new Entry<T>(key, value);
        this.lock.writeLock().lock();
        this.entries.put(key.getId(), (Entry<?>)entry);
        this.lock.writeLock().unlock();
    }

    private <T> Entry<T> getEntry(TrackedData<T> key) {
        Entry entry;
        this.lock.readLock().lock();
        try {
            entry = (Entry)this.entries.get(key.getId());
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting synched entity data");
            CrashReportSection crashReportSection = crashReport.addElement("Synched entity data");
            crashReportSection.add("Data ID", key);
            throw new CrashException(crashReport);
        } finally {
            this.lock.readLock().unlock();
        }
        return entry;
    }

    public <T> T get(TrackedData<T> data) {
        return this.getEntry(data).get();
    }

    public <T> void set(TrackedData<T> key, T value) {
        Entry<T> entry = this.getEntry(key);
        if (ObjectUtils.notEqual(value, entry.get())) {
            entry.set(value);
            this.trackedEntity.onTrackedDataSet(key);
            entry.setDirty(true);
            this.dirty = true;
        }
    }

    public boolean isDirty() {
        return this.dirty;
    }

    @Nullable
    public List<SerializedEntry<?>> getDirtyEntries() {
        ArrayList list = null;
        if (this.dirty) {
            this.lock.readLock().lock();
            for (Entry entry : this.entries.values()) {
                if (!entry.isDirty()) continue;
                entry.setDirty(false);
                if (list == null) {
                    list = new ArrayList();
                }
                list.add(entry.toSerialized());
            }
            this.lock.readLock().unlock();
        }
        this.dirty = false;
        return list;
    }

    @Nullable
    public List<SerializedEntry<?>> getChangedEntries() {
        ArrayList list = null;
        this.lock.readLock().lock();
        for (Entry entry : this.entries.values()) {
            if (entry.isUnchanged()) continue;
            if (list == null) {
                list = new ArrayList();
            }
            list.add(entry.toSerialized());
        }
        this.lock.readLock().unlock();
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeUpdatedEntries(List<SerializedEntry<?>> entries) {
        this.lock.writeLock().lock();
        try {
            for (SerializedEntry<?> serializedEntry : entries) {
                Entry entry = (Entry)this.entries.get(serializedEntry.id);
                if (entry == null) continue;
                this.copyToFrom(entry, serializedEntry);
                this.trackedEntity.onTrackedDataSet(entry.getData());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        this.dirty = true;
    }

    private <T> void copyToFrom(Entry<T> to, SerializedEntry<?> from) {
        if (!Objects.equals(from.handler(), to.data.getType())) {
            throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", to.data.getId(), this.trackedEntity, to.value, to.value.getClass(), from.value, from.value.getClass()));
        }
        to.set(from.value);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public void clearDirty() {
        this.dirty = false;
        this.lock.readLock().lock();
        for (Entry entry : this.entries.values()) {
            entry.setDirty(false);
        }
        this.lock.readLock().unlock();
    }

    public static class Entry<T> {
        final TrackedData<T> data;
        T value;
        private final T initialValue;
        private boolean dirty;

        public Entry(TrackedData<T> data, T value) {
            this.data = data;
            this.initialValue = value;
            this.value = value;
            this.dirty = true;
        }

        public TrackedData<T> getData() {
            return this.data;
        }

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return this.value;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public boolean isUnchanged() {
            return this.initialValue.equals(this.value);
        }

        public SerializedEntry<T> toSerialized() {
            return SerializedEntry.of(this.data, this.value);
        }
    }

    public record SerializedEntry<T>(int id, TrackedDataHandler<T> handler, T value) {
        public static <T> SerializedEntry<T> of(TrackedData<T> data, T value) {
            TrackedDataHandler<T> trackedDataHandler = data.getType();
            return new SerializedEntry<T>(data.getId(), trackedDataHandler, trackedDataHandler.copy(value));
        }

        public void write(PacketByteBuf buf) {
            int i = TrackedDataHandlerRegistry.getId(this.handler);
            if (i < 0) {
                throw new EncoderException("Unknown serializer type " + this.handler);
            }
            buf.writeByte(this.id);
            buf.writeVarInt(i);
            this.handler.write(buf, this.value);
        }

        public static SerializedEntry<?> fromBuf(PacketByteBuf buf, int id) {
            int i = buf.readVarInt();
            TrackedDataHandler<?> trackedDataHandler = TrackedDataHandlerRegistry.get(i);
            if (trackedDataHandler == null) {
                throw new DecoderException("Unknown serializer type " + i);
            }
            return SerializedEntry.fromBuf(buf, id, trackedDataHandler);
        }

        private static <T> SerializedEntry<T> fromBuf(PacketByteBuf buf, int id, TrackedDataHandler<T> handler) {
            return new SerializedEntry<T>(id, handler, handler.read(buf));
        }
    }
}

