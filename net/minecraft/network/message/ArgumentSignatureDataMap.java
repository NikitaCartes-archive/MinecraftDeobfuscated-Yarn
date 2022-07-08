/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.TextConvertibleArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

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
     * {@return the signature map with arguments from {@code builder} signed with
     * {@code signer}}
     */
    public static ArgumentSignatureDataMap sign(CommandContextBuilder<?> builder, ArgumentSigner signer) {
        List<Entry> list = ArgumentSignatureDataMap.collectArguments(builder).stream().map(entry -> {
            MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (Text)entry.getSecond());
            return new Entry((String)entry.getFirst(), messageSignatureData);
        }).toList();
        return new ArgumentSignatureDataMap(list);
    }

    /**
     * {@return the signable argument names and their values from {@code builder}}
     */
    private static List<Pair<String, Text>> collectArguments(CommandContextBuilder<?> builder) {
        CommandContextBuilder<?> commandContextBuilder = builder.getLastChild();
        ArrayList<Pair<String, Text>> list = new ArrayList<Pair<String, Text>>();
        for (ParsedCommandNode<?> parsedCommandNode : commandContextBuilder.getNodes()) {
            ArgumentCommandNode argumentCommandNode;
            CommandNode<?> commandNode = parsedCommandNode.getNode();
            if (!(commandNode instanceof ArgumentCommandNode) || !((commandNode = (argumentCommandNode = (ArgumentCommandNode)commandNode).getType()) instanceof TextConvertibleArgumentType)) continue;
            TextConvertibleArgumentType textConvertibleArgumentType = (TextConvertibleArgumentType)((Object)commandNode);
            ParsedArgument<?, ?> parsedArgument = commandContextBuilder.getArguments().get(argumentCommandNode.getName());
            if (parsedArgument == null) continue;
            Text text = ArgumentSignatureDataMap.resultToText(textConvertibleArgumentType, parsedArgument);
            list.add(Pair.of(argumentCommandNode.getName(), text));
        }
        return list;
    }

    private static <T> Text resultToText(TextConvertibleArgumentType<T> type, ParsedArgument<?, ?> argument) {
        return type.toText(argument.getResult());
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
        public MessageSignatureData sign(String var1, Text var2);
    }
}

