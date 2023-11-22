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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommandFunctionArgumentType implements ArgumentType<CommandFunctionArgumentType.FunctionArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
	private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("arguments.function.tag.unknown", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("arguments.function.unknown", id)
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
				public Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
					return CommandFunctionArgumentType.getFunctionTag(context, identifier);
				}

				@Override
				public Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(
					CommandContext<ServerCommandSource> context
				) throws CommandSyntaxException {
					return Pair.of(identifier, Either.right(CommandFunctionArgumentType.getFunctionTag(context, identifier)));
				}

				@Override
				public Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
					return Pair.of(identifier, CommandFunctionArgumentType.getFunctionTag(context, identifier));
				}
			};
		} else {
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new CommandFunctionArgumentType.FunctionArgument() {
				@Override
				public Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
					return Collections.singleton(CommandFunctionArgumentType.getFunction(context, identifier));
				}

				@Override
				public Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(
					CommandContext<ServerCommandSource> context
				) throws CommandSyntaxException {
					return Pair.of(identifier, Either.left(CommandFunctionArgumentType.getFunction(context, identifier)));
				}

				@Override
				public Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
					return Pair.of(identifier, Collections.singleton(CommandFunctionArgumentType.getFunction(context, identifier)));
				}
			};
		}
	}

	static CommandFunction<ServerCommandSource> getFunction(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
		return (CommandFunction<ServerCommandSource>)context.getSource()
			.getServer()
			.getCommandFunctionManager()
			.getFunction(id)
			.orElseThrow(() -> UNKNOWN_FUNCTION_EXCEPTION.create(id.toString()));
	}

	static Collection<CommandFunction<ServerCommandSource>> getFunctionTag(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
		Collection<CommandFunction<ServerCommandSource>> collection = context.getSource().getServer().getCommandFunctionManager().getTag(id);
		if (collection == null) {
			throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
		} else {
			return collection;
		}
	}

	public static Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<CommandFunctionArgumentType.FunctionArgument>getArgument(name, CommandFunctionArgumentType.FunctionArgument.class).getFunctions(context);
	}

	public static Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(
		CommandContext<ServerCommandSource> context, String name
	) throws CommandSyntaxException {
		return context.<CommandFunctionArgumentType.FunctionArgument>getArgument(name, CommandFunctionArgumentType.FunctionArgument.class).getFunctionOrTag(context);
	}

	public static Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(
		CommandContext<ServerCommandSource> context, String name
	) throws CommandSyntaxException {
		return context.<CommandFunctionArgumentType.FunctionArgument>getArgument(name, CommandFunctionArgumentType.FunctionArgument.class)
			.getIdentifiedFunctions(context);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface FunctionArgument {
		Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

		Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(
			CommandContext<ServerCommandSource> context
		) throws CommandSyntaxException;

		Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	}
}
