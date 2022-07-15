package net.minecraft.network.message;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
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
	 * {@return whether to preview {@code parseResults}}
	 * 
	 * <p>This returns {@code true} if the parsed arguments include {@link
	 * SignedArgumentType}.
	 */
	public static boolean shouldPreview(ParseResults<?> parseResults) {
		CommandContextBuilder<?> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedCommandNode<?> parsedCommandNode : commandContextBuilder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode) {
				ArgumentCommandNode<?, ?> argumentCommandNode = (ArgumentCommandNode<?, ?>)parsedArgument;
				if (argumentCommandNode.getType() instanceof SignedArgumentType) {
					ParsedArgument<?, ?> parsedArgumentx = (ParsedArgument<?, ?>)commandContextBuilder.getArguments().get(argumentCommandNode.getName());
					if (parsedArgumentx != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * {@return the signature map with arguments from {@code builder} signed with
	 * {@code signer}}
	 */
	public static ArgumentSignatureDataMap sign(CommandContextBuilder<?> builder, ArgumentSignatureDataMap.ArgumentSigner signer) {
		List<ArgumentSignatureDataMap.Entry> list = collectArguments(builder).stream().map(entry -> {
			MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (String)entry.getSecond());
			return new ArgumentSignatureDataMap.Entry((String)entry.getFirst(), messageSignatureData);
		}).toList();
		return new ArgumentSignatureDataMap(list);
	}

	/**
	 * {@return the signable argument names and their values from {@code builder}}
	 */
	private static List<Pair<String, String>> collectArguments(CommandContextBuilder<?> builder) {
		CommandContextBuilder<?> commandContextBuilder = builder.getLastChild();
		List<Pair<String, String>> list = new ArrayList();

		for (ParsedCommandNode<?> parsedCommandNode : commandContextBuilder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode) {
				ArgumentCommandNode<?, ?> argumentCommandNode = (ArgumentCommandNode<?, ?>)parsedArgument;
				ArgumentType var9 = argumentCommandNode.getType();
				if (var9 instanceof SignedArgumentType) {
					SignedArgumentType<?> signedArgumentType = (SignedArgumentType<?>)var9;
					ParsedArgument<?, ?> parsedArgumentx = (ParsedArgument<?, ?>)commandContextBuilder.getArguments().get(argumentCommandNode.getName());
					if (parsedArgumentx != null) {
						String string = resultToString(signedArgumentType, parsedArgumentx);
						list.add(Pair.of(argumentCommandNode.getName(), string));
					}
				}
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
