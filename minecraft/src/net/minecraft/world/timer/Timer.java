package net.minecraft.world.timer;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Timer<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final TimerCallbackSerializer<T> callback;
	private final Queue<Timer.Event<T>> events = new PriorityQueue(createEventComparator());
	private UnsignedLong eventCounter = UnsignedLong.ZERO;
	private final Map<String, Timer.Event<T>> eventsByName = Maps.<String, Timer.Event<T>>newHashMap();

	private static <T> Comparator<Timer.Event<T>> createEventComparator() {
		return (event, event2) -> {
			int i = Long.compare(event.triggerTime, event2.triggerTime);
			return i != 0 ? i : event.id.compareTo(event2.id);
		};
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
			this.eventsByName.remove(event.name);
			event.callback.call(server, this, time);
		}
	}

	private void setEvent(String name, long triggerTime, TimerCallback<T> callback) {
		this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
		Timer.Event<T> event = new Timer.Event<>(triggerTime, this.eventCounter, name, callback);
		this.eventsByName.put(name, event);
		this.events.add(event);
	}

	public boolean addEvent(String string, long l, TimerCallback<T> timerCallback) {
		if (this.eventsByName.containsKey(string)) {
			return false;
		} else {
			this.setEvent(string, l, timerCallback);
			return true;
		}
	}

	public void replaceEvent(String name, long triggerTime, TimerCallback<T> callback) {
		Timer.Event<T> event = (Timer.Event<T>)this.eventsByName.remove(name);
		if (event != null) {
			this.events.remove(event);
		}

		this.setEvent(name, triggerTime, callback);
	}

	private void addEvent(CompoundTag tag) {
		CompoundTag compoundTag = tag.getCompound("Callback");
		TimerCallback<T> timerCallback = this.callback.deserialize(compoundTag);
		if (timerCallback != null) {
			String string = tag.getString("Name");
			long l = tag.getLong("TriggerTime");
			this.addEvent(string, l, timerCallback);
		}
	}

	public void fromTag(ListTag tag) {
		this.events.clear();
		this.eventsByName.clear();
		this.eventCounter = UnsignedLong.ZERO;
		if (!tag.isEmpty()) {
			if (tag.getElementType() != 10) {
				LOGGER.warn("Invalid format of events: " + tag);
			} else {
				for (Tag tag2 : tag) {
					this.addEvent((CompoundTag)tag2);
				}
			}
		}
	}

	private CompoundTag serialize(Timer.Event<T> event) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", event.name);
		compoundTag.putLong("TriggerTime", event.triggerTime);
		compoundTag.put("Callback", this.callback.serialize(event.callback));
		return compoundTag;
	}

	public ListTag toTag() {
		ListTag listTag = new ListTag();
		this.events.stream().sorted(createEventComparator()).map(this::serialize).forEach(listTag::add);
		return listTag;
	}

	public static class Event<T> {
		public final long triggerTime;
		public final UnsignedLong id;
		public final String name;
		public final TimerCallback<T> callback;

		private Event(long triggerTime, UnsignedLong id, String name, TimerCallback<T> callback) {
			this.triggerTime = triggerTime;
			this.id = id;
			this.name = name;
			this.callback = callback;
		}
	}
}
