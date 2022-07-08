package net.minecraft.network.message;

/**
 * An interface wrapping {@link ArgumentSignatureDataMap} with metadata attached.
 */
public interface SignedCommandArguments {
	static SignedCommandArguments none() {
		final MessageMetadata messageMetadata = MessageMetadata.of();
		return new SignedCommandArguments() {
			@Override
			public MessageSignatureData getArgumentSignature(String argumentName) {
				return MessageSignatureData.EMPTY;
			}

			@Override
			public MessageMetadata metadata() {
				return messageMetadata;
			}

			@Override
			public boolean isPreviewSigned(String argumentName) {
				return false;
			}

			@Override
			public MessageChain.Unpacker decoder() {
				return MessageChain.Unpacker.UNSIGNED;
			}
		};
	}

	MessageSignatureData getArgumentSignature(String argumentName);

	MessageMetadata metadata();

	MessageChain.Unpacker decoder();

	boolean isPreviewSigned(String argumentName);

	/**
	 * A signature for command arguments, consisting of the sender, the timestamp,
	 * and the signature datas for the arguments.
	 */
	public static record Impl(MessageChain.Unpacker decoder, MessageMetadata metadata, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview)
		implements SignedCommandArguments {
		@Override
		public MessageSignatureData getArgumentSignature(String argumentName) {
			return this.argumentSignatures.get(argumentName);
		}

		@Override
		public boolean isPreviewSigned(String argumentName) {
			return this.signedPreview;
		}
	}
}
