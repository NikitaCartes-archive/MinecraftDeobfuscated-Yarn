/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.encryption.ArgumentSignatures;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface CommandArgumentSigner {
    public static final CommandArgumentSigner NONE = (context, argumentName, value) -> new SignedChatMessage(value, ChatMessageSignature.none());

    public SignedChatMessage signArgument(CommandContext<ServerCommandSource> var1, String var2, Text var3) throws CommandSyntaxException;

    public record Signatures(UUID sender, Instant time, ArgumentSignatures argumentSignatures) implements CommandArgumentSigner
    {
        @Override
        public SignedChatMessage signArgument(CommandContext<ServerCommandSource> commandContext, String string, Text text) {
            NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.createSignatureData(string);
            if (signatureData != null) {
                return new SignedChatMessage(text, new ChatMessageSignature(this.sender, this.time, signatureData));
            }
            return new SignedChatMessage(text, ChatMessageSignature.none());
        }
    }
}

