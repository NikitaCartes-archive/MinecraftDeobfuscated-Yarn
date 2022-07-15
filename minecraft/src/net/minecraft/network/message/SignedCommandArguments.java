package net.minecraft.network.message;

/**
 * An interface wrapping {@link ArgumentSignatureDataMap} with metadata attached.
 */
public interface SignedCommandArguments {
	static SignedCommandArguments none() {
		final MessageMetadata messageMetadata = MessageMetadata.of();
		return new SignedCommandArguments() {
			@Override
			public SignedCommandArguments.ArgumentSignature createSignature(String argumentName) {
				return SignedCommandArguments.ArgumentSignature.EMPTY;
			}

			@Override
			public MessageMetadata metadata() {
				return messageMetadata;
			}

			@Override
			public MessageChain.Unpacker decoder() {
				return MessageChain.Unpacker.UNSIGNED;
			}
		};
	}

	SignedCommandArguments.ArgumentSignature createSignature(String argumentName);

	MessageMetadata metadata();

	MessageChain.Unpacker decoder();

	/**
	 * A record holding the signature for a specific argument.
	 */
	public static record ArgumentSignature(MessageSignatureData signature, boolean signedPreview, LastSeenMessageList lastSeenMessages) {
		public static final SignedCommandArguments.ArgumentSignature EMPTY = new SignedCommandArguments.ArgumentSignature(
			MessageSignatureData.EMPTY, false, LastSeenMessageList.EMPTY
		);
	}

	/**
	 * A signature for command arguments, consisting of the sender, the timestamp,
	 * and the signature datas for the arguments.
	 */
	public static record Impl(
		MessageChain.Unpacker decoder,
		MessageMetadata metadata,
		ArgumentSignatureDataMap argumentSignatures,
		boolean signedPreview,
		LastSeenMessageList lastSeenMessages
	) implements SignedCommandArguments {
		@Override
		public SignedCommandArguments.ArgumentSignature createSignature(String argumentName) {
			return new SignedCommandArguments.ArgumentSignature(this.argumentSignatures.get(argumentName), this.signedPreview, this.lastSeenMessages);
		}
	}
}
