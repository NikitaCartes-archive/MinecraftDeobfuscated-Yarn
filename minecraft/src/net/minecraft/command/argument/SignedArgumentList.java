package net.minecraft.command.argument;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of parsed {@linkplain SignedArgumentType signed arguments}.
 * 
 * @see #of
 */
public record SignedArgumentList<S>(List<SignedArgumentList.ParsedArgument<S>> arguments) {
	/**
	 * {@return a new instance of this list from {@code parseResults}}
	 */
	public static <S> SignedArgumentList<S> of(ParseResults<S> parseResults) {
		String string = parseResults.getReader().getString();
		CommandContextBuilder<S> commandContextBuilder = parseResults.getContext();
		CommandContextBuilder<S> commandContextBuilder2 = commandContextBuilder;

		List<SignedArgumentList.ParsedArgument<S>> list;
		CommandContextBuilder<S> commandContextBuilder3;
		for(list = collectDecoratableArguments(string, commandContextBuilder);
			(commandContextBuilder3 = commandContextBuilder2.getChild()) != null;
			commandContextBuilder2 = commandContextBuilder3
		) {
			boolean bl = commandContextBuilder3.getRootNode() != commandContextBuilder.getRootNode();
			if (!bl) {
				break;
			}

			list.addAll(collectDecoratableArguments(string, commandContextBuilder3));
		}

		return new SignedArgumentList<>(list);
	}

	private static <S> List<SignedArgumentList.ParsedArgument<S>> collectDecoratableArguments(String argumentName, CommandContextBuilder<S> builder) {
		List<SignedArgumentList.ParsedArgument<S>> list = new ArrayList();

		for(ParsedCommandNode<S> parsedCommandNode : builder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode argumentCommandNode && argumentCommandNode.getType() instanceof SignedArgumentType) {
				com.mojang.brigadier.context.ParsedArgument<S, ?> parsedArgumentx = (com.mojang.brigadier.context.ParsedArgument)builder.getArguments()
					.get(argumentCommandNode.getName());
				if (parsedArgumentx != null) {
					String string = parsedArgumentx.getRange().get(argumentName);
					list.add(new SignedArgumentList.ParsedArgument<S>(argumentCommandNode, string));
				}
			}
		}

		return list;
	}

	/**
	 * A parsed signed argument, also used as the entry of {@link SignedArgumentList}.
	 */
	public static record ParsedArgument<S>(ArgumentCommandNode<S, ?> node, String value) {
		public String getNodeName() {
			return this.node.getName();
		}
	}
}
