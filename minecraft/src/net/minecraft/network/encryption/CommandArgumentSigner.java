package net.minecraft.network.encryption;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * A signer for command arguments.
 */
public interface CommandArgumentSigner {
	CommandArgumentSigner NONE = (context, argumentName, value) -> SignedChatMessage.of(value);

	/**
	 * {@return the signed argument's message from the argument name and value}
	 */
	SignedChatMessage signArgument(CommandContext<ServerCommandSource> context, String argumentName, Text value) throws CommandSyntaxException;

	/**
	 * A signature for command arguments, consisting of the sender, the timestamp,
	 * and the signature datas for the arguments.
	 */
	public static record Signatures(UUID sender, Instant timestamp, ArgumentSignatureDataMap argumentSignatures) implements CommandArgumentSigner {
		@Override
		public SignedChatMessage signArgument(CommandContext<ServerCommandSource> commandContext, String string, Text text) {
			NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.get(string);
			return signatureData != null ? SignedChatMessage.of(text, new ChatMessageSignature(this.sender, this.timestamp, signatureData)) : SignedChatMessage.of(text);
		}
	}
}
