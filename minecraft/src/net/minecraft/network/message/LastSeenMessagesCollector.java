package net.minecraft.network.message;

import java.util.Arrays;

/**
 * Collects the message that are last seen by a client.
 * 
 * <p>The message, along with the "last received" message, forms an
 * "acknowledgment" of received messages. They are sent to the server
 * when the client has enough messages received or when they send a message.
 * 
 * @implNote The maximum amount of message entries are specified in the constructor.
 * The vanilla clients collect 5 entries. Calling {@link #add} adds the message to
 * the beginning of the entries list, and evicts the oldest message. If there are
 * entries with the same sender profile ID, the older entry will be replaced with
 * {@code null} instead of filling the hole.
 * 
 * @see AcknowledgmentValidator
 * @see LastSeenMessageList
 */
public class LastSeenMessagesCollector {
	private final LastSeenMessageList.Entry[] entries;
	private int size;
	private LastSeenMessageList lastSeenMessages = LastSeenMessageList.EMPTY;

	public LastSeenMessagesCollector(int size) {
		this.entries = new LastSeenMessageList.Entry[size];
	}

	public void add(LastSeenMessageList.Entry entry) {
		LastSeenMessageList.Entry entry2 = entry;

		for (int i = 0; i < this.size; i++) {
			LastSeenMessageList.Entry entry3 = this.entries[i];
			this.entries[i] = entry2;
			entry2 = entry3;
			if (entry3.profileId().equals(entry.profileId())) {
				entry2 = null;
				break;
			}
		}

		if (entry2 != null && this.size < this.entries.length) {
			this.entries[this.size++] = entry2;
		}

		this.lastSeenMessages = new LastSeenMessageList(Arrays.asList((LastSeenMessageList.Entry[])Arrays.copyOf(this.entries, this.size)));
	}

	public LastSeenMessageList getLastSeenMessages() {
		return this.lastSeenMessages;
	}
}
