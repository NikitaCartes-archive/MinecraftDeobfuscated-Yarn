/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.command.argument.DecoratableArgumentType;
import net.minecraft.command.argument.SignedArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;

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
    public MessageSignatureData get(String argumentName) {
        for (Entry entry : this.entries) {
            if (!entry.name.equals(argumentName)) continue;
            return entry.signature;
        }
        return MessageSignatureData.EMPTY;
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.entries, (buf2, entry) -> entry.write((PacketByteBuf)buf2));
    }

    /**
     * {@return whether the parsed arguments include {@link SignedArgumentType}}
     */
    public static boolean hasSignedArgument(DecoratableArgumentList<?> arguments) {
        return arguments.arguments().stream().anyMatch(argument -> argument.argumentType() instanceof SignedArgumentType);
    }

    /**
     * {@return the signature map with {@code arguments} signed with
     * {@code signer}}
     */
    public static ArgumentSignatureDataMap sign(DecoratableArgumentList<?> arguments, ArgumentSigner signer) {
        List<Entry> list = ArgumentSignatureDataMap.toNameValuePairs(arguments).stream().map(entry -> {
            MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (String)entry.getSecond());
            return new Entry((String)entry.getFirst(), messageSignatureData);
        }).toList();
        return new ArgumentSignatureDataMap(list);
    }

    /**
     * {@return {@code arguments} converted to a list of signed name/value pairs}
     */
    public static List<Pair<String, String>> toNameValuePairs(DecoratableArgumentList<?> arguments) {
        ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        for (DecoratableArgumentList.ParsedArgument<?> parsedArgument : arguments.arguments()) {
            DecoratableArgumentType<?> decoratableArgumentType = parsedArgument.argumentType();
            if (!(decoratableArgumentType instanceof SignedArgumentType)) continue;
            SignedArgumentType signedArgumentType = (SignedArgumentType)decoratableArgumentType;
            String string = ArgumentSignatureDataMap.resultToString(signedArgumentType, parsedArgument.parsedValue());
            list.add(Pair.of(parsedArgument.getNodeName(), string));
        }
        return list;
    }

    private static <T> String resultToString(SignedArgumentType<T> type, ParsedArgument<?, ?> argument) {
        return type.toSignedString(argument.getResult());
    }

    public record Entry(String name, MessageSignatureData signature) {
        public Entry(PacketByteBuf buf) {
            this(buf.readString(16), new MessageSignatureData(buf));
        }

        public void write(PacketByteBuf buf) {
            buf.writeString(this.name, 16);
            this.signature.write(buf);
        }
    }

    @FunctionalInterface
    public static interface ArgumentSigner {
        public MessageSignatureData sign(String var1, String var2);
    }
}

