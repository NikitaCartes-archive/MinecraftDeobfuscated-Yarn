/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CommandFunctionArgumentType
implements ArgumentType<FunctionArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.function.tag.unknown", object));
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.function.unknown", object));

    public static CommandFunctionArgumentType commandFunction() {
        return new CommandFunctionArgumentType();
    }

    @Override
    public FunctionArgument parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            final Identifier identifier = Identifier.fromCommandInput(stringReader);
            return new FunctionArgument(){

                @Override
                public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    Tag<CommandFunction> tag = CommandFunctionArgumentType.getFunctionTag(commandContext, identifier);
                    return tag.values();
                }

                @Override
                public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    return Pair.of(identifier, Either.right(CommandFunctionArgumentType.getFunctionTag(commandContext, identifier)));
                }
            };
        }
        final Identifier identifier = Identifier.fromCommandInput(stringReader);
        return new FunctionArgument(){

            @Override
            public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Collections.singleton(CommandFunctionArgumentType.getFunction(commandContext, identifier));
            }

            @Override
            public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Pair.of(identifier, Either.left(CommandFunctionArgumentType.getFunction(commandContext, identifier)));
            }
        };
    }

    static CommandFunction getFunction(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        return context.getSource().getServer().getCommandFunctionManager().getFunction(id).orElseThrow(() -> UNKNOWN_FUNCTION_EXCEPTION.create(id.toString()));
    }

    static Tag<CommandFunction> getFunctionTag(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        Tag<CommandFunction> tag = context.getSource().getServer().getCommandFunctionManager().getTag(id);
        if (tag == null) {
            throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
        }
        return tag;
    }

    public static Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, FunctionArgument.class).getFunctions(context);
    }

    public static Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, FunctionArgument.class).getFunctionOrTag(commandContext);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static interface FunctionArgument {
        public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

        public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

