/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.command.argument.TextConvertibleArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record ArgumentSignatures(long salt, Map<String, byte[]> signatures) {
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatures(PacketByteBuf buf) {
        this(buf.readLong(), buf.readMap(buf2 -> buf2.readString(16), PacketByteBuf::readByteArray));
    }

    public static ArgumentSignatures none() {
        return new ArgumentSignatures(0L, Map.of());
    }

    @Nullable
    public NetworkEncryptionUtils.SignatureData createSignatureData(String argumentName) {
        byte[] bs = this.signatures.get(argumentName);
        if (bs != null) {
            return new NetworkEncryptionUtils.SignatureData(this.salt, bs);
        }
        return null;
    }

    public void write(PacketByteBuf buf2) {
        buf2.writeLong(this.salt);
        buf2.writeMap(this.signatures, (buf, argumentName) -> buf.writeString((String)argumentName, 16), PacketByteBuf::writeByteArray);
    }

    public static Map<String, Text> collectArguments(CommandContextBuilder<?> builder) {
        CommandContextBuilder<?> commandContextBuilder = builder.getLastChild();
        Object2ObjectArrayMap<String, Text> map = new Object2ObjectArrayMap<String, Text>();
        for (ParsedCommandNode<?> parsedCommandNode : commandContextBuilder.getNodes()) {
            ArgumentCommandNode argumentCommandNode;
            CommandNode<?> commandNode = parsedCommandNode.getNode();
            if (!(commandNode instanceof ArgumentCommandNode) || !((commandNode = (argumentCommandNode = (ArgumentCommandNode)commandNode).getType()) instanceof TextConvertibleArgumentType)) continue;
            TextConvertibleArgumentType textConvertibleArgumentType = (TextConvertibleArgumentType)((Object)commandNode);
            ParsedArgument<?, ?> parsedArgument = commandContextBuilder.getArguments().get(argumentCommandNode.getName());
            if (parsedArgument == null) continue;
            map.put(argumentCommandNode.getName(), ArgumentSignatures.resultToText(textConvertibleArgumentType, parsedArgument));
        }
        return map;
    }

    private static <T> Text resultToText(TextConvertibleArgumentType<T> type, ParsedArgument<?, ?> argument) {
        return type.toText(argument.getResult());
    }
}

