package net.minecraft.network.message;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Collects message signatures on the server to make a message chain.
 */
public class MessageSignatureStorage {
	private static final int MAX_ENTRIES = 128;
	private final MessageSignatureData[] signatures;

	public MessageSignatureStorage(int maxEntries) {
		this.signatures = new MessageSignatureData[maxEntries];
	}

	public static MessageSignatureStorage create() {
		return new MessageSignatureStorage(128);
	}

	public MessageSignatureData.Packer getPacker() {
		return signature -> {
			for (int i = 0; i < this.signatures.length; i++) {
				if (signature.equals(this.signatures[i])) {
					return i;
				}
			}

			return -1;
		};
	}

	public MessageSignatureData.Unpacker getUnpacker() {
		return index -> this.signatures[index];
	}

	public void add(SignedMessage message) {
		List<MessageSignatureData> list = message.signedBody().lastSeenMessages().entries();
		ArrayDeque<MessageSignatureData> arrayDeque = new ArrayDeque(list.size() + 1);
		arrayDeque.addAll(list);
		MessageSignatureData messageSignatureData = message.signature();
		if (messageSignatureData != null) {
			arrayDeque.add(messageSignatureData);
		}

		this.addFrom(arrayDeque);
	}

	@VisibleForTesting
	void addFrom(List<MessageSignatureData> signatures) {
		this.addFrom(new ArrayDeque(signatures));
	}

	private void addFrom(ArrayDeque<MessageSignatureData> deque) {
		Set<MessageSignatureData> set = new ObjectOpenHashSet<>(deque);

		for (int i = 0; !deque.isEmpty() && i < this.signatures.length; i++) {
			MessageSignatureData messageSignatureData = this.signatures[i];
			this.signatures[i] = (MessageSignatureData)deque.removeLast();
			if (messageSignatureData != null && !set.contains(messageSignatureData)) {
				deque.addFirst(messageSignatureData);
			}
		}
	}
}
