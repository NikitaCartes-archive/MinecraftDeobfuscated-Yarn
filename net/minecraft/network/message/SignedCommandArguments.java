/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import net.minecraft.network.message.ArgumentSignatureDataMap;
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

    public MessageSignatureData getArgumentSignature(String var1);

    public MessageMetadata metadata();

    public MessageChain.Unpacker decoder();

    public boolean isPreviewSigned(String var1);

    public record Impl(MessageChain.Unpacker decoder, MessageMetadata metadata, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview) implements SignedCommandArguments
    {
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

