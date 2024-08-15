package net.minecraft.util.crash;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.Queue;
import net.minecraft.util.collection.ArrayListDeque;

public class SuppressedExceptionsTracker {
	private static final int MAX_QUEUE_SIZE = 8;
	private final Queue<SuppressedExceptionsTracker.Entry> queue = new ArrayListDeque<SuppressedExceptionsTracker.Entry>();
	private final Object2IntLinkedOpenHashMap<SuppressedExceptionsTracker.Key> keyToCount = new Object2IntLinkedOpenHashMap<>();

	private static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public synchronized void onSuppressedException(String location, Throwable exception) {
		long l = currentTimeMillis();
		String string = exception.getMessage();
		this.queue.add(new SuppressedExceptionsTracker.Entry(l, location, exception.getClass(), string));

		while (this.queue.size() > 8) {
			this.queue.remove();
		}

		SuppressedExceptionsTracker.Key key = new SuppressedExceptionsTracker.Key(location, exception.getClass());
		int i = this.keyToCount.getInt(key);
		this.keyToCount.putAndMoveToFirst(key, i + 1);
	}

	public synchronized String collect() {
		long l = currentTimeMillis();
		StringBuilder stringBuilder = new StringBuilder();
		if (!this.queue.isEmpty()) {
			stringBuilder.append("\n\t\tLatest entries:\n");

			for (SuppressedExceptionsTracker.Entry entry : this.queue) {
				stringBuilder.append("\t\t\t")
					.append(entry.location)
					.append(":")
					.append(entry.cls)
					.append(": ")
					.append(entry.message)
					.append(" (")
					.append(l - entry.timestampMs)
					.append("ms ago)")
					.append("\n");
			}
		}

		if (!this.keyToCount.isEmpty()) {
			if (stringBuilder.isEmpty()) {
				stringBuilder.append("\n");
			}

			stringBuilder.append("\t\tEntry counts:\n");

			for (Object2IntMap.Entry<SuppressedExceptionsTracker.Key> entry2 : Object2IntMaps.fastIterable(this.keyToCount)) {
				stringBuilder.append("\t\t\t")
					.append(((SuppressedExceptionsTracker.Key)entry2.getKey()).location)
					.append(":")
					.append(((SuppressedExceptionsTracker.Key)entry2.getKey()).cls)
					.append(" x ")
					.append(entry2.getIntValue())
					.append("\n");
			}
		}

		return stringBuilder.isEmpty() ? "~~NONE~~" : stringBuilder.toString();
	}

	static record Entry(long timestampMs, String location, Class<? extends Throwable> cls, String message) {
	}

	static record Key(String location, Class<? extends Throwable> cls) {
	}
}
