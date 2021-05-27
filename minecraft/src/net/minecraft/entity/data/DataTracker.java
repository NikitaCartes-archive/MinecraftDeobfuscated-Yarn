package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Object2IntMap<Class<? extends Entity>> TRACKED_ENTITIES = new Object2IntOpenHashMap<>();
	private static final int field_33377 = 255;
	private static final int field_33378 = 254;
	private final Entity trackedEntity;
	private final Int2ObjectMap<DataTracker.Entry<?>> entries = new Int2ObjectOpenHashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
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

	private <T> void addTrackedData(TrackedData<T> trackedData, T object) {
		DataTracker.Entry<T> entry = new DataTracker.Entry<>(trackedData, object);
		this.lock.writeLock().lock();
		this.entries.put(trackedData.getId(), entry);
		this.empty = false;
		this.lock.writeLock().unlock();
	}

	private <T> DataTracker.Entry<T> getEntry(TrackedData<T> trackedData) {
		this.lock.readLock().lock();

		DataTracker.Entry<T> entry;
		try {
			entry = (DataTracker.Entry<T>)this.entries.get(trackedData.getId());
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Getting synched entity data");
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
		DataTracker.Entry<T> entry = this.getEntry(key);
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

	public static void entriesToPacket(@Nullable List<DataTracker.Entry<?>> list, PacketByteBuf packetByteBuf) {
		if (list != null) {
			for (DataTracker.Entry<?> entry : list) {
				writeEntryToPacket(packetByteBuf, entry);
			}
		}

		packetByteBuf.writeByte(255);
	}

	@Nullable
	public List<DataTracker.Entry<?>> getDirtyEntries() {
		List<DataTracker.Entry<?>> list = null;
		if (this.dirty) {
			this.lock.readLock().lock();

			for (DataTracker.Entry<?> entry : this.entries.values()) {
				if (entry.isDirty()) {
					entry.setDirty(false);
					if (list == null) {
						list = Lists.<DataTracker.Entry<?>>newArrayList();
					}

					list.add(entry.copy());
				}
			}

			this.lock.readLock().unlock();
		}

		this.dirty = false;
		return list;
	}

	@Nullable
	public List<DataTracker.Entry<?>> getAllEntries() {
		List<DataTracker.Entry<?>> list = null;
		this.lock.readLock().lock();

		for (DataTracker.Entry<?> entry : this.entries.values()) {
			if (list == null) {
				list = Lists.<DataTracker.Entry<?>>newArrayList();
			}

			list.add(entry.copy());
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static <T> void writeEntryToPacket(PacketByteBuf buf, DataTracker.Entry<T> entry) {
		TrackedData<T> trackedData = entry.getData();
		int i = TrackedDataHandlerRegistry.getId(trackedData.getType());
		if (i < 0) {
			throw new EncoderException("Unknown serializer type " + trackedData.getType());
		} else {
			buf.writeByte(trackedData.getId());
			buf.writeVarInt(i);
			trackedData.getType().write(buf, entry.get());
		}
	}

	@Nullable
	public static List<DataTracker.Entry<?>> deserializePacket(PacketByteBuf buf) {
		List<DataTracker.Entry<?>> list = null;

		int i;
		while ((i = buf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = Lists.<DataTracker.Entry<?>>newArrayList();
			}

			int j = buf.readVarInt();
			TrackedDataHandler<?> trackedDataHandler = TrackedDataHandlerRegistry.get(j);
			if (trackedDataHandler == null) {
				throw new DecoderException("Unknown serializer type " + j);
			}

			list.add(entryFromPacket(buf, i, trackedDataHandler));
		}

		return list;
	}

	private static <T> DataTracker.Entry<T> entryFromPacket(PacketByteBuf buf, int i, TrackedDataHandler<T> trackedDataHandler) {
		return new DataTracker.Entry<>(trackedDataHandler.create(i), trackedDataHandler.read(buf));
	}

	public void writeUpdatedEntries(List<DataTracker.Entry<?>> list) {
		this.lock.writeLock().lock();

		try {
			for (DataTracker.Entry<?> entry : list) {
				DataTracker.Entry<?> entry2 = this.entries.get(entry.getData().getId());
				if (entry2 != null) {
					this.copyToFrom(entry2, entry);
					this.trackedEntity.onTrackedDataSet(entry.getData());
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}

		this.dirty = true;
	}

	private <T> void copyToFrom(DataTracker.Entry<T> entry, DataTracker.Entry<?> entry2) {
		if (!Objects.equals(entry2.data.getType(), entry.data.getType())) {
			throw new IllegalStateException(
				String.format(
					"Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)",
					entry.data.getId(),
					this.trackedEntity,
					entry.value,
					entry.value.getClass(),
					entry2.value,
					entry2.value.getClass()
				)
			);
		} else {
			entry.set((T)entry2.get());
		}
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void clearDirty() {
		this.dirty = false;
		this.lock.readLock().lock();

		for (DataTracker.Entry<?> entry : this.entries.values()) {
			entry.setDirty(false);
		}

		this.lock.readLock().unlock();
	}

	public static class Entry<T> {
		final TrackedData<T> data;
		T value;
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

		public DataTracker.Entry<T> copy() {
			return new DataTracker.Entry<>(this.data, this.data.getType().copy(this.value));
		}
	}
}
