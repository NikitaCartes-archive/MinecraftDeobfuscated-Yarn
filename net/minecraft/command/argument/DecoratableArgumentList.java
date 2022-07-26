/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.DecoratableArgumentType;

/**
 * A list of parsed {@linkplain DecoratableArgumentType decoratable arguments}.
 * 
 * @see #of
 */
public record DecoratableArgumentList<S>(List<ParsedArgument<S>> arguments) {
    /**
     * {@return a new instance of this list from {@code parseResults}}
     */
    public static <S> DecoratableArgumentList<S> of(ParseResults<S> parseResults) {
        CommandContextBuilder<S> commandContextBuilder3;
        CommandContextBuilder<S> commandContextBuilder;
        CommandContextBuilder<S> commandContextBuilder2 = commandContextBuilder = parseResults.getContext();
        List<ParsedArgument<S>> list = DecoratableArgumentList.collectDecoratableArguments(commandContextBuilder2);
        while ((commandContextBuilder3 = commandContextBuilder2.getChild()) != null) {
            boolean bl;
            boolean bl2 = bl = commandContextBuilder3.getRootNode() != commandContextBuilder.getRootNode();
            if (!bl) break;
            list.addAll(DecoratableArgumentList.collectDecoratableArguments(commandContextBuilder3));
            commandContextBuilder2 = commandContextBuilder3;
        }
        return new DecoratableArgumentList<S>(list);
    }

    private static <S> List<ParsedArgument<S>> collectDecoratableArguments(CommandContextBuilder<S> contextBuilder) {
        ArrayList<ParsedArgument<S>> list = new ArrayList<ParsedArgument<S>>();
        for (ParsedCommandNode<S> parsedCommandNode : contextBuilder.getNodes()) {
            ArgumentCommandNode argumentCommandNode;
            CommandNode<S> commandNode = parsedCommandNode.getNode();
            if (!(commandNode instanceof ArgumentCommandNode) || !((commandNode = (argumentCommandNode = (ArgumentCommandNode)commandNode).getType()) instanceof DecoratableArgumentType)) continue;
            DecoratableArgumentType decoratableArgumentType = (DecoratableArgumentType)((Object)commandNode);
            com.mojang.brigadier.context.ParsedArgument<S, ?> parsedArgument = contextBuilder.getArguments().get(argumentCommandNode.getName());
            if (parsedArgument == null) continue;
            list.add(new ParsedArgument<S>(argumentCommandNode, parsedArgument, decoratableArgumentType));
        }
        return list;
    }

    /**
     * {@return whether {@code node} is in this list of parsed decorated arguments}
     */
    public boolean contains(CommandNode<?> node) {
        for (ParsedArgument<S> parsedArgument : this.arguments) {
            if (parsedArgument.node() != node) continue;
            return true;
        }
        return false;
    }

    public record ParsedArgument<S>(ArgumentCommandNode<S, ?> node, com.mojang.brigadier.context.ParsedArgument<S, ?> parsedValue, DecoratableArgumentType<?> argumentType) {
        public String getNodeName() {
            return this.node.getName();
        }
    }
}

