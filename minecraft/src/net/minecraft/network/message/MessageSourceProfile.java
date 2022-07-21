package net.minecraft.network.message;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Util;

/**
 * A pair of the profile ID and public key of the message's source.
 * 
 * <p>An instance can be obtained via {@link net.minecraft.entity.Entity#getMessageSourceProfile}.
 */
public record MessageSourceProfile(UUID profileId, @Nullable PlayerPublicKey playerPublicKey) {
	public static final MessageSourceProfile NONE = new MessageSourceProfile(Util.NIL_UUID, null);

	/**
	 * {@return {@code true} if this source profile does not have the profile ID set}
	 * 
	 * <p>Commands executed from server console or command block use such source profile.
	 */
	public boolean lacksProfileId() {
		return NONE.equals(this);
	}
}
