package net.minecraft.client.report;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

/**
 * An implementation of {@link ChatLog} using a fixed-size array and {@code 0} as the
 * starting index. When adding a log, the index is incremented, and the message at the
 * index is overwritten. If the index goes above the array size, the array index wraps
 * around but the message index is still incremented.
 * 
 * <p>For example, if the size is {@code 10}, after adding the 10th item, the next index is
 * {@code 10} because the log is 0-indexed. However, the next message will be stored at
 * {@code messages[0]}. Use {@link #wrapIndex} to calculate the wrapped index.
 */
@Environment(EnvType.CLIENT)
public class ChatLogImpl implements ChatLog {
	private final ReceivedMessage[] messages;
	private int maxIndex = -1;
	private int minIndex = -1;

	public ChatLogImpl(int maxMessages) {
		this.messages = new ReceivedMessage[maxMessages];
	}

	@Override
	public void add(ReceivedMessage message) {
		int i = this.incrementIndex();
		this.messages[this.wrapIndex(i)] = message;
	}

	/**
	 * {@return the incremented index}
	 * 
	 * @implNote This always increments {@link #maxIndex}, and increments {@link #minIndex}
	 * only if the array is already full.
	 */
	private int incrementIndex() {
		int i = ++this.maxIndex;
		if (i >= this.messages.length) {
			this.minIndex++;
		} else {
			this.minIndex = 0;
		}

		return i;
	}

	@Nullable
	@Override
	public ReceivedMessage get(int index) {
		return this.contains(index) ? this.messages[this.wrapIndex(index)] : null;
	}

	/**
	 * {@return the message {@code index} wrapped for accessing the backing array}
	 */
	private int wrapIndex(int index) {
		return index % this.messages.length;
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
	public int clampWithOffset(int index, int offset) {
		return MathHelper.clamp(index + offset, this.minIndex, this.maxIndex);
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
