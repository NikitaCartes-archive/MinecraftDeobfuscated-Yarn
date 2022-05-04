package net.minecraft.network.encryption;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.SignatureException;
import net.minecraft.text.Text;

public record SignedChatMessage(Text content, ChatMessageSignature signature) {
	public SignedChatMessage(String content, ChatMessageSignature signature) {
		this(Text.literal(content), signature);
	}

	public boolean verify(Signature signature) throws SignatureException {
		return this.signature.verify(signature, this.content);
	}

	public boolean verify(PlayerPublicKey publicKey) {
		try {
			return this.verify(publicKey.createSignatureInstance());
		} catch (NetworkEncryptionException | GeneralSecurityException var3) {
			return false;
		}
	}
}
