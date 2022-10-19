package net.minecraft.network.encryption;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.message.MessageChain;
import net.minecraft.util.Util;

public record ClientPlayerSession(UUID sessionId, @Nullable PlayerKeyPair keyPair) {
	public static ClientPlayerSession create(@Nullable PlayerKeyPair keyPair) {
		return new ClientPlayerSession(UUID.randomUUID(), keyPair);
	}

	public MessageChain.Packer createPacker(UUID sender) {
		Signer signer = this.createSigner();
		return signer != null ? new MessageChain(sender, this.sessionId).getPacker(signer) : MessageChain.Packer.NONE;
	}

	@Nullable
	public Signer createSigner() {
		return this.keyPair != null ? Signer.create(this.keyPair.privateKey(), "SHA256withRSA") : null;
	}

	public PublicPlayerSession toPublicSession() {
		return new PublicPlayerSession(this.sessionId, Util.map(this.keyPair, PlayerKeyPair::publicKey));
	}
}
