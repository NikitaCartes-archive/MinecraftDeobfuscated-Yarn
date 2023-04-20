package net.minecraft.network.encryption;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageVerifier;

public record PublicPlayerSession(UUID sessionId, PlayerPublicKey publicKeyData) {
	public MessageVerifier createVerifier() {
		return new MessageVerifier.Impl(this.publicKeyData.createSignatureInstance());
	}

	public MessageChain.Unpacker createUnpacker(UUID sender) {
		return new MessageChain(sender, this.sessionId).getUnpacker(this.publicKeyData);
	}

	public PublicPlayerSession.Serialized toSerialized() {
		return new PublicPlayerSession.Serialized(this.sessionId, this.publicKeyData.data());
	}

	public boolean isKeyExpired() {
		return this.publicKeyData.data().isExpired();
	}

	public static record Serialized(UUID sessionId, PlayerPublicKey.PublicKeyData publicKeyData) {
		public static PublicPlayerSession.Serialized fromBuf(PacketByteBuf buf) {
			return new PublicPlayerSession.Serialized(buf.readUuid(), new PlayerPublicKey.PublicKeyData(buf));
		}

		public static void write(PacketByteBuf buf, PublicPlayerSession.Serialized serialized) {
			buf.writeUuid(serialized.sessionId);
			serialized.publicKeyData.write(buf);
		}

		public PublicPlayerSession toSession(GameProfile gameProfile, SignatureVerifier servicesSignatureVerifier, Duration gracePeriod) throws PlayerPublicKey.PublicKeyException {
			return new PublicPlayerSession(
				this.sessionId, PlayerPublicKey.verifyAndDecode(servicesSignatureVerifier, gameProfile.getId(), this.publicKeyData, gracePeriod)
			);
		}
	}
}
