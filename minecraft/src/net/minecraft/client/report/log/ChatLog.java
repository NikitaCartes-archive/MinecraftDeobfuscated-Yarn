package net.minecraft.client.report.log;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
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

	public static Codec<ChatLog> createCodec(int maxSize) {
		return Codec.list(ChatLogEntry.CODEC)
			.comapFlatMap(
				entries -> entries.size() > maxSize
						? DataResult.error("Expected: a buffer of size less than or equal to " + maxSize + " but: " + entries.size() + " is greater than " + maxSize)
						: DataResult.success(new ChatLog(maxSize, entries)),
				ChatLog::toList
			);
	}

	public ChatLog(int maxSize) {
		this.entries = new ChatLogEntry[maxSize];
	}

	private ChatLog(int size, List<ChatLogEntry> entries) {
		this.entries = (ChatLogEntry[])entries.toArray(ChatLogEntry[]::new);
		this.currentIndex = entries.size();
	}

	private List<ChatLogEntry> toList() {
		List<ChatLogEntry> list = new ArrayList(this.size());

		for (int i = this.getMinIndex(); i <= this.getMaxIndex(); i++) {
			list.add(this.get(i));
		}

		return list;
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

	private int size() {
		return this.getMaxIndex() - this.getMinIndex() + 1;
	}
}
