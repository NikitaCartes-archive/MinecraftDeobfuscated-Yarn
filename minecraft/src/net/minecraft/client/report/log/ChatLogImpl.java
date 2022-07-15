package net.minecraft.client.report.log;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * An implementation of {@link ChatLog} using a fixed-size array and {@code 0} as the
 * starting index. When adding a log, the index is incremented, and the entry at the
 * index is overwritten. If the index goes above the array size, the array index wraps
 * around but the entry index is still incremented.
 * 
 * <p>For example, if the size is {@code 10}, after adding the 10th item, the next index is
 * {@code 10} because the log is 0-indexed. However, the next message will be stored at
 * {@code messages[0]}. Use {@link #wrapIndex} to calculate the wrapped index.
 */
@Environment(EnvType.CLIENT)
public class ChatLogImpl implements ChatLog {
	private final ChatLogEntry[] entries;
	private int maxIndex = -1;
	private int minIndex = -1;

	public ChatLogImpl(int maxEntries) {
		this.entries = new ChatLogEntry[maxEntries];
	}

	@Override
	public void add(ChatLogEntry entry) {
		int i = this.incrementIndex();
		this.entries[this.wrapIndex(i)] = entry;
	}

	/**
	 * {@return the incremented index}
	 * 
	 * @implNote This always increments {@link #maxIndex}, and increments {@link #minIndex}
	 * only if the array is already full.
	 */
	private int incrementIndex() {
		int i = ++this.maxIndex;
		if (i >= this.entries.length) {
			this.minIndex++;
		} else {
			this.minIndex = 0;
		}

		return i;
	}

	@Nullable
	@Override
	public ChatLogEntry get(int index) {
		return this.contains(index) ? this.entries[this.wrapIndex(index)] : null;
	}

	/**
	 * {@return the entry {@code index} wrapped for accessing the backing array}
	 */
	private int wrapIndex(int index) {
		return index % this.entries.length;
	}

	@Override
	public boolean contains(int index) {
		return index >= this.minIndex && index <= this.maxIndex;
	}

	@Override
	public int getOffsetIndex(int index, int offset) {
		int i = index + offset;
		return this.contains(i) ? i : -1;
	}

	@Override
	public int getMaxIndex() {
		return this.maxIndex;
	}

	@Override
	public int getMinIndex() {
		return this.minIndex;
	}
}
