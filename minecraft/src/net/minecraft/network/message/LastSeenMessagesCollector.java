package net.minecraft.network.message;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.BitSet;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Collects the message that are last seen by a client.
 * 
 * <p>The message, along with the "last received" message, forms an
 * "acknowledgment" of received messages. They are sent to the server
 * when the client has enough messages received or when they send a message.
 * 
 * @implNote The maximum amount of message entries are specified in the constructor.
 * The vanilla clients collect 5 entries. Calling {@link #add(MessageSignatureData, boolean)}
 * adds the message to the beginning of the entries list, and evicts the oldest message.
 * If there are entries with the same sender profile ID, the older entry will be replaced with
 * {@code null} instead of filling the hole.
 * 
 * @see AcknowledgmentValidator
 * @see LastSeenMessageList
 */
public class LastSeenMessagesCollector {
	private final AcknowledgedMessage[] acknowledgedMessages;
	private int nextIndex;
	private int messageCount;
	@Nullable
	private MessageSignatureData lastAdded;

	public LastSeenMessagesCollector(int size) {
		this.acknowledgedMessages = new AcknowledgedMessage[size];
	}

	public boolean add(MessageSignatureData signature, boolean displayed) {
		if (Objects.equals(signature, this.lastAdded)) {
			return false;
		} else {
			this.lastAdded = signature;
			this.add(displayed ? new AcknowledgedMessage(signature, true) : null);
			return true;
		}
	}

	private void add(@Nullable AcknowledgedMessage message) {
		int i = this.nextIndex;
		this.nextIndex = (i + 1) % this.acknowledgedMessages.length;
		this.messageCount++;
		this.acknowledgedMessages[i] = message;
	}

	public void remove(MessageSignatureData signature) {
		for (int i = 0; i < this.acknowledgedMessages.length; i++) {
			AcknowledgedMessage acknowledgedMessage = this.acknowledgedMessages[i];
			if (acknowledgedMessage != null && acknowledgedMessage.pending() && signature.equals(acknowledgedMessage.signature())) {
				this.acknowledgedMessages[i] = null;
				break;
			}
		}
	}

	public int resetMessageCount() {
		int i = this.messageCount;
		this.messageCount = 0;
		return i;
	}

	public LastSeenMessagesCollector.LastSeenMessages collect() {
		int i = this.resetMessageCount();
		BitSet bitSet = new BitSet(this.acknowledgedMessages.length);
		ObjectList<MessageSignatureData> objectList = new ObjectArrayList<>(this.acknowledgedMessages.length);

		for (int j = 0; j < this.acknowledgedMessages.length; j++) {
			int k = (this.nextIndex + j) % this.acknowledgedMessages.length;
			AcknowledgedMessage acknowledgedMessage = this.acknowledgedMessages[k];
			if (acknowledgedMessage != null) {
				bitSet.set(j, true);
				objectList.add(acknowledgedMessage.signature());
				this.acknowledgedMessages[k] = acknowledgedMessage.unmarkAsPending();
			}
		}

		LastSeenMessageList lastSeenMessageList = new LastSeenMessageList(objectList);
		LastSeenMessageList.Acknowledgment acknowledgment = new LastSeenMessageList.Acknowledgment(i, bitSet);
		return new LastSeenMessagesCollector.LastSeenMessages(lastSeenMessageList, acknowledgment);
	}

	public int getMessageCount() {
		return this.messageCount;
	}

	public static record LastSeenMessages(LastSeenMessageList lastSeen, LastSeenMessageList.Acknowledgment update) {
	}
}
