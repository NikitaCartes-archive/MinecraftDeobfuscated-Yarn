/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DataTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<? extends Entity>, Integer> TRACKED_ENTITIES = Maps.newHashMap();
    private final Entity trackedEntity;
    private final Map<Integer, Entry<?>> entries = Maps.newHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean empty = true;
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
                    LOGGER.debug("defineId called for: {} from {}", (Object)entityClass, (Object)class_, (Object)new RuntimeException());
                }
            } catch (ClassNotFoundException class_) {
                // empty catch block
            }
        }
        if (TRACKED_ENTITIES.containsKey(entityClass)) {
            i = TRACKED_ENTITIES.get(entityClass) + 1;
        } else {
            int j = 0;
            Class<? extends Entity> class2 = entityClass;
            while (class2 != Entity.class) {
                if (!TRACKED_ENTITIES.containsKey(class2 = class2.getSuperclass())) continue;
                j = TRACKED_ENTITIES.get(class2) + 1;
                break;
            }
            i = j;
        }
        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
        }
        TRACKED_ENTITIES.put(entityClass, i);
        return dataHandler.create(i);
    }

    public <T> void startTracking(TrackedData<T> key, T initialValue) {
        int i = key.getId();
        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
        }
        if (this.entries.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        }
        if (TrackedDataHandlerRegistry.getId(key.getType()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + key.getType() + " for " + i + "!");
        }
        this.addTrackedData(key, initialValue);
    }

    private <T> void addTrackedData(TrackedData<T> trackedData, T object) {
        Entry<T> entry = new Entry<T>(trackedData, object);
        this.lock.writeLock().lock();
        this.entries.put(trackedData.getId(), entry);
        this.empty = false;
        this.lock.writeLock().unlock();
    }

    private <T> Entry<T> getEntry(TrackedData<T> trackedData) {
        Entry<?> entry;
        this.lock.readLock().lock();
        try {
            entry = this.entries.get(trackedData.getId());
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting synched entity data");
            CrashReportSection crashReportSection = crashReport.addElement("Synched entity data");
            crashReportSection.add("Data ID", trackedData);
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

    public static void entriesToPacket(List<Entry<?>> list, PacketByteBuf packetByteBuf) {
        if (list != null) {
            int j = list.size();
            for (int i = 0; i < j; ++i) {
                DataTracker.writeEntryToPacket(packetByteBuf, list.get(i));
            }
        }
        packetByteBuf.writeByte(255);
    }

    @Nullable
    public List<Entry<?>> getDirtyEntries() {
        ArrayList<Entry<?>> list = null;
        if (this.dirty) {
            this.lock.readLock().lock();
            for (Entry<?> entry : this.entries.values()) {
                if (!entry.isDirty()) continue;
                entry.setDirty(false);
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(entry.copy());
            }
            this.lock.readLock().unlock();
        }
        this.dirty = false;
        return list;
    }

    @Nullable
    public List<Entry<?>> getAllEntries() {
        ArrayList<Entry<?>> list = null;
        this.lock.readLock().lock();
        for (Entry<?> entry : this.entries.values()) {
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(entry.copy());
        }
        this.lock.readLock().unlock();
        return list;
    }

    private static <T> void writeEntryToPacket(PacketByteBuf buf, Entry<T> entry) {
        TrackedData<T> trackedData = entry.getData();
        int i = TrackedDataHandlerRegistry.getId(trackedData.getType());
        if (i < 0) {
            throw new EncoderException("Unknown serializer type " + trackedData.getType());
        }
        buf.writeByte(trackedData.getId());
        buf.writeVarInt(i);
        trackedData.getType().write(buf, entry.get());
    }

    @Nullable
    public static List<Entry<?>> deserializePacket(PacketByteBuf buf) {
        short i;
        ArrayList<Entry<?>> list = null;
        while ((i = buf.readUnsignedByte()) != 255) {
            int j;
            TrackedDataHandler<?> trackedDataHandler;
            if (list == null) {
                list = Lists.newArrayList();
            }
            if ((trackedDataHandler = TrackedDataHandlerRegistry.get(j = buf.readVarInt())) == null) {
                throw new DecoderException("Unknown serializer type " + j);
            }
            list.add(DataTracker.entryFromPacket(buf, i, trackedDataHandler));
        }
        return list;
    }

    private static <T> Entry<T> entryFromPacket(PacketByteBuf buf, int i, TrackedDataHandler<T> trackedDataHandler) {
        return new Entry<T>(trackedDataHandler.create(i), trackedDataHandler.read(buf));
    }

    @Environment(value=EnvType.CLIENT)
    public void writeUpdatedEntries(List<Entry<?>> list) {
        this.lock.writeLock().lock();
        for (Entry<?> entry : list) {
            Entry<?> entry2 = this.entries.get(entry.getData().getId());
            if (entry2 == null) continue;
            this.copyToFrom(entry2, entry);
            this.trackedEntity.onTrackedDataSet(entry.getData());
        }
        this.lock.writeLock().unlock();
        this.dirty = true;
    }

    @Environment(value=EnvType.CLIENT)
    private <T> void copyToFrom(Entry<T> entry, Entry<?> entry2) {
        if (!Objects.equals(((Entry)entry2).data.getType(), ((Entry)entry).data.getType())) {
            throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", ((Entry)entry).data.getId(), this.trackedEntity, ((Entry)entry).value, ((Entry)entry).value.getClass(), ((Entry)entry2).value, ((Entry)entry2).value.getClass()));
        }
        entry.set(entry2.get());
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void clearDirty() {
        this.dirty = false;
        this.lock.readLock().lock();
        for (Entry<?> entry : this.entries.values()) {
            entry.setDirty(false);
        }
        this.lock.readLock().unlock();
    }

    public static class Entry<T> {
        private final TrackedData<T> data;
        private T value;
        private boolean dirty;

        public Entry(TrackedData<T> data, T value) {
            this.data = data;
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

        public Entry<T> copy() {
            return new Entry<T>(this.data, this.data.getType().copy(this.value));
        }
    }
}

