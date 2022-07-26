package net.minecraft.command.argument;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of parsed {@linkplain DecoratableArgumentType decoratable arguments}.
 * 
 * @see #of
 */
public record DecoratableArgumentList<S>(List<DecoratableArgumentList.ParsedArgument<S>> arguments) {
	/**
	 * {@return a new instance of this list from {@code parseResults}}
	 */
	public static <S> DecoratableArgumentList<S> of(ParseResults<S> parseResults) {
		CommandContextBuilder<S> commandContextBuilder = parseResults.getContext();
		CommandContextBuilder<S> commandContextBuilder2 = commandContextBuilder;
		List<DecoratableArgumentList.ParsedArgument<S>> list = collectDecoratableArguments(commandContextBuilder);

		CommandContextBuilder<S> commandContextBuilder3;
		while ((commandContextBuilder3 = commandContextBuilder2.getChild()) != null) {
			boolean bl = commandContextBuilder3.getRootNode() != commandContextBuilder.getRootNode();
			if (!bl) {
				break;
			}

			list.addAll(collectDecoratableArguments(commandContextBuilder3));
			commandContextBuilder2 = commandContextBuilder3;
		}

		return new DecoratableArgumentList<>(list);
	}

	private static <S> List<DecoratableArgumentList.ParsedArgument<S>> collectDecoratableArguments(CommandContextBuilder<S> contextBuilder) {
		List<DecoratableArgumentList.ParsedArgument<S>> list = new ArrayList();

		for (ParsedCommandNode<S> parsedCommandNode : contextBuilder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode) {
				ArgumentCommandNode<S, ?> argumentCommandNode = (ArgumentCommandNode<S, ?>)parsedArgument;
				ArgumentType var7 = argumentCommandNode.getType();
				if (var7 instanceof DecoratableArgumentType) {
					DecoratableArgumentType<?> decoratableArgumentType = (DecoratableArgumentType<?>)var7;
					com.mojang.brigadier.context.ParsedArgument<S, ?> parsedArgumentx = (com.mojang.brigadier.context.ParsedArgument<S, ?>)contextBuilder.getArguments()
						.get(argumentCommandNode.getName());
					if (parsedArgumentx != null) {
						list.add(new DecoratableArgumentList.ParsedArgument<>(argumentCommandNode, parsedArgumentx, decoratableArgumentType));
					}
				}
			}
		}

		return list;
	}

	/**
	 * {@return whether {@code node} is in this list of parsed decorated arguments}
	 */
	public boolean contains(CommandNode<?> node) {
		for (DecoratableArgumentList.ParsedArgument<S> parsedArgument : this.arguments) {
			if (parsedArgument.node() == node) {
				return true;
			}
		}

		return false;
	}

	/**
	 * A parsed decoratable argument, also used as the entry of {@link DecoratableArgumentList}.
	 */
	public static record ParsedArgument<S>(
		ArgumentCommandNode<S, ?> node, com.mojang.brigadier.context.ParsedArgument<S, ?> parsedValue, DecoratableArgumentType<?> argumentType
	) {
		public String getNodeName() {
			return this.node.getName();
		}
	}
}
