package net.minecraft.network.message;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * An interface wrapping {@link ArgumentSignatureDataMap}.
 */
public interface SignedCommandArguments {
	/**
	 * An empty signed command arguments that always returns {@code null} for
	 * {@link #getMessage}.
	 */
	SignedCommandArguments EMPTY = new SignedCommandArguments() {
		@Nullable
		@Override
		public SignedMessage getMessage(String argumentName) {
			return null;
		}
	};

	@Nullable
	SignedMessage getMessage(String argumentName);

	/**
	 * A basic implementation of {@link SignedCommandArguments}.
	 */
	public static record Impl(Map<String, SignedMessage> arguments) implements SignedCommandArguments {
		@Nullable
		@Override
		public SignedMessage getMessage(String argumentName) {
			return (SignedMessage)this.arguments.get(argumentName);
		}
	}
}
