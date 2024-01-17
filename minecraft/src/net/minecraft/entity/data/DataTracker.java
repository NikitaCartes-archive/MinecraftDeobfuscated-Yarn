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
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class DataTracker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Object2IntMap<Class<? extends Entity>> TRACKED_ENTITIES = new Object2IntOpenHashMap<>();
	private static final int MAX_DATA_VALUE_ID = 254;
	private final Entity trackedEntity;
	private final Int2ObjectMap<DataTracker.Entry<?>> entries = new Int2ObjectOpenHashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean dirty;

	public DataTracker(Entity trackedEntity) {
		this.trackedEntity = trackedEntity;
	}

	public static <T> TrackedData<T> registerData(Class<? extends Entity> entityClass, TrackedDataHandler<T> dataHandler) {
		if (LOGGER.isDebugEnabled()) {
			try {
				Class<?> class_ = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
				if (!class_.equals(entityClass)) {
					LOGGER.debug("defineId called for: {} from {}", entityClass, class_, new RuntimeException());
				}
			} catch (ClassNotFoundException var5) {
			}
		}

		int i;
		if (TRACKED_ENTITIES.containsKey(entityClass)) {
			i = TRACKED_ENTITIES.getInt(entityClass) + 1;
		} else {
			int j = 0;
			Class<?> class2 = entityClass;

			while (class2 != Entity.class) {
				class2 = class2.getSuperclass();
				if (TRACKED_ENTITIES.containsKey(class2)) {
					j = TRACKED_ENTITIES.getInt(class2) + 1;
					break;
				}
			}

			i = j;
		}

		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
		} else {
			TRACKED_ENTITIES.put(entityClass, i);
			return dataHandler.create(i);
		}
	}

	public <T> void startTracking(TrackedData<T> key, T initialValue) {
		int i = key.getId();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
		} else if (this.entries.containsKey(i)) {
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		} else if (TrackedDataHandlerRegistry.getId(key.getType()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + key.getType() + " for " + i + "!");
		} else {
			this.addTrackedData(key, initialValue);
		}
	}

	private <T> void addTrackedData(TrackedData<T> key, T value) {
		DataTracker.Entry<T> entry = new DataTracker.Entry<>(key, value);
		this.lock.writeLock().lock();
		this.entries.put(key.getId(), entry);
		this.lock.writeLock().unlock();
	}

	public <T> boolean containsKey(TrackedData<T> key) {
		return this.entries.containsKey(key.getId());
	}

	private <T> DataTracker.Entry<T> getEntry(TrackedData<T> key) {
		this.lock.readLock().lock();

		DataTracker.Entry<T> entry;
		try {
			entry = (DataTracker.Entry<T>)this.entries.get(key.getId());
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Getting synched entity data");
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
		this.set(key, value, false);
	}

	public <T> void set(TrackedData<T> key, T value, boolean force) {
		DataTracker.Entry<T> entry = this.getEntry(key);
		if (force || ObjectUtils.notEqual(value, entry.get())) {
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
	public List<DataTracker.SerializedEntry<?>> getDirtyEntries() {
		List<DataTracker.SerializedEntry<?>> list = null;
		if (this.dirty) {
			this.lock.readLock().lock();

			for (DataTracker.Entry<?> entry : this.entries.values()) {
				if (entry.isDirty()) {
					entry.setDirty(false);
					if (list == null) {
						list = new ArrayList();
					}

					list.add(entry.toSerialized());
				}
			}

			this.lock.readLock().unlock();
		}

		this.dirty = false;
		return list;
	}

	@Nullable
	public List<DataTracker.SerializedEntry<?>> getChangedEntries() {
		List<DataTracker.SerializedEntry<?>> list = null;
		this.lock.readLock().lock();

		for (DataTracker.Entry<?> entry : this.entries.values()) {
			if (!entry.isUnchanged()) {
				if (list == null) {
					list = new ArrayList();
				}

				list.add(entry.toSerialized());
			}
		}

		this.lock.readLock().unlock();
		return list;
	}

	public void writeUpdatedEntries(List<DataTracker.SerializedEntry<?>> entries) {
		this.lock.writeLock().lock();

		try {
			for (DataTracker.SerializedEntry<?> serializedEntry : entries) {
				DataTracker.Entry<?> entry = this.entries.get(serializedEntry.id);
				if (entry != null) {
					this.copyToFrom(entry, serializedEntry);
					this.trackedEntity.onTrackedDataSet(entry.getData());
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}

		this.trackedEntity.onDataTrackerUpdate(entries);
	}

	private <T> void copyToFrom(DataTracker.Entry<T> to, DataTracker.SerializedEntry<?> from) {
		if (!Objects.equals(from.handler(), to.data.getType())) {
			throw new IllegalStateException(
				String.format(
					Locale.ROOT,
					"Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)",
					to.data.getId(),
					this.trackedEntity,
					to.value,
					to.value.getClass(),
					from.value,
					from.value.getClass()
				)
			);
		} else {
			to.set((T)from.value);
		}
	}

	public boolean isEmpty() {
		return this.entries.isEmpty();
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

		public DataTracker.SerializedEntry<T> toSerialized() {
			return DataTracker.SerializedEntry.of(this.data, this.value);
		}
	}

	public static record SerializedEntry<T>(int id, TrackedDataHandler<T> handler, T value) {

		public static <T> DataTracker.SerializedEntry<T> of(TrackedData<T> data, T value) {
			TrackedDataHandler<T> trackedDataHandler = data.getType();
			return new DataTracker.SerializedEntry<>(data.getId(), trackedDataHandler, trackedDataHandler.copy(value));
		}

		public void write(RegistryByteBuf registryByteBuf) {
			int i = TrackedDataHandlerRegistry.getId(this.handler);
			if (i < 0) {
				throw new EncoderException("Unknown serializer type " + this.handler);
			} else {
				registryByteBuf.writeByte(this.id);
				registryByteBuf.writeVarInt(i);
				this.handler.codec().encode(registryByteBuf, this.value);
			}
		}

		public static DataTracker.SerializedEntry<?> fromBuf(RegistryByteBuf registryByteBuf, int id) {
			int i = registryByteBuf.readVarInt();
			TrackedDataHandler<?> trackedDataHandler = TrackedDataHandlerRegistry.get(i);
			if (trackedDataHandler == null) {
				throw new DecoderException("Unknown serializer type " + i);
			} else {
				return fromBuf(registryByteBuf, id, trackedDataHandler);
			}
		}

		private static <T> DataTracker.SerializedEntry<T> fromBuf(RegistryByteBuf registryByteBuf, int id, TrackedDataHandler<T> handler) {
			return new DataTracker.SerializedEntry<>(id, handler, handler.codec().decode(registryByteBuf));
		}
	}
}
