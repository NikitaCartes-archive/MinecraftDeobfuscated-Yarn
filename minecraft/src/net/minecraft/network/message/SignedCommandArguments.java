package net.minecraft.network.message;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * An interface wrapping {@link ArgumentSignatureDataMap} with metadata attached.
 */
public interface SignedCommandArguments {
	/**
	 * An empty signed command arguments that always returns {@code null} for
	 * {@link #createSignature}.
	 */
	SignedCommandArguments EMPTY = new SignedCommandArguments() {
		@Nullable
		@Override
		public SignedMessage createSignature(String argumentName) {
			return null;
		}
	};

	@Nullable
	SignedMessage createSignature(String argumentName);

	/**
	 * A signature for command arguments, consisting of the sender, the timestamp,
	 * and the signature datas for the arguments.
	 */
	public static record Impl(Map<String, SignedMessage> arguments) implements SignedCommandArguments {
		@Nullable
		@Override
		public SignedMessage createSignature(String argumentName) {
			return (SignedMessage)this.arguments.get(argumentName);
		}
	}
}
