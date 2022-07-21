package net.minecraft;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.DecoratableArgumentType;

public record class_7644<S>(List<class_7644.class_7645<S>> arguments) {
	public static <S> class_7644<S> method_45043(ParseResults<S> parseResults) {
		CommandContextBuilder<S> commandContextBuilder = parseResults.getContext();
		CommandContextBuilder<S> commandContextBuilder2 = commandContextBuilder;
		List<class_7644.class_7645<S>> list = method_45044(commandContextBuilder);

		CommandContextBuilder<S> commandContextBuilder3;
		while ((commandContextBuilder3 = commandContextBuilder2.getChild()) != null) {
			boolean bl = commandContextBuilder3.getRootNode() != commandContextBuilder.getRootNode();
			if (!bl) {
				break;
			}

			list.addAll(method_45044(commandContextBuilder3));
			commandContextBuilder2 = commandContextBuilder3;
		}

		return new class_7644<>(list);
	}

	private static <S> List<class_7644.class_7645<S>> method_45044(CommandContextBuilder<S> commandContextBuilder) {
		List<class_7644.class_7645<S>> list = new ArrayList();

		for (ParsedCommandNode<S> parsedCommandNode : commandContextBuilder.getNodes()) {
			CommandNode parsedArgument = parsedCommandNode.getNode();
			if (parsedArgument instanceof ArgumentCommandNode) {
				ArgumentCommandNode<S, ?> argumentCommandNode = (ArgumentCommandNode<S, ?>)parsedArgument;
				ArgumentType var7 = argumentCommandNode.getType();
				if (var7 instanceof DecoratableArgumentType) {
					DecoratableArgumentType<?> decoratableArgumentType = (DecoratableArgumentType<?>)var7;
					ParsedArgument<S, ?> parsedArgumentx = (ParsedArgument<S, ?>)commandContextBuilder.getArguments().get(argumentCommandNode.getName());
					if (parsedArgumentx != null) {
						list.add(new class_7644.class_7645<>(argumentCommandNode, parsedArgumentx, decoratableArgumentType));
					}
				}
			}
		}

		return list;
	}

	public boolean method_45045(CommandNode<?> commandNode) {
		for (class_7644.class_7645<S> lv : this.arguments) {
			if (lv.node() == commandNode) {
				return true;
			}
		}

		return false;
	}

	public static record class_7645<S>(ArgumentCommandNode<S, ?> node, ParsedArgument<S, ?> parsedValue, DecoratableArgumentType<?> previewType) {
		public String method_45046() {
			return this.node.getName();
		}
	}
}
