package net.minecraft.network.message;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A class that validates the clients' message acknowledgment.
 * 
 * <p>When clients receive or send messages, they send "acknowledgments" to the server,
 * containing messages they've last seen or received. If there are too many messages
 * waiting for message acknowledgments (more than {@value
 * net.minecraft.server.network.ServerPlayNetworkHandler#MAX_PENDING_ACKNOWLEDGMENTS}),
 * or if the acknowledgment is incorrect, the client will be disconnected.
 */
public class AcknowledgmentValidator {
	private static final int UNKNOWN = Integer.MIN_VALUE;
	private LastSeenMessageList prevValidated = LastSeenMessageList.EMPTY;
	private final ObjectList<LastSeenMessageList.Entry> pending = new ObjectArrayList<>();

	public void addPending(LastSeenMessageList.Entry entry) {
		this.pending.add(entry);
	}

	public int getPendingCount() {
		return this.pending.size();
	}

	/**
	 * {@return whether {@code messages} have multiple entries with the same profile ID}
	 * 
	 * <p>If {@code true}, validation fails.
	 */
	private boolean hasDuplicateProfiles(LastSeenMessageList messages) {
		Set<UUID> set = new HashSet(messages.entries().size());

		for (LastSeenMessageList.Entry entry : messages.entries()) {
			if (!set.add(entry.profileId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Orders {@code lastSeen}. {@code result} should be an array with the same size
	 * as {@code lastSeen}, and it will hold the order of each entry, where a larger
	 * integer corresponds to more recent message. If the integer is {@value #UNKNOWN},
	 * then the message is unknown (neither previously acknowledged nor is newly sent),
	 * which causes validation failure.
	 * 
	 * @return the order of the last received message; if any messages have higher order
	 * than this, it is a validation failure.
	 */
	private int order(List<LastSeenMessageList.Entry> lastSeen, int[] result, @Nullable LastSeenMessageList.Entry lastReceived) {
		Arrays.fill(result, Integer.MIN_VALUE);
		List<LastSeenMessageList.Entry> list = this.prevValidated.entries();
		int i = list.size();

		for (int j = i - 1; j >= 0; j--) {
			int k = lastSeen.indexOf(list.get(j));
			if (k != -1) {
				result[k] = -j - 1;
			}
		}

		int jx = Integer.MIN_VALUE;
		int k = this.pending.size();

		for (int l = 0; l < k; l++) {
			LastSeenMessageList.Entry entry = (LastSeenMessageList.Entry)this.pending.get(l);
			int m = lastSeen.indexOf(entry);
			if (m != -1) {
				result[m] = l;
			}

			if (entry.equals(lastReceived)) {
				jx = l;
			}
		}

		return jx;
	}

	/**
	 * Validates {@code acknowledgment}.
	 * 
	 * @return reasons for acknowledgment validation failure, or an empty set if
	 * validation is successful
	 */
	public Set<AcknowledgmentValidator.FailureReason> validate(LastSeenMessageList.Acknowledgment acknowledgment) {
		EnumSet<AcknowledgmentValidator.FailureReason> enumSet = EnumSet.noneOf(AcknowledgmentValidator.FailureReason.class);
		LastSeenMessageList lastSeenMessageList = acknowledgment.lastSeen();
		LastSeenMessageList.Entry entry = (LastSeenMessageList.Entry)acknowledgment.lastReceived().orElse(null);
		List<LastSeenMessageList.Entry> list = lastSeenMessageList.entries();
		int i = this.prevValidated.entries().size();
		int j = Integer.MIN_VALUE;
		int k = list.size();
		if (k < i) {
			enumSet.add(AcknowledgmentValidator.FailureReason.REMOVED_MESSAGES);
		}

		int[] is = new int[k];
		int l = this.order(list, is, entry);

		for (int m = k - 1; m >= 0; m--) {
			int n = is[m];
			if (n != Integer.MIN_VALUE) {
				if (n < j) {
					enumSet.add(AcknowledgmentValidator.FailureReason.OUT_OF_ORDER);
				} else {
					j = n;
				}
			} else {
				enumSet.add(AcknowledgmentValidator.FailureReason.UNKNOWN_MESSAGES);
			}
		}

		if (entry != null) {
			if (l != Integer.MIN_VALUE && l >= j) {
				j = l;
			} else {
				enumSet.add(AcknowledgmentValidator.FailureReason.UNKNOWN_MESSAGES);
			}
		}

		if (j >= 0) {
			this.pending.removeElements(0, j + 1);
		}

		if (this.hasDuplicateProfiles(lastSeenMessageList)) {
			enumSet.add(AcknowledgmentValidator.FailureReason.DUPLICATED_PROFILES);
		}

		this.prevValidated = lastSeenMessageList;
		return enumSet;
	}

	public static enum FailureReason {
		OUT_OF_ORDER("messages received out of order"),
		DUPLICATED_PROFILES("multiple entries for single profile"),
		UNKNOWN_MESSAGES("unknown message"),
		REMOVED_MESSAGES("previously present messages removed from context");

		private final String description;

		private FailureReason(String description) {
			this.description = description;
		}

		public String getDescription() {
			return this.description;
		}
	}
}
