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

public class CommandFunctionArgumentType implements ArgumentType<CommandFunctionArgumentType.FunctionArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
	private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.function.tag.unknown", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.function.unknown", object)
	);

	public static CommandFunctionArgumentType commandFunction() {
		return new CommandFunctionArgumentType();
	}

	public CommandFunctionArgumentType.FunctionArgument parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			stringReader.skip();
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new CommandFunctionArgumentType.FunctionArgument() {
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
		} else {
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new CommandFunctionArgumentType.FunctionArgument() {
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
	}

	static CommandFunction getFunction(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
		return (CommandFunction)context.getSource()
			.getMinecraftServer()
			.getCommandFunctionManager()
			.getFunction(id)
			.orElseThrow(() -> UNKNOWN_FUNCTION_EXCEPTION.create(id.toString()));
	}

	static Tag<CommandFunction> getFunctionTag(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
		Tag<CommandFunction> tag = context.getSource().getMinecraftServer().getCommandFunctionManager().getTaggedFunctions(id);
		if (tag == null) {
			throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
		} else {
			return tag;
		}
	}

	public static Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<CommandFunctionArgumentType.FunctionArgument>getArgument(name, CommandFunctionArgumentType.FunctionArgument.class).getFunctions(context);
	}

	public static Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(
		CommandContext<ServerCommandSource> commandContext, String string
	) throws CommandSyntaxException {
		return commandContext.<CommandFunctionArgumentType.FunctionArgument>getArgument(string, CommandFunctionArgumentType.FunctionArgument.class)
			.getFunctionOrTag(commandContext);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface FunctionArgument {
		Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;

		Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}
}
