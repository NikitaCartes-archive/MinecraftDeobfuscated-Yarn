package net.minecraft.network.message;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.command.argument.SignedArgumentType;
import net.minecraft.network.PacketByteBuf;

/**
 * A record holding the signatures for all signable arguments of an executed command.
 * 
 * @see SignedCommandArguments
 */
public record ArgumentSignatureDataMap(List<ArgumentSignatureDataMap.Entry> entries) {
	public static final ArgumentSignatureDataMap EMPTY = new ArgumentSignatureDataMap(List.of());
	private static final int MAX_ARGUMENTS = 8;
	private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

	public ArgumentSignatureDataMap(PacketByteBuf buf) {
		this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 8), ArgumentSignatureDataMap.Entry::new));
	}

	/**
	 * {@return the signature data for {@code argumentName}, or {@code null} if the
	 * argument name is not present in this signatures}
	 */
	public MessageSignatureData get(String argumentName) {
		for (ArgumentSignatureDataMap.Entry entry : this.entries) {
			if (entry.name.equals(argumentName)) {
				return entry.signature;
			}
		}

		return MessageSignatureData.EMPTY;
	}

	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.entries, (buf2, entry) -> entry.write(buf2));
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
	public static ArgumentSignatureDataMap sign(DecoratableArgumentList<?> arguments, ArgumentSignatureDataMap.ArgumentSigner signer) {
		List<ArgumentSignatureDataMap.Entry> list = toNameValuePairs(arguments).stream().map(entry -> {
			MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (String)entry.getSecond());
			return new ArgumentSignatureDataMap.Entry((String)entry.getFirst(), messageSignatureData);
		}).toList();
		return new ArgumentSignatureDataMap(list);
	}

	/**
	 * {@return {@code arguments} converted to a list of signed name/value pairs}
	 */
	public static List<Pair<String, String>> toNameValuePairs(DecoratableArgumentList<?> arguments) {
		List<Pair<String, String>> list = new ArrayList();

		for (DecoratableArgumentList.ParsedArgument<?> parsedArgument : arguments.arguments()) {
			if (parsedArgument.argumentType() instanceof SignedArgumentType<?> signedArgumentType) {
				String string = resultToString(signedArgumentType, parsedArgument.parsedValue());
				list.add(Pair.of(parsedArgument.getNodeName(), string));
			}
		}

		return list;
	}

	private static <T> String resultToString(SignedArgumentType<T> type, ParsedArgument<?, ?> argument) {
		return type.toSignedString((T)argument.getResult());
	}

	/**
	 * A functional interface that signs an argument of a command.
	 */
	@FunctionalInterface
	public interface ArgumentSigner {
		MessageSignatureData sign(String argumentName, String value);
	}

	/**
	 * An entry of the signatures map, consisting of the argument's name and signature data.
	 */
	public static record Entry(String name, MessageSignatureData signature) {

		public Entry(PacketByteBuf buf) {
			this(buf.readString(16), new MessageSignatureData(buf));
		}

		public void write(PacketByteBuf buf) {
			buf.writeString(this.name, 16);
			this.signature.write(buf);
		}
	}
}
