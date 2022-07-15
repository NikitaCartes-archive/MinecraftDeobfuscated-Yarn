/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;

/**
 * An interface wrapping {@link ArgumentSignatureDataMap} with metadata attached.
 */
public interface SignedCommandArguments {
    public static SignedCommandArguments none() {
        final MessageMetadata messageMetadata = MessageMetadata.of();
        return new SignedCommandArguments(){

            @Override
            public ArgumentSignature createSignature(String argumentName) {
                return ArgumentSignature.EMPTY;
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

    public ArgumentSignature createSignature(String var1);

    public MessageMetadata metadata();

    public MessageChain.Unpacker decoder();

    public record Impl(MessageChain.Unpacker decoder, MessageMetadata metadata, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview, LastSeenMessageList lastSeenMessages) implements SignedCommandArguments
    {
        @Override
        public ArgumentSignature createSignature(String argumentName) {
            return new ArgumentSignature(this.argumentSignatures.get(argumentName), this.signedPreview, this.lastSeenMessages);
        }
    }

    public record ArgumentSignature(MessageSignatureData signature, boolean signedPreview, LastSeenMessageList lastSeenMessages) {
        public static final ArgumentSignature EMPTY = new ArgumentSignature(MessageSignatureData.EMPTY, false, LastSeenMessageList.EMPTY);
    }
}

