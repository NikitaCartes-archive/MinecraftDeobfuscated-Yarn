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

	public void processEvents(T object, long l) {
		while (true) {
			Timer.Event<T> event = (Timer.Event<T>)this.events.peek();
			if (event == null || event.triggerTime > l) {
				return;
			}

			this.events.remove();
			this.eventsByName.remove(event.name);
			event.callback.method_974(object, this, l);
		}
	}

	private void setEvent(String string, long l, TimerCallback<T> timerCallback) {
		this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
		Timer.Event<T> event = new Timer.Event<>(l, this.eventCounter, string, timerCallback);
		this.eventsByName.put(string, event);
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

	public void replaceEvent(String string, long l, TimerCallback<T> timerCallback) {
		Timer.Event<T> event = (Timer.Event<T>)this.eventsByName.remove(string);
		if (event != null) {
			this.events.remove(event);
		}

		this.setEvent(string, l, timerCallback);
	}

	private void method_986(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag.getCompound("Callback");
		TimerCallback<T> timerCallback = this.callback.method_972(compoundTag2);
		if (timerCallback != null) {
			String string = compoundTag.getString("Name");
			long l = compoundTag.getLong("TriggerTime");
			this.addEvent(string, l, timerCallback);
		}
	}

	public void method_979(ListTag listTag) {
		this.events.clear();
		this.eventsByName.clear();
		this.eventCounter = UnsignedLong.ZERO;
		if (!listTag.isEmpty()) {
			if (listTag.getListType() != 10) {
				LOGGER.warn("Invalid format of events: " + listTag);
			} else {
				for (Tag tag : listTag) {
					this.method_986((CompoundTag)tag);
				}
			}
		}
	}

	private CompoundTag method_980(Timer.Event<T> event) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", event.name);
		compoundTag.putLong("TriggerTime", event.triggerTime);
		compoundTag.method_10566("Callback", this.callback.method_973(event.callback));
		return compoundTag;
	}

	public ListTag method_982() {
		ListTag listTag = new ListTag();
		this.events.stream().sorted(createEventComparator()).map(this::method_980).forEach(listTag::add);
		return listTag;
	}

	public static class Event<T> {
		public final long triggerTime;
		public final UnsignedLong id;
		public final String name;
		public final TimerCallback<T> callback;

		private Event(long l, UnsignedLong unsignedLong, String string, TimerCallback<T> timerCallback) {
			this.triggerTime = l;
			this.id = unsignedLong;
			this.name = string;
			this.callback = timerCallback;
		}
	}
}
