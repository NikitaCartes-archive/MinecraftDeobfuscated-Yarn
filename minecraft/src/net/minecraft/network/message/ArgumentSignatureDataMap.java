package net.minecraft.network.message;

import com.mojang.brigadier.arguments.ArgumentType;
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
import net.minecraft.text.Text;

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
	 * {@return the signature map with arguments from {@code builder} signed with
	 * {@code signer}}
	 */
	public static ArgumentSignatureDataMap sign(CommandContextBuilder<?> builder, ArgumentSignatureDataMap.ArgumentSigner signer) {
		List<ArgumentSignatureDataMap.Entry> list = collectArguments(builder).stream().map(entry -> {
			MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (Text)entry.getSecond());
			return new ArgumentSignatureDataMap.Entry((String)entry.getFirst(), messageSignatureData);
		}).toList();
		return new ArgumentSignatureDataMap(list);
	}

	/**
	 * {@return the signable argument names and their values from {@code builder}}
	 */
	private static List<Pair<String, Text>> collectArguments(CommandContextBuilder<?> builder) {
		CommandContextBuilder<?> commandContextBuilder = builder.getLastChild();
		List<Pair<String, Text>> list = new ArrayList();

		for (ParsedCommandNode<?> parsedCommandNode : commandContextBuilder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode) {
				ArgumentCommandNode<?, ?> argumentCommandNode = (ArgumentCommandNode<?, ?>)parsedArgument;
				ArgumentType var9 = argumentCommandNode.getType();
				if (var9 instanceof TextConvertibleArgumentType) {
					TextConvertibleArgumentType<?> textConvertibleArgumentType = (TextConvertibleArgumentType<?>)var9;
					ParsedArgument<?, ?> parsedArgumentx = (ParsedArgument<?, ?>)commandContextBuilder.getArguments().get(argumentCommandNode.getName());
					if (parsedArgumentx != null) {
						Text text = resultToText(textConvertibleArgumentType, parsedArgumentx);
						list.add(Pair.of(argumentCommandNode.getName(), text));
					}
				}
			}
		}

		return list;
	}

	private static <T> Text resultToText(TextConvertibleArgumentType<T> type, ParsedArgument<?, ?> argument) {
		return type.toText((T)argument.getResult());
	}

	/**
	 * A functional interface that signs an argument of a command.
	 */
	@FunctionalInterface
	public interface ArgumentSigner {
		MessageSignatureData sign(String argumentName, Text value);
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
