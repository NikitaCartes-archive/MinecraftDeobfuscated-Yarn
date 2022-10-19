/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;
import org.jetbrains.annotations.Nullable;

/**
 * A record holding the signatures for all signable arguments of an executed command.
 * 
 * @see SignedCommandArguments
 */
public record ArgumentSignatureDataMap(List<Entry> entries) {
    public static final ArgumentSignatureDataMap EMPTY = new ArgumentSignatureDataMap(List.of());
    private static final int MAX_ARGUMENTS = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatureDataMap(PacketByteBuf buf) {
        this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 8), Entry::new));
    }

    /**
     * {@return the signature data for {@code argumentName}, or {@code null} if the
     * argument name is not present in this signatures}
     */
    @Nullable
    public MessageSignatureData get(String argumentName) {
        for (Entry entry : this.entries) {
            if (!entry.name.equals(argumentName)) continue;
            return entry.signature;
        }
        return null;
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.entries, (buf2, entry) -> entry.write((PacketByteBuf)buf2));
    }

    /**
     * {@return the signature map with {@code arguments} signed with
     * {@code signer}}
     */
    public static ArgumentSignatureDataMap sign(SignedArgumentList<?> arguments, ArgumentSigner signer) {
        List<Entry> list = arguments.arguments().stream().map(argument -> {
            MessageSignatureData messageSignatureData = signer.sign(argument.value());
            if (messageSignatureData != null) {
                return new Entry(argument.getNodeName(), messageSignatureData);
            }
            return null;
        }).filter(Objects::nonNull).toList();
        return new ArgumentSignatureDataMap(list);
    }

    public record Entry(String name, MessageSignatureData signature) {
        public Entry(PacketByteBuf buf) {
            this(buf.readString(16), MessageSignatureData.fromBuf(buf));
        }

        public void write(PacketByteBuf buf) {
            buf.writeString(this.name, 16);
            MessageSignatureData.write(buf, this.signature);
        }
    }

    @FunctionalInterface
    public static interface ArgumentSigner {
        @Nullable
        public MessageSignatureData sign(String var1);
    }
}

