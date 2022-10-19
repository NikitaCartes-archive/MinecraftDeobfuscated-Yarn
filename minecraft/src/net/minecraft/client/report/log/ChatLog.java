package net.minecraft.client.report.log;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A chat log holds received message entries with sequential indices, where
 * newer entries receive bigger indices.
 * 
 * <p>Currently there is only one type of entries; {@link ReceivedMessage}, which is
 * an entry for full chat or game messages.
 */
@Environment(EnvType.CLIENT)
public class ChatLog {
	private final ChatLogEntry[] entries;
	private int currentIndex;

	public ChatLog(int size) {
		this.entries = new ChatLogEntry[size];
	}

	/**
	 * Adds {@code entry} to the log.
	 */
	public void add(ChatLogEntry entry) {
		this.entries[this.wrapIndex(this.currentIndex++)] = entry;
	}

	/**
	 * {@return the entry with index {@code index}, or {@code null} if there is no
	 * such entry in the log}
	 */
	@Nullable
	public ChatLogEntry get(int index) {
		return index >= this.getMinIndex() && index <= this.getMaxIndex() ? this.entries[this.wrapIndex(index)] : null;
	}

	private int wrapIndex(int index) {
		return index % this.entries.length;
	}

	public int getMinIndex() {
		return Math.max(this.currentIndex - this.entries.length, 0);
	}

	public int getMaxIndex() {
		return this.currentIndex - 1;
	}
}
