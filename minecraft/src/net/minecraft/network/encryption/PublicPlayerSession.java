package net.minecraft.network.encryption;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.util.Util;

public record PublicPlayerSession(UUID sessionId, @Nullable PlayerPublicKey publicKeyData) {
	public static final PublicPlayerSession MISSING = new PublicPlayerSession(Util.NIL_UUID, null);

	public MessageVerifier createVerifier() {
		return (MessageVerifier)(this.publicKeyData != null ? new MessageVerifier.Impl(this.publicKeyData.createSignatureInstance()) : MessageVerifier.NO_SIGNATURE);
	}

	public MessageChain.Unpacker createUnpacker(UUID sender) {
		return this.publicKeyData != null ? new MessageChain(sender, this.sessionId).getUnpacker(this.publicKeyData) : MessageChain.Unpacker.unsigned(sender);
	}

	public PublicPlayerSession.Serialized toSerialized() {
		return new PublicPlayerSession.Serialized(this.sessionId, Util.map(this.publicKeyData, PlayerPublicKey::data));
	}

	public boolean hasPublicKey() {
		return this.publicKeyData != null;
	}

	public static record Serialized(UUID sessionId, @Nullable PlayerPublicKey.PublicKeyData publicKeyData) {
		public static final PublicPlayerSession.Serialized MISSING = PublicPlayerSession.MISSING.toSerialized();

		public static PublicPlayerSession.Serialized fromBuf(PacketByteBuf buf) {
			return new PublicPlayerSession.Serialized(buf.readUuid(), buf.readNullable(PlayerPublicKey.PublicKeyData::new));
		}

		public static void write(PacketByteBuf buf, PublicPlayerSession.Serialized serialized) {
			buf.writeUuid(serialized.sessionId);
			buf.writeNullable(serialized.publicKeyData, (buf2, publicKeyData) -> publicKeyData.write(buf2));
		}

		public PublicPlayerSession toSession(GameProfile gameProfile, SignatureVerifier servicesSignatureVerifier, Duration gracePeriod) throws PlayerPublicKey.PublicKeyException {
			return this.publicKeyData == null
				? PublicPlayerSession.MISSING
				: new PublicPlayerSession(this.sessionId, PlayerPublicKey.verifyAndDecode(servicesSignatureVerifier, gameProfile.getId(), this.publicKeyData, gracePeriod));
		}
	}
}
