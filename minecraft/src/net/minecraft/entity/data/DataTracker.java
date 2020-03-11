package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private static final Map<Class<? extends Entity>, Integer> trackedEntities = Maps.<Class<? extends Entity>, Integer>newHashMap();
	private final Entity trackedEntity;
	private final Map<Integer, DataTracker.Entry<?>> entries = Maps.<Integer, DataTracker.Entry<?>>newHashMap();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
	private boolean dirty;

	public DataTracker(Entity entity) {
		this.trackedEntity = entity;
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
		if (trackedEntities.containsKey(entityClass)) {
			i = (Integer)trackedEntities.get(entityClass) + 1;
		} else {
			int j = 0;
			Class<?> class2 = entityClass;

			while (class2 != Entity.class) {
				class2 = class2.getSuperclass();
				if (trackedEntities.containsKey(class2)) {
					j = (Integer)trackedEntities.get(class2) + 1;
					break;
				}
			}

			i = j;
		}

		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else {
			trackedEntities.put(entityClass, i);
			return dataHandler.create(i);
		}
	}

	public <T> void startTracking(TrackedData<T> key, T initialValue) {
		int i = key.getId();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
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

	public <T> T get(TrackedData<T> trackedData) {
		return this.getEntry(trackedData).get();
	}

	public <T> void set(TrackedData<T> key, T object) {
		DataTracker.Entry<T> entry = this.getEntry(key);
		if (ObjectUtils.notEqual(object, entry.get())) {
			entry.set(object);
			this.trackedEntity.onTrackedDataSet(key);
			entry.setDirty(true);
			this.dirty = true;
		}
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void entriesToPacket(List<DataTracker.Entry<?>> list, PacketByteBuf packetByteBuf) throws IOException {
		if (list != null) {
			int i = 0;

			for (int j = list.size(); i < j; i++) {
				writeEntryToPacket(packetByteBuf, (DataTracker.Entry)list.get(i));
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

	private static <T> void writeEntryToPacket(PacketByteBuf packetByteBuf, DataTracker.Entry<T> entry) throws IOException {
		TrackedData<T> trackedData = entry.getData();
		int i = TrackedDataHandlerRegistry.getId(trackedData.getType());
		if (i < 0) {
			throw new EncoderException("Unknown serializer type " + trackedData.getType());
		} else {
			packetByteBuf.writeByte(trackedData.getId());
			packetByteBuf.writeVarInt(i);
			trackedData.getType().write(packetByteBuf, entry.get());
		}
	}

	@Nullable
	public static List<DataTracker.Entry<?>> deserializePacket(PacketByteBuf packetByteBuf) throws IOException {
		List<DataTracker.Entry<?>> list = null;

		int i;
		while ((i = packetByteBuf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = Lists.<DataTracker.Entry<?>>newArrayList();
			}

			int j = packetByteBuf.readVarInt();
			TrackedDataHandler<?> trackedDataHandler = TrackedDataHandlerRegistry.get(j);
			if (trackedDataHandler == null) {
				throw new DecoderException("Unknown serializer type " + j);
			}

			list.add(entryFromPacket(packetByteBuf, i, trackedDataHandler));
		}

		return list;
	}

	private static <T> DataTracker.Entry<T> entryFromPacket(PacketByteBuf packetByteBuf, int i, TrackedDataHandler<T> trackedDataHandler) {
		return new DataTracker.Entry<>(trackedDataHandler.create(i), trackedDataHandler.read(packetByteBuf));
	}

	@Environment(EnvType.CLIENT)
	public void writeUpdatedEntries(List<DataTracker.Entry<?>> list) {
		this.lock.writeLock().lock();

		for (DataTracker.Entry<?> entry : list) {
			DataTracker.Entry<?> entry2 = (DataTracker.Entry<?>)this.entries.get(entry.getData().getId());
			if (entry2 != null) {
				this.copyToFrom(entry2, entry);
				this.trackedEntity.onTrackedDataSet(entry.getData());
			}
		}

		this.lock.writeLock().unlock();
		this.dirty = true;
	}

	@Environment(EnvType.CLIENT)
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

		public DataTracker.Entry<T> copy() {
			return new DataTracker.Entry<>(this.data, this.data.getType().copy(this.value));
		}
	}
}
