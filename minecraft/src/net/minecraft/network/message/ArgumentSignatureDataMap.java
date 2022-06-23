package net.minecraft.network.message;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.argument.TextConvertibleArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.text.Text;

/**
 * A record holding the salt and signatures for all signable arguments of an executed command.
 */
public record ArgumentSignatureDataMap(long salt, Map<String, byte[]> signatures) {
	private static final int MAX_ARGUMENTS = 8;
	private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

	public ArgumentSignatureDataMap(PacketByteBuf buf) {
		this(buf.readLong(), buf.readMap(PacketByteBuf.getMaxValidator(HashMap::new, 8), buf2 -> buf2.readString(16), PacketByteBuf::readByteArray));
	}

	/**
	 * {@return an empty signature data map that has no signed arguments}
	 * 
	 * @apiNote This is used when there is no argument to sign, or when the signing fails.
	 */
	public static ArgumentSignatureDataMap empty() {
		return new ArgumentSignatureDataMap(0L, Map.of());
	}

	/**
	 * {@return the signature data for {@code argumentName}, or {@code null} if the
	 * argument name is not present in this signatures}
	 */
	@Nullable
	public NetworkEncryptionUtils.SignatureData get(String argumentName) {
		byte[] bs = (byte[])this.signatures.get(argumentName);
		return bs != null ? new NetworkEncryptionUtils.SignatureData(this.salt, bs) : null;
	}

	public void write(PacketByteBuf buf) {
		buf.writeLong(this.salt);
		buf.writeMap(this.signatures, (bufx, argumentName) -> bufx.writeString(argumentName, 16), PacketByteBuf::writeByteArray);
	}

	/**
	 * {@return the signable argument names and their values from {@code builder}}
	 */
	public static Map<String, Text> collectArguments(CommandContextBuilder<?> builder) {
		CommandContextBuilder<?> commandContextBuilder = builder.getLastChild();
		Map<String, Text> map = new Object2ObjectArrayMap<>();

		for (ParsedCommandNode<?> parsedCommandNode : commandContextBuilder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode) {
				ArgumentCommandNode<?, ?> argumentCommandNode = (ArgumentCommandNode<?, ?>)parsedArgument;
				ArgumentType var8 = argumentCommandNode.getType();
				if (var8 instanceof TextConvertibleArgumentType) {
					TextConvertibleArgumentType<?> textConvertibleArgumentType = (TextConvertibleArgumentType<?>)var8;
					ParsedArgument<?, ?> parsedArgumentx = (ParsedArgument<?, ?>)commandContextBuilder.getArguments().get(argumentCommandNode.getName());
					if (parsedArgumentx != null) {
						map.put(argumentCommandNode.getName(), resultToText(textConvertibleArgumentType, parsedArgumentx));
					}
				}
			}
		}

		return map;
	}

	private static <T> Text resultToText(TextConvertibleArgumentType<T> type, ParsedArgument<?, ?> argument) {
		return type.toText((T)argument.getResult());
	}
}
