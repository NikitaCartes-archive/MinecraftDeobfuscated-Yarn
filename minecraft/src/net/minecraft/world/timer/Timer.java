package net.minecraft.world.timer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;

public class Timer<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String CALLBACK_KEY = "Callback";
	private static final String NAME_KEY = "Name";
	private static final String TRIGGER_TIME_KEY = "TriggerTime";
	private final TimerCallbackSerializer<T> callback;
	private final Queue<Timer.Event<T>> events = new PriorityQueue(createEventComparator());
	private UnsignedLong eventCounter = UnsignedLong.ZERO;
	private final Table<String, Long, Timer.Event<T>> eventsByName = HashBasedTable.create();

	private static <T> Comparator<Timer.Event<T>> createEventComparator() {
		return Comparator.comparingLong(event -> event.triggerTime).thenComparing(event -> event.id);
	}

	public Timer(TimerCallbackSerializer<T> timerCallbackSerializer, Stream<? extends Dynamic<?>> nbts) {
		this(timerCallbackSerializer);
		this.events.clear();
		this.eventsByName.clear();
		this.eventCounter = UnsignedLong.ZERO;
		nbts.forEach(nbt -> {
			NbtElement nbtElement = nbt.convert(NbtOps.INSTANCE).getValue();
			if (nbtElement instanceof NbtCompound nbtCompound) {
				this.addEvent(nbtCompound);
			} else {
				LOGGER.warn("Invalid format of events: {}", nbtElement);
			}
		});
	}

	public Timer(TimerCallbackSerializer<T> timerCallbackSerializer) {
		this.callback = timerCallbackSerializer;
	}

	public void processEvents(T server, long time) {
		while (true) {
			Timer.Event<T> event = (Timer.Event<T>)this.events.peek();
			if (event == null || event.triggerTime > time) {
				return;
			}

			this.events.remove();
			this.eventsByName.remove(event.name, time);
			event.callback.call(server, this, time);
		}
	}

	public void setEvent(String name, long triggerTime, TimerCallback<T> callback) {
		if (!this.eventsByName.contains(name, triggerTime)) {
			this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
			Timer.Event<T> event = new Timer.Event<>(triggerTime, this.eventCounter, name, callback);
			this.eventsByName.put(name, triggerTime, event);
			this.events.add(event);
		}
	}

	public int remove(String name) {
		Collection<Timer.Event<T>> collection = this.eventsByName.row(name).values();
		collection.forEach(this.events::remove);
		int i = collection.size();
		collection.clear();
		return i;
	}

	public Set<String> getEventNames() {
		return Collections.unmodifiableSet(this.eventsByName.rowKeySet());
	}

	private void addEvent(NbtCompound nbt) {
		NbtCompound nbtCompound = nbt.getCompound("Callback");
		TimerCallback<T> timerCallback = this.callback.deserialize(nbtCompound);
		if (timerCallback != null) {
			String string = nbt.getString("Name");
			long l = nbt.getLong("TriggerTime");
			this.setEvent(string, l, timerCallback);
		}
	}

	private NbtCompound serialize(Timer.Event<T> event) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", event.name);
		nbtCompound.putLong("TriggerTime", event.triggerTime);
		nbtCompound.put("Callback", this.callback.serialize(event.callback));
		return nbtCompound;
	}

	public NbtList toNbt() {
		NbtList nbtList = new NbtList();
		this.events.stream().sorted(createEventComparator()).map(this::serialize).forEach(nbtList::add);
		return nbtList;
	}

	public static class Event<T> {
		public final long triggerTime;
		public final UnsignedLong id;
		public final String name;
		public final TimerCallback<T> callback;

		Event(long triggerTime, UnsignedLong id, String name, TimerCallback<T> callback) {
			this.triggerTime = triggerTime;
			this.id = id;
			this.name = name;
			this.callback = callback;
		}
	}
}
