/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.util.Optional;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import org.jetbrains.annotations.Nullable;

/**
 * A class for handling the "message chain".
 * 
 * <p>{@link MessageHeader} includes the signature of the last message the client has seen.
 * This can be used to verify the legitimacy of a chain of messages, since if the chain
 * is valid, the last message's  "previous signature" should be able to verify the preceding
 * message.
 * 
 * <p>Clients signing a message with its preceding message's signature is called
 * "packing", and the server creating a signed message with its preceding message's
 * signature is called "unpacking". Unpacked messages can then be verified to check the
 * chain's legitimacy.
 */
public class MessageChain {
    @Nullable
    private MessageSignatureData precedingSignature;

    private Signature pack(Signer signer, MessageMetadata metadata, DecoratedContents contents, LastSeenMessageList lastSeenMessages) {
        MessageSignatureData messageSignatureData;
        this.precedingSignature = messageSignatureData = MessageChain.sign(signer, metadata, this.precedingSignature, contents, lastSeenMessages);
        return new Signature(messageSignatureData);
    }

    private static MessageSignatureData sign(Signer signer, MessageMetadata metadata, @Nullable MessageSignatureData precedingSignature, DecoratedContents contents, LastSeenMessageList lastSeenMessages) {
        MessageHeader messageHeader = new MessageHeader(precedingSignature, metadata.sender());
        MessageBody messageBody = new MessageBody(contents, metadata.timestamp(), metadata.salt(), lastSeenMessages);
        byte[] bs = messageBody.digest().asBytes();
        return new MessageSignatureData(signer.sign(updatable -> messageHeader.update(updatable, bs)));
    }

    private SignedMessage unpack(Signature signature, MessageMetadata metadata, DecoratedContents contents, LastSeenMessageList lastSeenMessages) {
        SignedMessage signedMessage = MessageChain.createMessage(signature, this.precedingSignature, metadata, contents, lastSeenMessages);
        this.precedingSignature = signature.signature;
        return signedMessage;
    }

    private static SignedMessage createMessage(Signature signature, @Nullable MessageSignatureData precedingSignature, MessageMetadata metadata, DecoratedContents contents, LastSeenMessageList lastSeenMessage) {
        MessageHeader messageHeader = new MessageHeader(precedingSignature, metadata.sender());
        MessageBody messageBody = new MessageBody(contents, metadata.timestamp(), metadata.salt(), lastSeenMessage);
        return new SignedMessage(messageHeader, signature.signature, messageBody, Optional.empty(), FilterMask.PASS_THROUGH);
    }

    public Unpacker getUnpacker() {
        return this::unpack;
    }

    public Packer getPacker() {
        return this::pack;
    }

    public record Signature(MessageSignatureData signature) {
    }

    @FunctionalInterface
    public static interface Unpacker {
        public static final Unpacker UNSIGNED = (signature, metadata, content, lastSeenMessages) -> SignedMessage.ofUnsigned(metadata, content);

        public SignedMessage unpack(Signature var1, MessageMetadata var2, DecoratedContents var3, LastSeenMessageList var4);
    }

    @FunctionalInterface
    public static interface Packer {
        public Signature pack(Signer var1, MessageMetadata var2, DecoratedContents var3, LastSeenMessageList var4);
    }
}

