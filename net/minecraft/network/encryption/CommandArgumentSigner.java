/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.encryption.ArgumentSignatureDataMap;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * A signer for command arguments.
 */
public interface CommandArgumentSigner {
    public static final CommandArgumentSigner NONE = (context, argumentName, value) -> SignedChatMessage.of(value);

    /**
     * {@return the signed argument's message from the argument name and value}
     */
    public SignedChatMessage signArgument(CommandContext<ServerCommandSource> var1, String var2, Text var3) throws CommandSyntaxException;

    public record Signatures(UUID sender, Instant timestamp, ArgumentSignatureDataMap argumentSignatures) implements CommandArgumentSigner
    {
        @Override
        public SignedChatMessage signArgument(CommandContext<ServerCommandSource> commandContext, String string, Text text) {
            NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.get(string);
            if (signatureData != null) {
                return SignedChatMessage.of(text, new ChatMessageSignature(this.sender, this.timestamp, signatureData));
            }
            return SignedChatMessage.of(text);
        }
    }
}

