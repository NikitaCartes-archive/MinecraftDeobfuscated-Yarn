/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface DecoratableArgumentType<T>
extends ArgumentType<T> {
    @Nullable
    public static CompletableFuture<Text> decorate(ArgumentCommandNode<?, ?> node, CommandContextBuilder<ServerCommandSource> builder) throws CommandSyntaxException {
        ArgumentType<?> argumentType = node.getType();
        if (argumentType instanceof DecoratableArgumentType) {
            DecoratableArgumentType decoratableArgumentType = (DecoratableArgumentType)argumentType;
            return decoratableArgumentType.decorate(builder, node.getName());
        }
        return null;
    }

    public static boolean isDecoratableArgumentNode(CommandNode<?> node) {
        ArgumentCommandNode argumentCommandNode;
        return node instanceof ArgumentCommandNode && (argumentCommandNode = (ArgumentCommandNode)node).getType() instanceof DecoratableArgumentType;
    }

    @Nullable
    default public CompletableFuture<Text> decorate(CommandContextBuilder<ServerCommandSource> builder, String argumentName) throws CommandSyntaxException {
        ParsedArgument<ServerCommandSource, ?> parsedArgument = builder.getArguments().get(argumentName);
        if (parsedArgument != null && this.getFormatClass().isInstance(parsedArgument.getResult())) {
            return this.decorate(builder.getSource(), this.getFormatClass().cast(parsedArgument.getResult()));
        }
        return null;
    }

    public CompletableFuture<Text> decorate(ServerCommandSource var1, T var2) throws CommandSyntaxException;

    public Class<T> getFormatClass();
}

