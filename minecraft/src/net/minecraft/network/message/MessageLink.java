package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import java.security.SignatureException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

/**
 * Represents a link to the preceding message that a particular message has.
 */
public record MessageLink(int index, UUID sender, UUID sessionId) {
	public static MessageLink of(UUID sender) {
		return of(sender, Util.NIL_UUID);
	}

	public static MessageLink of(UUID sender, UUID sessionId) {
		return new MessageLink(0, sender, sessionId);
	}

	public void update(SignatureUpdatable.SignatureUpdater updater) throws SignatureException {
		updater.update(Uuids.toByteArray(this.sender));
		updater.update(Uuids.toByteArray(this.sessionId));
		updater.update(Ints.toByteArray(this.index));
	}

	/**
	 * {@return whether this link links to the {@code preceding} link}
	 * 
	 * <p>For the link to be considered valid, the two must have the same sender and session ID,
	 * and the newer link's index must be above the preceding link's index.
	 */
	public boolean linksTo(MessageLink preceding) {
		return this.index > preceding.index() && this.sender.equals(preceding.sender()) && this.sessionId.equals(preceding.sessionId());
	}

	/**
	 * {@return the next link used by the message's succeeding message}
	 * 
	 * <p>This can return {@code null} in an extremely rare case, where the index is about
	 * to overflow.
	 */
	@Nullable
	public MessageLink next() {
		return this.index == Integer.MAX_VALUE ? null : new MessageLink(this.index + 1, this.sender, this.sessionId);
	}
}
