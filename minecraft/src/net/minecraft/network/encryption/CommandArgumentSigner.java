package net.minecraft.network.encryption;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface CommandArgumentSigner {
	CommandArgumentSigner NONE = (context, argumentName, value) -> new SignedChatMessage(value, ChatMessageSignature.none());

	SignedChatMessage signArgument(CommandContext<ServerCommandSource> context, String argumentName, Text value) throws CommandSyntaxException;

	public static record Signatures(UUID sender, Instant time, ArgumentSignatures argumentSignatures) implements CommandArgumentSigner {
		@Override
		public SignedChatMessage signArgument(CommandContext<ServerCommandSource> commandContext, String string, Text text) {
			NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.createSignatureData(string);
			return signatureData != null
				? new SignedChatMessage(text, new ChatMessageSignature(this.sender, this.time, signatureData))
				: new SignedChatMessage(text, ChatMessageSignature.none());
		}
	}
}
