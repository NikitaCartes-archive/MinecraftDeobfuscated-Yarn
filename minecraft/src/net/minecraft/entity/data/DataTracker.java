package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;
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
	private boolean field_13330 = true;
	private boolean dirty;

	public DataTracker(Entity entity) {
		this.trackedEntity = entity;
	}

	public static <T> TrackedData<T> registerData(Class<? extends Entity> class_, TrackedDataHandler<T> trackedDataHandler) {
		if (LOGGER.isDebugEnabled()) {
			try {
				Class<?> class2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
				if (!class2.equals(class_)) {
					LOGGER.debug("defineId called for: {} from {}", class_, class2, new RuntimeException());
				}
			} catch (ClassNotFoundException var5) {
			}
		}

		int i;
		if (trackedEntities.containsKey(class_)) {
			i = (Integer)trackedEntities.get(class_) + 1;
		} else {
			int j = 0;
			Class<?> class3 = class_;

			while (class3 != Entity.class) {
				class3 = class3.getSuperclass();
				if (trackedEntities.containsKey(class3)) {
					j = (Integer)trackedEntities.get(class3) + 1;
					break;
				}
			}

			i = j;
		}

		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else {
			trackedEntities.put(class_, i);
			return trackedDataHandler.create(i);
		}
	}

	public <T> void startTracking(TrackedData<T> trackedData, T object) {
		int i = trackedData.getId();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else if (this.entries.containsKey(i)) {
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		} else if (TrackedDataHandlerRegistry.getId(trackedData.getType()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + trackedData.getType() + " for " + i + "!");
		} else {
			this.method_12776(trackedData, object);
		}
	}

	private <T> void method_12776(TrackedData<T> trackedData, T object) {
		DataTracker.Entry<T> entry = new DataTracker.Entry<>(trackedData, object);
		this.lock.writeLock().lock();
		this.entries.put(trackedData.getId(), entry);
		this.field_13330 = false;
		this.lock.writeLock().unlock();
	}

	private <T> DataTracker.Entry<T> method_12783(TrackedData<T> trackedData) {
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
		return this.method_12783(trackedData).get();
	}

	public <T> void set(TrackedData<T> trackedData, T object) {
		DataTracker.Entry<T> entry = this.method_12783(trackedData);
		if (ObjectUtils.notEqual(object, entry.get())) {
			entry.set(object);
			this.trackedEntity.onTrackedDataSet(trackedData);
			entry.setDirty(true);
			this.dirty = true;
		}
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void serializePacket(List<DataTracker.Entry<?>> list, PacketByteBuf packetByteBuf) throws IOException {
		if (list != null) {
			int i = 0;

			for (int j = list.size(); i < j; i++) {
				method_12782(packetByteBuf, (DataTracker.Entry)list.get(i));
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

					list.add(entry.method_12798());
				}
			}

			this.lock.readLock().unlock();
		}

		this.dirty = false;
		return list;
	}

	public void serializePacket(PacketByteBuf packetByteBuf) throws IOException {
		this.lock.readLock().lock();

		for (DataTracker.Entry<?> entry : this.entries.values()) {
			method_12782(packetByteBuf, entry);
		}

		this.lock.readLock().unlock();
		packetByteBuf.writeByte(255);
	}

	@Nullable
	public List<DataTracker.Entry<?>> getAllEntries() {
		List<DataTracker.Entry<?>> list = null;
		this.lock.readLock().lock();

		for (DataTracker.Entry<?> entry : this.entries.values()) {
			if (list == null) {
				list = Lists.<DataTracker.Entry<?>>newArrayList();
			}

			list.add(entry.method_12798());
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static <T> void method_12782(PacketByteBuf packetByteBuf, DataTracker.Entry<T> entry) throws IOException {
		TrackedData<T> trackedData = entry.method_12797();
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

			list.add(method_12777(packetByteBuf, i, trackedDataHandler));
		}

		return list;
	}

	private static <T> DataTracker.Entry<T> method_12777(PacketByteBuf packetByteBuf, int i, TrackedDataHandler<T> trackedDataHandler) {
		return new DataTracker.Entry<>(trackedDataHandler.create(i), trackedDataHandler.read(packetByteBuf));
	}

	@Environment(EnvType.CLIENT)
	public void method_12779(List<DataTracker.Entry<?>> list) {
		this.lock.writeLock().lock();

		for (DataTracker.Entry<?> entry : list) {
			DataTracker.Entry<?> entry2 = (DataTracker.Entry<?>)this.entries.get(entry.method_12797().getId());
			if (entry2 != null) {
				this.method_12785(entry2, entry);
				this.trackedEntity.onTrackedDataSet(entry.method_12797());
			}
		}

		this.lock.writeLock().unlock();
		this.dirty = true;
	}

	@Environment(EnvType.CLIENT)
	protected <T> void method_12785(DataTracker.Entry<T> entry, DataTracker.Entry<?> entry2) {
		entry.set((T)entry2.get());
	}

	public boolean method_12790() {
		return this.field_13330;
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
		private final TrackedData<T> field_13337;
		private T value;
		private boolean dirty;

		public Entry(TrackedData<T> trackedData, T object) {
			this.field_13337 = trackedData;
			this.value = object;
			this.dirty = true;
		}

		public TrackedData<T> method_12797() {
			return this.field_13337;
		}

		public void set(T object) {
			this.value = object;
		}

		public T get() {
			return this.value;
		}

		public boolean isDirty() {
			return this.dirty;
		}

		public void setDirty(boolean bl) {
			this.dirty = bl;
		}

		public DataTracker.Entry<T> method_12798() {
			return new DataTracker.Entry<>(this.field_13337, this.field_13337.getType().copy(this.value));
		}
	}
}
